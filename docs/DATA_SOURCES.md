# DATA_SOURCES.md

# JTracer Data Sources

## 1. Purpose

This document defines the approved data sources for JTracer.

JTracer must collect system, process, network, and device information from real local sources. The application must not invent live data or mix demo data with live data.

The goal is to create a trustworthy local observability tool that helps answer:

* Why is my laptop slow?
* Which process is consuming CPU or memory?
* Which process is making too many network connections?
* Which domains or IPs are applications talking to?
* Which devices are connected to my home network?
* Which devices are unknown or newly discovered?

---

## 2. Data Source Principles

JTracer must follow these principles.

### Local First

All data sources must run locally on the user’s machine.

### Metadata First

Collect safe metadata first:

* Process name
* PID
* CPU usage
* Memory usage
* Local IP and port
* Remote IP and port
* Protocol
* Connection state
* Domain or hostname when available
* LAN device IP, MAC, vendor, and hostname when available

### No Deep Inspection in MVP

The first prototype must not collect:

* Request body
* Response body
* Cookies
* Passwords
* Authorization headers
* Decrypted HTTPS payloads
* Full packet payloads

### No Silent Privilege Escalation

JTracer must not automatically run:

* sudo
* administrator elevation
* packet capture requiring root
* system-wide spying tools

### Real Data Only in Live Mode

Live mode must use real system sources only.

Demo or simulator data must be isolated and clearly marked as DEMO or SIMULATOR.

---

## 3. Data Source Categories

JTracer uses these source categories:

1. Host machine information
2. Process information
3. Process resource metrics
4. Outbound network connections
5. Domain and hostname resolution
6. LAN device discovery
7. Device identity enrichment
8. System health information
9. Future security and reputation enrichment

---

# 4. macOS Data Sources

macOS is the first implementation platform.

## 4.1 Process List and Process Identity

### Source

```bash
ps
```

### Example Commands

```bash
ps -axo pid=,ppid=,pcpu=,pmem=,rss=,comm=,command=
```

### Provides

* PID
* Parent PID
* Process name
* Command path
* CPU percentage
* Memory percentage
* RSS memory
* Command line

### Maps To Entities

* ObservedProcess
* ProcessMetricSample

### Limitations

* Some system processes may show limited detail.
* Process names can be truncated.
* PID values can be reused by the operating system.
* One snapshot does not represent long-term behavior.

### Rules

* Store process samples as time-series data.
* Do not overwrite old process metric samples.
* Use sessionId + pid + time window for stable identity.

---

## 4.2 Outbound Network Connections

### Source

```bash
lsof
```

### Example Commands

```bash
lsof -nP -iTCP -iUDP
```

Optional TCP state filtering may be added later.

### Provides

* Process name
* PID
* Protocol
* Local IP
* Local port
* Remote IP
* Remote port
* TCP state

### Maps To Entities

* NetworkConnection
* ObservedProcess
* RemoteEndpoint

### Limitations

* Polling may miss very short-lived connections.
* Without sudo, visibility may be limited to the current user’s processes.
* Does not provide HTTP method, URL path, status code, request body, response body, or headers.
* Does not provide accurate byte counts.
* HTTPS traffic remains encrypted.

### Rules

* Use this as the primary macOS MVP network source.
* Do not claim API-level visibility from lsof alone.
* Show uncertainty when domain or process mapping is incomplete.
* Closed connections are expected and valid.

---

## 4.3 Bandwidth Usage Per Process

### Source

```bash
nettop
```

### Example Commands

```bash
nettop -x -P -L 1 -n
```

### Provides

* Process-level network bytes in
* Process-level network bytes out
* Process network activity summary

### Maps To Entities

* ProcessMetricSample
* NetworkConnection
* SystemHealthSnapshot

### Status

Future Phase 2 source.

### Limitations

* Output parsing may be unstable across macOS versions.
* Mapping exact byte counts to a specific connection may not always be reliable.
* Should first be used for process-level network totals.

### Rules

* Use nettop for bandwidth trend enrichment only after lsof and ps are reliable.
* Do not block MVP on nettop.

---

## 4.4 Disk and Battery Information

### Sources

```bash
df
pmset
```

### Example Commands

```bash
df -h /
pmset -g batt
```

### Provides

* Disk usage
* Battery percentage
* Charging status

### Maps To Entities

