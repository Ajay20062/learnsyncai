#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_PORT="${1:-5500}"

echo "Starting backend..."
(cd "$ROOT_DIR/backend" && ./mvnw spring-boot:run) &

echo "Starting frontend static server on port ${FRONTEND_PORT}..."
(cd "$ROOT_DIR/frontend" && python3 -m http.server "$FRONTEND_PORT") &

echo "Frontend: http://localhost:${FRONTEND_PORT}/index.html"
echo "Backend:  http://localhost:8080"
wait
