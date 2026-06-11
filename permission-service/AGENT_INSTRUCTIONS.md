Purpose
- Owns authorization data and exposes permission-check APIs used by other services.

Key files
- `src/main/java/.../permission/PermissionServiceApplication.java` — Spring Boot entrypoint.
- `src/main/java/.../permission/controller/` — REST controllers for permission checks.
- `src/main/resources/application.yml` — configuration for DB and discovery.

API contract
- The expected minimal contract is a `POST /api/check` endpoint accepting `{ user, resource, action }` and returning an allow/deny response. Keep the contract narrow and fast.

Database & persistence
- Permission models are stored in the shared PostgreSQL schema; changes should be applied via SQL migrations under `permission-service/src/main/resources/db/migration` or the centralized migration path.

Run / Test
- Build: `mvn -pl permission-service package`
- Docker-compose: `docker compose up --build` will start the service on port `8084`.

Notes for agents
- Permission checks are synchronous and performance-sensitive; prefer compact indexes and caching where appropriate.
- When adding fields to permission tables, create accompanying migrations and update any services that query the model.
