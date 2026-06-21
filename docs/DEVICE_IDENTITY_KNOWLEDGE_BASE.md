# DEVICE_IDENTITY_KNOWLEDGE_BASE.md

# JTracer Device Identity Knowledge Base

## 1. Purpose

This document defines how JTracer identifies devices connected to the local network.

The goal is not to build a complete database of every device model in the world.

The goal is to intelligently infer likely device identity using multiple signals.

JTracer must classify devices into useful human-readable categories.

Examples:

* Apple iPhone
* Android Phone
* MacBook
* Windows Laptop
* Amazon Echo
* Fire TV
* Roku
* Apple TV
* Samsung Smart TV
* Sony Smart TV
* Printer
* Router
* Smart Camera
* Unknown Device

Device identity is a core differentiator of JTracer.

---

# 2. Philosophy

JTracer must not guess blindly.

Device identity must be based on explainable evidence.

JTracer must combine multiple identity signals.

The system must assign confidence to every classification.

Unknown devices are valid and should never be hidden.

User labels always override automatic classification.

---

# 3. Identity Resolution Pipeline

JTracer should resolve device identity in stages.

```text
Raw Device Discovery
      ↓
MAC Address
      ↓
OUI Vendor Lookup
      ↓
Hostname Analysis
      ↓
mDNS Service Analysis
      ↓
Rule Engine
      ↓
Confidence Scoring
      ↓
User Label Override
      ↓
Final Device Identity
```

Every stage improves confidence.

---

# 4. Identity Signals

JTracer will use these signals.

Priority order:

```text
User Label
mDNS Service Name
Hostname
MAC OUI Vendor
Historical Device Behavior
Known Device Rules
IP Range Patterns
Unknown Device
```

Higher priority signals override lower priority signals.

---

# 5. Signal 1 — MAC OUI Vendor Database

Every MAC address begins with an Organizationally Unique Identifier (OUI).

Example

```text
FC:A6:67:11:22:33
```

The prefix identifies the manufacturer.

Example

```text
FC:A6:67 → Amazon Technologies
A4:B1:C1 → Apple
B8:3E:59 → Roku
3C:5A:B4 → Google
```

Purpose

Determine manufacturer.

Example output

```text
Manufacturer = Amazon Technologies
Confidence = Medium
```

### Storage Format

Local JSON file.

Example

```json
{
  "FC:A6:67": {
    "vendor": "Amazon Technologies"
  },

  "A4:B1:C1": {
    "vendor": "Apple Inc."
  },

  "B8:3E:59": {
    "vendor": "Roku Inc."
  }
}
```

### Rules

Vendor alone is not enough to determine exact device type.

Example

```text
Apple can mean:

iPhone
MacBook
Apple TV
iPad
Apple Watch
```

---

# 6. Signal 2 — Hostname Analysis

Many devices expose hostnames.

Examples

```text
johns-iphone.local
apple-tv-bedroom
roku-living-room
echo-bedroom
hp-printer-office
sony-bravia-tv
macbook-pro-dilip
```

Purpose

Extract device clues from hostname patterns.

Example

```text
Hostname = echo-bedroom.local

Contains "echo"

Likely = Amazon Echo
```

### Storage Format

Pattern matching rules.

Example

```json
{
  "hostnameContains": "iphone",
  "deviceType": "PHONE",
  "displayName": "Apple iPhone",
  "confidence": 80
}
```

---

# 7. Signal 3 — mDNS Service Discovery

Many smart devices advertise services.

Example protocols

```text
_airplay._tcp
_googlecast._tcp
_amzn-wplay._tcp
_printer._tcp
_raop._tcp
_ipp._tcp
_homekit._tcp
```

Examples

```text
_airplay._tcp → Apple TV or Apple device
_googlecast._tcp → Chromecast device
_amzn-wplay._tcp → Amazon smart device
_ipp._tcp → Printer
```

Purpose

Detect smart devices through advertised services.

Example output

```text
mDNS Service = _googlecast._tcp

Likely Device = Chromecast

Confidence = High
```

### Rules

mDNS has high confidence.

If mDNS exists, prioritize heavily.

---

# 8. Signal 4 — Vendor and Hostname Combination Rules

Combine multiple signals.

Example

```text
Vendor = Amazon Technologies
Hostname = echo-bedroom.local
```

Inference

```text
Amazon Echo
Confidence = High
```

Example

```text
Vendor = Apple
Hostname = iphone-bedroom
```

Inference

```text
Apple iPhone
Confidence = High
```

Example

```text
Vendor = Apple
Hostname unknown
```

Inference

```text
Apple Device
Confidence = Medium
```

Multiple signals increase confidence.

---

# 9. Signal 5 — Historical Device Behavior

Device behavior provides clues.

Examples

```text
Always online
Uses low bandwidth
Never changes IP
```

Possible:

```text
Router
Smart Speaker
Smart Camera
```

Example

```text
Uses high streaming bandwidth at night
```

Possible:

```text
TV
Streaming Device
```

Example

```text
Frequently disconnects and reconnects
```

Possible:

```text
Mobile Phone
Tablet
Laptop
```

### Status

Future phase.

Not MVP.

---

# 10. Signal 6 — User Label Override

