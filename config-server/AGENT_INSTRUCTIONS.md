Purpose
- Centralized configuration server using Spring Cloud Config. Holds the local `config-repo` used by services at runtime.

Key files
- `src/main/resources/config-repo/` — per-service YAML files (e.g., `resource-catalog-service.yml`, `gateway-service.yml`, etc.).
- `src/main/java/.../configserver/ConfigServerApplication.java` — Spring Boot entrypoint.

How configuration is applied
- Services fetch their configuration from the config server (`http://config-server:8888`) using their spring application name.
- For the DataBank, Flyway was enabled for `resource-catalog-service` via `config-repo/resource-catalog-service.yml` (see `flyway:` properties).

Run / Test
- Build: `mvn -pl config-server package`
- Docker-compose: `docker compose up --build` exposes the config server on port `8888`.

Notes for agents
- To add or change runtime config for services, update files under `config-repo/` and restart the config server or refresh the client apps.
- Use the config-repo to surface DB migration locations, Flyway settings, and feature flags for services.
