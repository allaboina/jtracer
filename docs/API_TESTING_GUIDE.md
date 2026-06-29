# API Testing Guide

Hands-on guide for testing JTracer REST APIs with **curl** and **Postman**.  
Written for first-time API testers who want to understand how requests flow in a real product.

**Start here if you want a checklist:** [API_TESTING_CHECKLIST.md](./API_TESTING_CHECKLIST.md) — step-by-step tracker you can work through over multiple sessions.

---

## 1. How JTracer APIs work (mental model)

```text
Your Mac (host)
  │
  ├─ Collectors (native)     ps, lsof, arp  →  every 3–60 seconds
  │       │
  │       ▼
  ├─ Spring Boot backend     persists to SQLite (./data/jtracer-live.db)
  │       │
  │       ▼
  └─ REST API (port 8080)    reads DB → returns JSON
          ▲
          │
     curl / Postman / future React UI
```

**Key ideas:**

| Concept | JTracer example |
|---------|-----------------|
| **Server** | Spring Boot app listening on `127.0.0.1:8080` |
| **Endpoint** | URL path like `/api/v1/processes` |
| **HTTP method** | `GET` = read data, `POST` = send data (e.g. device label) |
| **Request** | What you send (URL, query params, optional JSON body) |
| **Response** | JSON with `success`, `timestamp`, `data` or `error` |
| **Live data** | Collectors must be running; API reads what was already saved |

The API does **not** run `ps` or `lsof` when you call it. It returns the **latest persisted snapshot** from collectors.

---

## 2. Prerequisites

```bash
# Terminal 1 — start backend (collectors + API)
cd backend
cp ../config/application.yml.example src/main/resources/application.yml   # if needed
mkdir -p ../data
mvn spring-boot:run
```

Wait until logs show collector activity (process/network/LAN polls).  
First health snapshot appears after ~10 seconds.

**Base URL (always use this):**

```text
http://127.0.0.1:8080
```

---

## 3. Standard response shape

**Success:**

```json
{
  "success": true,
  "timestamp": "2026-06-26T01:04:21.542932Z",
  "data": { }
}
```

**Error (example — no health data yet):**

```json
{
  "success": false,
  "timestamp": "...",
  "error": {
    "code": "NO_HEALTH_DATA",
    "message": "No system health snapshot available yet"
  }
}
```

**HTTP status codes you will see:**

| Code | Meaning |
|------|---------|
| 200 | OK — request succeeded |
| 404 | Resource not found (bad process/device ID) |
| 503 | Service unavailable (e.g. no health snapshot yet) |

---

## 4. curl cheat sheet

Pretty-print JSON (recommended):

```bash
curl -s http://127.0.0.1:8080/api/v1/system/health | python3 -m json.tool
```

| Flag | Purpose |
|------|---------|
| `-s` | Silent (hide progress bar) |
| `-X POST` | Use POST method |
| `-H 'Content-Type: application/json'` | Tell server body is JSON |
| `-d '{"label":"..."}'` | Request body |

---

## 5. Endpoint walkthrough (recommended order)

### Step 1 — System health (single object)

**Path:** `GET /api/v1/system/health`

```bash
curl -s http://127.0.0.1:8080/api/v1/system/health | python3 -m json.tool
```

**What to verify:** `data.cpuPct`, `data.activeProcessCount`, `data.onlineLanDeviceCount` match your Mac activity.

**Trace in code:** `SystemController` → `SystemHealthQueryService` → `system_health_snapshots` table.

---

### Step 1b — System health history (time series)

**Path:** `GET /api/v1/system/snapshots`

```bash
curl -s "http://127.0.0.1:8080/api/v1/system/snapshots?minutes=60" | python3 -m json.tool
```

**What to verify:** `data` is an array of `{ timestamp, cpuPct, memoryPct }` points. More points appear as health collector runs (~every 10s).

**Tracker:** See [API_TESTING_CHECKLIST.md](./API_TESTING_CHECKLIST.md) for a full checkbox list.

---

### Step 2 — Process list (array)

**Path:** `GET /api/v1/processes`

```bash
# Top CPU processes (default sort)
curl -s "http://127.0.0.1:8080/api/v1/processes?sort=cpu&limit=10" | python3 -m json.tool

# Search by name
curl -s "http://127.0.0.1:8080/api/v1/processes?search=chrome&limit=5" | python3 -m json.tool

# Sort by memory
curl -s "http://127.0.0.1:8080/api/v1/processes?sort=memory&limit=10" | python3 -m json.tool
```

