Keycloak realm import

This folder contains `realm-export.json` that defines the `resource-allocation` realm, two roles (`teacher`, `student`), two clients (`gateway-client`, `frontend`), and sample users `teacher1`/`teacher-pass` and `student1`/`student-pass`.

The Docker Compose configuration mounts this file into the Keycloak container and sets `KEYCLOAK_IMPORT` so Keycloak imports it on first start.
