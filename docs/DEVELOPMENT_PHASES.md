# DEVELOPMENT_PHASES.md

# JTracer Development Phases

## Implementation Status (March 2026)

| Phase | Name | Status |
|-------|------|--------|
| 0 | Documentation foundation | ✅ Complete |
| 1 | Core domain + database | ✅ Complete |
| 2 | macOS process collector | ✅ Complete |
| 3 | macOS network collector | ✅ Complete |
| 4 | LAN scanner | ✅ Complete |
| 5 | Device identity engine | Planned |
| 6 | Backend API | Planned |
| 7 | Validation (pre-UI) | Planned |
| 8 | React dashboard | Planned |

Public repository publishes architecture, domain model, parsers, and interfaces.
Full collector implementations remain in the private development workspace.
See [PUBLIC_RELEASE.md](./PUBLIC_RELEASE.md).

---

## 1. Purpose

This document defines the recommended implementation sequence for JTracer.

The goal is to build the product in controlled phases so AI coding agents do not jump directly into UI, fake data, or overbuilt security features.

JTracer must be developed in this order:

```text
Product intent
→ entity model
→ data sources
→ collectors
→ persistence
→ APIs
→ validation
→ UI
→ insights
→ cross-platform expansion
```

A visually impressive dashboard with unreliable data is not success.

---

# 2. Development Strategy

JTracer should be built as a prototype first, not as a full commercial product.

The first prototype must prove:

1. Real process data can be collected
2. Real network connection data can be collected
3. Real LAN device data can be collected
4. Data can be persisted correctly
5. APIs can expose normalized data
6. UI can answer investigation questions
7. Insights are evidence-based

---

# 3. Phase 0 — Project Foundation

## Goal

Create a clean project structure and documentation foundation before coding.

## Deliverables

* PRODUCT_INTENT.md
* ENTITY_DESIGN.md
* DATA_SOURCES.md
* MVP_ACCEPTANCE_CRITERIA.md
* AI_AGENT_RULES.md
* TECH_STACK.md
* API_CONTRACT.md
* DEVELOPMENT_PHASES.md

## Acceptance Criteria

* All documents exist in the repository
* AI agent is instructed to read all documents before coding
* No implementation begins before documentation foundation is complete

---

# 4. Phase 1 — Core Domain and Database

## Goal

Implement the database schema and core domain entities.

## Entities

* HostMachine
* ObservationSession
* SystemHealthSnapshot
* ObservedProcess
* ProcessMetricSample
* NetworkConnection
* RemoteEndpoint
* DomainIdentity
* NetworkScanSession
* LanDevice
* DeviceIdentity
* DeviceIdentityRule
* UserDeviceLabel
* Insight

## Deliverables

* Entity classes
* Database migrations
* Repository layer
* Unit tests for entity persistence

## Acceptance Criteria

* Database starts cleanly
* Migrations run successfully
* Live and demo data can be separated
* No UI work is started yet

---

# 5. Phase 2 — macOS Process Collector

## Goal

Collect real process and resource data from macOS.

## Data Sources

```text
ps
df
pmset
```

## Deliverables

* MacProcessSnapshotProvider
* MacProcessMetricProvider
* MacSystemHealthProvider
* Parser tests
* Process persistence service

## Acceptance Criteria

* Running processes are detected
* CPU usage is captured
* Memory usage is captured
* RSS memory is captured
* Disk usage is captured
* Battery status is captured when available
* No fake data is used in live mode

---

# 6. Phase 3 — macOS Network Collector

## Goal

Collect outbound network connection metadata from macOS.

## Data Sources

```text
lsof
hostname lookup
reverse DNS
```

## Deliverables

* MacNetworkConnectionProvider
* Lsof parser
* Domain resolver
* Connection persistence service
* Process-to-connection correlation

## Acceptance Criteria

* Outbound connections are detected
* Connections are linked to process PID when available
* Remote IP and port are captured
* Domain is resolved when possible
* Short-lived connection limitations are documented
* No HTTPS body/header capture exists in MVP

---

# 7. Phase 4 — LAN Scanner

## Goal

Discover devices connected to the local network.

## Data Sources

```text
ping
arp -a
hostname lookup
mDNS lookup
```

## Deliverables

* Subnet discovery
* Ping sweep
* ARP cache parser
* LAN device persistence
* Device online/offline tracking

## Acceptance Criteria

* Online LAN devices are detected
* IP address is captured
* MAC address is captured when available
* Vendor is captured when available
* Device status changes from NEW to ONLINE to OFFLINE
* Unknown devices are displayed, not hidden

---

# 8. Phase 5 — Device Intelligence Engine

## Goal

Classify discovered LAN devices into useful human-readable identities.

## Deliverables

* Local OUI vendor database
* Device identity rules JSON/YAML
* DeviceIdentityResolver
* Confidence scoring
* User label support

## Identity Signals

* MAC OUI prefix
* Vendor name
* Hostname
* mDNS name
* User label
* Device history

## Example Output

```text
Raw:
Vendor = Amazon Technologies
Hostname = echo-bedroom.local

Resolved:
Display Name = Amazon Echo
Device Type = SMART_SPEAKER
Confidence = LIKELY
Evidence = Vendor contains Amazon and hostname contains echo
```

## Acceptance Criteria

* Device identity includes confidence
* Device identity includes evidence
* User labels override rule-based identity
* Vendor alone is not treated as exact model
* Unknown devices remain visible

---

# 9. Phase 6 — Backend API