Users may manually label devices.

Example

```text
192.168.1.15 = Living Room Fire TV
```

Example

```text
A4:B1:C2:11:22:33 = Son iPad
```

User labels always override automatic classification.

### Example

Automatic classification

```text
Apple Device
```

User label

```text
Apple TV Living Room
```

Final output

```text
Apple TV Living Room
```

Confidence becomes:

```text
CONFIRMED
```

---

# 11. Device Categories

Approved categories.

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
GAME_CONSOLE
SMART_HOME
WATCH
IOT
UNKNOWN
```

Rules:

Never invent categories outside this list without approval.

---

# 12. Known Device Families

Initial knowledge base.

---

## Apple Devices

Examples

```text
iPhone
iPad
MacBook
Apple TV
Apple Watch
HomePod
AirPort Router
iMac
Mac Mini
Mac Studio
```

Signals

```text
Vendor = Apple
Hostname contains iphone
mDNS = airplay
mDNS = raop
```

---

## Amazon Devices

Examples

```text
Amazon Echo
Echo Dot
Echo Show
Fire TV
Fire TV Stick
Kindle
Amazon Smart Plug
Ring Camera
```

Signals

```text
Vendor = Amazon Technologies
Hostname contains echo
Hostname contains firetv
mDNS contains amzn
```

---

## Google Devices

Examples

```text
Chromecast
Google Home
Google Nest
Nest Camera
Pixel Phone
```

Signals

```text
Vendor = Google
mDNS contains googlecast
Hostname contains nest
```

---

## Roku Devices

Examples

```text
Roku TV
Roku Streaming Stick
Roku Box
```

Signals

```text
Vendor = Roku
Hostname contains roku
```

---

## Samsung Devices

Examples

```text
Samsung TV
Samsung Phone
Samsung Tablet
Samsung Refrigerator
Samsung Smart Home Device
```

Signals

```text
Vendor = Samsung
Hostname contains samsung
```

---

## Sony Devices

Examples

```text
Sony TV
PlayStation
Sony Camera
Sony Speaker
```

Signals

```text
Vendor = Sony
Hostname contains sony
```

---

## Microsoft Devices

Examples

```text
Windows Laptop
Xbox
Surface Tablet
Surface Laptop
```

Signals

```text
Vendor = Microsoft
Hostname contains xbox
Hostname contains surface
```

---

## HP Devices

Examples

```text
HP Laptop
HP Printer
HP Desktop
```

Signals

```text
Vendor = Hewlett Packard
Hostname contains hp
mDNS printer services
```

---

## Dell Devices

Examples

```text
Dell Laptop
Dell Desktop
Dell Server
Alienware PC
```

Signals

```text
Vendor = Dell
Hostname contains dell
```

---

## Lenovo Devices

Examples

```text
ThinkPad
Lenovo Laptop
Lenovo Desktop
Motorola Device
```

Signals

```text
Vendor = Lenovo
Hostname contains thinkpad
Hostname contains lenovo
```

---

# 13. Device Identity Rule File Structure

Store rules locally.

Format:

```text
JSON or YAML
```

Example

```json
{
  "ruleName": "Amazon Echo Rule",

  "vendorContains": ["Amazon"],

  "hostnameContains": [
    "echo",
    "alexa"
  ],

  "mdnsContains": [
    "_amzn-wplay"
  ],

  "deviceType": "SMART_SPEAKER",

  "displayName": "Amazon Echo",

  "confidence": 90
}
```

---

# 14. Confidence Scoring

Confidence score range

```text
0 to 100
```

Rules

```text
User label = 100

mDNS exact match = 90

Vendor + hostname = 85

Hostname only = 70

Vendor only = 50

Historical behavior = 40

Unknown = 0
```

Example

```text
Vendor = Apple
Hostname = iphone-bedroom
```

Result

```text
Apple iPhone

Confidence = 85
```

---

# 15. Local Knowledge Base Files

Store in repository.

```text
knowledge-base/

oui-vendors.json

device-rules.json

mdns-services.json

known-manufacturers.json
```

---

# 16. Online Knowledge Base Updates Future

Future versions may fetch updates online.

Possible sources

* IEEE OUI vendor database
* Public mDNS service lists
* Public device fingerprint databases

Update strategy

```text
Fetch update
Validate signatures
Update local files
Use new rules
```

But MVP remains local only.

---

# 17. Forbidden Approaches

Not allowed.

Forbidden

```text
Build giant database of every product model

Store millions of devices

Depend on cloud API for every lookup

Blindly guess device identity

Hide unknown devices

Treat vendor as exact device model
```

Reason

Not scalable for MVP.

---

# 18. Success Criteria

Device identity engine is successful when:

```text
Amazon Echo → recognized

Apple TV → recognized

Fire TV → recognized

Roku → recognized

iPhone → recognized

MacBook → recognized

Printer → recognized

Router → recognized

Unknown device → visible

User can override incorrect classification
```

The system must explain WHY it reached its conclusion.

Example

```text
Amazon Echo

Confidence = 88%

Evidence:

Vendor = Amazon Technologies

Hostname contains echo

mDNS service = _amzn-wplay
```

Every classification must be explainable.
