# GitHub Public Release — Step-by-Step

Repository: **`jtracer`**  
Profile: [github.com/allaboina](https://github.com/allaboina)  
Remote: `https://github.com/allaboina/jtracer.git`

---

## Prerequisites

1. [GitHub CLI](https://cli.github.com/) installed and authenticated (`gh auth login`)
2. Public release script executed successfully
3. No local database files or personal paths in the output tree

---

## Step 1 — Build the public tree

From your **private** workspace:

```bash
cd /path/to/jtracev2
chmod +x scripts/prepare-public-release.sh
./scripts/prepare-public-release.sh
```

Default output: `../jtracer-observability-engine`

Verify sanitization:

```bash
grep -r "jskanda\|/Users/" ../jtracer-observability-engine || echo "OK: no personal paths"
find ../jtracer-observability-engine -name "*.db" -o -name "target" | head
# Should return nothing
```

---

## Step 2 — Create GitHub repository

Using GitHub CLI:

```bash
gh repo create allaboina/jtracer-observability-engine \
  --public \
  --description "Cross-layer endpoint observability: process, network, and LAN intelligence (local-first)" \
  --license mit
```

Or create manually at [github.com/new](https://github.com/new):
- Name: `jtracer-observability-engine`
- Visibility: **Public**
- Do **not** initialize with README (the script output includes one)

---

## Step 3 — Initialize git and first commit

```bash
cd ../jtracer-observability-engine

git init
git branch -M main

git add .
git status   # Review: no data/, target/, *.db, application-local.yml

git commit -m "$(cat <<'EOF'
Initial public release: observability engine architecture and domain model.

Publish system design, Flyway schema, JPA entities, collector parsers,
service interfaces, and phased development documentation. Host collector
implementations remain in private workspace per PUBLIC_RELEASE policy.
EOF
)"
```

---

## Step 4 — Connect remote and push

HTTPS:

```bash
git remote add origin https://github.com/allaboina/jtracer-observability-engine.git
git push -u origin main
```

SSH:

```bash
git remote add origin git@github.com:allaboina/jtracer-observability-engine.git
git push -u origin main
```

---

## Step 5 — Pin repository on GitHub profile

1. Go to [github.com/allaboina](https://github.com/allaboina)
2. **Customize your pins** → pin `jtracer-observability-engine`
3. Add topics: `observability`, `spring-boot`, `java`, `network-monitoring`, `systems-engineering`

---

## Step 6 — Optional profile README

Create a public profile repo `allaboina/allaboina` with `README.md`:

```markdown
## Hi, I'm [Your Name]

Backend / cloud engineer building local-first observability systems.

### Featured project
**[JTracer Observability Engine](https://github.com/allaboina/jtracer-observability-engine)** —
cross-layer endpoint monitoring (process → network → LAN) with Java 21 and Spring Boot.

### Stack
Java · Spring Boot · SQLite · macOS systems programming · React (planned)
```

---

## Updating the public repo later

After private development progresses:

```bash
cd /path/to/jtracev2
./scripts/prepare-public-release.sh

cd ../jtracer-observability-engine
git add .
git commit -m "Update public docs and domain model for Phase N"
git push
```

---

## Private workspace git (optional)

Keep full implementation under separate private repo:

```bash
cd /path/to/jtracev2
git init
git branch -M main
git add .
git commit -m "Private workspace: full JTracer implementation"

gh repo create allaboina/jtracer-private --private --source=. --remote=origin --push
```

Never push `data/`, `*.db`, or `application-local.yml` — `.gitignore` enforces this.

---

## Files never to commit (either repo)

| Pattern | Reason |
|---------|--------|
| `data/` | Local SQLite databases |
| `*.db`, `*.sqlite` | Runtime databases |
| `.env` | Secrets |
| `application-local.yml` | Machine-specific config |
| `target/` | Maven build output |
| `backend/target/surefire-reports/` | Contains local paths |
| `.idea/`, `.DS_Store` | IDE/OS noise |