## Goal

Expose normalized data through backend APIs.

## APIs

* GET /api/v1/system/health
* GET /api/v1/system/snapshots
* GET /api/v1/processes
* GET /api/v1/processes/{id}
* GET /api/v1/processes/{id}/metrics
* GET /api/v1/processes/{id}/connections
* GET /api/v1/connections
* GET /api/v1/domains
* GET /api/v1/devices
* GET /api/v1/devices/{id}
* POST /api/v1/devices/{id}/label
* GET /api/v1/insights

## Acceptance Criteria

* APIs return live normalized data
* APIs do not expose platform-specific details
* APIs do not return fake fallback data
* Frontend does not call collectors directly

---

# 10. Phase 7 — Validation Before UI

## Goal

Validate data correctness before dashboard development.

## Manual Tests

1. Open Chrome and browse websites
2. Run curl from terminal
3. Open Cursor or VS Code
4. Start a high CPU task
5. Connect phone to WiFi
6. Disconnect a smart device
7. Run API calls and verify results

## Acceptance Criteria

* Process data is accurate
* Network data is accurate
* LAN device data is acceptable
* Device identity confidence is understandable
* Known limitations are documented

No UI work should begin until this phase passes.

---

# 11. Phase 8 — MVP Dashboard UI

## Goal

Build the first useful dashboard.

## Required Sections

* System health summary
* Top CPU processes
* Top memory processes
* Process network activity
* Remote domains
* LAN devices
* Basic insights

## Required Pages

* Dashboard
* Process detail page
* Device detail page
* Domain investigation page

## Acceptance Criteria

User can answer within 10 seconds:

* Why is my laptop slow?
* Which process is using high CPU?
* Which process is using high memory?
* Which process is making network connections?
* Which domains are applications talking to?
* Which devices are connected?
* Which devices are unknown?

---

# 12. Phase 9 — Basic Insight Engine

## Goal

Generate evidence-based summaries.

## Example Insights

```text
Chrome is using high memory.
Cursor made many outbound connections.
Unknown device appeared on the network.
Node is using high CPU.
Battery may be draining due to high CPU processes.
```

## Rules

Every insight must have evidence.

Example:

```text
Cursor used more than 20 percent CPU for 5 minutes.
```

Forbidden:

```text
Cursor seems suspicious.
```

without evidence.

## Acceptance Criteria

* Insights are rule-based first
* Every insight links to supporting data
* Insights can be dismissed
* No AI-generated security claims in MVP

---

# 13. Phase 10 — Metrics History and Trends

## Goal

Add time-series observability.

## Deliverables

* Process CPU history
* Process memory history
* System CPU history
* System memory history
* LAN device history
* Connection frequency history

## Acceptance Criteria

* User can see whether a process is getting worse over time
* User can identify recent spikes
* User can compare current state to recent history

---

# 14. Phase 11 — Windows Prototype

## Goal

Prove cross-platform architecture.

## Windows Data Sources

```text
Get-Process
Get-NetTCPConnection
netstat
arp
Get-NetNeighbor
```

## Deliverables

* WindowsProcessSnapshotProvider
* WindowsNetworkConnectionProvider
* WindowsLanDiscoveryProvider

## Acceptance Criteria

* Windows process list works
* Windows network connection list works
* Windows LAN discovery works at basic level
* UI does not require changes for Windows data

---

# 15. Phase 12 — Linux Prototype

## Goal

Add Linux support after macOS and Windows design stabilizes.

## Linux Data Sources

```text
ps
ss
lsof
ip neigh
/proc
```

## Acceptance Criteria

* Linux adapters produce same normalized DTOs
* Backend and UI remain unchanged

---

# 16. Phase 13 — Productization

## Goal

Prepare for broader usage.

## Deliverables

* Installer strategy
* Local service startup
* Database retention
* Settings page
* Export options
* Error reporting without cloud telemetry
* Documentation
* Upgrade strategy

## Acceptance Criteria

* Non-developer user can install and run the app
* Data remains local
* User can reset or purge data
* App can update safely

---

# 17. Phase 14 — Future Security Awareness

## Goal

Add lightweight security awareness after observability foundation is mature.

## Possible Features

* Unknown device alert
* New external domain alert
* Unusual connection frequency alert
* Suspicious port alert
* High battery drain alert
* Process behavior baseline

## Not Allowed Without Explicit Approval

* Antivirus claims
* Malware detection claims
* Packet capture
* HTTPS MITM
* Firewall modification
* Automatic blocking

---

# 18. Recommended Cursor Execution Order

Use Cursor with one task at a time.

## Prompt 1

Create project skeleton and documentation references.

## Prompt 2

Implement entities and database migrations only.

## Prompt 3

Implement macOS process collector only.

## Prompt 4

Implement macOS network collector only.

## Prompt 5

Implement LAN scanner only.

## Prompt 6

Implement device identity engine only.

## Prompt 7

Implement backend APIs only.

## Prompt 8

Create validation scripts.

## Prompt 9

Build dashboard UI only after validation passes.

## Prompt 10

Build insight engine.

---

# 19. Success Definition

JTracer prototype is successful when it gives truthful answers to real problems.

The MVP succeeds when it helps answer:

```text
Why is my laptop slow?
Which process is using CPU?
Which process is using memory?
Which process is making network requests?
Which domains are applications talking to?
Which devices are connected to the home network?
Which devices are unknown?
What changed recently?
What should I investigate?
```

If the application cannot answer these questions with real data, it is not complete.
