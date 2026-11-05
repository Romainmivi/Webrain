#!/usr/bin/env bash
# Lance l'application Webrain avec la base MySQL configurée dans .env

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Erreur: fichier .env introuvable dans $SCRIPT_DIR"
  exit 1
fi

# Charge les variables d'environnement depuis .env
set -a
source "$ENV_FILE"
set +a

echo "=== Webrain - Démarrage avec MySQL ==="
echo "Base: $DB_URL"
echo "Utilisateur: $DB_USER"
echo "=========================="

exec ./mvnw spring-boot:run