* SystemHealthSnapshot

### Limitations

* Battery is not available on desktops.
* pmset output may vary.
* Running battery command too frequently may be inefficient.

### Rules

* Cache battery status for a short interval.
* Store periodic snapshots for trend analysis later.

---

## 4.5 LAN Device Discovery

### Sources

```bash
ping
arp
```

### Example Commands

```bash
ping -c 1 -W 500 <ip>
arp -a
```

### Provides

* Online IP addresses
* MAC addresses when ARP cache is populated
* Hostname when ARP output includes it
* Local network presence

### Maps To Entities

* NetworkScanSession
* LanDevice
* DeviceIdentity

### Limitations

* Some devices block ping.
* ARP cache may not contain all devices.
* MAC address may be missing.
* Device may be online but not responding.
* Vendor does not prove exact device type.

### Rules

* Use ping sweep to populate ARP cache.
* Use ARP results for MAC lookup.
* Treat missing MAC as lower-confidence identity.
* Show unknown devices instead of hiding them.

---

## 4.6 Hostname and mDNS Resolution

### Sources

```text
InetAddress reverse lookup
mDNS / Bonjour lookup
local hostname resolution
```

### Provides

* Hostname
* Local device name
* Possible friendly device clues

### Maps To Entities

* DomainIdentity
* LanDevice
* DeviceIdentity

### Limitations

* Reverse DNS may be inaccurate.
* mDNS names may not exist.
* Some devices do not expose friendly names.
* Lookup may timeout.

### Rules

* Use timeouts.
* Store confidence level.
* Do not treat hostname as guaranteed truth.

---

# 5. Windows Data Sources

Windows support is a future goal. The architecture must be prepared for it.

## 5.1 Process List and Resource Metrics

### Possible Sources

```powershell
Get-Process
Get-CimInstance Win32_Process
```

### Example Commands

```powershell
Get-Process
Get-CimInstance Win32_Process
```

### Provides

* Process ID
* Process name
* CPU time
* Memory usage
* Executable path
* Command line

### Maps To Entities

* ObservedProcess
* ProcessMetricSample

### Limitations

* Some fields may require elevated permissions.
* CPU percentage may require calculating deltas over time.
* Command line may not always be visible.

### Rules

* Implement Windows as a separate platform adapter.
* Do not modify common domain entities for Windows-specific fields.
* Normalize Windows data into the same entity model.

---

## 5.2 Windows Network Connections

### Possible Sources

```powershell
Get-NetTCPConnection
netstat
```

### Example Commands

```powershell
Get-NetTCPConnection
netstat -ano
```

### Provides

* Local IP
* Local port
* Remote IP
* Remote port
* TCP state
* Owning process ID

### Maps To Entities

* NetworkConnection
* ObservedProcess
* RemoteEndpoint

### Limitations

* UDP process mapping may need additional handling.
* Some visibility may require administrator permission.
* Domain names are not provided directly.

### Rules

* Start with Get-NetTCPConnection or netstat.
* Join owning process ID to process table.
* Keep Windows implementation isolated in capture/windows.

---

## 5.3 Windows LAN Discovery

### Possible Sources

```powershell
Test-Connection
arp -a
Get-NetNeighbor
```

### Provides

* Reachable IP addresses
* ARP neighbor entries
* MAC addresses
* Interface information

### Maps To Entities

* NetworkScanSession
* LanDevice
* DeviceIdentity

### Rules

* Implement after macOS scanner is stable.
* Do not require administrator permission for MVP if possible.
* Use same DeviceIdentityRule system across platforms.

---

# 6. Linux Data Sources

Linux support is future scope but must be considered in design.

## 6.1 Process Metrics

### Possible Sources

```bash
ps
/proc
```

### Example Commands

```bash
ps -eo pid,ppid,pcpu,pmem,rss,comm,args
```

### Provides

* PID
* Parent PID
* CPU usage
* Memory usage
* RSS
* Command line

### Maps To Entities

* ObservedProcess
* ProcessMetricSample

---

## 6.2 Linux Network Connections

### Possible Sources

```bash
ss
netstat
lsof
/proc/net/tcp
/proc/net/udp
```

### Example Commands

```bash
ss -tunap
lsof -nP -iTCP -iUDP
```

### Provides

* Local IP
* Local port
* Remote IP
* Remote port
* Protocol
* State
* Process mapping when available

