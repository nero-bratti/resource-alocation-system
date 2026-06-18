Frontend (Vue 3 + Vite)

Run locally:

```bash
cd frontend
npm install
npm run dev
```

Build for production:

```bash
npm run build
```

Docker build:

```bash
docker build -t resource-allocation-frontend .
```

Notes:
- The SPA uses Keycloak JS (`keycloak-js`) and expects a Keycloak server available at `http://localhost:8081` with realm `resource-allocation` and client `frontend` (public).
- It calls backend APIs through the gateway proxy on `/api` and forwards bearer tokens to the gateway.
- The gateway route configuration has been updated to forward `/api/databank/**` to `resource-catalog-service`.
- Adjust the Keycloak settings in `src/App.vue` if your environment differs.
