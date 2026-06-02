#!/bin/sh
# wrapper that preserves start-dev but ensures import path exists
if [ -n "$KEYCLOAK_IMPORT" ] && [ -f "$KEYCLOAK_IMPORT" ]; then
  echo "Import file present: $KEYCLOAK_IMPORT"
else
  echo "KEYCLOAK_IMPORT not set or file missing"
fi
exec /opt/keycloak/bin/kc.sh "$@"
