# ENTITY_DESIGN.md

# JTracer Entity Design

## 1. Purpose

This document defines the core domain entities for JTracer.

JTracer is a local personal observability tool that connects:

* Laptop performance
* Running processes
* Outbound network connections
* Home network devices
* Device identity
* Basic insights and alerts

The goal of this entity design is to prevent the application from becoming only a generic dashboard. Every feature must map back to these entities and relationships.

---

## 2. Core Domain Model

JTracer should be designed around these main entities:

1. ObservationSession
2. HostMachine
3. ObservedProcess
4. ProcessMetricSample
5. NetworkConnection
6. RemoteEndpoint
7. DomainIdentity
8. LanDevice
9. DeviceIdentity
10. DeviceIdentityRule
11. NetworkScanSession
12. Insight
13. Alert
14. UserDeviceLabel
15. SystemHealthSnapshot

---

## 3. Entity Relationship Overview

```text
HostMachine
  └── ObservationSession
        ├── ObservedProcess
        │     ├── ProcessMetricSample
        │     └── NetworkConnection
        │             ├── RemoteEndpoint
        │             └── DomainIdentity
        │
        ├── NetworkScanSession
        │     └── LanDevice
        │             ├── DeviceIdentity
        │             └── UserDeviceLabel
        │
        ├── SystemHealthSnapshot
        ├── Insight
        └── Alert
```

The most important correlation path is:

```text
Laptop slow
  → ObservedProcess
  → ProcessMetricSample
  → NetworkConnection
  → RemoteEndpoint or LanDevice
  → Insight or Alert
```

---

## 4. Entity: HostMachine

Represents the computer where JTracer is running.

### Purpose

Used to support future cross-platform operation across macOS, Windows, and Linux.

### Fields

| Field        | Type        | Notes                       |
| ------------ | ----------- | --------------------------- |
| id           | UUID/String | Stable generated machine id |
| hostname     | String      | Computer hostname           |
| osType       | Enum        | MACOS, WINDOWS, LINUX       |
| osVersion    | String      | Operating system version    |
| architecture | String      | arm64, x86_64               |
| primaryUser  | String      | Optional local username     |
| createdAt    | Timestamp   | First registered            |
| lastSeenAt   | Timestamp   | Last JTracer run            |

### Notes

Do not hardcode macOS concepts into this entity.

---

## 5. Entity: ObservationSession

Represents one JTracer runtime session.

### Purpose

Groups process samples, connection samples, device scans, health snapshots, insights, and alerts captured during one run.

### Fields

| Field           | Type        | Notes                                      |
| --------------- | ----------- | ------------------------------------------ |
| id              | UUID/String | Session id                                 |
| hostMachineId   | FK          | Links to HostMachine                       |
| startedAt       | Timestamp   | Session start                              |
| endedAt         | Timestamp   | Nullable                                   |
| captureMode     | Enum        | LIVE, DEMO, SIMULATOR                      |
| platformAdapter | String      | macos-lsof, windows-powershell, linux-proc |
| appVersion      | String      | JTracer version                            |
| status          | Enum        | RUNNING, STOPPED, FAILED                   |
| failureReason   | String      | Nullable                                   |

### Rules

* Live sessions must not mix with seed or demo data.
* Demo sessions must be clearly marked as DEMO.
* Simulator sessions must be clearly marked as SIMULATOR.

---

## 6. Entity: ObservedProcess

Represents an application or operating system process observed by JTracer.

### Purpose

This is the central entity for answering:

* Why is my laptop slow?
* Which process is using high CPU?
* Which process is making too many network connections?

### Fields

| Field          | Type        | Notes                       |
| -------------- | ----------- | --------------------------- |
| id             | UUID/String | Internal id                 |
| sessionId      | FK          | ObservationSession          |
| pid            | Integer     | OS process id               |
| parentPid      | Integer     | Optional                    |
| processName    | String      | Chrome, Cursor, node, Slack |
| executablePath | String      | Best effort                 |
| commandLine    | String      | Optional, may be long       |
| appName        | String      | Friendly app name if known  |
| appBundleId    | String      | macOS bundle id, nullable   |
| userName       | String      | Process owner               |
| firstSeenAt    | Timestamp   | First observed              |
| lastSeenAt     | Timestamp   | Last observed               |
| status         | Enum        | RUNNING, EXITED, UNKNOWN    |

