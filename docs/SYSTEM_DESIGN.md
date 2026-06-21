# JTracer v2 — System Design

This document is the consolidated system design reference for JTracer v2. It synthesizes the product, architecture, and implementation plans defined in the `/docs` folder and serves as the primary entry point for engineers and portfolio reviewers.

**Related documents:** [PRODUCT_INTENT.md](./PRODUCT_INTENT.md) · [TECH_STACK.md](./TECH_STACK.md) · [ARCHITECTURE_DECISIONS.md](./ARCHITECTURE_DECISIONS.md) · [ENTITY_DESIGN.md](./ENTITY_DESIGN.md) · [DEVELOPMENT_PHASES.md](./DEVELOPMENT_PHASES.md) · [API_CONTRACT.md](./API_CONTRACT.md)

**Architecture diagram:** [diagrams/system-design.mmd](./diagrams/system-design.mmd)

---

## Product Overview

JTracer is a **local-first personal observability platform** that unifies laptop performance monitoring, outbound network connection visibility, and home network device awareness into a single dashboard.

Unlike Activity Monitor, browser DevTools, or router admin panels—which each answer one slice of the problem—JTracer connects the dots between system slowness, process resource usage, network destinations, and devices on the local LAN.

**Design principles:** local-first, privacy-first, metadata-first, explainable insights, lightweight footprint, cross-platform by adapter design.

**Initial target:** macOS development and testing. Windows and Linux follow via platform adapters without changing the domain model or UI.

---

## Core Problem JTracer v2 Solves

Modern laptops and home networks are opaque. Users experience slowness, battery drain, unexplained network activity, and unknown devices on WiFi—but must jump between disconnected tools to investigate.

| Symptom | Typical tool today | JTracer v2 answer |
|--------|-------------------|-------------------|
| Laptop is slow | Activity Monitor | Top CPU/memory processes with evidence |
| Too many network calls | Browser DevTools (browser only) | Per-process outbound connections and domains |
| Unknown WiFi device | Router app (limited detail) | LAN discovery + device identity classification |
| What should I close? | Manual guesswork | Evidence-based insights |

JTracer v2 solves this by correlating **process → connection → domain → LAN device** in one investigation-friendly workflow, without cloud accounts, telemetry, or deep packet inspection.

---

## High-Level Architecture

JTracer uses a **hybrid architecture**: host-native collectors observe the real machine; the API, database, and frontend form a separable application stack that may later be containerized.

```text
┌─────────────────────────────────────────────────────────────┐
│                        macOS Host                           │
│  ┌─────────────────┐  ┌──────────────────┐  ┌───────────┐ │
│  │ Process Resolver│  │ Connection       │  │ LAN       │ │
│  │ (ps, df, pmset) │  │ Metadata (lsof)  │  │ Scanner   │ │
│  └────────┬────────┘  └────────┬─────────┘  └─────┬─────┘ │
│           │                    │                   │       │
│           │         ┌──────────┴─────────┐         │       │
│           │         │ DNS/Domain Resolver│         │       │
│           │         └──────────┬─────────┘         │       │
│           │                    │                   │       │
│           │         ┌──────────┴───────────────────┘       │
│           │         │ Device Identity Knowledge Base       │
│           └─────────┼───────────────────────────────────────┤
│                     ▼                                       │
│           ┌─────────────────────┐                           │
│           │ Spring Boot API     │◄──── WebSocket / REST     │
│           │ (Java 21)           │                           │
│           └─────────┬───────────┘                           │
│                     │                                       │
│           ┌─────────▼───────────┐    ┌──────────────────┐ │
│           │ SQLite              │    │ React Dashboard  │ │
│           └─────────────────────┘    └──────────────────┘ │
└─────────────────────────────────────────────────────────────┘

Optional: Docker packages API + UI + DB (not host collectors)
Future:   Kubernetes for productized multi-user deployment
```

**Critical constraint:** collectors and the LAN scanner **must run on the host OS**. Normal Docker containers observe the container namespace, not host processes like Chrome, Cursor, or Terminal.

---

