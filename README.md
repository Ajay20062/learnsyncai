# LearnSyncAI

Professional full-stack learning consistency platform with:
- JWT authentication
- AI-assisted study plan generation
- Progress tracking and dashboard analytics
- Adaptive reminder engine

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Security, Spring Data JPA
- Database: H2 (default local), MySQL 8 (docker/production-style)
- Frontend: HTML/CSS/JavaScript
- Tooling: Maven Wrapper, Docker Compose, GitHub Actions CI

## Project Structure

```text
learnsyncai/
├─ backend/
├─ database/
├─ frontend/
├─ scripts/
├─ docker-compose.yml
└─ README.md
```

## Quick Start (Recommended)

### 1) Start backend

```bash
cd backend
./mvnw spring-boot:run
```

Windows:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
cd backend
.\mvnw.cmd spring-boot:run
```

### 2) Start frontend static server

```bash
cd frontend
python -m http.server 5500
```

Open: `http://localhost:5500/index.html`

## One-command local dev

Windows:

```powershell
.\scripts\dev.ps1
```

Linux/macOS:

```bash
./scripts/dev.sh
```

## Configuration

Backend config is in `backend/src/main/resources/application.yml`.

Important environment variables:
- `JWT_SECRET`
- `OPENAI_API_KEY` (optional, fallback logic is built in)
- `CORS_ALLOWED_ORIGIN_PATTERNS`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Default local DB is embedded H2 for zero-setup startup.

## API Endpoints

Auth:
- `POST /api/auth/signup`
- `POST /api/auth/login`

Study plan:
- `POST /api/plan/generate`
- `GET /api/plan/latest`
- `GET /api/plan/adapt/weekly`

Progress:
- `PATCH /api/progress`
- `GET /api/progress/dashboard`

Reminder:
- `GET /api/reminder`
- `PUT /api/reminder`
- `POST /api/reminder/auto-adjust`

Health:
- `GET /actuator/health`
- `GET /actuator/info`

## CI/CD Quality Baseline

- Backend CI workflow runs on every push/PR to `main`
- Uses Java 17 and Maven wrapper
- Executes backend test suite

Workflow file:
- `.github/workflows/backend-ci.yml`

## Docker Setup

```bash
docker compose up --build
```

Services:
- MySQL: `localhost:3306`
- Backend: `localhost:8080`

## Frontend API Base Override

By default frontend uses:
- current host + `:8080/api` when served over `http/https`
- fallback `http://localhost:8080/api`

Optional overrides:
- `window.LEARNSYNCAI_API_BASE = "http://your-host:8080/api"`
- `localStorage.setItem("learnsync.apiBase", "http://your-host:8080/api")`