### Derived Fields

| Field                   | Meaning                          |
| ----------------------- | -------------------------------- |
| latestCpuPct            | From latest ProcessMetricSample  |
| latestMemoryPct         | From latest ProcessMetricSample  |
| latestRssMb             | From latest ProcessMetricSample  |
| connectionCount         | Count of NetworkConnection rows  |
| externalConnectionCount | Connections to public IPs        |
| lanConnectionCount      | Connections to local network IPs |

### Rules

* PID alone is not globally unique because PIDs are reused.
* Use sessionId + pid + firstSeenAt as stable identity logic.
* Process names must not be blindly trusted for security decisions.

---

## 7. Entity: ProcessMetricSample

Represents one resource usage snapshot for a process.

### Purpose

Supports CPU, memory, disk, battery, and future time-series graphs.

### Fields

| Field              | Type        | Notes                |
| ------------------ | ----------- | -------------------- |
| id                 | UUID/String | Internal id          |
| observedProcessId  | FK          | ObservedProcess      |
| sessionId          | FK          | ObservationSession   |
| sampledAt          | Timestamp   | Sample time          |
| cpuPct             | Decimal     | CPU usage            |
| memoryPct          | Decimal     | Memory usage         |
| rssMb              | Decimal     | Resident memory      |
| threadCount        | Integer     | Optional             |
| openFileCount      | Integer     | Optional             |
| diskReadBytes      | Long        | Future               |
| diskWriteBytes     | Long        | Future               |
| networkBytesIn     | Long        | Future               |
| networkBytesOut    | Long        | Future               |
| batteryImpactScore | Decimal     | Future derived score |

### Rules

* This is time-series data.
* Do not overwrite old samples.
* Store enough history to show trends.

---

## 8. Entity: NetworkConnection

Represents one outbound network connection observed from the local machine.

### Purpose

Supports developer debugging and network observability.

### Fields

| Field             | Type        | Notes                              |
| ----------------- | ----------- | ---------------------------------- |
| id                | UUID/String | Internal id                        |
| sessionId         | FK          | ObservationSession                 |
| observedProcessId | FK          | Process that owns connection       |
| protocol          | Enum        | TCP, UDP                           |
| localIp           | String      | Local address                      |
| localPort         | Integer     | Local port                         |
| remoteIp          | String      | Remote address                     |
| remotePort        | Integer     | Remote port                        |
| remoteEndpointId  | FK          | RemoteEndpoint                     |
| domainIdentityId  | FK          | Nullable                           |
| firstSeenAt       | Timestamp   | First observed                     |
| lastSeenAt        | Timestamp   | Last observed                      |
| durationMs        | Long        | Derived                            |
| state             | Enum        | ACTIVE, CLOSED, LISTENING, UNKNOWN |
| direction         | Enum        | OUTBOUND, INBOUND, LOCAL           |
| confidence        | Enum        | HIGH, MEDIUM, LOW                  |
| sourceAdapter     | String      | lsof, netstat, powershell, etc.    |

### Rules

* First prototype should capture Level 1 metadata only.
* No request body, response body, cookies, passwords, or HTTPS decrypted payload.
* Closed connections are valid and expected because many connections are short-lived.
* The connection must always be traceable back to a process when OS data allows it.

---

## 9. Entity: RemoteEndpoint

Represents an external or local destination contacted by a process.

### Purpose

Avoids repeating endpoint details across many connections.

### Fields

