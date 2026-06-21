# MVP_ACCEPTANCE_CRITERIA.md

# JTracer MVP Acceptance Criteria

## 1. Purpose

This document defines the minimum functional requirements required for the first successful working prototype of JTracer.

The MVP is considered successful only when real system data is captured correctly and the application can answer the core user questions defined in PRODUCT_INTENT.md.

The MVP must prioritize reliable data collection over advanced visualization.

A beautiful dashboard with incorrect or fake data is considered failure.

---

## 2. MVP Philosophy

The first prototype must prove one thing.

JTracer can reliably observe local machine behavior and local network behavior using real data.

The MVP is not intended to be:

* Production software
* Security software
* Antivirus replacement
* Deep packet inspection tool
* Enterprise observability platform

The MVP must prioritize correctness and trustworthiness.

---

## 3. Core User Questions

The MVP succeeds only if it can answer these questions.

### Question 1

Why is my laptop slow?

### Question 2

Which processes are consuming high CPU or memory?

### Question 3

Which processes are making many network connections?

### Question 4

Which domains or IPs are applications talking to?

### Question 5

Which devices are connected to my home network?

### Question 6

Which devices are unknown or unclassified?

---

## 4. Mandatory Functional Workflows

The MVP must successfully complete these workflows.

---

### Workflow A — Process Observability

User opens dashboard.

System shows:

* Running processes
* CPU usage per process
* Memory usage per process
* Process name
* PID
* Process executable path

Success conditions:

* Top CPU processes are correctly identified
* Top memory processes are correctly identified
* Process list updates automatically
* Historical samples are stored

Pass criteria:

At least 95 percent of visible user processes must be detected.

---

### Workflow B — Network Observability

User selects a process.

System shows:

* Active outbound connections
* Remote IP addresses
* Remote ports
* Protocol
* Connection state
* Number of connections created by process

Success conditions:

* Process-to-network correlation works
* Connections update automatically
* Closed short-lived connections are captured when possible

Pass criteria:

At least 80 percent of active outbound connections are visible.

---

### Workflow C — Domain Visibility

User selects process network activity.

System shows:

Example:

Chrome → github.com → Port 443

Success conditions:

* Reverse DNS lookup works
* Domain names resolve when possible
* Unknown domains are clearly marked

Pass criteria:

At least 60 percent of public outbound IPs resolve to hostname.

---

### Workflow D — LAN Device Discovery

User opens network devices section.

System shows:

* Online devices
* Offline devices
* Device IP address
* MAC address when available
* Vendor when available
* Hostname when available

Success conditions:

LAN scan discovers active devices.

Pass criteria:

At least 80 percent of active LAN devices are detected.

---

### Workflow E — Device Intelligence

User views LAN devices.

System shows likely device identity.

Example:

* Apple iPhone
* Amazon Echo
* Fire TV
* Roku
* Smart TV
* Unknown Device

Success conditions:

Device classification engine works.

Pass criteria:

At least 50 percent of home devices receive probable classification.

Unknown devices must be shown instead of hidden.

---

### Workflow F — System Health Dashboard

User opens dashboard.

System shows:

* Total CPU usage
* Total memory usage
* Battery percentage
* Disk usage
* Active process count
* Active connection count

Success conditions:

Dashboard updates automatically.

Pass criteria:

All system metrics refresh continuously.

---

## 5. Data Integrity Requirements

MVP fails if any of these happen.

Not allowed:

* Fake process data in live mode
* Seed data mixed with live mode
* Old cached database rows reused incorrectly
* Hardcoded process names
* Demo timestamps shown in live mode

Pass criteria:

Live mode shows only real system data.

---

## 6. Performance Requirements

The MVP must remain lightweight.

Maximum CPU usage by JTracer:

```text
Less than 5 percent average CPU
```

Maximum memory usage:

```text
Less than 300 MB
```

LAN scan execution:

```text
Under 10 seconds
```

Dashboard refresh:

```text
Under 3 seconds
```

---

## 7. Privacy Requirements

The MVP must never capture:

* Passwords
* Cookies
* Authorization headers
* HTTPS decrypted traffic
* Request body
* Response body
* Packet payloads

Pass criteria:

Metadata only.

---

## 8. MVP Technical Requirements

The MVP must support:

* Local-only execution
* Cross-platform architecture design
* Separate platform adapters
* Background collectors
* Persistent local storage
* Historical snapshots

The MVP must not require:

* Cloud services
* User accounts
* Administrator privileges by default

---

## 9. Required Working APIs

These APIs must work correctly.

```text
GET /api/system/health
GET /api/processes
GET /api/processes/{id}
GET /api/processes/{id}/metrics
GET /api/processes/{id}/connections
GET /api/connections
GET /api/devices
GET /api/system/snapshots
GET /api/insights
```

Pass criteria:

All APIs return valid live data.

---

## 10. UI Requirements

The MVP UI must show:

Section 1

System Health

Section 2

Top CPU Processes

Section 3

Top Memory Processes

Section 4

Process Network Connections

Section 5

Remote Domains

Section 6

LAN Devices

Section 7

Basic Insights

The UI does not need advanced animations.

Correct data is more important than design.

---

## 11. Manual Validation Tests

The MVP must pass these manual tests.

Test 1

Open multiple browser tabs.

Expected:

New outbound connections appear.

Test 2

Run heavy process.

Expected:

High CPU process appears.

Test 3

Open terminal and run curl command.

Expected:

New process network connection appears.

Test 4

Connect phone to WiFi.

Expected:

New LAN device appears.

Test 5

Disconnect TV or smart speaker.

Expected:

Device status changes offline.

Test 6

Close heavy application.

Expected:

CPU usage drops.

---

## 12. MVP Failure Conditions

The MVP is considered failure if:

* UI works but live data is fake
* Network connections cannot be traced to processes
* Device discovery is unreliable
* Process metrics are incorrect
* Live and demo data mix together
* The app slows the machine significantly
* Unknown devices are silently ignored

---

## 13. MVP Success Statement

The MVP is successful when a user can open JTracer and answer within 10 seconds:

* Which application is slowing my laptop?
* Which processes are using CPU or memory?
* Which processes are making network requests?
* Which domains are applications talking to?
* Which devices are connected to the local network?
* Which devices should be investigated?

If these questions cannot be answered, the MVP is incomplete.
