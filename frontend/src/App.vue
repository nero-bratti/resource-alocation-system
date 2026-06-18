<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-start mb-4">
      <div>
        <h1 class="mb-1">Resource Allocation Dashboard</h1>
        <p class="text-muted mb-0">A small interactive dashboard that calls backend services through the gateway.</p>
      </div>
      <div class="text-end">
        <button v-if="!authenticated" class="btn btn-primary mb-2" @click="login">Login</button>
        <button v-else class="btn btn-outline-secondary mb-2" @click="logout">Logout</button>
        <div v-if="authenticated" class="badge bg-success">Signed in as {{ user }}</div>
      </div>
    </div>

    <div v-if="loading" class="alert alert-info">Connecting to Keycloak...</div>
    <div v-else>
      <div v-if="authenticated">
        <div class="alert alert-light">
          Use the Data Bank panel below to list, create, update, and delete entries through the gateway.
        </div>
        <DataBank :token="token" />
      </div>
      <div v-else class="alert alert-secondary">
        Please login to start interacting with the system. The dashboard calls backend APIs through the gateway proxy at <code>/api</code>.
      </div>
    </div>
  </div>
</template>

<script>
import Keycloak from 'keycloak-js'
import DataBank from './components/DataBank.vue'

export default {
  components: { DataBank },
  data() {
    return {
      kc: null,
      authenticated: false,
      token: null,
      user: null,
      loading: true
    }
  },
  mounted() {
    this.kc = new Keycloak({
      url: 'http://localhost:8081/auth',
      realm: 'resource-allocation',
      clientId: 'frontend'
    })

    this.kc.init({ onLoad: 'check-sso', pkceMethod: 'S256' })
      .then(authenticated => {
        this.authenticated = authenticated
        this.loading = false
        if (authenticated) {
          this.token = this.kc.token
          this.user = this.kc.tokenParsed?.preferred_username || this.kc.tokenParsed?.sub
          setInterval(() => this.kc.updateToken(30)
            .then(refreshed => { if (refreshed) this.token = this.kc.token })
            .catch(() => this.kc.login()), 60000)
        }
      })
      .catch(() => {
        this.loading = false
      })
  },
  methods: {
    login() {
      this.kc.login()
    },
    logout() {
      this.kc.logout()
      this.authenticated = false
      this.token = null
      this.user = null
    }
  }
}
</script>

<style>
body { background: #f8f9fa; }
</style>
