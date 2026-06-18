# AGENTS Instructions for discovery-server

## Purpose
- Acts as the Eureka discovery server for the monorepo.

## Key decisions
- Core tech: Spring Cloud Netflix Eureka (single-node for this project).
- Registry persistence: registry snapshots are persisted to the shared PostgreSQL database so registrations survive server restarts.
- Clients: services self-register with Eureka.
- Observability: Actuator endpoints and Prometheus (via Micrometer) are enabled.

## Security decisions
- Registry exposure: this project uses an internal trusted Docker network. The Eureka server is not publicly exposed beyond the `docker-compose` network. For this small monorepo we keep the registry unprotected (no TLS or mTLS) but behind the gateway and internal network.
- Production recommendation: enable mTLS and/or token-based access for the registry, or place it behind a VPN. If you enable security, update `application.yml` to set `server.ssl.*` and add client auth configuration.

## Implementation notes
- Persistence:
  - A JPA entity `DiscoveryRegistration` stores minimal instance data.
  - `RegistryPersistenceService` snapshots the live Eureka registry (via the public `/eureka/apps?format=json` endpoint) every 30s and upserts entries into the DB.
  - On startup (`ApplicationReadyEvent`) the service restores persisted instances by posting the saved instance payloads to the local Eureka registration API (`POST /eureka/apps/{appName}`), so services remain discoverable across restarts.
- Scheduling: enabled via `@EnableScheduling` on the main application class.
- DB config: `spring.datasource.*` keys are used from `application.yml` (defaults point at the `postgres` service in `docker-compose.yml`).

## Resilience & HA notes
- Current deployment: single-node Eureka server. For small projects this keeps complexity low.
- If you later need HA: run a small cluster of Eureka nodes (3 nodes) behind a load balancer and configure peer awareness via `eureka.client.service-url.defaultZone` to the other nodes. Consider persisting registry state to a shared DB (as implemented here) or use a replicated store.
- Tradeoffs: restoring persisted instances will re-register services even if they are down; use lower snapshot durations or check probe health during restore if stronger accuracy is required.

## Files of interest
- `DiscoveryServerApplication.java` — main class; scheduling enabled.
- `src/main/resources/application.yml` — local config (DB, ports, management endpoints).
- `src/main/java/.../persistence/DiscoveryRegistration.java` — JPA entity.
- `src/main/java/.../persistence/DiscoveryRegistrationRepository.java` — JPA repository.
- `src/main/java/.../persistence/RegistryPersistenceService.java` — snapshot/restore logic.

## How to run (local docker-compose)
1. Start the full stack with Docker Compose (from repo root):

```bash
docker-compose up --build
```

2. Discovery UI: http://localhost:8761
3. Prometheus metrics: http://localhost:8761/actuator/prometheus

## Testing and verification
- Quick smoke: after stack starts, register a service (or start other services) and verify entries at the UI.
- Restart the `discovery-server` container; persisted registrations should be re-posted and visible without restarting clients.

## Tests
- A simple smoke test `DiscoveryServerSmokeTest` starts the context with an H2 in-memory DB and verifies repository wiring. For more confidence add Testcontainers-based integration tests that run against real Postgres and start a mock service that registers itself.

## For AI agents / maintainers
- When modifying persistence or snapshot behavior, update `AGENTS.md` with the new behaviour and reasoning.
- Keep the snapshot interval conservative (30s default) to avoid DB churn; tuning can be done in `RegistryPersistenceService`.
- Be mindful that restoring persisted instances posts to the Eureka API — if services are actually down, the registry will show them as UP until a real heartbeat fails. The system trades short-term accuracy for survivability across restart; this is a deliberate choice for this small project.
