# Exemplos da aula — a mesma API em dois stacks

Uma API de tarefas, construída do zero. Na Sprint 1, a visão comparativa Kotlin/Ktor × Java/Quarkus (14/09), persistência, migrações, testes, OpenAPI, validação e arquitetura. Na Sprint 2 (19 e 21/10), um serviço em Go que a API chama por gRPC, os containers e a implantação.

| Pasta | Stack | Roda em | Reconstruir ao vivo |
|-------|-------|---------|---------------------|
| [`ktor-tarefas/`](ktor-tarefas/PASSOS.md) | Kotlin + Ktor (engine CIO) + Koin + Exposed | `docker compose up -d` e `./gradlew run` → :8080 | [PASSOS.md](ktor-tarefas/PASSOS.md) |
| [`quarkus-tarefas/`](quarkus-tarefas/PASSOS.md) | Java + Quarkus (Quarkus REST) + CDI + Panache | `mvn quarkus:dev` → :8081 (Docker aberto) | [PASSOS.md](quarkus-tarefas/PASSOS.md) |
| [`avisos-go/`](avisos-go/PASSOS.md) | Go + gRPC, contrato em [`protos/`](protos/) | `go run ./cmd/avisos` → :50051 | [PASSOS.md](avisos-go/PASSOS.md) (passos 13 a 18) |
| [`compose.yaml`](compose.yaml) | banco, serviço Go e API, em containers | `docker compose up --build` → :8080 | Passo 17 |
| [`implantacao/`](implantacao/) | modelos de CI com imagens no GHCR e do Render | — | Passo 18 |
| [`kotlin-playground/`](kotlin-playground/) | demonstrações curtas de Kotlin | no [Kotlin Playground](https://play.kotlinlang.org/) | — |

As versões acompanham o MUSI (Kotlin 2.4.10 / Ktor 3.5.2 / Quarkus 3.28.2 / Java 25).

Os passos são numerados igualmente nos dois: **1** servidor no ar · **2** modelo e `GET` em JSON · **3** `POST` · **4** repositório por interface · **5** injeção de dependência (Koin × CDI) · **6** banco e migração (Flyway) · **7** repositório com banco (Exposed × Panache) · **8** testes (Testcontainers × Dev Services) · **9** OpenAPI · **10** tasks do `mise` · **11** validação e *problem details* · **12** camadas e teste de arquitetura (ArchUnit).

Na Sprint 2, os passos continuam, iguais para os dois: **13** o serviço Go · **14** o contrato `.proto` com Buf · **15** o servidor gRPC · **16** a API chama o Go por gRPC · **17** tudo em containers (`exemplos/compose.yaml`) · **18** implantação (modelos em `implantacao/`).

## Introdução a Go (aula de 19/10)

A aula começa pelos exemplos do Curso básico de Go, que segue o livro *Aprenda Go com Testes*. Eles ficam num repositório à parte, com uma pasta por assunto; no Codespace do branch `aula`, já vêm clonados em `/workspaces/aprenda-go-com-testes`.

```bash
git clone https://github.com/classrooms-fmarquesfilho/aprenda-go-com-testes
cd aprenda-go-com-testes/2-hello-world && go test -v
go test -run 'TestHello/região' -v      # só os casos com "região" no nome
```

| Pasta | Assunto |
|---|---|
| `2-hello-world` | ferramentas, tipos, testes com `t.Run`; funções como valores (*functional options*) |
| `3-metodos-interfaces` | `struct`, métodos e interfaces satisfeitas implicitamente |
| `4-ponteiros-erros` | receptor por valor × por ponteiro; erros com tipo próprio e `errors.As` |
| `5-injecao-de-dependencia` | `io.Writer`, testado com `bytes.Buffer` |

Depois, o Passo 13 aplica o mesmo no serviço de avisos ([PASSOS.md](avisos-go/PASSOS.md)).

Os dois usam PostgreSQL 17 e a mesma migração (`V1__cria_tarefas.sql`). Testes e o modo dev do Quarkus precisam de Docker aberto.

Sem Docker na máquina (no laboratório, por exemplo), use um Codespace deste repositório (Code → Codespaces). O `.devcontainer/` traz Java 25, Maven, Go e Docker, e já deixa as dependências e as imagens do PostgreSQL baixadas; a criação leva cerca de 5 minutos. Lá dentro, os comandos são os mesmos, e as portas 8080 e 8081 aparecem na aba **Portas**.

A mesma requisição nos dois:

```bash
curl localhost:8080/tarefas    # Ktor
curl localhost:8081/tarefas    # Quarkus
```
