Purpose
- Local Keycloak instance for OpenID Connect authentication and sample users/clients for local development.

Key files
- `realm-export.json` — exported realm including sample clients (`gateway-client`, `frontend`) and users (`teacher1`, `student1`).
- `Dockerfile` and `docker-entrypoint.sh` — how the local Keycloak image is built and started.

Run / Test
- Docker-compose: `docker compose up --build` starts Keycloak (mapped to host port `8081`).
- Admin console: `http://localhost:8081` with `admin` / `admin`.

Notes for agents
- To update the realm export, modify `realm-export.json` and rebuild the local Keycloak image. For automated tests you may prefer a lightweight test realm created at startup.
- The frontend uses the `frontend` public client; the gateway uses `gateway-client` (confidential) — check `config-repo` client secrets.
