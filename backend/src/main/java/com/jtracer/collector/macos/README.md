# macOS Collector Implementations (Private)

Host-native collectors (`MacPsCollector`, `MacLsofCollector`, platform providers) are
**not included in the public repository**.

The public repo includes:
- Parser layer (`parser/`)
- Domain model and DTOs
- Service and collector **interfaces**
- Flyway schema migrations

This separation keeps the portfolio repository focused on architecture and data modeling
while host execution logic remains in the private development workspace.
