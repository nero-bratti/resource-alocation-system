Purpose
- API gateway using Spring Cloud Gateway. Responsible for authentication, routing, and exposing a unified public API surface.

Key files
- `src/main/java/.../gateway/GatewayServiceApplication.java` — Spring Boot entrypoint.
- `src/main/resources/application.yml` — gateway routes and security settings.
- `config-server/src/main/resources/config-repo/gateway-service.yml` — centralized gateway config.

Security & tokens
- Gateway performs OpenID Connect login with Keycloak and exchanges user JWTs for an internal service token used by backend services. Backend services should *not* trust forwarded user JWTs.

Run / Test
- Build: `mvn -pl gateway-service package`
- Docker-compose: `docker compose up --build` starts the gateway on port `8080`.

Notes for agents
- When changing authentication behavior update both the gateway config and Keycloak realm client configuration.
- Keep routing and composition logic small; move business logic to backend services.
- When updating gateway discovery or routing, ensure the gateway uses Eureka and discovery-server:
  - add `spring-cloud-starter-netflix-eureka-client` to `gateway-service/pom.xml`
  - set `eureka.client.serviceUrl.defaultZone` to `http://discovery-server:8761/eureka/`
  - keep route definitions in `config-server/src/main/resources/config-repo/gateway-service.yml`
  - verify backend services register with Eureka using `spring.application.name`
