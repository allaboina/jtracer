# Validation Assets

Add portfolio screenshots here before publishing to GitHub.

## Recommended captures

| File | Content |
|------|---------|
| `architecture.png` | Rendered Mermaid diagram from `docs/diagrams/system-design.mmd` |
| `process-collection.png` | Log line: `Persisted N process snapshots` |
| `network-collection.png` | Log line: `Persisted N network connections` |
| `sqlite-processes.png` | Terminal: `SELECT process_name, pid FROM observed_processes LIMIT 10` |
| `sqlite-connections.png` | Terminal: connection query with remote_ip, remote_port, state |
| `mvn-test.png` | `mvn test` passing |

## How to generate

```bash
# Render diagram: https://mermaid.live — paste docs/diagrams/system-design.mmd

# Run app (private workspace)
cd backend && mvn spring-boot:run

# Query DB
sqlite3 data/jtracer-live.db ".headers on" "SELECT process_name, pid, status FROM observed_processes LIMIT 8;"
```

Reference screenshots from README using:

```markdown
![Process validation](docs/assets/sqlite-processes.png)
```

Do **not** commit screenshots that contain real hostnames, usernames, or private IP layouts if you prefer anonymity — blur or use demo DB.
