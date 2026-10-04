#!/usr/bin/env bash
# Instala as dependências dos dois exemplos e imagens do PostgreSQL.
# Falhas aqui não impedem o Codespace de abrir; o primeiro build só fica mais lento.
set -uo pipefail
cd "$(dirname "$0")/.."

# Tasks do mise.toml (`mise tasks`). `trust` porque o arquivo vem do repositório.
mise trust && mise install || echo "aviso: mise não terminou; os comandos completos continuam valendo"

(cd exemplos/ktor-tarefas && ./gradlew --quiet testClasses) || echo "aviso: Gradle não terminou"
(cd exemplos/quarkus-tarefas && mvn -q -B test-compile) || echo "aviso: Maven não terminou"

# postgres:17-alpine: compose.yaml e Testcontainers (Ktor); postgres:17: Dev Services (Quarkus).
docker pull -q postgres:17-alpine || echo "aviso: não baixou postgres:17-alpine"
docker pull -q postgres:17 || echo "aviso: não baixou postgres:17"

# Branch `aula`: o código começa no Passo 4. Compila também o último passo, numa cópia
# temporária, para que as dependências dos passos seguintes já estejam baixadas.
git fetch --quiet --tags origin || true
ultimo="$(git tag --list 'passo-*' --sort=v:refname | tail -1)"
if [ -n "$ultimo" ] && git worktree add --quiet --detach /tmp/ultimo-passo "$ultimo"; then
  (cd /tmp/ultimo-passo/exemplos/ktor-tarefas && ./gradlew --quiet testClasses) || echo "aviso: Gradle (último passo) não terminou"
  (cd /tmp/ultimo-passo/exemplos/quarkus-tarefas && mvn -q -B test-compile) || echo "aviso: Maven (último passo) não terminou"
  git worktree remove --force /tmp/ultimo-passo
fi
