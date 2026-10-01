# Smart Expense Tracker — Backend (et-server)

Spring Boot 3.5 REST API backing the `ET-frontend` React app. Java 21, PostgreSQL,
JWT auth.

## Requirements

- Java 21
- Maven (use the wrapper: `.\mvnw.cmd` on Windows, `./mvnw` otherwise)
- PostgreSQL (tested on 18)

## Setup

1. Create the database:

```sql
CREATE DATABASE expensetrackerdb;
```

2. Copy `.env.example` to `.env` and fill in real values:

```
DB_URL=jdbc:postgresql://localhost:5432/expensetrackerdb
DB_USERNAME=your-username
DB_PASSWORD=your-password
DB_DDL_AUTO=update
JWT_SECRET=replace-with-a-256-bit-random-secret
JWT_EXPIRATION_MS=86400000
FRONTEND_URL=http://localhost:5173
```

`.env` is loaded at startup by `DotenvEnvironmentPostProcessor` and is gitignored.
Secrets never leave the server.

3. Run:

```powershell
.\mvnw.cmd spring-boot:run
```

The app starts on `http://localhost:8089`. Swagger/OpenAPI docs are at
`http://localhost:8089/swagger-ui.html`, and the unauthenticated health probe at
`http://localhost:8089/health`.

## Run as a Docker container

```powershell
docker build -t et-server:local .
docker run --rm -p 8089:8089 --env-file .env et-server:local
```

Inside a container, `localhost` in `DB_URL` points at the container, not your
host. When pointing the image at a database on the host machine, use:

```powershell
docker run --rm -p 8089:8089 --env-file .env `
  -e "DB_URL=jdbc:postgresql://host.docker.internal:5432/expensetrackerdb" `
  et-server:local
```

`Dockerfile` is a two-stage build:

- **build** — `eclipse-temurin:21-jdk-jammy`, runs the Maven wrapper
  (`./mvnw`, pinned to Maven 3.9.16) so the container build matches local and CI
  builds. Dependencies are resolved in their own layer that only invalidates when
  `pom.xml` changes.
- **runtime** — `eclipse-temurin:21-jre-jammy`, carries only the fat jar. Runs as
  an unprivileged user (uid 1001) and ships a `HEALTHCHECK` against `/health`.

Details worth knowing:

- `-DskipTests` is deliberate: `ExpenseTrackerApplicationTests.contextLoads` boots
  the full Spring context, which needs `JWT_SECRET` and a reachable database,
  neither of which exist during an image build. Run tests in CI against a real
  database.
- `.dockerignore` excludes `.env` and `target/`, so local credentials can never
  end up in an image layer.
- `JAVA_TOOL_OPTIONS` sets `-XX:MaxRAMPercentage=75.0` so the heap respects the
  container memory limit instead of guessing from host memory.
- `server.shutdown=graceful` makes Render's `SIGTERM` drain in-flight requests
  instead of dropping them mid-response.
- `Dockerfile.alpine` is a verified, equivalent musl-based variant (~410 MB
  instead of ~530 MB) if you want the smaller image.

## Deploy to Render

The repo root ships a `render.yaml` blueprint, so the backend can be created via
**Render → New → Blueprint** pointing at this repository. Render builds
`et-server/Dockerfile`, so leave Build/Start commands empty.

If you prefer Render's native Java runtime instead of Docker:

| Setting        | Value                                           |
| -------------- | ----------------------------------------------- |
| Root Directory | `et-server`                                     |
| Runtime        | Java (21)                                       |
| Build Command  | `./mvnw -B clean package -DskipTests`           |
| Start Command  | `java -jar target/et-server-0.0.1-SNAPSHOT.jar` |
| Health Check   | `/health`                                       |

Required environment variables (set them on the service, never commit them):

```
DB_URL=<postgres connection string>
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET=<random string, at least 32 characters>
JWT_EXPIRATION_MS=86400000
FRONTEND_URL=https://your-app.vercel.app
EXPOSE_API_DOCS=false
```

Notes:

- `server.port` already reads `${PORT:8089}`, which is the variable Render injects.
- `FRONTEND_URL` accepts a comma-separated list, and entries may be Spring origin
  patterns. Set `ALLOW_VERCEL_PREVIEWS=true` to also accept every
  `https://*.vercel.app` deployment (preview builds); keep it off for production.
- Render's free Postgres expires after 30 days. For a project you intend to keep,
  use a managed database (Neon, Supabase, Aiven) and paste its connection string.

## Verifying

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package -DskipTests=false
```

## API surface

All endpoints return/expect JSON: `application/json`. Protected endpoints require
`Authorization: Bearer <token>`.

| Method & path                        | Purpose                                   |
| ------------------------------------ | ----------------------------------------- |
| `POST /auth/register`                | Create account (returns `token` + `user`) |
| `POST /auth/login`                   | Sign in (returns `token` + `user`)        |
| `POST /auth/logout`                  | No-op (stateless JWT)                     |
| `GET/PUT /users/me`                  | Profile read / update                     |
| `PUT /users/me/settings`             | Update currency/language/theme prefs       |
| `PUT /users/me/password`             | Change password                           |
| `GET/POST /expenses`, `/incomes`     | List / create                             |
| `GET/PUT/DELETE /expenses/{id}`, ... | Read / update / delete                    |
| `GET/POST /goals`, `/budgets`        | Savings goals / budgets                   |
| `GET/POST /notifications`            | Notifications                             |
| `PATCH /notifications/{id}/read`     | Mark one read                             |
| `PATCH /notifications/read-all`      | Mark all read                             |
| `GET /health`                        | Unauthenticated health probe (Render)     |

Key behaviours:

- Passwords are hashed with BCrypt; JWT expires after `JWT_EXPIRATION_MS`.
- Registering creates a "Welcome aboard" notification.
- Creating/updating a budget whose `spent > total` creates a once-only
  "Budget Exceeded" notification.
- `GlobalExceptionHandler` returns consistent `{timestamp, status, error, message, path}`
  JSON with the correct HTTP status (400/401/403/404/409/500).

## Configuration notes

- `spring.jpa.hibernate.ddl-auto` defaults to `update` (dev) — tables are created
  from the entities. Run Hibernate's `validate` (`DB_DDL_AUTO=validate`) before
  production.
- CORS allows the origins in `FRONTEND_URL` (comma-separated, origin patterns
  allowed). With an empty value the app still starts but every browser request
  is blocked, and a warning is logged at startup.

## Project layout

```
config/      OpenAPI + CORS + security + dotenv loader
controller/  REST controllers (no /api prefix — matches the frontend client)
dto/         request/response records + mapping
entity/      JPA entities
exception/   domain exceptions + global handler
repository/  Spring Data repositories
security/    JWT filter/service, user details, current-user resolver
service/     business logic (auth, CRUD, notifications)
```