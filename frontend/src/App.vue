<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between mb-3">
      <h1>Resource Allocation — Data Bank</h1>
      <div>
        <button v-if="!authenticated" class="btn btn-primary" @click="login">Login</button>
        <button v-else class="btn btn-secondary me-2" @click="logout">Logout</button>
        <span v-if="authenticated" class="badge bg-success">{{ user }}</span>
      </div>
    </div>

    <DataBank v-if="authenticated" :token="token" />
    <div v-else class="alert alert-info">Please login to manage Data Bank entries.</div>
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
      user: null
    }
  },
  mounted() {
    // Keycloak config — adjust host/realm/client as needed for your environment
    this.kc = new Keycloak({
      url: 'http://localhost:8081/auth',
      realm: 'resource-allocation',
      clientId: 'frontend'
    })

    this.kc.init({ onLoad: 'check-sso', pkceMethod: 'S256' }).then(authenticated => {
      this.authenticated = authenticated
      if (authenticated) {
        this.token = this.kc.token
        this.user = this.kc.tokenParsed?.preferred_username || this.kc.tokenParsed?.sub
        // Refresh token periodically
        setInterval(() => this.kc.updateToken(30).catch(() => this.kc.login()), 60000)
      }
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
