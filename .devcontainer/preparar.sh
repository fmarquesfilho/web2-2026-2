#!/usr/bin/env bash
# Aquece o Codespace: dependências dos dois exemplos e imagens do PostgreSQL.
# Falhas aqui não impedem o Codespace de abrir; o primeiro build só fica mais lento.
set -uo pipefail
cd "$(dirname "$0")/.."

(cd exemplos/ktor-tarefas && ./gradlew --quiet testClasses) || echo "aviso: Gradle não terminou"
(cd exemplos/quarkus-tarefas && mvn -q -B test-compile) || echo "aviso: Maven não terminou"

# postgres:17-alpine: compose.yaml e Testcontainers (Ktor); postgres:17: Dev Services (Quarkus).
docker pull -q postgres:17-alpine || echo "aviso: não baixou postgres:17-alpine"
docker pull -q postgres:17 || echo "aviso: não baixou postgres:17"
