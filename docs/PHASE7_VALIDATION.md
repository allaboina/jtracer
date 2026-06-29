# Phase 7 — Validation Runbook

Validate JTracer data correctness **before** building the React dashboard (Phase 8).

**Rule:** No UI work until this phase passes.

**Companion docs:**
- [API_TESTING_CHECKLIST.md](./API_TESTING_CHECKLIST.md) — endpoint-by-endpoint API tests
- [MVP_ACCEPTANCE_CRITERIA.md](./MVP_ACCEPTANCE_CRITERIA.md) — pass thresholds
- [DEVELOPMENT_PHASES.md](./DEVELOPMENT_PHASES.md) — phase goals

---

## How to use this runbook

1. Start backend once: `cd backend && mvn spring-boot:run`
2. Work through sections **A → F** in order (each maps to an MVP workflow).
3. Mark **Status** as `⬜` pending, `✅` pass, `❌` fail, `⏭️` skipped (with reason).
4. Fill **Evidence** with command output snippets or API JSON fields.
5. Section **G** captures known limitations — required before Phase 8.

**Base URL:** `http://127.0.0.1:8080`  
**Database:** `./data/jtracer-live.db`

---

## Pre-flight

| # | Check | Command | Status | Evidence |
|---|-------|---------|--------|----------|
| P1 | Backend running | `curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/api/v1/system/health` | ⬜ | Expect `200` (or `503` in first 15s) |
| P2 | Collectors active | Backend logs show process/network/health polls | ⬜ | |
| P3 | Smoke script | `./scripts/smoke-api.sh` | ⬜ | All `200` |
| P4 | DB exists | `ls -la data/jtracer-live.db` | ⬜ | |

---

## A — Process observability

**MVP question:** Which processes are consuming high CPU or memory?

### A1 — Process count sanity

Compare API health count vs macOS `ps`.

```bash
# API
curl -s http://127.0.0.1:8080/api/v1/system/health | python3 -c "import sys,json; d=json.load(sys.stdin)['data']; print('API processes:', d['activeProcessCount'])"

# macOS ground truth (running processes)
ps -ax -o pid= | wc -l | xargs echo "ps count:"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Count within ~10% | API count close to `ps` count | ⬜ | |

### A2 — Top CPU processes match Activity Monitor

```bash
# API top 5 CPU
curl -s "http://127.0.0.1:8080/api/v1/processes?sort=cpu&limit=5" | python3 -m json.tool

# macOS top 5 by CPU (snapshot)
ps -axo pid,pcpu,comm | sort -k2 -rn | head -6
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Top 3 names overlap | ≥2 of top 3 process names appear in both lists | ⬜ | |
| CPU values plausible | Not all zero on active system | ⬜ | |

### A3 — Search finds known apps

Open **Chrome** or **Cursor**, wait 5s, then:

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes?search=chrome&limit=5" | python3 -m json.tool
curl -s "http://127.0.0.1:8080/api/v1/processes?search=cursor&limit=5" | python3 -m json.tool
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Chrome visible | Rows returned when Chrome open | ⬜ | |
| Cursor visible | Rows returned when Cursor open | ⬜ | |

### A4 — Process detail + metrics history

```bash
# Copy processId from A2, then:
export PROCESS_ID="<uuid-from-list>"
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}" | python3 -m json.tool
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}/metrics?minutes=10" | python3 -c "import sys,json; print('metric points:', len(json.load(sys.stdin)['data']))"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Detail has commandLine | Non-empty for most processes | ⬜ | |
| Metrics accumulate | ≥2 points after 1–2 min runtime | ⬜ | |

### A5 — High CPU stress test (manual)

1. Run a CPU-heavy task (e.g. `yes > /dev/null` in a terminal, or export video).
2. Wait 10s.
3. Re-run A2 — stressed process should appear near top.

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Stress process detected | Appears in top CPU list | ⬜ | |

**Workflow A pass threshold:** ≥95% of visible user processes detected (subjective spot-check of 10 common apps).

| Workflow A overall | Status | Notes |
|--------------------|--------|-------|
| | ⬜ | |

---

## B — Network observability

**MVP question:** Which processes are making network connections?

### B1 — Connection count sanity

```bash
curl -s http://127.0.0.1:8080/api/v1/system/health | python3 -c "import sys,json; print('API connections:', json.load(sys.stdin)['data']['activeConnectionCount'])"

