# Exemplos da aula — a mesma API em dois stacks

Uma API de tarefas, construída do zero. Serve às aulas de **14/09** (visão comparativa Kotlin/Ktor ×
Java/Quarkus) e **21/09** (persistência, migrações, testes e OpenAPI).

| Pasta | Stack | Roda em | Reconstruir ao vivo |
|-------|-------|---------|---------------------|
| [`ktor-tarefas/`](ktor-tarefas/PASSOS.md) | Kotlin + Ktor (engine CIO) + Koin + Exposed | `docker compose up -d` e `./gradlew run` → :8080 | [PASSOS.md](ktor-tarefas/PASSOS.md) |
| [`quarkus-tarefas/`](quarkus-tarefas/PASSOS.md) | Java + Quarkus (Quarkus REST) + CDI + Panache | `mvn quarkus:dev` → :8081 (Docker aberto) | [PASSOS.md](quarkus-tarefas/PASSOS.md) |

As versões acompanham o MUSI (Kotlin 2.4.10 / Ktor 3.5.2 / Quarkus 3.28.2 / Java 25).

Os passos são numerados igualmente nos dois: **1** servidor no ar · **2** modelo e
`GET` em JSON · **3** `POST` · **4** repositório por interface · **5** injeção de
dependência (Koin × CDI) · **6** banco e migração (Flyway) · **7** repositório com banco
(Exposed × Panache) · **8** testes (Testcontainers × Dev Services) · **9** OpenAPI.

Os dois usam PostgreSQL 17 e a mesma migração (`V1__cria_tarefas.sql`). Testes e o modo dev
do Quarkus precisam de Docker aberto.

Sem Docker na máquina (no laboratório, por exemplo), use um Codespace deste repositório
(Code → Codespaces). O `.devcontainer/` traz Java 25, Maven, Go e Docker, e já deixa as
dependências e as imagens do PostgreSQL baixadas; a criação leva cerca de 5 minutos. Lá
dentro, os comandos são os mesmos, e as portas 8080 e 8081 aparecem na aba **Portas**.

A mesma requisição nos dois:

```bash
curl localhost:8080/tarefas    # Ktor
curl localhost:8081/tarefas    # Quarkus
```
