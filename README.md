# AI Learning Consistency Agent

Full-stack learning consistency app with AI-assisted study plans, progress tracking, adaptive recommendations, and reminders.

## 1) Complete Folder Structure

```text
learnsyncai/
├─ backend/
│  ├─ pom.xml
│  ├─ Dockerfile
│  └─ src/main/
│     ├─ java/com/learnsyncai/agent/
│     │  ├─ AiLearningConsistencyAgentApplication.java
│     │  ├─ config/
│     │  │  ├─ AppConfig.java
│     │  │  ├─ JwtAuthFilter.java
│     │  │  ├─ JwtService.java
│     │  │  └─ SecurityConfig.java
│     │  ├─ controller/
│     │  │  ├─ AuthController.java
│     │  │  ├─ PlanController.java
│     │  │  ├─ ProgressController.java
│     │  │  └─ ReminderController.java
│     │  ├─ dto/
│     │  ├─ exception/
│     │  ├─ model/
│     │  ├─ repository/
│     │  └─ service/
│     └─ resources/
│        └─ application.yml
├─ database/
│  ├─ schema.sql
│  └─ sample_data.sql
├─ frontend/
│  ├─ index.html
│  ├─ dashboard.html
│  ├─ study-plan.html
│  ├─ progress.html
│  ├─ css/styles.css
│  └─ js/
│     ├─ api.js
│     ├─ auth.js
│     ├─ dashboard.js
│     ├─ plan.js
│     └─ progress.js
├─ docker-compose.yml
└─ README.md
```

## 2) Backend (Spring Boot) Highlights

- Java 17, Spring Boot 3, MVC + REST architecture.
- JWT authentication with bcrypt password hashing.
- Layered structure:
  - `controller/` HTTP APIs
  - `service/` business logic, AI integration, reminder scheduler
  - `repository/` JPA access
  - `model/` entities
  - `dto/` request/response contracts
  - `config/` security and app configuration
- Validation and centralized exception handling included.

## 3) Frontend Highlights

- Responsive HTML/CSS/JS UI (mobile + desktop).
- Pages:
  - Login/Signup: `frontend/index.html`
  - Dashboard: `frontend/dashboard.html`
  - Study Plan: `frontend/study-plan.html`
  - Progress Tracker: `frontend/progress.html`
- Chart.js weekly progress visualization.
- Dark mode toggle included.

## 4) SQL Schema

- Schema file: `database/schema.sql`
- Sample test data: `database/sample_data.sql`
- Tables:
  - `users`
  - `study_plans`
  - `tasks`
  - `progress_logs`
  - `reminders`

## 5) API Endpoints

### Auth
- `POST /api/auth/signup`
- `POST /api/auth/login`

### Study Plan
- `POST /api/plan/generate`
- `GET /api/plan/latest`
- `GET /api/plan/adapt/weekly`

### Progress
- `PATCH /api/progress`
- `GET /api/progress/dashboard`

### Reminder
- `GET /api/reminder`
- `PUT /api/reminder`
- `POST /api/reminder/auto-adjust`

## 6) AI Integration

Implemented in `backend/src/main/java/com/learnsyncai/agent/service/AiService.java`.

Prompts implemented:
1. Study plan generation
2. Adaptive plan recommendation
3. Motivation message generation

Set your key with:

```bash
OPENAI_API_KEY=your_key_here
```

If key is missing/unavailable, fallback logic still generates usable plans/messages.

## 7) Run Instructions (Step-by-step)

### Option A: Local run

1. Start MySQL and create DB:
   - Run `database/schema.sql`
   - Optional: run `database/sample_data.sql`
   - Optional only: local quick start now defaults to embedded H2 if MySQL env vars are not set.
2. Configure backend env vars:
   - `JWT_SECRET`
   - `OPENAI_API_KEY` (optional but recommended)
   - `CORS_ALLOWED_ORIGIN_PATTERNS` (optional, default: `http://localhost:*`)
   - MySQL username/password in `application.yml` or env overrides
3. Run backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
4. Run frontend:
   - Serve `frontend/` with a static server (for example VS Code Live Server) at `http://localhost:5500` (recommended).
   - Direct `file://` access is also permitted by default CORS config (`null` origin), but a local server is preferred.
5. Open:
   - `http://localhost:5500/index.html`

### Option B: Docker run

```bash
docker compose up --build
```

- Backend: `http://localhost:8080`
- Frontend (served separately from local `frontend/` folder)

## 8) Sample Test Data

From `database/sample_data.sql`:
- Demo user: `demo@learnsync.ai`
- Password: `Password@123`
- Preloaded 1 study plan + tasks + progress logs + reminder settings.

## Bonus Implemented

- Docker setup (`backend/Dockerfile`, `docker-compose.yml`)
- Mobile responsive UI
- Dark mode support

## Notes

- Reminder engine runs on scheduler (`app.reminder.cron`, default 8 PM daily).
- Email reminders are sent if mail credentials are configured; otherwise logs are generated as fallback.
