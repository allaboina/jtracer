# API Testing Checklist & Tracker

Use this document to validate JTracer APIs at your own pace.  
Each row is one test you can check off when done.

**Companion docs:**
- [API_TESTING_GUIDE.md](./API_TESTING_GUIDE.md) — concepts, curl examples, Postman setup, code tracing
- [API_CONTRACT.md](./API_CONTRACT.md) — full request/response spec

**How to use this file:**
1. Start the backend once (see Setup below).
2. Work top-to-bottom — later steps need IDs from earlier steps.
3. Change `Status` from `⬜` to `✅` when the test passes.
4. Add notes in the **Notes** column if something looks wrong.

---

## Setup (do once per session)

| # | Step | Command / action | Status | Notes |
|---|------|------------------|--------|-------|
| S1 | Create data directory | `mkdir -p data` | ⬜ | |
| S2 | Copy config if missing | `cp config/application.yml.example backend/src/main/resources/application.yml` | ⬜ | Skip if file exists |
| S3 | Start backend | `cd backend && mvn spring-boot:run` | ⬜ | Leave running |
| S4 | Wait for collectors | Watch logs for process/network/health polls (~10–15s) | ⬜ | |
| S5 | Smoke test (optional) | `./scripts/smoke-api.sh` from repo root | ⬜ | All HTTP 200 (health may be 503 first) |
| S6 | Postman collection | Create `JTracer Local` with `baseUrl` = `http://127.0.0.1:8080` | ⬜ | See guide §6 |

**Base URL:** `http://127.0.0.1:8080`

---

## Master endpoint tracker

| # | Endpoint | Method | Status | Tested on | Notes |
|---|----------|--------|--------|-----------|-------|
| 1 | `/api/v1/system/health` | GET | ⬜ | | |
| 2 | `/api/v1/system/snapshots` | GET | ⬜ | | |
| 3 | `/api/v1/processes` | GET | ⬜ | | |
| 4 | `/api/v1/processes/{id}` | GET | ⬜ | | |
| 5 | `/api/v1/processes/{id}/metrics` | GET | ⬜ | | |
| 6 | `/api/v1/processes/{id}/connections` | GET | ⬜ | | |
| 7 | `/api/v1/connections` | GET | ⬜ | | |
| 8 | `/api/v1/domains` | GET | ⬜ | | |
| 9 | `/api/v1/devices` | GET | ⬜ | | |
| 10 | `/api/v1/devices/{id}` | GET | ⬜ | | |
| 11 | `/api/v1/devices/{id}/label` | POST | ⬜ | | |
| 12 | `/api/v1/insights` | GET | ⬜ | | May return `[]` until insight rules run |
| 13 | `/api/v1/insights/{id}/dismiss` | POST | ⬜ | | Skip if no insights |
| 14 | `/ws/live` | WebSocket | ⬜ | | See WebSocket section |

---

## Detailed test steps

### 1 — System health (current snapshot)

**Learn:** Single-object response; proves collectors → DB → API pipeline works.

```bash
curl -s http://127.0.0.1:8080/api/v1/system/health | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| HTTP status | `200` (or `503` in first 10s) | ⬜ | |
| `success` | `true` | ⬜ | |
| `data.cpuPct` | Number > 0 on active Mac | ⬜ | |
| `data.activeProcessCount` | Hundreds typical | ⬜ | |
| `data.onlineLanDeviceCount` | ≥ 0 | ⬜ | |

**Code trace:** `SystemController` → `SystemHealthQueryService` → `system_health_snapshots`

---

### 2 — System health history (time series)

**Learn:** Same resource, historical rows; used for trend charts.

```bash
curl -s "http://127.0.0.1:8080/api/v1/system/snapshots?minutes=60" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| HTTP status | `200` | ⬜ | |
| `data` | Array | ⬜ | |
| Each point | `timestamp`, `cpuPct`, `memoryPct` | ⬜ | |
| More points over time | Re-run after 2+ minutes | ⬜ | Health polls every ~10s |

**Code trace:** `SystemController` → `SystemHealthQueryService.listSnapshots`

---

### 3 — Process list

**Learn:** Array response + query parameters.

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes?sort=cpu&limit=10" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| `data` | Non-empty array | ⬜ | |
| Each item | `processId`, `pid`, `processName`, `cpuPct` | ⬜ | |
| **Save** `processId` | Copy top row UUID | ⬜ | `export PROCESS_ID="..."` |