**Query parameters:**

| Param | Values | Purpose |
|-------|--------|---------|
| `sort` | `cpu`, `memory`, `name` | Order results |
| `limit` | number (max 200, default 50) | How many rows |
| `search` | text | Filter by process name |

**What to verify:** `data` is an array; each item has `processId`, `pid`, `processName`, `cpuPct`, `connectionCount`.

**Copy a `processId`** from the response for the next steps.

---

### Step 3 — Process detail

**Path:** `GET /api/v1/processes/{processId}`

```bash
PROCESS_ID="paste-uuid-here"
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}" | python3 -m json.tool
```

**What to verify:** `commandLine`, `firstSeenAt`, `lastSeenAt`, `status`.

---

### Step 4 — Process metrics (time series)

**Path:** `GET /api/v1/processes/{processId}/metrics`

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}/metrics?minutes=30" | python3 -m json.tool
```

**What to verify:** `data` is an array of `{ timestamp, cpuPct, memoryPct, rssMb }` points (more points after backend runs longer).

---

### Step 5 — Process connections

**Path:** `GET /api/v1/processes/{processId}/connections`

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}/connections" | python3 -m json.tool
```

**What to verify:** `remoteIp`, `domain`, `port`, `protocol` for that process only.

---

### Step 6 — All connections

**Path:** `GET /api/v1/connections`

```bash
curl -s http://127.0.0.1:8080/api/v1/connections | python3 -m json.tool

# TCP only
curl -s "http://127.0.0.1:8080/api/v1/connections?protocol=TCP" | python3 -m json.tool

# Filter by process
curl -s "http://127.0.0.1:8080/api/v1/connections?processId=${PROCESS_ID}" | python3 -m json.tool
```

**Generate traffic to see changes:**

```bash
curl -s https://example.com > /dev/null
# Wait ~3s (network poll), call /api/v1/connections again
```

---

### Step 7 — Domains

**Path:** `GET /api/v1/domains`

```bash
curl -s "http://127.0.0.1:8080/api/v1/domains?sort=frequency" | python3 -m json.tool
```

**What to verify:** `connectionCount`, `processCount`, `lastSeenAt` per domain.

---

### Step 8 — LAN devices

**Path:** `GET /api/v1/devices`

```bash
curl -s http://127.0.0.1:8080/api/v1/devices | python3 -m json.tool

# Online only
curl -s "http://127.0.0.1:8080/api/v1/devices?status=ONLINE" | python3 -m json.tool

# Unknown / low-confidence devices
curl -s "http://127.0.0.1:8080/api/v1/devices?unknown=true" | python3 -m json.tool
```

**Copy a `deviceId`** for detail and labeling.

---

### Step 9 — Device detail

**Path:** `GET /api/v1/devices/{deviceId}`

```bash
DEVICE_ID="paste-uuid-here"
curl -s "http://127.0.0.1:8080/api/v1/devices/${DEVICE_ID}" | python3 -m json.tool
```

---

### Step 10 — Label a device (POST)

**Path:** `POST /api/v1/devices/{deviceId}/label`

```bash
curl -s -X POST "http://127.0.0.1:8080/api/v1/devices/${DEVICE_ID}/label" \
  -H "Content-Type: application/json" \
  -d '{"label":"Living Room Apple TV","deviceType":"TV"}' | python3 -m json.tool
```

**What to verify:** `displayName` becomes your label; `confidence` becomes `CONFIRMED`.

---

## 6. Postman setup

### Create a collection

1. Open Postman → **New** → **Collection** → name it `JTracer Local`.
2. Add collection variable:
   - `baseUrl` = `http://127.0.0.1:8080`
   - `processId` = (empty — fill after first processes call)
   - `deviceId` = (empty — fill after devices call)

### Add requests (use `{{baseUrl}}`)

