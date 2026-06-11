FROM quay.io/keycloak/keycloak:22.0.4
USER root
COPY realm-export.json /opt/keycloak/realm/realm-export.json
RUN chown -R 1000:0 /opt/keycloak/realm && chmod -R 755 /opt/keycloak/realm
USER 1000