**Also try:**

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes?search=chrome&limit=5" | python3 -m json.tool
curl -s "http://127.0.0.1:8080/api/v1/processes?sort=memory&limit=10" | python3 -m json.tool
```

| Variant | Status | Notes |
|---------|--------|-------|
| `search=chrome` | ⬜ | |
| `sort=memory` | ⬜ | |

---

### 4 — Process detail

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| HTTP status | `200` | ⬜ | |
| `commandLine` | Present for most processes | ⬜ | |
| Bad ID | `404` + `NOT_FOUND` | ⬜ | Try `curl .../processes/bad-id` |

---

### 5 — Process metrics (history)

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}/metrics?minutes=30" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| `data` | Array of `{timestamp, cpuPct, memoryPct, rssMb}` | ⬜ | |
| More points after wait | Re-run after 1–2 min | ⬜ | Process poll ~5s |

---

### 6 — Process connections

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}/connections" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| Browser process | May have many rows | ⬜ | Try Chrome/Firefox `processId` |
| Each row | `remoteIp`, `port`, `protocol` | ⬜ | |

---

### 7 — All connections

```bash
curl -s http://127.0.0.1:8080/api/v1/connections | python3 -m json.tool
```

**Live update test:**

```bash
curl -s https://example.com > /dev/null
sleep 4
curl -s http://127.0.0.1:8080/api/v1/connections | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| Initial list | Non-empty on active Mac | ⬜ | |
| After traffic | New/updated rows (~3s poll) | ⬜ | |
| `?protocol=TCP` | Filtered list | ⬜ | |

---

### 8 — Domains

```bash
curl -s "http://127.0.0.1:8080/api/v1/domains?sort=frequency" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| `connectionCount` | Per domain | ⬜ | |
| Browse websites first | More domains appear | ⬜ | |

---

### 9 — LAN devices

```bash
curl -s http://127.0.0.1:8080/api/v1/devices | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| `data` | Array (may be empty on isolated network) | ⬜ | |
| **Save** `deviceId` | Copy one UUID | ⬜ | `export DEVICE_ID="..."` |
| `?status=ONLINE` | Online devices only | ⬜ | |
| `?unknown=true` | Low-confidence devices | ⬜ | |

---

### 10 — Device detail

```bash
curl -s "http://127.0.0.1:8080/api/v1/devices/${DEVICE_ID}" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| `displayName`, `confidence`, `evidence` | Present | ⬜ | |

---

### 11 — Label device (POST)

**Learn:** POST sends JSON body; server writes to DB.

```bash
curl -s -X POST "http://127.0.0.1:8080/api/v1/devices/${DEVICE_ID}/label" \
  -H "Content-Type: application/json" \
  -d '{"label":"Test Device","deviceType":"TV"}' | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| `displayName` | Your label | ⬜ | |
| `confidence` | `CONFIRMED` | ⬜ | |
| Re-fetch detail | Label persisted | ⬜ | |

---

### 12 — Insights

```bash
curl -s http://127.0.0.1:8080/api/v1/insights | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| HTTP status | `200` | ⬜ | |
| `data` | Array (empty OK for now) | ⬜ | Insight generation is Phase 7+ |
| If rows exist | `insightId`, `severity`, `title`, `explanation` | ⬜ | Save `INSIGHT_ID` |

---

### 13 — Dismiss insight (POST)

Skip if step 12 returned an empty array.

```bash
curl -s -X POST "http://127.0.0.1:8080/api/v1/insights/${INSIGHT_ID}/dismiss" | python3 -m json.tool
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| Insight removed from active list | Re-call GET /insights | ⬜ | |

---

### 14 — WebSocket live updates

**Learn:** Server pushes events when collectors write new data (no polling from client).

**Option A — websocat (if installed):**

```bash
websocat ws://127.0.0.1:8080/ws/live
```

**Option B — Postman:** New → WebSocket Request → `ws://127.0.0.1:8080/ws/live` → Connect

**Option C — Browser console:**

```javascript
const ws = new WebSocket('ws://127.0.0.1:8080/ws/live');
ws.onmessage = (e) => console.log(JSON.parse(e.data));
```

