# PRODUCT_INTENT.md

# JTracer Product Intent

## 1. Product Vision

JTracer is a local personal observability tool for understanding what is happening inside a computer and across the user’s home network.

The product combines three capabilities in one dashboard:

1. Personal laptop observability
2. Home network monitoring
3. Developer network debugging

Security monitoring is a future extension after the core monitoring foundation becomes reliable.

JTracer is initially built as a personal tool for the developer’s own use. If the prototype becomes stable and useful, it may later be shared with home users, developers, and technical power users who want a simple way to understand system performance, application network behavior, and connected home devices.

---

## 2. Core Problem

Modern laptops and home networks are difficult to understand from one place.

A user may experience:

* Laptop slowness
* High memory usage
* Battery drain
* Too many background applications
* Unknown network activity
* Too many API calls from browsers, terminals, or development tools
* Slow home internet caused by many connected devices
* Unknown or suspicious devices connected to WiFi

Existing tools solve only parts of this problem:

* Activity Monitor and Task Manager show system resource usage
* Browser DevTools shows browser-level network requests
* WiFi router apps show connected devices
* Network tools show technical socket details
* Security tools focus on protection but may be expensive or heavy

JTracer’s goal is to combine these views into one simple local dashboard.

---

## 3. Primary Questions JTracer Must Answer

The first prototype must answer these questions clearly.

### Priority 1

Why is my laptop slow?

The application must help identify which applications or processes are consuming high CPU, memory, disk, battery, or network resources.

### Priority 2

Which processes are making too many network requests?

The application must show which processes are creating repeated outbound connections and where those connections are going.

### Priority 3

Which websites, domains, or remote servers are my applications talking to?

The application must show process-to-domain and process-to-IP relationships in a simple investigation-friendly format.

### Priority 4

Which devices are connected to my home network?

The application must discover local network devices such as phones, laptops, smart TVs, Fire TV, Roku, Apple TV, Amazon Echo, printers, routers, cameras, and other smart appliances.

### Priority 5

What should I close or investigate?

The application should eventually summarize which applications may be slowing the system, draining battery, creating too many connections, or behaving unusually.

---

## 4. Product Scope for First Prototype

The first prototype should stay lightweight and focus on reliable visibility.

### In Scope

* Local process monitoring
* CPU usage by process
* Memory usage by process
* Basic disk usage and battery status
* Outbound network connection metadata
* Process-to-domain mapping
* Process-to-IP mapping
* Local network device discovery
* Device identity classification
* Simple summaries and alerts
* Historical snapshots for basic trend analysis
* Local-only dashboard

### Out of Scope for First Prototype

* HTTPS decryption
* Packet payload capture
* Request and response body capture
* Enterprise monitoring
* Cloud-based telemetry
* Antivirus replacement
* Full malware detection
* Multi-user administration
* Remote dashboard access

---

## 5. Network Inspection Boundary

JTracer must begin with Level 1 network inspection.

### Level 1 — Safe Metadata Mode

Example:

Chrome → api.github.com → Port 443

This includes:

* Process name
* Process ID
* Local IP and port
* Remote IP and port
* Protocol
* Connection state
* Best-effort domain name
* Timestamp
* Frequency of connections

JTracer must not capture request bodies, response bodies, passwords, cookies, or decrypted HTTPS traffic in the first prototype.

Developer mode with HTTP method, URL path, and response status may be considered later only after the Level 1 foundation is reliable.

---

## 6. User Profile

The first user is the developer building the tool.

Initial user type:

* Technical user
* Developer
* Cloud or DevOps learner
* Home user with technical curiosity
* Power user who wants better system visibility

Future user type:

* Home users who want to understand connected devices
* Developers debugging local applications
* Users who want a cheaper and simpler monitoring tool compared to heavy commercial products
* Users who want to understand system performance without installing a full security suite

---

## 7. Product Identity

JTracer is not only a system monitor.

JTracer is not only a network scanner.

JTracer is not only a developer debugging tool.

JTracer is a combined local observability dashboard that connects these areas:

* System performance
* Application processes
* Outbound network behavior
* Home network devices
* Simple user-friendly insights

The product should help the user connect the dots between:

* A slow laptop
* A high-memory process
* A process making many network calls
* A domain or IP being contacted
* A local device or external server involved
* A recommendation or alert explaining what may need attention

---

## 8. Device Intelligence as a Core Feature

Device identity is a core differentiator for JTracer.

The application should maintain a local device identity knowledge base that helps classify connected devices.

