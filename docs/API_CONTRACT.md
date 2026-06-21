# API_CONTRACT.md

# JTracer API Contract

## 1. Purpose

This document defines the approved API contract for JTracer.

The API layer exists to expose normalized observability data to the frontend.

The frontend must never directly execute system commands or understand platform-specific collector implementations.

The API contract is designed around user investigation workflows rather than database table structure.

The goal is simple.

A user must be able to investigate:

* Why the laptop is slow
* Which process is consuming resources
* Which process is making network connections
* Which domains applications are talking to
* Which devices are connected to the network
* Which devices are unknown
* What the system recommends investigating

---

# 2. API Design Rules

All APIs must follow these rules.

### Rule 1

Frontend never calls collectors directly.

Forbidden:

```text
Frontend → execute ps
Frontend → execute lsof
Frontend → call scanner directly
```

Allowed:

```text
Frontend → Backend API → Normalized data
```

---

### Rule 2

Frontend must not know platform details.

Frontend must not know:

* macOS
* Windows
* Linux
* ps
* lsof
* PowerShell
* arp
* ping

The backend normalizes platform-specific data.

---

### Rule 3

APIs must return live data only.

Live APIs must never return:

* Demo data
* Seed data
* Simulated data

If data collection fails:

Return failure clearly.

Do not return fake fallback data.

---

### Rule 4

APIs must be investigation-oriented.

The API must help answer user questions.

Not simply expose database tables.

---

# 3. API Versioning

Current version:

```text
/api/v1
```

All endpoints must begin with:

```text
/api/v1
```

Example:

```text
/api/v1/processes
```

Future versions:

```text
/api/v2
```

---

# 4. Standard Response Format

Success response

```json
{
  "success": true,
  "timestamp": "2026-06-11T18:30:00Z",
  "data": {}
}
```

Error response

```json
{
  "success": false,
  "timestamp": "2026-06-11T18:30:00Z",
  "error": {
    "code": "COLLECTOR_FAILED",
    "message": "Unable to collect process data"
  }
}
```

Rules:

* Always return success flag
* Always return timestamp
* Never hide collector failures

---

# 5. System Health APIs

Purpose:

Provide machine-level health summary.

---

## Get Current System Health

```text
GET /api/v1/system/health
```

Purpose:

Show current machine health.

Response

```json
{
  "success": true,
  "data": {
    "cpuPct": 28.5,
    "memoryPct": 71.2,
    "usedMemoryMb": 11234,
    "totalMemoryMb": 16384,
    "diskUsagePct": 61.4,
    "batteryPct": 82,
    "batteryCharging": true,
    "activeProcessCount": 221,
    "activeConnectionCount": 43,
    "onlineLanDeviceCount": 9
  }
}
```

Frontend usage:

Dashboard summary cards.

---

## Get Historical System Health

```text
GET /api/v1/system/snapshots
```

Optional query params

```text
?minutes=60
?hours=24
```

Purpose:

Provide system trend graphs.

Response

```json
{
  "success": true,
  "data": [
    {
      "timestamp": "...",
      "cpuPct": 25.4,
      "memoryPct": 67.1
    }
  ]
}
```

---

# 6. Process APIs

Purpose:

Investigate running processes.

---

## Get Active Processes

```text
GET /api/v1/processes
```

Optional filters

```text
?sort=cpu
?sort=memory
?limit=20
?search=chrome
```

Response

```json
{
  "success": true,
  "data": [
    {
      "processId": "abc123",
      "pid": 2245,
      "processName": "Google Chrome",
      "cpuPct": 14.2,
      "memoryPct": 22.3,
      "rssMb": 1820,
      "connectionCount": 19,
      "status": "RUNNING"
    }
  ]
}
```

Purpose:

Dashboard process list.

---

## Get Process Details

```text
GET /api/v1/processes/{processId}
```

Response

```json
{
  "success": true,
  "data": {
    "processId": "abc123",
    "pid": 2245,
    "processName": "Google Chrome",
    "commandLine": "...",
    "firstSeenAt": "...",
    "lastSeenAt": "...",
    "status": "RUNNING"
  }
}
```

Purpose:

Detailed process page.

---

## Get Process Metric History

```text
GET /api/v1/processes/{processId}/metrics
```

Optional params

```text
?minutes=30
```

Response

```json
{
  "success": true,
  "data": [
    {
      "timestamp": "...",
      "cpuPct": 14.5,
      "memoryPct": 22.1,
      "rssMb": 1800
    }
  ]
}
```

Purpose:

CPU and memory trend graphs.

---

# 7. Network Connection APIs

Purpose:

Show outbound process network activity.

---

## Get Active Connections

```text
GET /api/v1/connections
```

Optional filters

```text
?protocol=tcp
?processId=abc123
?sort=recent
```

Response

```json
{
  "success": true,
  "data": [
    {
      "connectionId": "conn123",
      "processId": "abc123",
      "processName": "Google Chrome",
      "protocol": "TCP",
      "localIp": "192.168.1.22",
      "localPort": 55123,
      "remoteIp": "140.82.112.3",
      "remotePort": 443,
      "state": "ACTIVE",
      "domain": "github.com"
    }
  ]
}
```

Purpose:

Connection investigation.

---

## Get Connections For Specific Process

```text
GET /api/v1/processes/{processId}/connections
```

Response

```json
{
  "success": true,
  "data": [
    {
      "remoteIp": "140.82.112.3",
      "domain": "github.com",
      "port": 443,
      "protocol": "TCP"
    }
  ]
}
```

Purpose:

Process-level network investigation.

---

# 8. Domain APIs

Purpose:

Show external domains contacted by applications.

---

## Get Domains

