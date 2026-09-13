# Exemplos da aula — a mesma API em dois stacks

Uma API de tarefas, construída do zero. Serve à aula de **14/09** (visão comparativa Kotlin/Ktor ×
Java/Quarkus).

| Pasta | Stack | Roda em | Reconstruir ao vivo |
|-------|-------|---------|---------------------|
| [`ktor-tarefas/`](ktor-tarefas/PASSOS.md) | Kotlin + Ktor (engine CIO) + Koin | `./gradlew run` → :8080 | [PASSOS.md](ktor-tarefas/PASSOS.md) |
| [`quarkus-tarefas/`](quarkus-tarefas/PASSOS.md) | Java + Quarkus (Quarkus REST) + CDI | `mvn quarkus:dev` → :8081 | [PASSOS.md](quarkus-tarefas/PASSOS.md) |

As versões acompanham o MUSI (Kotlin 2.4.10 / Ktor 3.5.2 / Quarkus 3.28.2 / Java 25).

Os cinco passos são numerados igualmente nos dois: **1** servidor no ar · **2** modelo e
`GET` em JSON · **3** `POST` · **4** repositório por interface · **5** injeção de
dependência (Koin × CDI).

A mesma requisição nos dois:

```bash
curl localhost:8080/tarefas    # Ktor
curl localhost:8081/tarefas    # Quarkus
```