| Field            | Type        | Notes                                                     |
| ---------------- | ----------- | --------------------------------------------------------- |
| id               | UUID/String | Internal id                                               |
| ipAddress        | String      | Remote IP                                                 |
| port             | Integer     | Remote port                                               |
| endpointType     | Enum        | PUBLIC_INTERNET, LAN_DEVICE, LOOPBACK, MULTICAST, UNKNOWN |
| asn              | String      | Future                                                    |
| organization     | String      | Future                                                    |
| countryCode      | String      | Future                                                    |
| reputationStatus | Enum        | UNKNOWN, TRUSTED, SUSPICIOUS, BLOCKED                     |
| firstSeenAt      | Timestamp   | First observed                                            |
| lastSeenAt       | Timestamp   | Last observed                                             |

### Rules

* IP alone may not identify the real application service.
* Public IP enrichment is future scope.
* LAN IPs should be correlated with LanDevice when possible.

---

## 10. Entity: DomainIdentity

Represents a domain or hostname associated with an IP or connection.

### Purpose

Supports the question:

Which website or domain is this app talking to?

### Fields

| Field       | Type        | Notes                                                 |
| ----------- | ----------- | ----------------------------------------------------- |
| id          | UUID/String | Internal id                                           |
| hostname    | String      | api.github.com                                        |
| rootDomain  | String      | github.com                                            |
| source      | Enum        | REVERSE_DNS, MDNS, HOSTNAME, USER_LABEL, FUTURE_PROXY |
| confidence  | Enum        | HIGH, MEDIUM, LOW                                     |
| firstSeenAt | Timestamp   | First observed                                        |
| lastSeenAt  | Timestamp   | Last observed                                         |

### Rules

* Reverse DNS can be inaccurate.
* Domain confidence must be shown internally.
* Do not present uncertain domain identity as guaranteed truth.

---

## 11. Entity: LanDevice

Represents a device discovered on the local network.

### Purpose

Supports home network monitoring and device intelligence.

### Fields

| Field            | Type        | Notes                                                                                     |
| ---------------- | ----------- | ----------------------------------------------------------------------------------------- |
| id               | UUID/String | Prefer MAC-derived id when available                                                      |
| scanSessionId    | FK          | NetworkScanSession                                                                        |
| ipAddress        | String      | Current IP                                                                                |
| macAddress       | String      | Nullable                                                                                  |
| hostname         | String      | Nullable                                                                                  |
| mdnsName         | String      | Nullable                                                                                  |
| vendor           | String      | From OUI lookup when MAC is available                                                     |
| deviceIdentityId | FK          | Optional                                                                                  |
| deviceType       | Enum        | PHONE, LAPTOP, TV, STREAMING_DEVICE, SMART_SPEAKER, ROUTER, PRINTER, CAMERA, IOT, UNKNOWN |
| status           | Enum        | NEW, ONLINE, OFFLINE, UNKNOWN                                                             |
| firstSeenAt      | Timestamp   | First discovered                                                                          |
| lastSeenAt       | Timestamp   | Last seen                                                                                 |
| onlineSinceAt    | Timestamp   | Nullable                                                                                  |
| lastIpChangeAt   | Timestamp   | Nullable                                                                                  |
| confidence       | Enum        | CONFIRMED, LIKELY, UNKNOWN                                                                |
| sourceAdapter    | String      | ping, arp, mdns, netbios                                                                  |

### Rules

* MAC address is preferred identity.
* If MAC is missing, fallback to ipAddress with lower confidence.
* Vendor does not equal device model.
* Unknown device is valid and should not be hidden.
* User labels should override guessed names.

---

## 12. Entity: DeviceIdentity

Represents the best-known identity of a LAN device.

### Purpose

Turns raw network device data into a user-friendly label.

Example:

Raw:

```text
vendor = Amazon Technologies
hostname = echo-bedroom.local
```

Identity:

```text
Amazon Echo - likely smart speaker
```

### Fields

