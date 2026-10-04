# Branch `aula` — o exemplo de Tarefas, passo a passo

Este branch guarda o exemplo de Tarefas (Ktor e Quarkus) em cada etapa da construção. O
branch começa no **Passo 4**, onde a primeira parte da Sprint 1 terminou, e cada passo
seguinte está numa tag (`passo-05` a `passo-12`). O estado final é o mesmo da `main`.

## Como usar

Crie o Codespace a partir deste branch (**Code → Codespaces → New with options → Branch:
`aula`**) ou, no seu computador:

```bash
git fetch --tags origin
git switch aula
```

Escreva o código de cada passo a partir do anterior. Para conferir ou alcançar a turma:

```bash
./passo.sh        # lista os passos e mostra em qual o código está
./passo.sh 6      # leva o código para o estado final do Passo 6
```

O `./passo.sh` guarda o que você tinha digitado (`git stash list`) antes de trocar.

## Comandos

| | Ktor (porta 8080) | Quarkus (porta 8081) |
|---|---|---|
| Pasta | `exemplos/ktor-tarefas` | `exemplos/quarkus-tarefas` |
| Rodar | `./gradlew run` | `mvn quarkus:dev` |
| Testar | `./gradlew test` | `mvn test` |
| Banco (Passo 6 em diante) | `docker compose up -d` | sobe sozinho (Dev Services) |

Ao voltar para um passo anterior no Quarkus, rode `mvn clean` antes de subir de novo.

## O que muda em cada passo

| Passo | Assunto | Diferença em relação ao passo anterior |
|---|---|---|
| 5 | Injeção de dependência (Koin × CDI) | [passo-04...passo-05](../../compare/passo-04...passo-05) |
| 6 | Banco, pool e migração (Flyway) | [passo-05...passo-06](../../compare/passo-05...passo-06) |
| 7 | Repositório com banco (Exposed × Panache) | [passo-06...passo-07](../../compare/passo-06...passo-07) |
| 8 | Testes: sem banco e com PostgreSQL | [passo-07...passo-08](../../compare/passo-07...passo-08) |
| 9 | OpenAPI e Swagger UI | [passo-08...passo-09](../../compare/passo-08...passo-09) |
| 10 | O ambiente em tasks do mise | [passo-09...passo-10](../../compare/passo-09...passo-10) |
| 11 | Validação e erros em *problem details* | [passo-10...passo-11](../../compare/passo-10...passo-11) |
| 12 | Camadas em pacotes e teste de arquitetura | [passo-11...passo-12](../../compare/passo-11...passo-12) |

A explicação de cada passo está em `exemplos/ktor-tarefas/PASSOS.md` e
`exemplos/quarkus-tarefas/PASSOS.md`.
