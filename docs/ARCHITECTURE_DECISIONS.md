# ARCHITECTURE_DECISIONS.md

# JTracer Architecture Decisions

## 1. Purpose

This document records major architecture decisions for JTracer.

Every future implementation must respect these decisions unless the developer explicitly changes them.

This document exists to prevent architecture drift during AI-assisted development.

---

## 2. Decision 1 — JTracer Is Local-First

### Decision

JTracer will run locally on the user’s machine.

### Reason

The product monitors personal system behavior, local processes, network connections, and home devices.

This data is sensitive and should remain under user control.

### Implication

No cloud account, no default telemetry, and no remote ingestion in MVP.

---

## 3. Decision 2 — Host Collectors Must Run on Host OS

### Decision

Collectors that read process, socket, ARP, ping, battery, and disk data must run directly on the host operating system.

### Reason

Normal Docker containers cannot reliably observe host processes or host network sockets.

A containerized collector would mostly observe the container namespace, not Chrome, Cursor, Slack, Terminal, smart devices, or real host activity.

### Implication

Collectors are host-native components.

Docker may be used for backend, frontend, database, or development packaging, but not for host observability collectors unless host namespace access is explicitly solved.

---

## 4. Decision 3 — Docker Is Optional and Limited

### Decision

Docker can be used for non-host-observability components.

Allowed:

* Backend API
* Frontend
* Database
* Development packaging

Not allowed by default:

* Host process collector
* Host network socket collector
* LAN scanner that must access host network state

### Reason

The application must observe the real computer, not only a container.

---

## 5. Decision 4 — Java/Spring Boot Is Approved for MVP Backend

### Decision

The MVP backend will use:

```text
Java 21
Spring Boot
Maven
```

### Reason

This stack supports stable APIs, background services, persistence, tests, and future productization.

The developer is comfortable with Java and Spring Boot.

### Implication

Do not replace backend framework without explicit approval.

---

## 6. Decision 5 — React/TypeScript/Vite Is Approved for MVP Frontend

### Decision

The MVP frontend will use:

```text
React
TypeScript
Vite
```

### Reason

This stack is practical for dashboard development and already aligns with previous prototype work.

### Implication

Frontend is a visualization layer only.

It must not execute system commands or directly call collectors.

---

## 7. Decision 6 — SQLite First, PostgreSQL Later

### Decision

The MVP will use SQLite for local storage.

Future productized versions may support PostgreSQL.

### Reason

SQLite is lightweight, local, simple, and appropriate for a personal desktop prototype.

PostgreSQL may be added later for heavier analytics and broader deployment.

### Implication

Entity design should avoid SQLite-only assumptions where possible.

---

## 8. Decision 7 — Metadata-First Collection

### Decision

JTracer MVP will collect metadata only.

Allowed:

* Process name
* PID
* CPU usage
* Memory usage
* Local IP and port
* Remote IP and port
* Protocol
* Connection state
* Domain when available
* LAN device IP
* MAC address when available
* Vendor when available
* Hostname when available

Not allowed in MVP:

* Request body
* Response body
* Cookies
* Passwords
* Authorization headers
* HTTPS decrypted payloads
* Full packet payloads

### Reason

Metadata provides useful observability while keeping privacy risk manageable.

---

## 9. Decision 8 — No Packet Capture in MVP

### Decision

JTracer will not use tcpdump, Wireshark, libpcap, packet sniffing, or kernel-level packet capture in MVP.

### Reason

Packet capture increases privilege, performance, privacy, and implementation risk.

### Implication

Network observability begins with safe OS metadata sources such as lsof, ps, arp, ping, netstat, and platform equivalents.

---

## 10. Decision 9 — No HTTPS MITM in MVP

### Decision

JTracer will not decrypt HTTPS traffic in MVP.

### Reason

MITM requires certificates, explicit user trust, deep security review, and sensitive data handling.

### Implication

The MVP must not claim to show full API request bodies or HTTPS paths.

Level 1 visibility is acceptable:

```text
Process → domain/IP → port
```

---

## 11. Decision 10 — Device Intelligence Is Core

### Decision

LAN device identity classification is a core feature, not a side feature.

### Reason

One unique value of JTracer is combining laptop observability with home network awareness.

### Implication

The product must support:

* Device discovery
* Vendor lookup
* Hostname/mDNS analysis
* Device identity rules
* Confidence scoring
* Unknown device visibility
* User labeling

---

## 12. Decision 11 — Unknown Devices Must Be Visible

### Decision

Unknown devices must be shown clearly.

### Reason

