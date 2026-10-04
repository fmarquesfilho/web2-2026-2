#!/usr/bin/env bash
# Leva o código do exemplo para o estado final de um passo (o gabarito).
#
#   ./passo.sh        lista os passos e mostra em qual o código está
#   ./passo.sh 6      vai para o Passo 6 e lista o que mudou desde o Passo 5
#
# O que você digitou e ainda não salvou em commit é guardado antes da troca
# (`git stash list` mostra; `git stash pop` traz de volta).
set -euo pipefail
cd "$(dirname "$0")"

[ -n "$(git tag --list 'passo-*')" ] || git fetch --quiet --tags origin

if [ $# -eq 0 ]; then
  aqui="$(git describe --tags --match 'passo-*' 2>/dev/null || true)"
  for t in $(git tag --list 'passo-*' --sort=v:refname); do
    [ "$t" = "$aqui" ] && marca="->" || marca="  "
    echo "$marca $(git log -1 --format=%s "$t")"
  done
  exit 0
fi

alvo="passo-$(printf '%02d' "$((10#$1))")"
git rev-parse --quiet --verify "refs/tags/$alvo" > /dev/null || { echo "não existe $alvo; rode ./passo.sh para ver a lista" >&2; exit 1; }

if [ -n "$(git status --porcelain)" ]; then
  git stash push --quiet --include-untracked -m "aula: antes de ir para $alvo"
  echo "Seu código foi guardado (git stash list)."
fi

anterior="$(git tag --list 'passo-*' --sort=v:refname | grep -B1 -x "$alvo" | head -1)"
git checkout --quiet "$alvo"
echo "== $(git log -1 --format=%s)"
if [ "$anterior" != "$alvo" ]; then git diff --stat "$anterior" "$alvo" | cat; fi