| Check | Expected | Status | Notes |
|-------|----------|--------|-------|
| Connection opens | No immediate error | ⬜ | |
| `system.health.updated` | Every ~10s | ⬜ | |
| `connection.updated` | After network poll (~3s) | ⬜ | Generate traffic |
| `process.updated` | After process poll (~5s) | ⬜ | |
| `device.updated` | After LAN scan (~60s) | ⬜ | |

**Message shape:**

```json
{
  "event": "system.health.updated",
  "timestamp": "...",
  "payload": { }
}
```

---

## Postman quick reference

| Request name | Method | URL |
|--------------|--------|-----|
| System Health | GET | `{{baseUrl}}/api/v1/system/health` |
| System Snapshots | GET | `{{baseUrl}}/api/v1/system/snapshots?minutes=60` |
| List Processes | GET | `{{baseUrl}}/api/v1/processes?sort=cpu&limit=10` |
| Process Detail | GET | `{{baseUrl}}/api/v1/processes/{{processId}}` |
| Process Metrics | GET | `{{baseUrl}}/api/v1/processes/{{processId}}/metrics` |
| Process Connections | GET | `{{baseUrl}}/api/v1/processes/{{processId}}/connections` |
| List Connections | GET | `{{baseUrl}}/api/v1/connections` |
| List Domains | GET | `{{baseUrl}}/api/v1/domains?sort=frequency` |
| List Devices | GET | `{{baseUrl}}/api/v1/devices` |
| Device Detail | GET | `{{baseUrl}}/api/v1/devices/{{deviceId}}` |
| Label Device | POST | `{{baseUrl}}/api/v1/devices/{{deviceId}}/label` |
| List Insights | GET | `{{baseUrl}}/api/v1/insights` |
| Dismiss Insight | POST | `{{baseUrl}}/api/v1/insights/{{insightId}}/dismiss` |
| Live WebSocket | WS | `ws://127.0.0.1:8080/ws/live` |

**Auto-save IDs (Tests tab on List Processes):**

```javascript
const json = pm.response.json();
if (json.data?.length) pm.collectionVariables.set("processId", json.data[0].processId);
```

---

## Real-world validation scenarios (Phase 7 preview)

Do these when you have more time — they prove the product works, not just the API shape.

| # | Scenario | What to do | API to verify | Status | Notes |
|---|----------|------------|---------------|--------|-------|
| V1 | Browse the web | Open Chrome, visit 3 sites | `/domains`, `/connections` | ⬜ | |
| V2 | Terminal traffic | `curl https://example.com` | `/connections` after 3s | ⬜ | |
| V3 | High CPU | Run a heavy task | `/processes?sort=cpu` | ⬜ | |
| V4 | New WiFi device | Connect phone to WiFi | `/devices` after ~60s | ⬜ | |
| V5 | Label accuracy | Label unknown device | GET device detail | ⬜ | |
| V6 | DB cross-check | `sqlite3 data/jtracer-live.db "SELECT COUNT(*) FROM observed_processes;"` | Compare to health `activeProcessCount` | ⬜ | |

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| Connection refused | Backend not running | `mvn spring-boot:run` |
| `NO_HEALTH_DATA` / 503 | Collectors not warmed up | Wait 15s, retry |
| Empty processes | Wrong session or collectors off | Check `jtracer.collector.auto-start: true` |
| 404 on ID | Wrong UUID | Copy from list response |
| Stale connections | Poll delay | Wait 3–5s after traffic |
| WebSocket fails | Backend not running or wrong URL | Use `ws://127.0.0.1:8080/ws/live` |
| Empty insights | Normal for MVP | Insight rules not fully wired yet |

---

## Progress summary

Fill in when you finish a block:

| Block | Endpoints | Done | Date |
|-------|-----------|------|------|
| Setup | S1–S6 | ⬜ / 6 | |
| System | 1–2 | ⬜ / 2 | |
| Processes | 3–6 | ⬜ / 4 | |
| Network | 7–8 | ⬜ / 2 | |
| Devices | 9–11 | ⬜ / 3 | |
| Insights | 12–13 | ⬜ / 2 | |
| WebSocket | 14 | ⬜ / 1 | |
| Scenarios | V1–V6 | ⬜ / 6 | |

**Overall:** ___ / 14 endpoints + ___ / 6 scenarios

---

*Last updated: Phase 6 completion — snapshots, insights, WebSocket*
