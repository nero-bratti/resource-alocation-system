Purpose
- Owns resource metadata (rooms, labs, equipment) and now hosts a small Data Bank for arbitrary JSON payloads keyed by application keys.

Key files
- `src/main/java/.../resourcecatalog/ResourceCatalogApplication.java` — Spring Boot entrypoint.
- `src/main/resources/db/migration/V1__create_data_bank.sql` — Flyway migration that creates the `data_bank` table.
- `src/main/java/.../resourcecatalog/databank/` — `DataBankEntry`, `DataBankRepository`, `DataBankService`, `DataBankController`.
- `src/main/resources/application.yml` — service config (DB, discovery, security). Flyway is enabled via the config-repo.

Data Bank specifics
- Table: `data_bank` with columns `id`, `entry_key` (unique), `payload` (JSONB), `created_at`, `updated_at`.
- Endpoints: `/api/databank` (GET list, GET by key, POST upsert, DELETE by key).
- Migrations: Flyway migrations must be placed in `src/main/resources/db/migration` and are run at app startup if Flyway is enabled in the config-repo.

Run / Test
- Build: `mvn -pl resource-catalog-service package`
- Docker-compose: `docker compose up --build` will start the service on port `8082` and run Flyway migrations (if enabled in config-repo).

Notes for agents
- Database changes: use Flyway SQL migrations (SQL-first) and add tests that validate the migration with Testcontainers.
- Keep DataBank API minimal; treat the table as a small configuration/data store — enforce RBAC via `permission-service` checks when needed.