```text
GET /api/v1/domains
```

Optional params

```text
?sort=frequency
?processId=abc123
```

Response

```json
{
  "success": true,
  "data": [
    {
      "domain": "github.com",
      "connectionCount": 24,
      "processCount": 3,
      "lastSeenAt": "..."
    }
  ]
}
```

Purpose:

Find frequently contacted domains.

---

## Domain Investigation

```text
GET /api/v1/domains/{domain}
```

Response

```json
{
  "success": true,
  "data": {
    "domain": "github.com",
    "connectedProcesses": [
      "Chrome",
      "Cursor"
    ],
    "totalConnections": 31
  }
}
```

Purpose:

Domain investigation.

---

# 9. LAN Device APIs

Purpose:

Show devices connected to home network.

---

## Get Devices

```text
GET /api/v1/devices
```

Optional filters

```text
?status=online
?type=phone
?unknown=true
```

Response

```json
{
  "success": true,
  "data": [
    {
      "deviceId": "dev123",
      "ipAddress": "192.168.1.50",
      "macAddress": "A4:B1:C2:44:55:66",
      "vendor": "Apple",
      "displayName": "Apple iPhone",
      "deviceType": "PHONE",
      "confidence": "LIKELY",
      "status": "ONLINE"
    }
  ]
}
```

Purpose:

Device dashboard.

---

## Get Device Details

```text
GET /api/v1/devices/{deviceId}
```

Response

```json
{
  "success": true,
  "data": {
    "deviceId": "dev123",
    "hostname": "iphone-bedroom",
    "vendor": "Apple",
    "deviceType": "PHONE",
    "firstSeenAt": "...",
    "lastSeenAt": "...",
    "status": "ONLINE"
  }
}
```

Purpose:

Detailed device investigation.

---

## Label Device

```text
POST /api/v1/devices/{deviceId}/label
```

Request

```json
{
  "label": "Living Room Apple TV"
}
```

Response

```json
{
  "success": true
}
```

Purpose:

User labels override guessed identity.

---

# 10. Network Scan APIs

Purpose:

LAN scanner status.

---

## Current Scan Status

```text
GET /api/v1/network-scans/current
```

Response

```json
{
  "success": true,
  "data": {
    "status": "RUNNING",
    "subnet": "192.168.1.0/24",
    "scannedHosts": 122,
    "onlineDevices": 11
  }
}
```

Purpose:

Show scan progress.

---

## Scan History

```text
GET /api/v1/network-scans/history
```

Purpose:

Historical LAN scans.

---

# 11. Insight APIs

Purpose:

Provide explainable recommendations.

---

## Get Active Insights

```text
GET /api/v1/insights
```

Response

```json
{
  "success": true,
  "data": [
    {
      "insightId": "ins001",
      "severity": "WARNING",
      "title": "Chrome high memory usage",
      "explanation": "Chrome memory usage stayed above 2 GB for 5 minutes"
    }
  ]
}
```

Purpose:

Dashboard recommendations.

---

## Dismiss Insight

```text
POST /api/v1/insights/{insightId}/dismiss
```

Purpose:

User dismisses suggestion.

---

# 12. Session APIs

Purpose:

Track current JTracer runtime session.

---

## Current Session

```text
GET /api/v1/session/current
```

Response

```json
{
  "success": true,
  "data": {
    "sessionId": "session123",
    "osType": "MACOS",
    "startedAt": "...",
    "captureMode": "LIVE"
  }
}
```

Purpose:

Show runtime metadata.

---

# 13. Health APIs

Purpose:

Collector health monitoring.

---

## Collector Health

```text
GET /api/v1/health
```

Response

```json
{
  "success": true,
  "data": {
    "processCollector": "RUNNING",
    "networkCollector": "RUNNING",
    "lanScanner": "RUNNING",
    "database": "CONNECTED",
    "captureMode": "LIVE"
  }
}
```

Purpose:

Troubleshooting.

---

# 14. WebSocket Events

Purpose:

Live UI updates.

WebSocket endpoint

```text
/ws/live
```

Supported events

### Process Updates

```text
process.updated
```

Payload

```json
{
  "pid": 2245,
  "cpuPct": 22.1
}
```

---

### Connection Updates

```text
connection.updated
```

Payload

```json
{
  "processName": "Chrome",
  "remoteIp": "140.82.112.3"
}
```

---

### Device Updates

```text
device.updated
```

Payload

```json
{
  "deviceId": "abc123",
  "status": "ONLINE"
}
```

---

### Insight Updates

```text
insight.created
```

Payload

```json
{
  "title": "Unknown device detected"
}
```

---

# 15. Forbidden APIs

Do not build these APIs in MVP.

Forbidden

```text
/api/packets
/api/http-body
/api/request-body
/api/response-body
/api/cookies
/api/passwords
/api/security-scan
/api/malware-check
/api/firewall-modify
```

Reason:

Out of scope.

---

# 16. First UI Pages Supported By APIs

Page 1

```text
Dashboard
```

Uses

```text
system/health
processes
connections
devices
insights
```

Page 2

```text
Process Detail
```

Uses

```text
processes/{id}
processes/{id}/metrics
processes/{id}/connections
```

Page 3

```text
Device Detail
```

Uses

```text
devices/{id}
```

Page 4

```text
Domain Investigation
```

Uses

```text
domains
domains/{domain}
```

---

# 17. Final Rule

APIs exist to answer investigation questions.

Not to expose database tables.

Every API must help answer:

* Why is my laptop slow?
* Which process is consuming resources?
* Which process is making network requests?
* Which domains are applications talking to?
* Which devices are connected to the network?
* Which devices are unknown?
* What should the user investigate?
