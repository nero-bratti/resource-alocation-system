Purpose
- Booking service: manages reservations and allocation rules for resources.

Key files
- `src/main/java/.../booking/BookingServiceApplication.java` — Spring Boot entrypoint.
- `src/main/java/.../booking/controller/` — REST controllers for booking operations.
- `src/main/resources/application.yml` — service configuration (DB, discovery, security).

Database & persistence
- Uses the shared PostgreSQL instance and the single shared schema defined at the repo level.
- Booking domain tables live in the shared schema (bookings, allocations). Migrations should be applied via the repo migration strategy (Flyway SQL migrations in each service's `src/main/resources/db/migration`).

Integration notes
- Booking logic must consult `permission-service` for entitlement checks (synchronous REST call). Keep service boundaries intact.
- Do not accept forwarded user JWTs; the gateway exchanges user JWTs for an internal service token.

Run / Test
- Build: `mvn -pl booking-service package`
- Docker-compose: `docker compose up --build` will start the service on port `8083`.

Notes for agents
- When making DB changes add a new `V{n}__*.sql` under `booking-service/src/main/resources/db/migration` or the central migration location used by the config server.
- Add WebMvc or slice tests for controllers; use Testcontainers for full integration tests that validate migrations.
