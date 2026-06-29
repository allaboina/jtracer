# Phase 7 Validation — Run Log

Record validation runs here. Update status in [PHASE7_VALIDATION.md](./PHASE7_VALIDATION.md) as you complete manual steps.

---

## Run 1 — 2026-06-29 (automated + spot checks)

**Environment:** macOS, backend `mvn spring-boot:run` from `backend/`

### Automated (`./scripts/validate-phase7.sh`)

| Check | Result | Notes |
|-------|--------|-------|
| P1 Health HTTP 200 | ✅ | |
| All GET endpoints 200 | ❌ | `/api/v1/insights` returned 500 |
| A1 Process count vs ps | ⏭️ | DB path: use `backend/data/jtracer-live.db` |
| B1–B2 Connections | ✅ | 132 active; list non-empty after traffic |
| C1 Domains | ✅ | 20 rows |
| D1 Devices | ✅ | 11 rows |
| F2 Health snapshots | ✅ | 4 points |

**Root cause (insights 500):** `insights` table missing from Flyway migrations.  
**Fix:** Added `V5__insights_schema.sql`. Restart backend to apply.

### A2 — Top CPU overlap (spot check)

| Source | Top processes |
|--------|----------------|
| API | WindowServer 45%, Cursor Helper 35%, Chrome Helper 30% |
| `ps` | java 174%, WindowServer 44%, Chrome Helper 31%, Cursor 16% |

**Result:** ✅ Top names overlap (WindowServer, Chrome, Cursor). Java spike from `mvn spring-boot:run` itself.

### Manual steps still needed (your schedule)

| Step | Section | Action |
|------|---------|--------|
| A3 | Process | Browse in Chrome; search `?search=chrome` |
| A5 | Process | CPU stress test (`yes > /dev/null`) |
| C1 manual | Domain | Browse 3 sites; verify domains list |
| D2 | LAN | Connect phone to WiFi; wait 60s |
| D3 | LAN | Disconnect device; wait 60s |
| E2 | Identity | Label a device via POST |
| F3 | WebSocket | Connect to `ws://127.0.0.1:8080/ws/live` |

---

## Run 2 — 2026-06-29 (after V5 insights migration)

**Fix applied:** `V5__insights_schema.sql` — `insights` table created via Flyway.

### Automated (`./scripts/validate-phase7.sh`)

| Check | Result |
|-------|--------|
| All endpoints HTTP 200 | ✅ |
| A1 Process count API=562 vs ps=565 | ✅ within 15% |
| A2 Top CPU metric | ✅ (after script fix: skip processes without samples) |
| B1–B2 Network | ✅ 131 connections |
| C1 Domains | ✅ 21 rows |
| D1 Devices | ✅ 11 rows |
| F2 Snapshots | ✅ 12 points |
| DB rows | ✅ 3805 processes, 2780 connections |

**Automated gate:** ✅ PASS (14/14 after script fix)

---

## Sign-off tracker

| Workflow | Run 1 | Run 2 | Final |
|----------|-------|-------|-------|
| A Process | Partial | | ⬜ |
| B Network | ✅ auto | | ⬜ |
| C Domain | Partial | | ⬜ |
| D LAN | Partial (11 devices) | | ⬜ |
| E Identity | Not run | | ⬜ |
| F Health | ✅ auto | | ⬜ |
| G Limitations | Documented in runbook | | ⬜ |
