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

Notes:
- The SPA uses Keycloak JS (`keycloak-js`) and expects a Keycloak server available at `http://localhost:8081` with realm `resource-allocation` and client `frontend` (public).
- It calls the gateway at `http://localhost:8080/api/databank` and passes the user bearer token. The gateway is expected to perform token-exchange to internal tokens for backend services.
- Adjust Keycloak URL/realm/client in `src/App.vue` if your environment differs.