| Field          | Type        | Notes                                                         |
| -------------- | ----------- | ------------------------------------------------------------- |
| id             | UUID/String | Internal id                                                   |
| displayName    | String      | Apple iPhone, Amazon Echo, Roku                               |
| deviceType     | Enum        | PHONE, TV, STREAMING_DEVICE, SMART_SPEAKER, LAPTOP, UNKNOWN   |
| manufacturer   | String      | Apple, Amazon, Roku                                           |
| model          | String      | Nullable                                                      |
| confidence     | Enum        | CONFIRMED, LIKELY, UNKNOWN                                    |
| identitySource | Enum        | USER_LABEL, HOSTNAME_RULE, OUI_RULE, MDNS_RULE, COMBINED_RULE |
| evidence       | String      | Explanation of why this identity was chosen                   |
| createdAt      | Timestamp   | Created                                                       |
| updatedAt      | Timestamp   | Updated                                                       |

### Rules

* DeviceIdentity must be explainable.
* Never claim exact model unless evidence supports it.
* User-provided labels have highest priority.

---

## 13. Entity: DeviceIdentityRule

Represents a local rule used to classify a LAN device.

### Purpose

Supports the device intelligence knowledge base.

### Example Rules

```json
{
  "vendorContains": "Amazon",
  "hostnameContains": ["echo", "alexa"],
  "deviceType": "SMART_SPEAKER",
  "displayName": "Amazon Echo",
  "confidence": "LIKELY"
}
```

```json
{
  "vendorContains": "Roku",
  "deviceType": "STREAMING_DEVICE",
  "displayName": "Roku Device",
  "confidence": "LIKELY"
}
```

### Fields

| Field            | Type        | Notes                      |
| ---------------- | ----------- | -------------------------- |
| id               | UUID/String | Rule id                    |
| ruleName         | String      | Human readable             |
| vendorContains   | List/String | Optional                   |
| hostnameContains | List/String | Optional                   |
| macOuiPrefix     | String      | Optional                   |
| mdnsContains     | List/String | Optional                   |
| deviceType       | Enum        | Output type                |
| displayName      | String      | Output display name        |
| confidence       | Enum        | CONFIRMED, LIKELY, UNKNOWN |
| priority         | Integer     | Higher priority wins       |
| enabled          | Boolean     | Can disable bad rules      |
| createdAt        | Timestamp   | Created                    |
| updatedAt        | Timestamp   | Updated                    |

### Rules

* Rules should be stored in bundled JSON or YAML initially.
* User-defined rules may be added later.
* Rules must be tested with sample devices.

---

## 14. Entity: NetworkScanSession

Represents one LAN scan execution.

### Purpose

Groups device discovery results.

### Fields

| Field                | Type        | Notes                      |
| -------------------- | ----------- | -------------------------- |
| id                   | UUID/String | Scan id                    |
| observationSessionId | FK          | Current JTracer session    |
| startedAt            | Timestamp   | Scan start                 |
| endedAt              | Timestamp   | Scan end                   |
| subnetCidr           | String      | Example: 192.168.1.0/24    |
| adapterName          | String      | WiFi, Ethernet             |
| scannedHostCount     | Integer     | Number of IPs scanned      |
| onlineDeviceCount    | Integer     | Found online               |
| unknownDeviceCount   | Integer     | Unknown identity           |
| status               | Enum        | RUNNING, COMPLETED, FAILED |
| failureReason        | String      | Nullable                   |

### Rules

* Scanner data is separate from outbound connection capture.
* Scanner must not directly update process/network connection tables.
* Correlation happens through IP/domain relationship logic.

---

## 15. Entity: UserDeviceLabel

Represents a user-defined friendly name for a device.

### Purpose

Lets the user correct device identity.

Example:

```text
192.168.1.22 / A1:B2:C3:D4:E5:F6 = Living Room Fire TV
```

### Fields

| Field       | Type        | Notes               |
| ----------- | ----------- | ------------------- |
| id          | UUID/String | Internal id         |
| lanDeviceId | FK          | Device              |
| macAddress  | String      | Useful stable key   |
| label       | String      | Living Room Fire TV |
| deviceType  | Enum        | Optional            |
| createdAt   | Timestamp   | Created             |
| updatedAt   | Timestamp   | Updated             |

### Rules

* User label overrides guessed identity.
* User label should persist across IP address changes if MAC is same.

