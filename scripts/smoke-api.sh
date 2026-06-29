#!/usr/bin/env bash
# Quick smoke test for JTracer REST APIs (backend must be running).
set -euo pipefail

BASE="${1:-http://127.0.0.1:8080}"

check() {
  local path="$1"
  local code
  code=$(curl -s -o /dev/null -w "%{http_code}" "${BASE}${path}")
  echo "GET ${path} → HTTP ${code}"
}

echo "JTracer API smoke test → ${BASE}"
echo ""

check "/api/v1/system/health"
check "/api/v1/system/snapshots?minutes=60"
check "/api/v1/processes?limit=3"
check "/api/v1/connections"
check "/api/v1/domains"
check "/api/v1/devices"
check "/api/v1/insights"

echo ""
echo "Done. Expect HTTP 200 (health may be 503 in first 10s after startup)."
