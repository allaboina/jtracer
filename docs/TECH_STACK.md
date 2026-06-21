# TECH_STACK.md

# JTracer Technical Stack

## 1. Purpose

This document defines the approved technical stack and deployment strategy for JTracer.

The goal is to keep the architecture practical, cross-platform, local-first, and future-ready without overengineering the first prototype.

---

## 2. Product Architecture Summary

JTracer will use a hybrid architecture.

Host OS collectors must run directly on the user’s operating system because they need visibility into real host processes, network sockets, ARP tables, battery status, and system metrics.

The API, database, and frontend may be containerized later, but host collectors must not be hidden inside normal Docker containers.

---

## 3. Approved MVP Stack

## Frontend

```text
React
TypeScript
Vite
```

Purpose:

* Dashboard UI
* Process views
* Network connection views
* LAN device views
* Insight views

Rules:

* Frontend must not collect system data directly.
* Frontend must call backend APIs only.
* Frontend must not call scanner or collectors directly.

---

## Backend API

```text
Java 21
Spring Boot
Maven
```

Purpose:

* REST APIs
* WebSocket updates
* Business logic
* Entity persistence
* Insight generation
* Authentication boundary
* Coordination between collectors, scanner, and UI

Rules:

* Backend owns the public local API.
* Backend must remain platform-independent where possible.
* Backend should not contain hardcoded macOS logic in common services.

---

## Database

### MVP

```text
SQLite
```

Purpose:

* Local storage
* Process snapshots
* Network connections
* LAN devices
* Device identity rules
* User labels
* Insights

Why SQLite:

* Simple local setup
* No separate database server required
* Easy to delete and reset
* Good fit for personal desktop prototype

### Future Productization

```text
PostgreSQL
```

Purpose:

* Better analytics
* Better indexing
* Better time-series querying
* More scalable deployment option

Rule:

The entity model should be designed so SQLite can be replaced or supplemented by PostgreSQL later.

---

## Host Collectors

Collectors are responsible for reading real system data from the operating system.

Collectors must run on the host OS, not inside normal Docker containers.

### macOS Collector

Approved sources:

```text
ps
lsof
ping
arp -a
df
pmset
hostname lookup
mDNS lookup
nettop future
```

### Windows Collector Future

Approved sources:

```text
Get-Process
Get-NetTCPConnection
netstat
arp
Get-NetNeighbor
PowerShell
```

### Linux Collector Future

Approved sources:

```text
ps
ss
lsof
ip neigh
arp
ping
/proc
```

---

## LAN Scanner

Initial implementation may be part of the host collector or a separate local service.

Preferred MVP strategy:

```text
scanner module/service running on host OS
```

Purpose:

* Discover LAN devices
* Resolve MAC addresses
* Resolve vendor identity
* Classify device type
* Track online/offline device status

Rules:

* Scanner must not be called directly by frontend.
* Backend must proxy or aggregate scanner data.
* Scanner must not require sudo/admin permissions for MVP.
* Scanner must not scan external networks.

---

## Communication Pattern

Approved communication path:

```text
Frontend → Backend API → Database
Frontend → Backend API → Collector/Scanner data
Collector/Scanner → Backend API or local database
```

Frontend must never directly execute system commands.

---

## API Style

Approved:

```text
REST API
WebSocket for live updates
```

REST is used for:

* Listing processes
* Listing connections
* Listing devices
* Fetching detail pages
* Fetching historical metrics

WebSocket is used for:

* Live dashboard refresh
* New process metric updates
* New connection updates
* New LAN device status updates
* Insight updates

---

## Containerization Strategy

JTracer will not fully containerize host collectors.

### Allowed Docker Usage

Docker may be used for:

```text
frontend
backend API
database
development environment
future product packaging support
```

### Not Allowed in Normal Docker

Do not run host collectors inside normal Docker containers if the collector is expected to observe the real host machine.

Reason:

Docker containers see the container namespace, not the full host process and network namespace.

Incorrect architecture:

```text
Docker container collector
  → sees container processes
  → misses host Chrome, Cursor, Slack, Terminal, etc.
```

Correct architecture:

```text
Host OS Collector
  → sees real host processes and network sockets
  → sends data to backend
```

---

## Recommended Runtime Architecture

### MVP Local Runtime