---

## 16. Entity: SystemHealthSnapshot

Represents overall machine health at a point in time.

### Purpose

Supports system summary cards and trend graphs.

### Fields

| Field                 | Type        | Notes                |
| --------------------- | ----------- | -------------------- |
| id                    | UUID/String | Internal id          |
| sessionId             | FK          | ObservationSession   |
| sampledAt             | Timestamp   | Sample time          |
| totalCpuPct           | Decimal     | Overall CPU usage    |
| totalMemoryPct        | Decimal     | Overall memory usage |
| usedMemoryMb          | Decimal     | Optional             |
| totalMemoryMb         | Decimal     | Optional             |
| diskUsagePct          | Decimal     | Disk usage           |
| batteryPct            | Decimal     | Nullable for desktop |
| batteryCharging       | Boolean     | Nullable             |
| activeProcessCount    | Integer     | Count                |
| activeConnectionCount | Integer     | Count                |
| onlineLanDeviceCount  | Integer     | Count                |

### Rules

* This entity is for dashboard-level health.
* Process-level details belong to ProcessMetricSample.

---

## 17. Entity: Insight

Represents an explainable observation generated by the system.

### Purpose

Gives the user useful summaries instead of raw tables only.

### Examples

```text
Chrome is using high memory.
Cursor made 120 outbound connections in the last 10 minutes.
An unknown device appeared on the network.
Node is using high CPU and making repeated calls to localhost.
```

### Fields

| Field        | Type        | Notes                               |
| ------------ | ----------- | ----------------------------------- |
| id           | UUID/String | Internal id                         |
| sessionId    | FK          | ObservationSession                  |
| entityType   | Enum        | PROCESS, CONNECTION, DEVICE, SYSTEM |
| entityId     | String      | Related entity id                   |
| severity     | Enum        | INFO, WARNING, CRITICAL             |
| title        | String      | Short message                       |
| explanation  | String      | Human-friendly detail               |
| evidenceJson | JSON/Text   | Supporting metrics                  |
| generatedAt  | Timestamp   | Created                             |
| ruleId       | String      | Rule that generated it              |
| status       | Enum        | ACTIVE, DISMISSED, RESOLVED         |

### Rules

* Every insight must have evidence.
* No AI-generated insight should be shown without measurable evidence.
* Rule-based insights should come before AI summaries.

---

## 18. Entity: Alert

Represents an actionable notification.

### Purpose

Alerts are stronger than insights.

### Examples

```text
Memory usage above 90%.
Unknown device joined network.
Process made unusual number of outbound connections.
Battery draining quickly due to high CPU process.
```

### Fields

| Field             | Type        | Notes                        |
| ----------------- | ----------- | ---------------------------- |
| id                | UUID/String | Internal id                  |
| insightId         | FK          | Optional                     |
| severity          | Enum        | INFO, WARNING, CRITICAL      |
| title             | String      | Short message                |
| message           | String      | Detailed message             |
| recommendedAction | String      | Suggested user action        |
| createdAt         | Timestamp   | Created                      |
| acknowledgedAt    | Timestamp   | Nullable                     |
| resolvedAt        | Timestamp   | Nullable                     |
| status            | Enum        | OPEN, ACKNOWLEDGED, RESOLVED |

### Rules

* Alerts should be few and useful.
* Do not alert for every small spike.
* First prototype may show insights only and defer alerts.

---

## 19. Core Enums

### OsType

```text
MACOS
WINDOWS
LINUX
UNKNOWN
```

### CaptureMode

```text
LIVE
DEMO
SIMULATOR
```

### ProcessStatus

```text
RUNNING
EXITED
UNKNOWN
```

### ConnectionState

```text
ACTIVE
CLOSED
LISTENING
UNKNOWN
```

### EndpointType

```text
PUBLIC_INTERNET
LAN_DEVICE
LOOPBACK
MULTICAST
UNKNOWN
```

### DeviceType

