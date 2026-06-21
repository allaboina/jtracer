# JTracer — Public Release Policy

This document defines what is published to the public GitHub repository
[`jtracer`](https://github.com/allaboina/jtracer)
versus what stays in the private development workspace.

---

## Repository strategy

| Workspace | Purpose |
|-----------|---------|
| **Private** (`jtracev2` local) | Full implementation, live tests, collectors, persistence, local DB |
| **Public** (`jtracer-observability-engine`) | Architecture, domain model, parsers, interfaces, documentation |

Build the public tree with:

```bash
chmod +x scripts/prepare-public-release.sh
./scripts/prepare-public-release.sh
```

---

## Included in public release

| Category | Examples |
|----------|----------|
| **Documentation** | `SYSTEM_DESIGN.md`, `DEVELOPMENT_PHASES.md`, `PHASE4_DESIGN.md`, API contract, entity design |
| **Domain model** | JPA entities, enums |
| **DTOs** | Collector snapshot DTOs |
| **Interfaces** | `ProcessSnapshotProvider`, `NetworkConnectionProvider`, `*Service` interfaces |
| **Parsers** | `PsOutputParser`, `LsofOutputParser`, `DfOutputParser`, `PmsetOutputParser` |
| **Parser tests** | Fixture-based unit tests (sanitized, no real usernames) |
| **Schema** | Flyway SQL migrations |
| **Configuration** | `JtracerProperties`, `application.yml.example` |
| **Utilities** | `EndpointClassifier` (IP classification logic) |

---

## Excluded from public release

| Category | Reason |
|----------|--------|
| `*ServiceImpl.java` | Persistence/coordination implementation detail |
| `MacPsCollector`, `MacLsofCollector`, `Mac*Provider` | Host command execution |
| `DefaultHostCommandExecutor`, `DefaultDomainResolver` | Runtime host integration |
| `CollectorCoordinatorServiceImpl` | Scheduler orchestration |
| `JTracerApplication.java` | Runnable entrypoint (private deploy) |
| Integration tests (`@Tag("live")`) | Require local machine + personal process data |
| `application.yml` (local copy) | Use `config/application.yml.example` |
| `data/`, `*.db`, logs | Runtime artifacts |
| `AI_AGENT_RULES.md` | Internal development tooling |
| `target/`, IDE files | Build artifacts |

---

## Sanitization checklist

Before any push, verify:

- [ ] No `*.db` or `data/` directory contents
- [ ] No `.env` or `application-local.yml`
- [ ] No `/Users/` paths in committed files
- [ ] Test fixtures use generic usernames (`devuser`, not real OS usernames)
- [ ] No surefire reports or `target/` directories
- [ ] Run `./scripts/prepare-public-release.sh` and push **only** the output tree

---

## What reviewers see

The public repository demonstrates:

1. **Systems thinking** — cross-layer observability (process → network → LAN)
2. **Domain-driven design** — normalized entities, confidence scoring, adapter pattern
3. **Engineering discipline** — phased delivery, Flyway migrations, parser tests
4. **Privacy posture** — metadata-only, no packet capture, localhost binding

Implementation depth is intentionally scoped. Full collector and persistence code
remains available for demo, interview walkthrough, or future open-source expansion.