### Maps To Entities

* NetworkConnection
* ObservedProcess
* RemoteEndpoint

### Rules

* Prefer ss for Linux future adapter.
* Avoid requiring root for first Linux prototype.

---

## 6.3 Linux LAN Discovery

### Possible Sources

```bash
ping
ip neigh
arp
```

### Provides

* Online IPs
* Neighbor table
* MAC addresses
* Device presence

### Maps To Entities

* NetworkScanSession
* LanDevice
* DeviceIdentity

---

# 7. Device Identity Data Sources

Device identity is a core feature of JTracer.

## 7.1 MAC OUI Vendor Database

### Source

Bundled local JSON or YAML file.

### Example

```json
{
  "A4:B1:C1": "Apple Inc.",
  "FC:A6:67": "Amazon Technologies Inc.",
  "B8:3E:59": "Roku, Inc."
}
```

### Provides

* Vendor name from MAC prefix

### Maps To Entities

* LanDevice
* DeviceIdentity

### Limitations

* Vendor does not prove device type.
* Many devices use randomized MAC addresses.
* Some vendors manufacture multiple device categories.

### Rules

* Use vendor as one signal only.
* Never classify exact model using OUI alone.
* Maintain confidence level.

---

## 7.2 Device Identity Rules

### Source

Bundled local JSON or YAML file.

### Example

```json
{
  "ruleName": "Amazon Echo hostname rule",
  "vendorContains": "Amazon",
  "hostnameContains": ["echo", "alexa"],
  "deviceType": "SMART_SPEAKER",
  "displayName": "Amazon Echo",
  "confidence": "LIKELY"
}
```

### Provides

* Friendly device classification
* Device type
* Confidence score
* Explainable evidence

### Maps To Entities

* DeviceIdentityRule
* DeviceIdentity
* LanDevice

### Rules

* Rules must be local.
* Rules must be explainable.
* User label overrides rule-based identity.
* Bad rules must be disableable.

---

## 7.3 User Labels

### Source

User input from JTracer UI.

### Example

```text
A4:B1:C1:22:33:44 = Living Room Apple TV
```

### Provides

* Confirmed friendly names
* Strong identity signal

### Maps To Entities

* UserDeviceLabel
* DeviceIdentity
* LanDevice

### Rules

* User label has highest priority.
* Persist user label across IP changes when MAC is available.
* Allow user to edit or remove labels.

---

# 8. Domain and Endpoint Data Sources

## 8.1 Reverse DNS

### Source

Operating system DNS resolver.

### Provides

* Possible hostname for remote IP

### Maps To Entities

* DomainIdentity
* RemoteEndpoint

### Limitations

* Can be inaccurate.
* May reveal lookup activity to DNS infrastructure.
* CDN IPs may map to generic names.

### Rules

* Store confidence as LOW or MEDIUM unless verified by stronger source.
* Use timeout.
* Do not block capture loop on DNS.

---

## 8.2 Future HTTP Proxy Metadata

### Status

Future only.

### Provides

* HTTP method
* Host
* Path
* Status code
* Duration

### Maps To Entities

* Future HttpEvent
* DomainIdentity
* NetworkConnection

### Rules

* Not part of first prototype.
* Must require explicit user consent.
* Must include redaction before persistence.
* Must not store bodies by default.

---

# 9. Data Source to Entity Mapping

| Data Source                  | Entity                                             |
| ---------------------------- | -------------------------------------------------- |
| ps                           | ObservedProcess, ProcessMetricSample               |
| lsof                         | NetworkConnection, ObservedProcess, RemoteEndpoint |
| nettop                       | ProcessMetricSample, SystemHealthSnapshot          |
| df                           | SystemHealthSnapshot                               |
| pmset                        | SystemHealthSnapshot                               |
| ping                         | NetworkScanSession, LanDevice                      |
| arp                          | LanDevice, DeviceIdentity                          |
| mDNS / hostname lookup       | DomainIdentity, DeviceIdentity                     |
| OUI vendor JSON              | DeviceIdentity, LanDevice                          |
| Device identity rules JSON   | DeviceIdentityRule, DeviceIdentity                 |
| User labels                  | UserDeviceLabel, DeviceIdentity                    |
| Windows Get-Process          | ObservedProcess, ProcessMetricSample               |
| Windows Get-NetTCPConnection | NetworkConnection                                  |
| Linux ps / proc              | ObservedProcess, ProcessMetricSample               |
| Linux ss / lsof              | NetworkConnection                                  |

