Purpose
- Service registry using Eureka (Netflix OSS) to enable service discovery.

Key files
- `src/main/java/.../discovery/DiscoveryServerApplication.java` — Spring Boot entrypoint.
- `src/main/resources/application.yml` — config for Eureka server and port mapping.

Run / Test
- Build: `mvn -pl discovery-server package`
- Docker-compose: `docker compose up --build` starts Eureka on port `8761`.

Notes for agents
- New services should register with Eureka via `spring.cloud.discovery.enabled=true` and appropriate bootstrap config.
- Use the discovery server to inspect registered instances during local integration tests.
