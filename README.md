# Resource Allocation System

A Spring Boot microservices monorepo for scheduling and authorizing teacher and student use of rooms, computer labs, and audio-visual equipment.

## Architecture

- `config-server`: Spring Cloud Config server with centralized configuration.
- `discovery-server`: Eureka-based service registry.
- `gateway-service`: Spring Cloud Gateway with OpenID Connect routing and API composition.
- `resource-catalog-service`: catalog of rooms, labs, and equipment.
- `booking-service`: reservations and allocation rules.
- `permission-service`: access entitlement and resource rights.
- `frontend`: minimal static front-end dashboard.

Supporting infrastructure:

- `keycloak`: OpenID Connect identity provider.
- `postgres`: shared relational database.
- `prometheus` / `grafana`: metrics and dashboards.
- `otel-collector`: OpenTelemetry collector for traces and metrics.
- `sonarqube`: static code quality analysis.

## Getting started

Run all microservices locally with Docker Compose:

```bash
docker compose up --build
```

Open the following portals:

- Gateway: http://localhost:8080
- Frontend: http://localhost:3000
- Eureka: http://localhost:8761
- Config server: http://localhost:8888
- Keycloak admin: http://localhost:8081
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001
- SonarQube: http://localhost:9000

## Authentication

This scaffold uses Keycloak as the OpenID Connect provider.

- Keycloak admin: `admin` / `admin` (available at http://localhost:8081)
- In the current local Docker setup, Keycloak may report as unhealthy even while the admin UI is reachable on port 8081, so it is best treated as a development dependency rather than a hard health gate.
- Sample users included for development:
	- `teacher1` / `teacher-pass` (role `teacher`)
	- `student1` / `student-pass` (role `student`)

Clients in the realm:

- `gateway-client`: confidential client used by the gateway (secret: `gateway-secret`).
- `frontend`: public client used by the minimal frontend.

The `frontend` login link is available in `frontend/index.html` and points to the local Keycloak realm `resource-allocation`.

## Data Bank (resource-catalog)

A small Data Bank feature is implemented inside the `resource-catalog-service` to store arbitrary JSON payloads keyed by an application-defined key. This is implemented on the shared PostgreSQL database and managed via Flyway migrations.

- Table: `data_bank` (created by Flyway migration `resource-catalog-service/src/main/resources/db/migration/V1__create_data_bank.sql`). Columns: `id`, `entry_key` (unique), `payload` (JSONB), `created_at`, `updated_at`.
- Endpoints: exposed by `resource-catalog-service` at `/api/databank`:
	- `GET /api/databank` — list entries
	- `GET /api/databank/{key}` — get entry by key
	- `POST /api/databank` — upsert entry (JSON body: `{ "key": "k", "payload": {...} }`)
	- `DELETE /api/databank/{key}` — delete entry

Flyway is enabled for `resource-catalog-service` via the config-repo (see `config-server/src/main/resources/config-repo/resource-catalog-service.yml`) and migrations run on application startup by default. If you prefer running migrations as a separate CI/CD job, update the Compose/CI configuration accordingly.

Tests: a `WebMvcTest` for the controller is included at `resource-catalog-service/src/test/java/.../DataBankControllerTest.java` to validate controller behavior. For full integration verification, run the application with Postgres (or use Testcontainers) to validate migrations and JPA mappings.

## Java / Maven

This repository uses a Maven aggregator at the root and Spring Boot 3.

To build all modules:

```bash
mvn -pl config-server,discovery-server,gateway-service,resource-catalog-service,booking-service,permission-service package
```

## Notes

This scaffold is intentionally minimal:

- service modules expose OpenAPI documentation using Swagger annotations.
- centralized configuration is provided through Spring Cloud Config.
- service discovery is handled by Eureka.
- security is planned with Keycloak / OpenID Connect.
- observability is staged through OTEL, Prometheus, and Grafana.

## Development approach: SDD and AI-assisted delivery

This project was shaped with a lightweight Specification-Driven Development (SDD) workflow and a small set of repository-local AI skills. The goal was to make architectural decisions explicit before implementation and to keep the coding loop fast, repeatable, and verifiable.

### How the workflow worked

- SDD first: requirements and architectural choices were captured as concrete decisions before coding began. This covered service boundaries, authentication flow, persistence strategy, API shape, and observability.
- Grill-me style decision framing: ambiguous design questions were resolved through a short interview-style process so the implementation would follow a consistent blueprint rather than ad-hoc changes.
- AI skills for repeatable tasks: reusable prompts and task definitions were stored in `ai-skills/` so common development steps could be invoked consistently and reviewed safely.
- Incremental validation: each feature was wired into the local stack, then verified with tests and live smoke checks rather than relying only on static reasoning.
- Evidence-driven iteration: the project was considered ready when Maven tests passed and the Docker Compose stack came up with core services healthy.

### Practical technique summary

- Define the architecture and constraints before implementation.
- Break work into small, verifiable slices (for example: config server, discovery, gateway, catalog, booking, permissions).
- Use AI assistance to scaffold, explain, and refine implementation steps while keeping human review in the loop.
- Prefer runnable evidence such as unit tests, integration tests, and local service health checks.

This combination of SDD and skill-based AI guidance helped keep the repository understandable, modular, and easier to evolve as new features are added.

## AI Skills

This repository supports a lightweight, repo-local AI-skills manifest in `ai-skills/manifest.yaml` and sample skill definitions in `ai-skills/`.
Use the included runner to list or dry-run skills:

```bash
python tools/skill_runner.py --list
python tools/skill_runner.py run check-permission-endpoint --dry-run
```

Skills are intentionally dry-run by default; mark them `auto_execute: true` in `ai-skills/manifest.yaml` only after validating safety.
