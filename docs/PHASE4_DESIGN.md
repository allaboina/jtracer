# JTracer Phase 4 – LAN Discovery Engine Design

## Objective

Extend JTracer observability platform to discover and monitor devices connected to the local network (LAN).

Phase 4 must operate independently and must not interfere with existing collectors:

* Process Collector (5 sec)
* Network Collector (3 sec)
* System Health Collector (10 sec)

LAN scanning will run every 60 seconds.

---

## Architecture Principles

Phase 4 is an additive subsystem.

Requirements:

* No modification of Phase 1–3 collectors
* Separate persistence layer
* Independent scheduler execution
* Correlation with Phase 3 network data only through IP matching
* Lightweight network discovery
* No packet capture or privileged access

---

## Collector Architecture

LAN Discovery Engine consists of:

### 1. Subnet Discovery

Determine local network information.

Commands:

```bash
ifconfig
route -n get default
```

Collect:

* local IP
* subnet mask
* gateway
* adapter name

---

### 2. ARP Scanner

Primary discovery method.

Command:

```bash
arp -a
```

Collect:

* IP address
* MAC address
* hostname (if available)

Characteristics:

* lightweight
* no network flooding
* uses kernel ARP cache

---

### 3. Optional Ping Seeder

Purpose:

Populate ARP cache for silent devices.

Strategy:

Ping only IPs already observed by Phase 3.

Source:

```sql
SELECT DISTINCT remote_ip
FROM network_connections
WHERE remote_ip LIKE '192.168.%'
   OR remote_ip LIKE '10.%';
```

Command:

```bash
ping -c 1 <ip>
```

Rules:

* max 20 concurrent workers
* timeout 300 ms

This remains optional for MVP.

---

## Parser Layer

Component:

```text
MacArpParser
```

Input:

```bash
arp -a
```

Output DTO:

```java
LanDeviceRawDto
```

Fields:

* ipAddress
* macAddress
* hostname
* adapterName
* confidenceScore

---

## Persistence Layer

Separate persistence service.

Component:

```text
LanDevicePersistenceService
```

Responsibilities:

* Insert new LAN devices
* Update existing devices
* Track last seen timestamp
* Mark missing devices OFFLINE after consecutive scan misses

Must not update:

* network_connections
* remote_endpoints
* observed_processes

---

## Database Schema

### Table 1 – lan_devices

Fields:

* id
* ip_address
* mac_address
* hostname
* vendor
* status
* confidence
* first_seen_at
* last_seen_at

Status values:

* NEW
* ONLINE
* OFFLINE
* UNKNOWN

---

### Table 2 – network_scan_sessions

Fields:

* id
* subnet_cidr
* adapter_name
* devices_found
* scan_duration_ms
* started_at
* completed_at
* status

Status values:

* SUCCESS
* FAILED
* PARTIAL

---

## Scheduler Design

Polling interval:

```yaml
jtracer:
  polling:
    lan-scan-seconds: 60
```

Scheduler isolation required.

Current risk:

Spring uses single scheduler thread.

Mitigation:

Use ThreadPoolTaskScheduler.

Configuration:

```java
poolSize = 4
```

Dedicated threads:

* process collector
* network collector
* system health collector
* lan scanner

---

## Correlation With Phase 3

No direct schema coupling.

Relationship:

```text
network_connections.remote_ip
            =
lan_devices.ip_address
```

Query-time correlation only.

Example:

```sql
SELECT *
FROM network_connections nc
JOIN lan_devices ld
ON nc.remote_ip = ld.ip_address;
```

---

## Acceptance Criteria

Validation checklist:

### Automated Tests

```bash
mvn test
mvn test -Plive-tests
```

---

### Database Validation

```sql
SELECT COUNT(*) FROM lan_devices;
SELECT COUNT(*) FROM network_scan_sessions;
```

Expected:

Non-zero rows after 2 minutes.

---

### Device Detection

Expected:

Devices discovered:

* phones
* laptops
* routers
* printers
* TVs

---

### Offline Detection

Disconnect device.

Expected:

Status transitions:

ONLINE → OFFLINE

within two scan cycles.

---

### Scheduler Isolation

Verify logs continue normally during LAN scan.

Expected:

Process collector:

5 sec

Network collector:

3 sec

System health:

10 sec

No delay during LAN scan.

---

## Known Limitations

Current limitations:

* ARP only detects devices present in ARP cache
* Silent devices may not appear
* Ping sweep disabled in MVP
* No packet capture
* No device vendor resolution

Future phases:

Phase 5:

* Device identity engine
* MAC vendor lookup
* Friendly device names
* mDNS discovery

---

## Architecture Decision

Approved design:

* arp -a primary discovery
* 60 second polling
* separate LAN tables
* IP-based correlation with Phase 3
* no packet capture
* no nmap dependency

Phase 4 implementation approved.
