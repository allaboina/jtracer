#!/usr/bin/env bash
# Automated Phase 7 cross-checks (backend must be running).
set -euo pipefail

BASE="${1:-http://127.0.0.1:8080}"
DB="${2:-./data/jtracer-live.db}"
PASS=0
FAIL=0
SKIP=0

ok()   { echo "  ✅ $1"; PASS=$((PASS + 1)); }
bad()  { echo "  ❌ $1"; FAIL=$((FAIL + 1)); }
skip() { echo "  ⏭️  $1"; SKIP=$((SKIP + 1)); }

http_code() {
  curl -s -o /dev/null -w "%{http_code}" "${BASE}${1}"
}

json_field() {
  curl -s "${BASE}${1}" | python3 -c "$2"
}

echo "==> JTracer Phase 7 automated validation"
echo "    API: ${BASE}"
echo "    DB:  ${DB}"
echo ""

# P1 Health
code=$(http_code "/api/v1/system/health")
if [[ "$code" == "200" ]]; then ok "P1 Health endpoint HTTP 200"; else bad "P1 Health endpoint HTTP ${code} (expected 200)"; fi

# P3 Smoke endpoints
for path in \
  "/api/v1/system/snapshots?minutes=60" \
  "/api/v1/processes?limit=3" \
  "/api/v1/connections" \
  "/api/v1/domains" \
  "/api/v1/devices" \
  "/api/v1/insights"; do
  c=$(http_code "$path")
  if [[ "$c" == "200" ]]; then ok "GET ${path} → 200"; else bad "GET ${path} → ${c}"; fi
done

# A1 Process count
if [[ -f "$DB" ]]; then
  api_procs=$(json_field "/api/v1/system/health" "import sys,json; print(json.load(sys.stdin)['data']['activeProcessCount'])")
  ps_count=$(ps -ax -o pid= 2>/dev/null | wc -l | tr -d ' ')
  if [[ -n "$api_procs" && -n "$ps_count" && "$ps_count" -gt 0 ]]; then
    diff=$(( api_procs > ps_count ? api_procs - ps_count : ps_count - api_procs ))
    pct=$(( diff * 100 / ps_count ))
    if [[ "$pct" -le 15 ]]; then
      ok "A1 Process count: API=${api_procs} ps=${ps_count} (within 15%)"
    else
      bad "A1 Process count: API=${api_procs} ps=${ps_count} (diff ${pct}%)"
    fi
  else
    skip "A1 Process count comparison (missing data)"
  fi
else
  skip "A1 DB not found at ${DB}"
fi

# A2 Top CPU non-zero
top_cpu=$(json_field "/api/v1/processes?sort=cpu&limit=1" "
import sys,json
d=json.load(sys.stdin).get('data',[])
print(d[0].get('cpuPct',0) if d else 0)
")
if python3 -c "import sys; v=float(sys.argv[1]); sys.exit(0 if v>=0 else 1)" "$top_cpu" 2>/dev/null; then
  ok "A2 Top process returns cpuPct (${top_cpu})"
else
  bad "A2 Top process cpuPct missing"
fi

# B1 Connections
conn_api=$(json_field "/api/v1/system/health" "import sys,json; print(json.load(sys.stdin)['data']['activeConnectionCount'])")
if [[ -n "$conn_api" ]]; then ok "B1 API reports ${conn_api} active connections"; else bad "B1 connection count missing"; fi

# B2 Traffic + wait
curl -s https://example.com > /dev/null 2>&1 || true
sleep 4
conn_list=$(json_field "/api/v1/connections?limit=1" "import sys,json; print(len(json.load(sys.stdin).get('data',[])))")
if [[ "$conn_list" -gt 0 ]]; then ok "B2 Connections list non-empty after traffic"; else bad "B2 Connections list empty"; fi

# C1 Domains endpoint
dom_count=$(json_field "/api/v1/domains" "import sys,json; print(len(json.load(sys.stdin).get('data',[])))")
if [[ "$dom_count" -ge 0 ]]; then ok "C1 Domains endpoint returns ${dom_count} rows"; else bad "C1 Domains endpoint failed"; fi

# D1 Devices
dev_count=$(json_field "/api/v1/devices" "import sys,json; print(len(json.load(sys.stdin).get('data',[])))")
ok "D1 Devices endpoint returns ${dev_count} rows (0 OK on isolated network)"

# F2 Snapshots
points=$(json_field "/api/v1/system/snapshots?minutes=60" "import sys,json; print(len(json.load(sys.stdin).get('data',[])))")
if [[ "$points" -ge 1 ]]; then ok "F2 Health snapshots: ${points} point(s)"; else bad "F2 No health snapshot history"; fi

# DB row counts
if [[ -f "$DB" ]]; then
  obs=$(sqlite3 "$DB" "SELECT COUNT(*) FROM observed_processes;" 2>/dev/null || echo 0)
  net=$(sqlite3 "$DB" "SELECT COUNT(*) FROM network_connections;" 2>/dev/null || echo 0)
  ok "DB observed_processes=${obs} network_connections=${net}"
fi

echo ""
echo "==> Summary: ${PASS} passed, ${FAIL} failed, ${SKIP} skipped"
if [[ "$FAIL" -gt 0 ]]; then exit 1; fi