| Name | Method | URL |
|------|--------|-----|
| System Health | GET | `{{baseUrl}}/api/v1/system/health` |
| List Processes | GET | `{{baseUrl}}/api/v1/processes?sort=cpu&limit=10` |
| Process Detail | GET | `{{baseUrl}}/api/v1/processes/{{processId}}` |
| Process Metrics | GET | `{{baseUrl}}/api/v1/processes/{{processId}}/metrics?minutes=30` |
| Process Connections | GET | `{{baseUrl}}/api/v1/processes/{{processId}}/connections` |
| List Connections | GET | `{{baseUrl}}/api/v1/connections` |
| List Domains | GET | `{{baseUrl}}/api/v1/domains?sort=frequency` |
| List Devices | GET | `{{baseUrl}}/api/v1/devices` |
| Device Detail | GET | `{{baseUrl}}/api/v1/devices/{{deviceId}}` |
| Label Device | POST | `{{baseUrl}}/api/v1/devices/{{deviceId}}/label` |

**POST body (Label Device):** Body → raw → JSON:

```json
{
  "label": "Living Room Apple TV",
  "deviceType": "TV"
}
```

### Postman Tests tab (auto-save IDs)

On **List Processes**, add this in **Tests**:

```javascript
const json = pm.response.json();
if (json.data && json.data.length > 0) {
  pm.collectionVariables.set("processId", json.data[0].processId);
}
```

On **List Devices**, add:

```javascript
const json = pm.response.json();
if (json.data && json.data.length > 0) {
  pm.collectionVariables.set("deviceId", json.data[0].deviceId);
}
```

Then run **List Processes** once before **Process Detail**.

---

## 7. Tracing request paths in code

Use this map when you want to follow a request from URL to database:

```text
HTTP Request
    │
    ▼
com.jtracer.api.v1.*Controller     ← URL mapping (@GetMapping)
    │
    ▼
com.jtracer.service.*QueryService  ← business read logic (interface)
    │
    ▼
com.jtracer.service.*QueryServiceImpl   ← private workspace only
    │
    ▼
com.jtracer.repository.*Repository    ← SQL via JPA
    │
    ▼
SQLite tables (observed_processes, network_connections, lan_devices, ...)
```

| Endpoint | Controller | Query service | Main tables |
|----------|------------|---------------|-------------|
| `/api/v1/system/health` | `SystemController` | `SystemHealthQueryService` | `system_health_snapshots` |
| `/api/v1/processes` | `ProcessController` | `ProcessQueryService` | `observed_processes`, `process_metric_samples` |
| `/api/v1/connections` | `ConnectionController` | `ConnectionQueryService` | `network_connections` |
| `/api/v1/domains` | `DomainController` | `DomainQueryService` | `network_connections`, `domain_identities` |
| `/api/v1/devices` | `DeviceController` | `DeviceQueryService` | `lan_devices`, `device_identities` |

**In Cursor:** `Cmd+P` → type `ProcessController` → follow injected service → repository.

---

## 8. Real-time behavior checklist

| Action | Wait | Then call |
|--------|------|-----------|
| Start backend | ~10s | `/api/v1/system/health` |
| Browse in Chrome | ~3–5s | `/api/v1/processes?search=chrome` |
| `curl https://example.com` | ~3s | `/api/v1/connections` |
| Phone joins WiFi | ~60s | `/api/v1/devices` |

Data refreshes on **collector poll intervals**, not when you hit the API.

---

## 9. Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| Connection refused | Backend not running | `mvn spring-boot:run` |
| `NO_HEALTH_DATA` | Collectors haven't written yet | Wait 10–15s, retry |
| Empty `processes` array | No RUNNING processes in session | Check collectors started (`jtracer.collector.auto-start: true`) |
| 404 on process/device | Wrong UUID or wrong session | Copy ID from list response |
| Stale connection list | Normal poll delay | Wait 3s after generating traffic |

---

## 10. Production vs local (JTracer)

| | Local MVP | Future production |
|--|-----------|-------------------|
| Host | `127.0.0.1:8080` | Same machine or LAN with auth |
| HTTPS | No | Yes (reverse proxy) |
| Auth | None | Token / login before UI Phase |
| Data | Your Mac only | Still local-first by design |

The **request/response pattern is identical** in production; only URL, auth, and TLS change.

---

## 11. Quick validation script

Save as `scripts/smoke-api.sh` (optional):

```bash
#!/usr/bin/env bash
BASE="http://127.0.0.1:8080"
for path in \
  "/api/v1/system/health" \
  "/api/v1/processes?limit=3" \
  "/api/v1/connections" \
  "/api/v1/domains" \
  "/api/v1/devices"; do
  echo "==> GET $path"
  curl -s -o /dev/null -w "HTTP %{http_code}\n" "${BASE}${path}"
done
```

Expected: all `HTTP 200` (health may be `503` in first 10 seconds).