Hiding unknown devices creates false confidence.

### Implication

The UI must display unknown or low-confidence devices instead of filtering them away.

---

## 13. Decision 12 — User Labels Override Automatic Identity

### Decision

User-provided device labels have highest priority.

### Reason

A user can confirm device identity more accurately than automated inference.

### Implication

If user labels a device as “Living Room Fire TV,” that label overrides vendor or hostname guessing.

---

## 14. Decision 13 — Entity Model Comes Before UI

### Decision

JTracer development must begin with domain entities and persistence before dashboard work.

### Reason

The previous prototype drifted because UI and features were created before the product model was fully defined.

### Implication

AI agents must not build dashboards before entities, collectors, APIs, and validation are working.

---

## 15. Decision 14 — APIs Are Investigation-Oriented

### Decision

APIs should support investigation workflows, not expose raw database tables blindly.

### Reason

The product exists to answer user questions:

* Why is my laptop slow?
* Which process is consuming resources?
* Which process is making network calls?
* Which domains are involved?
* Which devices are connected?

### Implication

API design must follow API_CONTRACT.md.

---

## 16. Decision 15 — Live Data and Demo Data Must Never Mix

### Decision

Live mode and demo/simulator mode must be strictly separated.

### Reason

The user must trust JTracer output.

Fake data in live mode destroys product credibility.

### Implication

Use separate database modes, explicit capture mode markers, and visible UI indicators.

---

## 17. Decision 16 — Insights Must Be Evidence-Based

### Decision

Every insight must include measurable evidence.

### Example

Allowed:

```text
Cursor used more than 20 percent CPU for 5 minutes.
```

Not allowed:

```text
Cursor seems suspicious.
```

### Reason

The tool must be explainable and trustworthy.

---

## 18. Decision 17 — Security Monitoring Is Future Scope

### Decision

Security monitoring will be added after observability works.

### Reason

Suspicious behavior detection depends on reliable process, connection, device, and history data.

### Implication

Do not build malware detection, firewall modification, or blocking features in MVP.

---

## 19. Decision 18 — Cross-Platform by Adapter Design

### Decision

JTracer should support macOS first, then Windows and Linux through adapters.

### Required Direction

```text
collectors/common
collectors/macos
collectors/windows
collectors/linux
```

### Reason

The UI and domain model should remain stable across operating systems.

### Implication

Platform commands must not leak into common services or frontend code.

---

## 20. Decision 19 — Performance Budget Matters

### Decision

JTracer must remain lightweight.

Target budget:

```text
Average CPU usage < 5%
Memory usage < 300 MB
LAN scan < 10 seconds
```

### Reason

A monitoring tool must not become the reason the laptop is slow.

### Implication

Avoid aggressive polling, repeated DNS lookups, and uncontrolled scan loops.

---

## 21. Decision 20 — AI Implements, Developer Owns Architecture

### Decision

AI coding agents may implement bounded tasks but must not redefine architecture.

### Reason

The product intent belongs to the developer.

AI previously drifted toward generic dashboard implementation.

### Implication

Every AI task should be small, bounded, and validated against documentation.

---

## 22. Required Document Set

Before coding, AI agents must read:

```text
PRODUCT_INTENT.md
ENTITY_DESIGN.md
DATA_SOURCES.md
MVP_ACCEPTANCE_CRITERIA.md
AI_AGENT_RULES.md
TECH_STACK.md
API_CONTRACT.md
DEVELOPMENT_PHASES.md
DEVICE_IDENTITY_KNOWLEDGE_BASE.md
ARCHITECTURE_DECISIONS.md
```

---

## 23. Decision 21 — Persistence Provider Abstraction

### Decision

Storage access must be abstracted behind swappable persistence providers:

```text
PersistenceProvider
├── LocalSQLitePersistenceProvider   # MVP/default
├── TursoPersistenceProvider         # future cloud/sync option
└── PostgresPersistenceProvider      # future team/server mode
```

### Reason

MVP requires a zero-setup local file database, but productization may need edge sync (Turso) or multi-user server storage (Postgres) without rewriting collectors.

### Implication

- MVP implements `LocalSQLitePersistenceProvider` only (Spring Data JPA + Flyway + SQLite).
- Turso and Postgres providers are documented and planned; not implemented in MVP.
- Provider selection is configuration-driven (`jtracer.persistence.provider`).

---

## 24. Final Architecture Principle

JTracer should be designed as a system first and an application second.

The dashboard is only the visible layer.

The core value is the ability to observe, correlate, explain, and summarize real local system behavior.
