# CI and staging pipeline plan

This repository now uses a three-step delivery gate for changes:

1. Test gate
   - Run the Maven reactor tests for all Java services on pull requests and on pushes to main.
   - Keep the job fast by using the existing module list and Maven parallelism.

2. Migration gate
   - Run Flyway migrations against a disposable Postgres instance whenever migration files or database-related configuration changes.
   - This protects the shared schema from drift and ensures the SQL scripts stay executable.

3. Staging gate
   - Build the Java services and Docker images.
   - Start the stack in a disposable staging-like environment.
   - Run a smoke test against the gateway endpoint before marking the deployment as successful.

The staging workflow is intentionally environment-agnostic at first. It uses Docker Compose and health checks so it can work with no external secrets, and it can later be extended to a real remote target when one is available.
