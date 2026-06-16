# AGENTS Instructions for resource-alocation-system

## Current repository state
- The repository now contains a Maven aggregator `pom.xml` and a scaffolded microservices layout with service modules:
   - `config-server`, `discovery-server`, `gateway-service`, `resource-catalog-service`, `booking-service`, `permission-service`, and `frontend`.
- Supporting infra artifacts: `docker-compose.yml`, `prometheus.yml`, `otel-collector-config.yml`, and a `keycloak/` folder with a realm export and Dockerfile.
- A minimal front-end (`frontend/index.html`) and basic controllers for services were scaffolded. `notification-service` was removed per direction.

## Primary objective
- Help the user understand and expand this repository by asking the right questions first.
- Once the stack and requirements are confirmed, scaffold the microservices and minimal infra to enable local end-to-end testing.
- When implementing features, preserve service boundaries and document design trade-offs for review.

## Project architecture and design (chosen for this project)
- Language / platform: Java with Spring Boot (Java 17) and Maven.
- Repo style: monorepo Maven aggregator with multiple independent Spring Boot service modules (separate artifact per service).
- Orchestration: Docker Compose for local dev, images produced with module-aware Dockerfiles.
- Service list and responsibilities:
   - `config-server`: Spring Cloud Config (centralized config)
   - `discovery-server`: Eureka service registry
   - `gateway-service`: Spring Cloud Gateway (authentication + routing only)
   - `resource-catalog-service`: owns resource metadata (rooms, labs, equipment)
   - `booking-service`: owns reservation state and booking logic
   - `permission-service`: owns authorization model and exposes a permission check API
   - `frontend`: minimal static dashboard for manual testing
- Communication: synchronous REST only (no async event bus for MVP).
- Authentication & security: Keycloak (OpenID Connect). Gateway performs authentication and exchanges the user JWT for an internal service token; backend services trust only the internal token (decoupled model).
- API contract: a unified public API exposed by the gateway; backend services may have internal OpenAPI docs but they are not the public contract.
- Persistence: a single shared PostgreSQL database instance with a single schema for the system. Database-level foreign keys will be used for referential integrity.
- Authorization storage: `permission-service` stores its authorization model in separate tables in the shared schema.
- Permission validation flow: `booking-service` calls `permission-service` over REST (service boundary preserved). `permission-service` exposes a simple `checkPermission(user, resource, action)` endpoint. `booking-service` will fetch/refresh entitlements on every request to evaluate permissions locally (strict consistency).
- Observability: OpenTelemetry collector + Prometheus + Grafana; services expose actuator metrics and Prometheus scrape endpoints.
- Quality: SonarQube is included in the Compose stack for local static analysis runs.

## Skill trigger: `grill-me`
- When the user says `grill-me`, the agent performs a decision-tree interview. This repository has used that flow; the following decisions were resolved during the session (see above): language, repo style, services, shared DB, DB-level FKs, gateway behavior, Keycloak, REST-only comms, unified gateway contract, and permission validation strategy.
- Continue using `grill-me` to exercise alternative design branches before major refactors.

## Immediate agent actions (what was performed)
1. Confirmed stack and high-level design choices via the `grill-me` interview.
2. Scaffolding created in this repo (Maven aggregator, module folders, basic controllers, Dockerfiles, and `docker-compose.yml`).
3. Added Keycloak realm export and updated `docker-compose.yml` to import the realm for local testing.
4. Added minimal Prometheus and OpenTelemetry configuration, a tiny `frontend` placeholder, and basic test-dependency entries in module POMs.
5. Implemented a small "data-bank" feature inside `resource-catalog-service`:
   - Added Flyway migration `V1__create_data_bank.sql` to create a `data_bank` table (JSONB payload, timestamps, trigger).
   - Added JPA `DataBankEntry` entity, `DataBankRepository`, `DataBankService`, and `DataBankController` exposing CRUD endpoints at `/api/databank`.
   - Enabled Flyway for `resource-catalog-service` via the `config-repo` config so migrations run on startup (config updated in `config-server/src/main/resources/config-repo/resource-catalog-service.yml`).
   - Added a `WebMvcTest` (`DataBankControllerTest`) to validate the controller behavior without requiring a running DB.

## Probing questions (answered)
- Should `booking-service` be responsible only for reservation management, while `permission-service` owns all entitlement and access rules? -> Booking manages reservations; `permission-service` owns authorization.
- Shared DB or service-owned persistence? -> Shared DB (single schema) was chosen.
- Single schema or per-service schemas? -> Single schema.
- Should gateway handle auth and routing only, and services enforce business permissions? -> Gateway handles auth/routing only; services enforce business-level permissions.
- Sync REST only or add async events? -> REST only.
- Unified API contract at gateway or per-service? -> Unified contract via gateway (simpler public surface).
- Should backend services expose internal docs? -> Simpler: only gateway docs for clients.
- Resource metadata owner? -> `resource-catalog-service`.
- Permission validation strategy? -> `booking-service` calls `permission-service` via REST; `permission-service` provides a simple check endpoint; booking evaluates locally and refreshes entitlement data every request.
- Token flow? -> Gateway exchanges user JWT for an internal service token; services use that internal token and do not trust forwarded user JWTs.

## Working conventions for this repo
- Keep the monorepo layout minimal and explicit: each microservice module is an independent Spring Boot application with its own `pom.xml` and Dockerfile.
- Preserve service boundaries in code even though a shared DB is used; prefer REST APIs for inter-service checks.
- Document decisions in `AGENTS.md` and `README.md` and propagate high-level configs into `config-server`'s `config-repo` for runtime overrides.

## Next steps (recommended)
1. Implement gateway OIDC client configuration and token-exchange (internal service token) flow.
2. Add a small `permission-service` REST endpoint `POST /api/check` with a minimal contract and unit/integration tests.
3. Wire `booking-service` to call `permission-service` synchronously and validate the check flow.
4. Add CI job templates that run `mvn -T 1C -DskipTests=false package` and Sonar analysis.

Additional next steps related to DataBank:
5. Wire infra: decide whether to run Flyway as part of app startup or via a separate CI/CD migration job; update `docker-compose.yml` if you prefer a dedicated `flyway` container.
6. Add integration tests that run against an ephemeral Postgres (Testcontainers) to validate migrations + JPA mappings.
7. Document backup and retention policies for `data_bank` (encryption at rest, daily backups, retention 30-90 days by default).

If you want, I can begin with item 1 (gateway token-exchange) or item 2 (permission-service check endpoint).