# macOS ESTABLISHED TCP (approximate)
lsof -nP -iTCP -sTCP:ESTABLISHED 2>/dev/null | tail -n +2 | wc -l | xargs echo "lsof ESTABLISHED:"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Count same order of magnitude | Within ~30% (lsof vs API differ by design) | ⬜ | |

### B2 — Generate traffic and observe update

```bash
curl -s https://example.com > /dev/null
sleep 4
curl -s "http://127.0.0.1:8080/api/v1/connections?limit=5" | python3 -m json.tool
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Connections list non-empty | On active Mac | ⬜ | |
| New activity reflected | List changes after traffic (~3s poll) | ⬜ | |

### B3 — Process ↔ connection correlation

Pick a browser `processId` from A3:

```bash
curl -s "http://127.0.0.1:8080/api/v1/processes/${PROCESS_ID}/connections" | python3 -m json.tool
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Browser has connections | Rows with remoteIp/port when browsing | ⬜ | |
| Fields present | remoteIp, port, protocol | ⬜ | |

**Workflow B pass threshold:** ≥80% of active outbound connections visible (spot-check 10 connections in Activity Monitor / `lsof` vs API).

| Workflow B overall | Status | Notes |
|--------------------|--------|-------|
| | ⬜ | |

---

## C — Domain visibility

**MVP question:** Which domains or IPs are applications talking to?

### C1 — Domain list after browsing

Browse 3 websites in Chrome, wait 5s:

```bash
curl -s "http://127.0.0.1:8080/api/v1/domains?sort=frequency" | python3 -m json.tool
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Domains appear | Non-empty after browsing | ⬜ | |
| connectionCount > 0 | Per domain | ⬜ | |

### C2 — DNS resolution rate (spot check)

Pick 5 connections from API with public IPs; verify hostname or domain field populated.

```bash
curl -s "http://127.0.0.1:8080/api/v1/connections?limit=20" | python3 -c "
import sys,json
for c in json.load(sys.stdin)['data'][:10]:
    print(c.get('remoteIp'), '->', c.get('domain') or '(no domain)')
"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| ≥60% have domain | Per MVP criteria | ⬜ | |

**Workflow C overall** | Status | Notes |
|----------------------|--------|-------|
| | ⬜ | |

---

## D — LAN device discovery

**MVP question:** Which devices are on my home network?

### D1 — Device list

```bash
curl -s http://127.0.0.1:8080/api/v1/devices | python3 -m json.tool
sqlite3 data/jtracer-live.db "SELECT ip_address, hostname, status, device_type FROM lan_devices LIMIT 10;"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Devices returned | Matches your network (may be 0 on isolated LAN) | ⬜ | |
| IP + status present | Each row has ipAddress, status | ⬜ | |

### D2 — New device on WiFi (manual)

1. Connect phone to same WiFi.
2. Wait **60s** (LAN poll interval).
3. Re-run D1 — phone or new IP should appear.

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| New device detected | New row or status ONLINE | ⬜ | |

### D3 — Device goes offline (manual)

Disconnect a smart device or phone from WiFi, wait 60s, re-check.

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Status updates | OFFLINE or lastSeenAt changes | ⬜ | |

**Workflow D pass threshold:** ≥80% of active LAN devices detected.

| Workflow D overall | Status | Notes |
|--------------------|--------|-------|
| | ⬜ | |

---

## E — Device intelligence

**MVP question:** Which devices are unknown or unclassified?

### E1 — Identity fields

```bash
curl -s "http://127.0.0.1:8080/api/v1/devices?unknown=true" | python3 -m json.tool
curl -s "http://127.0.0.1:8080/api/v1/devices" | python3 -c "
import sys,json
for d in json.load(sys.stdin)['data'][:8]:
    print(d.get('displayName'), '|', d.get('deviceType'), '|', d.get('confidence'))