## Backend Modules

Planned Spring Boot backend structure (`backend/`):

| Module | Responsibility |
|--------|----------------|
| **Domain / Entities** | HostMachine, ObservationSession, ObservedProcess, ProcessMetricSample, NetworkConnection, RemoteEndpoint, DomainIdentity, LanDevice, DeviceIdentity, Insight, etc. |
| **Repositories** | JPA or JDBC persistence against SQLite; migrations; live/demo database separation |
| **Collector Coordination** | Schedules polling; invokes platform adapters; normalizes raw snapshots to DTOs |
| **Platform Adapters** | `collectors/macos`, `collectors/windows`, `collectors/linux` implementing common interfaces |
| **Scanner Service** | Subnet discovery, ping sweep, ARP parsing, device online/offline state |
| **Device Identity Service** | OUI lookup, rule engine, confidence scoring, user label override |
| **Insight Engine** | Rule-based, evidence-backed summaries (Phase 9) |
| **REST Controllers** | Investigation-oriented `/api/v1/*` endpoints per [API_CONTRACT.md](./API_CONTRACT.md) |
| **WebSocket Handler** | Live updates at `/ws/live` for processes, connections, devices, insights |
| **Session Management** | ObservationSession lifecycle, capture mode (LIVE / DEMO / SIMULATOR) |

**Required platform interfaces** (from [TECH_STACK.md](./TECH_STACK.md)):

```text
ProcessSnapshotProvider
ProcessMetricProvider
NetworkConnectionProvider
LanDiscoveryProvider
DeviceIdentityResolver
SystemHealthProvider
```

---

## Frontend Modules

Planned React TypeScript dashboard (`web/`):

| Module | Responsibility |
|--------|----------------|
| **API Client** | Typed REST client for `/api/v1/*`; no direct collector access |
| **WebSocket Client** | Subscribes to live process, connection, device, and insight events |
| **Dashboard** | System health summary, top CPU/memory processes, connection counts, LAN device count, active insights |
| **Process Views** | Process list, detail page, metric history, per-process connections |
| **Network Views** | Connection list, domain investigation page |
| **Device Views** | LAN device list, detail page, user labeling |
| **Session Indicator** | Displays capture mode (LIVE vs DEMO) and collector health |
| **Shared Components** | Tables, severity badges, confidence indicators, evidence panels |

**Rule:** the frontend is a visualization layer only. It never executes `ps`, `lsof`, or scanner commands.

---

## Database / Local Storage Design

**MVP database:** SQLite (single local file, no separate DB server).

**Future:** PostgreSQL for heavier analytics and packaged deployment.

### Core tables (MVP)

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

### Storage rules

- **Time-series:** ProcessMetricSample and SystemHealthSnapshot append-only; do not overwrite history.
- **Live vs demo:** Separate databases or strict session isolation; never mix LIVE and DEMO rows.
- **Identity:** Process identity uses `sessionId + pid + firstSeenAt` because PIDs are reused.
- **Device identity:** MAC-preferred; IP fallback with lower confidence.
- **Knowledge base files:** Bundled JSON/YAML under `knowledge-base/` (OUI vendors, device rules, mDNS services).

See [ENTITY_DESIGN.md](./ENTITY_DESIGN.md) for full field definitions and relationships.

---

## Packet Capture and Process Tracking Flow

> **Terminology note:** JTracer MVP performs **Level 1 connection metadata collection** via OS tools (`lsof`, `ps`). It does **not** use tcpdump, libpcap, or kernel packet capture per [ARCHITECTURE_DECISIONS.md](./ARCHITECTURE_DECISIONS.md). The flow below describes metadata capture and correlation, not payload inspection.

### Process tracking flow

```text
1. Scheduler triggers process poll (every 3–5 seconds)
2. MacProcessSnapshotProvider runs `ps` (+ `df`, `pmset` for system health)
3. Raw output parsed into ObservedProcess + ProcessMetricSample DTOs
4. Persistence service upserts process records, appends metric samples
5. SystemHealthSnapshot aggregated and stored
6. WebSocket emits process.updated events
7. Frontend dashboard refreshes top CPU/memory lists
```