```text
Host OS
  ├── macOS collector
  ├── LAN scanner
  ├── Spring Boot backend
  ├── SQLite database
  └── React frontend dev server
```

### Future Hybrid Runtime

```text
Host OS
  ├── native collector agent
  ├── native LAN scanner agent
  └── Docker Compose
        ├── backend API
        ├── frontend
        └── PostgreSQL or SQLite volume
```

### Future Product Runtime

```text
Native desktop installer
  ├── local collector agent
  ├── local backend service
  ├── embedded database
  └── packaged frontend
```

---

## Repository Structure

Preferred structure:

```text
jtracer/
  docs/
    PRODUCT_INTENT.md
    ENTITY_DESIGN.md
    DATA_SOURCES.md
    MVP_ACCEPTANCE_CRITERIA.md
    AI_AGENT_RULES.md
    TECH_STACK.md

  backend/
    Spring Boot API
    entities
    repositories
    services
    controllers

  web/
    React TypeScript dashboard

  collectors/
    common/
    macos/
    windows/
    linux/

  scanner/
    LAN discovery and device identity

  config/
    environment examples

  scripts/
    local development scripts
```

---

## Platform Adapter Design

Core business logic must depend on interfaces, not OS-specific implementations.

### Required Interfaces

```text
ProcessSnapshotProvider
ProcessMetricProvider
NetworkConnectionProvider
LanDiscoveryProvider
DeviceIdentityResolver
SystemHealthProvider
```

### Implementation Examples

```text
MacProcessSnapshotProvider
MacNetworkConnectionProvider
MacLanDiscoveryProvider

WindowsProcessSnapshotProvider
WindowsNetworkConnectionProvider

LinuxProcessSnapshotProvider
LinuxNetworkConnectionProvider
```

Rules:

* Platform-specific code stays in platform folders.
* Common services consume normalized DTOs.
* UI must not know which platform produced the data.

---

## Build Tooling

Approved:

```text
Maven for Java
npm for frontend
Docker Compose for optional development packaging
```

Future:

```text
GitHub Actions
JUnit
Vitest
Playwright optional
```

---

## Testing Stack

Backend:

```text
JUnit 5
Spring Boot Test
MockMvc
Testcontainers future
```

Frontend:

```text
Vitest
React Testing Library future
Playwright future
```

Collectors:

```text
Fixture-based parser tests
Manual host validation tests
Platform-specific integration tests
```

Scanner:

```text
Unit tests for subnet math
ARP parser fixture tests
Device identity rule tests
State transition tests
```

---

## Security and Privacy Stack Rules

JTracer must not use these in MVP:

```text
tcpdump
Wireshark embedding
packet payload capture
HTTPS MITM
browser extension
kernel extension
system extension
sudo automation
cloud telemetry
```

Future security features must require explicit approval.

---

## Why Not Use Python Backend?

Python is not selected for backend MVP because JTracer needs:

* Long-running local services
* System collectors
* Strong typing
* Multi-threaded polling
* Cross-platform packaging
* Stable API structure

Java and Spring Boot are more suitable for this project’s long-term direction.

---

## Why Not Use Go or Rust Now?

Go or Rust may be useful later for lightweight collectors.

However, they are not required for the first successful prototype.

Current decision:

```text
Use Java/Spring Boot first.
Consider Go or Rust only if collector performance or packaging becomes a real blocker.
```

---

## Future Native Collector Option

If Java collectors become heavy or limited, create native collectors later.

Possible future structure:

```text
collector-macos-go
collector-windows-go
collector-linux-go
```

or

```text
collector-macos-rust
collector-windows-rust
collector-linux-rust
```

But do not start there.

First prove the product value.

---

## MVP Stack Decision

The approved MVP stack is:

```text
Frontend:
React + TypeScript + Vite

Backend:
Java 21 + Spring Boot + Maven

Database:
SQLite

Collectors:
Host OS collectors using platform commands

Scanner:
Host OS LAN scanner

Packaging:
Local scripts first
Docker only for non-host-observability components
```

---

## Final Rule

Technology must support the product intent.

Technology must not redefine the product.

The stack must help JTracer answer:

* Why is my laptop slow?
* Which process is using resources?
* Which process is making network connections?
* Which domains are applications talking to?
* Which devices are connected to the home network?
* Which devices are unknown?
* What should the user investigate?