"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| displayName present | Not all blank | ⬜ | |
| confidence shown | LIKELY / POSSIBLE / UNKNOWN / CONFIRMED | ⬜ | |
| Unknown devices visible | `?unknown=true` returns low-confidence rows | ⬜ | |

### E2 — User label overrides rules

```bash
export DEVICE_ID="<uuid-from-devices-list>"
curl -s -X POST "http://127.0.0.1:8080/api/v1/devices/${DEVICE_ID}/label" \
  -H "Content-Type: application/json" \
  -d '{"label":"Validation Test Device","deviceType":"OTHER"}' | python3 -m json.tool
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Label persisted | displayName = your label | ⬜ | |
| confidence = CONFIRMED | After label | ⬜ | |

**Workflow E pass threshold:** ≥50% of home devices get probable classification; unknowns still shown.

| Workflow E overall | Status | Notes |
|--------------------|--------|-------|
| | ⬜ | |

---

## F — System health dashboard data

**MVP question:** Why is my laptop slow?

### F1 — Health snapshot vs system

```bash
curl -s http://127.0.0.1:8080/api/v1/system/health | python3 -m json.tool
```

Compare mentally with **Activity Monitor → CPU / Memory / Disk**:

| Field | Pass if plausible | Status | Evidence |
|-------|-------------------|--------|----------|
| cpuPct | Roughly matches system load | ⬜ | |
| memoryPct | Roughly matches Memory pressure | ⬜ | |
| diskUsagePct | Roughly matches disk used % | ⬜ | |
| batteryPct | Matches menu bar (laptop) | ⬜ | |

### F2 — Health history (trends)

```bash
curl -s "http://127.0.0.1:8080/api/v1/system/snapshots?minutes=30" | python3 -c "import sys,json; print('points:', len(json.load(sys.stdin)['data']))"
```

| Check | Pass if | Status | Evidence |
|-------|---------|--------|----------|
| Multiple points | ≥3 after 30+ min runtime (or ≥1 if just started) | ⬜ | |

### F3 — WebSocket live updates

Connect to `ws://127.0.0.1:8080/ws/live` (Postman or browser console).

| Event | Pass if received | Status | Evidence |
|-------|------------------|--------|----------|
| `connected` | On connect | ⬜ | |
| `system.health.updated` | Every ~10s | ⬜ | |
| `connection.updated` | After B2 traffic | ⬜ | |

| Workflow F overall | Status | Notes |
|--------------------|--------|-------|
| | ⬜ | |

---

## G — Known limitations (document before Phase 8)

| # | Limitation | Impact | Workaround |
|---|------------|--------|------------|
| G1 | API reads DB snapshots, not live `ps`/`lsof` | 3–10s delay | Wait for poll interval |
| G2 | HTTPS content not inspected | Domains from DNS/SNI metadata only | Expected for MVP |
| G3 | LAN scan ~60s interval | New devices slow to appear | Wait 60s after join |
| G4 | Insights list may be empty | Rules engine not fully built | Phase 9 |
| G5 | macOS collectors only in private workspace | Linux/Windows not validated | Phase 10+ |
| G6 | Connection count vs `lsof` may differ | Different counting rules | Use trends, not exact match |

Add any failures from sections A–F:

| Issue found | Section | Severity | Fix planned? |
|-------------|---------|----------|--------------|
| | | | |

---

## Phase 7 sign-off

| Workflow | Description | Status |
|----------|-------------|--------|
| A | Process observability | ⬜ |
| B | Network observability | ⬜ |
| C | Domain visibility | ⬜ |
| D | LAN discovery | ⬜ |
| E | Device intelligence | ⬜ |
| F | System health | ⬜ |
| G | Limitations documented | ⬜ |

**Phase 7 complete when:** All workflows A–F are `✅` or `⏭️` with documented reason, and G is filled.

**Validated by:** _______________  
**Date:** _______________

---

## Automated validation script

Run cross-checks without manual browsing:

```bash
./scripts/validate-phase7.sh
```

See script output for pass/fail per automated check.