### Connection metadata flow

```text
1. Scheduler triggers network poll (every 2–3 seconds)
2. MacNetworkConnectionProvider runs `lsof -nP -iTCP -iUDP`
3. Parser extracts PID, protocol, local/remote IP:port, TCP state
4. Process Resolver joins lsof PID → ObservedProcess
5. DNS/Domain Resolver performs best-effort reverse DNS (with timeout + confidence)
6. RemoteEndpoint and DomainIdentity records created or updated
7. NetworkConnection persisted with process correlation
8. WebSocket emits connection.updated events
9. User investigates: Process → Connections → Domain → Remote IP
```

### Level 1 visibility example

```text
Google Chrome (PID 2245) → github.com → 140.82.112.3:443 (TCP, ACTIVE)
```

**Not in MVP:** request bodies, response bodies, cookies, HTTPS decryption, HTTP method/path.

### Known limitations

- Short-lived connections may be missed between poll intervals.
- Without elevated privileges, `lsof` may only see the current user's processes.
- Reverse DNS can be inaccurate; confidence must be surfaced.

---

## Device Identity Knowledge Base Flow

```text
1. LAN Scanner discovers subnet (ping sweep → ARP cache)
2. Raw LanDevice records created: IP, MAC, hostname, vendor (OUI lookup)
3. Device Identity Resolver pipeline:
     a. Check UserDeviceLabel (priority 100 — CONFIRMED)
     b. mDNS service analysis (priority ~90)
     c. Hostname pattern rules (priority ~70)
     d. Vendor + hostname combination rules (priority ~85)
     e. OUI vendor alone (priority ~50 — never exact model)
     f. Fallback: UNKNOWN (always visible)
4. DeviceIdentityRule engine evaluates bundled rules from knowledge-base/
5. DeviceIdentity created with displayName, deviceType, confidence, evidence
6. LanDevice updated with classification; status tracked (NEW → ONLINE → OFFLINE)
7. API exposes GET /api/v1/devices; user may POST label to override
```

**Example resolution:**

```text
Raw:    Vendor = Amazon Technologies, Hostname = echo-bedroom.local
Result: Amazon Echo (SMART_SPEAKER), Confidence = LIKELY
Evidence: Vendor contains Amazon AND hostname contains echo
```

See [DEVICE_IDENTITY_KNOWLEDGE_BASE.md](./DEVICE_IDENTITY_KNOWLEDGE_BASE.md) for signal priorities and rule file structure.

---

## Dockerization Strategy

Docker is **optional and limited** to non-host-observability components.

### Allowed in Docker

| Component | Notes |
|-----------|-------|
| Spring Boot API | Containerized REST + WebSocket service |
| React frontend | Static build served by nginx or Vite dev in compose |
| SQLite | Volume-mounted database file |
| Dev environment | One-command local stack for API + UI development |

### Not allowed in normal Docker

| Component | Reason |
|-----------|--------|
| Process collector | Container sees container processes, not host Chrome/Cursor |
| Network connection collector | Container network namespace ≠ host sockets |
| LAN scanner | Must read host ARP table and reach LAN subnet |

### Recommended MVP local runtime

```text
Host OS
  ├── macOS collector (native)
  ├── LAN scanner (native)
  ├── Spring Boot backend (native or Docker)
  ├── SQLite (local file)
  └── React dev server (native or Docker)
```

### Future hybrid runtime (Docker Compose)

```text
Host OS
  ├── native collector agent
  ├── native LAN scanner agent
  └── Docker Compose
        ├── backend API
        ├── frontend
        └── PostgreSQL or SQLite volume
```

---

## Future Kubernetes Deployment Strategy

Kubernetes is **future productization scope**, not MVP. The design anticipates it without blocking local macOS development.

### Planned separation

| Tier | Deployment |
|------|------------|
| **Edge agent** | Native binary on each host (collectors + scanner); reports to API |
| **Control plane** | K8s-deployed Spring Boot API, frontend, PostgreSQL |
| **Ingress** | TLS-terminated gateway for local network or VPN access |