---

# 10. Data Quality and Confidence

Every uncertain data point should carry confidence.

## Confidence Levels

```text
HIGH
MEDIUM
LOW
UNKNOWN
```

## Examples

| Data                                    | Confidence      |
| --------------------------------------- | --------------- |
| PID from lsof                           | HIGH            |
| Process name from ps                    | HIGH            |
| Domain from reverse DNS                 | LOW or MEDIUM   |
| Device type from user label             | HIGH            |
| Device vendor from MAC OUI              | MEDIUM          |
| Exact device model from vendor only     | UNKNOWN         |
| Device type from hostname + vendor rule | LIKELY / MEDIUM |

---

# 11. Polling and Sampling Rules

## Process Metrics

Initial interval:

```text
3 seconds to 5 seconds
```

## Network Connections

Initial interval:

```text
2 seconds to 3 seconds
```

## LAN Scanner

Initial interval:

```text
60 seconds
```

## System Health

Initial interval:

```text
10 seconds to 30 seconds
```

## Rules

* Do not overload the machine.
* Do not scan LAN too frequently.
* Do not block UI on slow scan.
* Use background workers.
* Store timestamps for every sample.

---

# 12. Live Data vs Demo Data

JTracer must strictly separate live data from demo data.

## Live Mode

Allowed:

* ps
* lsof
* ping
* arp
* hostname lookup
* local JSON rules

Not allowed:

* Hardcoded fake connections
* Hardcoded fake processes
* Demo timestamps
* Simulator rows

## Demo Mode

Allowed:

* Seed data
* Simulator data
* Fake process names
* Fake devices

Required:

* Demo data must be clearly marked.
* Demo database must not be reused as live database.
* UI must show DEMO mode clearly.

---

# 13. Privacy Rules by Data Source

| Source         | Privacy Risk              | Rule                          |
| -------------- | ------------------------- | ----------------------------- |
| ps             | Shows local processes     | Store only required fields    |
| lsof           | Shows connection metadata | No payloads                   |
| nettop         | Shows bandwidth           | Process-level only initially  |
| arp            | Shows local device MACs   | Local only                    |
| reverse DNS    | May trigger DNS lookup    | Use timeout and document      |
| HTTP proxy     | Can expose sensitive info | Future only, explicit consent |
| packet capture | High risk                 | Out of scope for MVP          |

---

# 14. Platform Adapter Design

Each platform must implement common interfaces.

## Process Adapter

```text
ProcessSnapshotProvider
  collectProcesses(): List<ObservedProcessSnapshot>
```

## Process Metrics Adapter

```text
ProcessMetricProvider
  collectMetrics(): List<ProcessMetricSnapshot>
```

## Network Adapter

```text
NetworkConnectionProvider
  collectConnections(): List<ConnectionSnapshot>
```

## LAN Scanner Adapter

```text
LanDiscoveryProvider
  scanLocalNetwork(): NetworkScanResult
```

## Identity Adapter

```text
DeviceIdentityResolver
  resolveIdentity(LanDeviceRaw): DeviceIdentityResult
```

Rules:

* Platform adapters collect raw data.
* Core services normalize raw data.
* UI must not know whether data came from macOS, Windows, or Linux.

---

# 15. First Prototype Approved Sources

For the first working prototype, use only these sources.

## macOS

```text
ps
lsof
ping
arp -a
hostname / mDNS lookup
df
pmset
local OUI JSON
local device identity rules JSON
user labels
```

## Not Yet

```text
tcpdump
Wireshark
packet capture
HTTPS MITM
browser extension
kernel extension
system extension
cloud threat intelligence
remote telemetry
```

---

# 16. Success Criteria

The data source design is successful when JTracer can reliably show:

1. Top CPU processes
2. Top memory processes
3. Process metric history
4. Active and recently closed outbound connections
5. Process-to-remote-IP mapping
6. Process-to-domain mapping when available
7. LAN devices with IP and status
8. LAN devices with MAC when available
9. LAN devices with vendor when available
10. Device identity with confidence level
11. Unknown devices clearly displayed
12. Basic insights supported by evidence

If a data source cannot support these outputs, it must be marked as future or unreliable.