```text
PHONE
TABLET
LAPTOP
DESKTOP
TV
STREAMING_DEVICE
SMART_SPEAKER
ROUTER
PRINTER
CAMERA
SMART_HOME
GAME_CONSOLE
IOT
UNKNOWN
```

### Confidence

```text
CONFIRMED
LIKELY
LOW
UNKNOWN
```

### Severity

```text
INFO
WARNING
CRITICAL
```

---

## 20. MVP Database Tables

For the first prototype, implement only these tables.

```text
host_machines
observation_sessions
observed_processes
process_metric_samples
network_connections
remote_endpoints
domain_identities
network_scan_sessions
lan_devices
device_identities
device_identity_rules
user_device_labels
system_health_snapshots
insights
```

Defer these until later:

```text
alerts
http_events
geo_ip_reputation
process_baselines
anomaly_scores
daily_summaries
```

---

## 21. Minimum MVP API Shape

### System

```text
GET /api/system/health
GET /api/system/snapshots
```

### Processes

```text
GET /api/processes
GET /api/processes/{id}
GET /api/processes/{id}/metrics
GET /api/processes/{id}/connections
```

### Connections

```text
GET /api/connections
GET /api/connections/{id}
GET /api/endpoints
GET /api/domains
```

### LAN Devices

```text
GET /api/devices
GET /api/devices/{id}
POST /api/devices/{id}/label
GET /api/network-scans
```

### Insights

```text
GET /api/insights
POST /api/insights/{id}/dismiss
```

---

## 22. Investigation Workflows

### Workflow 1: Why is my laptop slow?

```text
SystemHealthSnapshot
  → top ObservedProcess
  → latest ProcessMetricSample
  → NetworkConnection count
  → Insight
```

### Workflow 2: Which process is making too many network requests?

```text
ObservedProcess
  → NetworkConnection
  → RemoteEndpoint
  → DomainIdentity
  → Insight
```

### Workflow 3: Which website is this app talking to?

```text
ObservedProcess
  → NetworkConnection
  → DomainIdentity
  → RemoteEndpoint
```

### Workflow 4: Which devices are connected to my home network?

```text
NetworkScanSession
  → LanDevice
  → DeviceIdentity
  → UserDeviceLabel
```

### Workflow 5: Is this connection going to a local device?

```text
NetworkConnection.remoteIp
  → match LanDevice.ipAddress
  → show DeviceIdentity
```

---

## 23. Development Guardrails

AI agents and developers must follow these guardrails.

1. Do not create UI screens before the entity model is implemented.
2. Do not mix live data with seed data.
3. Do not build security scoring before reliable process and device data exists.
4. Do not treat vendor name as exact device model.
5. Do not capture request bodies or decrypted HTTPS payloads in the MVP.
6. Do not let the frontend call scanner directly.
7. Do not merge scanner logic into process capture logic.
8. Do not hardcode macOS logic into common entities.
9. Every insight must include evidence.
10. Every entity must support future cross-platform adapters.

---

## 24. First Implementation Order

Implement in this order.

### Step 1 — Core Session and Host

* HostMachine
* ObservationSession
* SystemHealthSnapshot

### Step 2 — Process Observability

* ObservedProcess
* ProcessMetricSample

### Step 3 — Network Observability

* NetworkConnection
* RemoteEndpoint
* DomainIdentity

### Step 4 — LAN Device Intelligence

* NetworkScanSession
* LanDevice
* DeviceIdentity
* DeviceIdentityRule
* UserDeviceLabel

### Step 5 — Insight Layer

* Insight
* Basic rule engine

### Step 6 — UI

Only after the above entities and APIs are available, build the dashboard.

---

## 25. Success Criteria

The entity model is successful when JTracer can answer:

1. Which process is using the most CPU?
2. Which process is using the most memory?
3. Which process made the most network connections?
4. Which domains did a process talk to?
5. Which devices are online on the LAN?
6. Which devices are unknown?
7. Did any unknown device appear recently?
8. Which process should the user investigate or close?
9. What changed in the last 5 minutes?
10. What evidence supports each insight?

If the data model cannot answer these questions, the design is incomplete.
