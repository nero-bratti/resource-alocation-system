<template>
  <div>
    <div class="row">
      <div class="col-md-6">
        <h4>Entries</h4>
        <ul class="list-group">
          <li v-for="e in entries" :key="e.id" class="list-group-item d-flex justify-content-between align-items-start">
            <div>
              <div class="fw-bold">{{ e.key }}</div>
              <small class="text-muted">{{ e.updatedAt }}</small>
            </div>
            <div>
              <button class="btn btn-sm btn-outline-primary me-2" @click="loadEntry(e.key)">View</button>
              <button class="btn btn-sm btn-outline-danger" @click="deleteEntry(e.key)">Delete</button>
            </div>
          </li>
        </ul>
      </div>

      <div class="col-md-6">
        <h4>Editor</h4>
        <form @submit.prevent="save">
          <div class="mb-3">
            <label class="form-label">Key</label>
            <input v-model="form.key" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">Payload (JSON)</label>
            <textarea v-model="form.payload" class="form-control" rows="10" required></textarea>
          </div>
          <div class="d-flex gap-2">
            <button class="btn btn-primary" type="submit">Save (upsert)</button>
            <button class="btn btn-secondary" type="button" @click="clear">Clear</button>
          </div>
        </form>
        <div v-if="message" class="mt-3 alert" :class="messageClass">{{ message }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  props: ['token'],
  data() {
    return {
      entries: [],
      form: { key: '', payload: '' },
      message: null,
      messageClass: 'alert-info'
    }
  },
  mounted() {
    this.fetchAll()
  },
  methods: {
    authHeader() {
      return { Authorization: `Bearer ${this.token}` }
    },
    fetchAll() {
      axios.get('http://localhost:8080/api/databank', { headers: this.authHeader() })
        .then(r => { this.entries = r.data })
        .catch(e => this.showMessage('Failed to load entries', 'alert-danger'))
    },
    loadEntry(key) {
      axios.get(`http://localhost:8080/api/databank/${encodeURIComponent(key)}`, { headers: this.authHeader() })
        .then(r => {
          this.form.key = r.data.key
          // payload may be JSON; pretty-print if possible
          try { this.form.payload = JSON.stringify(JSON.parse(r.data.payload), null, 2) } catch { this.form.payload = r.data.payload }
        })
        .catch(() => this.showMessage('Failed to load entry', 'alert-danger'))
    },
    save() {
      // ensure payload is valid JSON string
      try { JSON.parse(this.form.payload) } catch (e) { this.showMessage('Payload must be valid JSON', 'alert-warning'); return }

      axios.post('http://localhost:8080/api/databank', { key: this.form.key, payload: this.form.payload }, { headers: this.authHeader() })
        .then(() => { this.showMessage('Saved', 'alert-success'); this.fetchAll() })
        .catch(() => this.showMessage('Save failed', 'alert-danger'))
    },
    deleteEntry(key) {
      if (!confirm('Delete entry?')) return
      axios.delete(`http://localhost:8080/api/databank/${encodeURIComponent(key)}`, { headers: this.authHeader() })
        .then(() => { this.showMessage('Deleted', 'alert-success'); this.fetchAll() })
        .catch(() => this.showMessage('Delete failed', 'alert-danger'))
    },
    clear() { this.form.key = ''; this.form.payload = ''; this.message = null },
    showMessage(text, cls) { this.message = text; this.messageClass = cls }
  }
}
</script>

<style scoped>
.list-group { max-height: 60vh; overflow: auto; }
</style>
