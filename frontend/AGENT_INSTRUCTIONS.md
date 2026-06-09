Purpose
- Minimal static frontend for manual testing and demo of the gateway-auth flow.

Key files
- `index.html` — small dashboard and login link that points to the Keycloak realm `resource-allocation`.
- `Dockerfile` — builds a static server image for the frontend.

Run / Test
- Docker-compose: `docker compose up --build` serves the frontend on port `3000`.

Notes for agents
- The frontend uses the `frontend` Keycloak client (public) to initiate login; sample users exist in the included realm export.
- For end-to-end tests, interact via the gateway (`http://localhost:8080`) which fronts the frontend and backend APIs.