### Migration path

1. **Phase 1 (MVP):** All components on single macOS host; SQLite file.
2. **Phase 2 (Compose):** Docker for API/UI/DB; native collectors unchanged.
3. **Phase 3 (K8s):** API and UI as deployments; PVC for database; collector as DaemonSet or host-installed agent with hostNetwork/hostPID if ever containerized.
4. **Phase 4 (Product):** Native desktop installer bundling collector + embedded backend + local DB.

### K8s considerations

- Collectors require `hostNetwork`, `hostPID`, or out-of-cluster agents—never standard pod isolation for observability.
- No cloud telemetry by default; all data remains user-controlled.
- PostgreSQL preferred over SQLite at scale for time-series queries and retention policies.

---

## Dependency Graph

```text
                    ┌──────────────────┐
                    │  React Frontend  │
                    └────────┬─────────┘
                             │ REST + WebSocket
                    ┌────────▼─────────┐
                    │  Spring Boot API │
                    └───┬──────────┬───┘
                        │          │
           ┌────────────┘          └────────────┐
           ▼                                     ▼
    ┌─────────────┐                    ┌─────────────────┐
    │   SQLite    │                    │ Insight Engine  │
    └─────────────┘                    └────────┬────────┘
           ▲                                    │
           │                          reads aggregates
    ┌──────┴───────────────────────────────────┴──────┐
    │              Persistence Services                │
    └──────┬──────────────┬──────────────┬────────────┘
           │              │              │
    ┌──────▼──────┐ ┌─────▼─────┐ ┌──────▼──────────┐
    │  Process    │ │ Connection│ │  LAN Scanner    │
    │  Collector  │ │ Collector │ │  + Identity KB  │
    └──────┬──────┘ └─────┬─────┘ └──────┬──────────┘
           │              │              │
    ┌──────▼──────────────▼──────────────▼──────────┐
    │           macOS Host OS (ps, lsof, arp, ping)  │
    └────────────────────────────────────────────────┘

Knowledge base (read-only at runtime):
  oui-vendors.json → device-rules.json → mdns-services.json
  UserDeviceLabel (SQLite, user-writable)
```

**Build order dependency:** entities → collectors → persistence → APIs → validation → UI → insights.

---

## Phase-wise Build Plan

Aligned with [DEVELOPMENT_PHASES.md](./DEVELOPMENT_PHASES.md):

| Phase | Focus | Key deliverables |
|-------|-------|------------------|
| **0** | Foundation | Documentation set (complete) |
| **1** | Domain + DB | Entities, migrations, repositories |
| **2** | macOS process collector | `ps`, `df`, `pmset` adapters |
| **3** | macOS network collector | `lsof` parser, domain resolver, process correlation |
| **4** | LAN scanner | Ping sweep, ARP parser, device status |
| **5** | Device intelligence | OUI DB, rule engine, confidence scoring |
| **6** | Backend API | REST `/api/v1/*`, WebSocket `/ws/live` |
| **7** | Validation | Manual tests before any UI work |
| **8** | MVP dashboard | React pages per API contract |
| **9** | Insight engine | Evidence-based rule summaries |
| **10** | Metrics history | Time-series graphs |
| **11–12** | Cross-platform | Windows, then Linux adapters |
| **13** | Productization | Installer, settings, retention |
| **14** | Security awareness | Future; after observability is stable |

---

## Risks and Unknowns

| Risk | Impact | Mitigation |
|------|--------|------------|
| `lsof` polling misses short connections | Incomplete network picture | Document limitation; tune poll interval; consider nettop later |
| Non-root `lsof` visibility gaps | Other users' processes hidden | Document; optional future elevation with explicit consent |
| Reverse DNS inaccuracy | Wrong domain labels | Confidence levels; never present LOW as guaranteed |
| Randomized MAC addresses | Weak device identity | IP fallback; user labels; show UNKNOWN |
| ARP cache incompleteness | Missed LAN devices | Ping sweep to populate cache; periodic rescans |
| SQLite time-series growth | Disk usage over long runs | Retention policy (future); PostgreSQL migration path |
| Cross-platform parser drift | Windows/Linux adapters diverge | Common DTOs; fixture-based parser tests |
| Performance overhead | JTracer itself slows laptop | <5% CPU, <300 MB RAM budget; conservative polling |
| Docker misuse | Collectors containerized incorrectly | Architecture decisions enforced in CI/docs review |