Example device categories:

* iPhone
* Android phone
* MacBook
* Windows laptop
* iPad
* Apple TV
* Fire TV
* Roku
* Amazon Echo
* Google Nest
* Smart TV
* Samsung TV
* Sony TV
* LG TV
* Printer
* Router
* Smart camera
* Smart bulb
* Unknown device

The system may use:

* MAC OUI prefix
* Vendor name
* Hostname
* mDNS name
* IP behavior
* User-defined labels
* Device history

Device classification must include confidence levels:

* Confirmed
* Likely
* Unknown

Vendor name alone must not be treated as guaranteed device type.

---

## 9. Cross-Platform Direction

JTracer should eventually support:

* macOS
* Windows
* Linux

The first implementation may begin with macOS because development and testing are currently happening on Mac.

The architecture must avoid hardcoding macOS logic into the core product model.

Platform-specific capture logic should be isolated behind adapters.

Example:

capture/macos
capture/windows
capture/linux
capture/common

The user interface and domain model should remain platform-independent.

---

## 10. Observability Platform Direction

JTracer should eventually behave like a lightweight personal observability platform.

Future capabilities may include:

* CPU trend graphs
* Memory trend graphs
* Disk usage trend graphs
* Battery impact summaries
* Network request frequency graphs
* Bandwidth usage per process
* Device online and offline history
* Alerts for high resource usage
* Alerts for unknown devices
* Alerts for unusual network behavior
* Daily or hourly system summary

The first prototype should stay simple, but the entity model must support future trends and alerts.

---

## 11. Uniqueness

JTracer is unique because it combines multiple everyday troubleshooting tools into one local dashboard.

It brings together capabilities similar to:

* Activity Monitor
* Windows Task Manager
* Browser DevTools
* WiFi router connected-device view
* Basic network monitoring
* Lightweight observability dashboard

The product becomes valuable when it can answer:

* Which app is slowing my laptop?
* Which process is consuming memory?
* Which app is making too many connections?
* Which domain is this app talking to?
* Which devices are online at home?
* Is there an unknown device?
* Which app should I close now?
* What changed recently?

---

## 12. Design Principles

JTracer must follow these principles.

### Local First

All data should be processed locally on the user’s machine.

### Privacy First

No cloud upload, no telemetry, no account requirement, and no hidden data collection.

### Metadata First

Capture metadata before attempting deeper inspection.

### Explainable

Every summary or alert should show evidence.

Example:

Cursor is using high CPU because CPU usage stayed above 20% for 5 minutes and it made 120 outbound connections.

### Lightweight

The application should not become heavier than the problem it is solving.

### Cross Platform by Design

Core entities and APIs must be platform-independent.

### Investigation Friendly

The UI should help users move from symptom to root cause.

Example:

Laptop slow → high memory process → process details → network activity → recommendation.

---

## 13. First Prototype Success Criteria

The first successful prototype must demonstrate these workflows.

### Workflow 1 — Laptop Slowness

User opens dashboard and sees top CPU and memory consuming processes.

### Workflow 2 — Process Network Activity

User clicks a process and sees which domains and IPs it is connecting to.

### Workflow 3 — Home Network Devices

User sees current online and offline devices on the local network.

### Workflow 4 — Device Identity

User sees likely device names such as Apple iPhone, Amazon Echo, Roku, Fire TV, or Unknown Device.

### Workflow 5 — Basic Summary

User sees a simple summary such as:

Chrome is using high memory.
Cursor is using high CPU.
Node is making multiple outbound connections.
An unknown device appeared on the network.

---

## 14. Product Guardrails for AI Development

Future AI coding agents must not design the product only as a generic dashboard.

They must preserve the core intent:

JTracer is a local observability and home network awareness tool that helps users understand system slowness, process behavior, network connections, and connected devices from one place.

AI agents must not:

* Mix fake data with live data
* Build UI before entity design
* Add deep packet capture without explicit approval
* Add HTTPS decryption in the first prototype
* Add cloud services by default
* Ignore device identity
* Ignore process-to-network correlation
* Treat this as only a network scanner
* Treat this as only a CPU monitor

---

## 15. Long-Term Vision

The long-term vision is to make JTracer a practical, affordable, local-first observability dashboard for personal computers and home networks.

It should help users understand:

* What is running
* What is consuming resources
* What is talking to the network
* What devices are connected
* What changed
* What needs attention

Security monitoring may be added later after the core monitoring and observability foundation is mature.