---

## Resolved Architecture Decisions

The following items were previously open questions. They are now finalized for MVP implementation.

### 1. Collector packaging

**Decision:** Keep collectors inside the Spring Boot application for MVP, using platform adapter interfaces (`ProcessSnapshotProvider`, `NetworkConnectionProvider`, etc.).

Extract collectors into a separate native agent only after the Phase 8 dashboard is working and validated with real data.

### 2. Poll intervals

**Decision:**

| Collector | Interval |
|-----------|----------|
| Process metrics | Every 5 seconds |
| Network connections | Every 3 seconds |
| LAN scan | Every 60 seconds |
| System health | Every 10 seconds |

All intervals must be configurable via `application.yml`.

### 3. mDNS depth

**Decision:** MVP starts with hostname resolution, ARP lookup, and OUI vendor lookup only.

Full Bonjour/mDNS service discovery is deferred to a Phase 5 enhancement after the core LAN scanner and device identity pipeline are stable.

### 4. Database retention

**Decision:**

- Default retention: **7 days** for MVP
- Active session data is retained in full for the duration of the session
- A scheduled cleanup job will be added in a later phase
- Retention is configurable: **1 day**, **7 days**, or **30 days**

### 5. Demo mode

**Decision:** Use separate SQLite database files to prevent mixing real and synthetic data.

```text
jtracer-live.db   # production user data
jtracer-demo.db   # demo and test data
```

### 6. Authentication

**Decision:** MVP binds the API to **localhost only**. No login is required initially.

Add local token authentication before exposing the API beyond localhost (e.g., LAN access or remote dashboard).

### 7. nettop integration

**Decision:** **Not in MVP.**

Use `lsof` first for process-to-connection mapping. Add `nettop` later only for bandwidth enrichment after the core observability flow is stable.

### 8. Kubernetes agent model

**Decision:** Do **not** containerize collectors in MVP.

Future deployment model:

- **Native host agent** installed on the laptop (collectors + scanner)
- **Kubernetes** runs only the API, frontend, and database

Avoid privileged DaemonSet deployments unless the product pivots to enterprise or server-focused observability.

---

## Implementation Status

| Milestone | Status |
|-----------|--------|
| Phase 1 — Core domain and database | ✅ Complete |
| Phase 2 — macOS process collector | ✅ Complete |
| Phase 3 — macOS network collector | ✅ Complete |
| Phase 4 — LAN scanner | 🚧 Design approved ([PHASE4_DESIGN.md](./PHASE4_DESIGN.md)) |

**Validated:** `mvn test` (unit), `mvn test -Plive-tests` (macOS live collectors).

---

## First Implementation Milestone

**Milestone: Phase 1 — Core Domain and Database** ✅

Deliver a running Spring Boot application with:

- [x] All MVP entity classes per [ENTITY_DESIGN.md](./ENTITY_DESIGN.md)
- [x] Flyway migrations creating MVP tables (V1 process schema, V2 network schema)
- [x] Repository layer with unit tests for persistence
- [x] ObservationSession with `captureMode` = LIVE | DEMO | SIMULATOR
- [x] Clean database startup; live and demo data paths separated
- [x] No UI, no fake seed data in live mode

**Exit criteria:** `mvn test` passes; database migrations run cleanly; entities persist and retrieve correctly.

**Next milestone:** Phase 4 — LAN Scanner (see [PHASE4_DESIGN.md](./PHASE4_DESIGN.md)).

---

*This document should be updated when architecture decisions change. See [ARCHITECTURE_DECISIONS.md](./ARCHITECTURE_DECISIONS.md) for the authoritative decision log.*
