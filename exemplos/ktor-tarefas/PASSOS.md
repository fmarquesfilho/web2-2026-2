# Passos — API de Tarefas em Ktor

Esta pasta contém o estado final (depois de concluído o Passo 12); use os passos para reconstruir
ao vivo a partir de um projeto limpo. Passos 1 a 5: aula de 14/09. Passos 6 a 12: segunda parte
da Sprint 1, em vídeo. Até o Passo 11, todo o código fica no pacote `br.ufrn.exemplo.tarefas`;
no Passo 12 ele se divide em `dominio` e `adaptadores`.

Para partir de um repositório limpo, gere em [start.ktor.io](https://start.ktor.io) com os
plugins *Content Negotiation*, *kotlinx.serialization* e o engine *CIO* — ou comece
copiando só `build.gradle.kts`, `settings.gradle.kts` e o `gradlew` desta pasta.

Para rodar a qualquer momento: `./gradlew run` (sobe em `http://localhost:8080`). Do Passo 6
em diante, suba antes o banco: `docker compose up -d`.

---

## Passo 1 — Um servidor web mínimo

`src/main/kotlin/br/ufrn/exemplo/tarefas/Aplicacao.kt`:

```kotlin
package br.ufrn.exemplo.tarefas

import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        routing { get("/") { call.respondText("no ar") } }
    }.start(wait = true)
}
```

Testar: `curl localhost:8080` → `no ar`.

> O servidor é código: `embeddedServer` + engine (CIO). Não é necessário instanciar
> contêiner de aplicação nem configurar XML nenhum.

📖 [Ktor — configuration in code](https://ktor.io/docs/server-configuration-code.html)

---

## Passo 2 — Modelo e `GET /tarefas` em JSON

Crie `Tarefa` e ligue o JSON:

```kotlin
@Serializable
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

fun Application.modulo() {
    install(ContentNegotiation) { json() }          // liga o JSON
    val tarefas = listOf(Tarefa(1, "Estudar Ktor"), Tarefa(2, "Estudar Quarkus"))
    routing {
        get("/tarefas") { call.respond(tarefas) }   // vira JSON automaticamente
    }
}
```

Testar: `curl localhost:8080/tarefas` → lista em JSON.

> `@Serializable` + `ContentNegotiation` permite serialização sem
> escrever `toJson`. `respond(objeto)` negocia o formato pelo `Accept`.

📖 [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html) · [Ktor — content negotiation](https://ktor.io/docs/server-serialization.html)

---

## Passo 3 — `POST /tarefas`

```kotlin
@Serializable
data class NovaTarefa(val titulo: String)

// dentro de routing, com a lista agora mutável:
post("/tarefas") {
    val nova = call.receive<NovaTarefa>()
    val criada = Tarefa(proximoId++, nova.titulo)
    tarefas.add(criada)
    call.respond(HttpStatusCode.Created, criada)
}
```

Testar:
```bash
curl -X POST localhost:8080/tarefas -H 'Content-Type: application/json' \
     -d '{"titulo":"Escrever teste"}'
```

> `receive<T>()` desserializa o corpo. O status da resposta é `201 Created`.

📖 [Ktor — requests](https://ktor.io/docs/server-requests.html) · [Ktor — responses](https://ktor.io/docs/server-responses.html)

---

## Passo 4 — Refatoração: Separar o repositório

Extraia a interface e a implementação para `Tarefa.kt`:

```kotlin
interface RepositorioDeTarefas {
    fun listar(): List<Tarefa>
    fun adicionar(nova: NovaTarefa): Tarefa
}

class RepositorioEmMemoria : RepositorioDeTarefas { /* ...lista + proximoId... */ }
```

As rotas passam a falar com a **interface**, não com a lista.

> A rota não sabe se os dados vêm de memória ou banco. Na Sprint 1
> troca-se a implementação por Postgres **sem precisar mexer nas rotas**.

---

## Passo 5 — Injeção de dependência com Koin

```kotlin
install(Koin) {
    modules(module { single<RepositorioDeTarefas> { RepositorioEmMemoria() } })
}
// nas rotas:
val repositorio by inject<RepositorioDeTarefas>()
```

> O grafo de dependências fica num lugar só. No caso do Quarkus (via CDI)
> o mesmo papel vem de anotações (`@ApplicationScoped` / `@Inject`).

📖 [Koin com Ktor](https://insert-koin.io/docs/quickstart/ktor) · [Kotlin — delegated properties](https://kotlinlang.org/docs/delegated-properties.html)


---

## Passo 6 — Banco, pool e migração

`compose.yaml` com um PostgreSQL 17 e a primeira migração em
`src/main/resources/db/migration/V1__cria_tarefas.sql`:

```sql
CREATE TABLE tarefas (
    id     SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    feita  BOOLEAN NOT NULL DEFAULT FALSE
);
```

```kotlin
val dataSource = HikariDataSource(HikariConfig().apply { jdbcUrl = config.url; /* ... */ })
Flyway.configure().dataSource(dataSource).load().migrate()
```

> O esquema vem **só da migração**. O Flyway guarda o que já aplicou em
> `flyway_schema_history`; subir de novo não reaplica. Precisa de
> `flyway-database-postgresql`, além de `flyway-core`.

📖 [Flyway — Versioned migrations](https://documentation.red-gate.com/flyway/flyway-concepts/migrations/versioned-migrations)

---

## Passo 7 — Repositório com Exposed

```kotlin
object Tarefas : Table("tarefas") {
    val id = integer("id").autoIncrement()
    val titulo = varchar("titulo", 200)
    val feita = bool("feita").default(false)
    override val primaryKey = PrimaryKey(id)
}

class RepositorioPostgres(private val db: Database) : RepositorioDeTarefas {
    override suspend fun listar() = suspendTransaction(db) {
        Tarefas.selectAll().orderBy(Tarefas.id to SortOrder.ASC).map { it.paraTarefa() }
    }
    // buscar(id) e adicionar(nova): ver Banco.kt
}
```

> A interface ganhou `suspend`: JDBC bloqueia, e `suspendTransaction` evita travar a
> thread do servidor. **As rotas não mudaram** — trocou só a implementação no Koin.

📖 [Exposed — CRUD (DSL)](https://www.jetbrains.com/help/exposed/dsl-crud-operations.html)

---

## Passo 8 — Testes: rota sem banco e integração com Testcontainers

```kotlin
// RotasTest: sem Docker, com o repositório em memória
testApplication { application { configurar(RepositorioEmMemoria()) } /* ... */ }

// TarefasTest: PostgreSQL de verdade num container
private val postgres = PostgreSQLContainer("postgres:17-alpine").apply { start() }
testApplication { application { modulo(ConfigBanco(postgres.jdbcUrl, postgres.username, postgres.password)) } }
```

Rodar: `./gradlew test` (precisa de Docker para o `TarefasTest`).

> `modulo` monta com banco; `configurar(repositorio)` recebe a porta — é o que deixa o
> teste de rota trocar o banco pela memória.

📖 [Testcontainers — Postgres](https://java.testcontainers.org/modules/databases/postgres/) · [Ktor — Testing](https://ktor.io/docs/server-testing.html)

---

## Passo 9 — OpenAPI e Swagger UI

```kotlin
get("/{id}") { /* ... */ }.describe {
    summary = "Busca uma tarefa pelo id"
    parameters { path("id") { schema = jsonSchema<Int>() } }
    responses { HttpStatusCode.OK { schema = jsonSchema<Tarefa>() } }
}
// ...
swaggerUI(path = "docs") { info = OpenApiInfo(title = "Tarefas", version = "1.0") }
```

Abra `http://localhost:8080/docs`; a especificação fica em `/docs/documentation.yaml`.

> `describe` é **experimental** no Ktor 3.5 (`@OptIn(ExperimentalKtorApi::class)`).

📖 [Ktor — OpenAPI specification generation](https://ktor.io/docs/openapi-spec-generation.html)

---

## Passo 10 — o ambiente em tasks

Os comandos dos nove passos viram nomes curtos no [`mise.toml`](../../mise.toml) da raiz do
repositório, que também fixa as versões do JDK e do Maven:

```toml
[tools]
java  = "temurin-25"
maven = "3.9"

[tasks."ktor:banco"]
description = "Sobe o PostgreSQL 17 do exemplo Ktor (porta 5432)"
dir = "exemplos/ktor-tarefas"
run = "docker compose up -d"

[tasks."ktor:run"]
dir = "exemplos/ktor-tarefas"
run = "./gradlew run"
```

```bash
mise install          # uma vez: JDK 25 e Maven
mise tasks            # a lista
mise run ktor:banco   # == docker compose up -d
mise run ktor:run     # == ./gradlew run
mise run ktor:test    # == ./gradlew test
mise run ktor:demo    # cria e lista uma tarefa, com curl
```

> Isto não muda o projeto: cada task é o mesmo comando dos passos anteriores, com nome. O
> ganho é o `mise install`, que dá a mesma versão de JDK para todo mundo, e o
> `mise tasks`, que documenta o projeto sozinho. No Codespace, o `.devcontainer` já instala
> tudo isso.

📖 [mise — tasks](https://mise.jdx.dev/tasks/)


---

## Passo 11 — Validação e erros em *problem details*

A regra fica no domínio, em Kotlin puro; a rota só lança; o `StatusPages` decide o status.

```kotlin
// Tarefa.kt
data class NovaTarefa(val titulo: String) {
    fun violacoes(): List<String> = when {
        titulo.isBlank() -> listOf("titulo: não pode ficar em branco")
        titulo.length > 200 -> listOf("titulo: no máximo 200 caracteres")
        else -> emptyList()
    }
}
class EntradaInvalida(val violacoes: List<String>) : RuntimeException(violacoes.joinToString("; "))

// Rotas.kt
post {
    val nova = call.receive<NovaTarefa>()
    val violacoes = nova.violacoes()
    if (violacoes.isNotEmpty()) throw EntradaInvalida(violacoes)
    val criada = repositorio.adicionar(nova)
    call.response.header(HttpHeaders.Location, "/tarefas/${criada.id}")
    call.respond(HttpStatusCode.Created, criada)
}

// Erros.kt — exceção vira resposta, num lugar só
install(StatusPages) {
    exception<BadRequestException> { call, causa -> call.responderProblema(HttpStatusCode.BadRequest, /* ... */) }
    exception<NotFoundException> { call, causa -> call.responderProblema(HttpStatusCode.NotFound, /* ... */) }
    exception<EntradaInvalida> { call, causa -> call.responderProblema(HttpStatusCode.UnprocessableEntity, /* ... */) }
    exception<Throwable> { call, causa -> call.responderProblema(HttpStatusCode.InternalServerError, /* ... */) }
}
```

Dependência nova: `io.ktor:ktor-server-status-pages`.

```bash
curl -s -i -X POST localhost:8080/tarefas -H 'Content-Type: application/json' -d '{"titulo":"   "}'
```

```
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/problem+json

{"type":"/problemas/entrada-invalida","title":"Entrada inválida","status":422,"detail":"A entrada viola 1 regra(s).","violacoes":["titulo: não pode ficar em branco"]}
```

| Pedido | Resposta |
|---|---|
| `POST {"titulo":"   "}` | `422`, a regra do domínio |
| `POST {}` | `400`, a forma do JSON (falta o campo) |
| `GET /tarefas/abc` | `400` |
| `GET /tarefas/9999` | `404` |
| `POST` válido | `201`, com `Location: /tarefas/{id}` |

> **400 × 422.** `400`: a requisição não tem a forma esperada (JSON quebrado, campo
> faltando, id que não é número). `422`: a forma está certa, mas viola uma regra. Todos
> os erros saem em `application/problem+json` (RFC 9457), com `type`, `title`, `status`
> e `detail`; `violacoes` é um membro de extensão.

📖 [RFC 9457 — Problem Details](https://www.rfc-editor.org/rfc/rfc9457) · [Ktor — Status pages](https://ktor.io/docs/server-status-pages.html)

---

## Passo 12 — Camadas em pacotes e o teste de arquitetura

```
br.ufrn.exemplo.tarefas
├── Aplicacao.kt                 raiz de composição: liga as camadas
├── dominio/Tarefa.kt            Tarefa, NovaTarefa, EntradaInvalida, a porta
└── adaptadores/
    ├── http/  Rotas.kt, Erros.kt
    ├── banco/ Banco.kt          Exposed, Hikari, Flyway
    └── memoria/ RepositorioEmMemoria.kt
```

```kotlin
// src/test/kotlin/.../arquitetura/ArquiteturaTest.kt (ArchUnit 1.5.0)
val regraDoDominio = noClasses().that().resideInAPackage("..dominio..")
    .should().dependOnClassesThat().resideInAnyPackage(
        "io.ktor..", "org.koin..", "org.jetbrains.exposed..", "java.sql..", "javax.sql..",
        "com.zaxxer..", "org.flywaydb..", "..adaptadores..",
    )
```

Dependência nova: `testImplementation("com.tngtech.archunit:archunit:1.5.0")`.

Confira que a regra **falha** quando deve: acrescente ao `dominio/Tarefa.kt`
`fun Tarefa.status() = io.ktor.http.HttpStatusCode.OK` e rode `./gradlew test` — o
`ArquiteturaTest` acusa a dependência. O teste `a regra do dominio pega uma violacao` faz
essa prova automaticamente, com uma classe-fixture só dos testes (`violacao/dominio/Contaminado.kt`).

> O domínio aceita `kotlinx.serialization` (o `@Serializable` da `Tarefa`), por
> simplicidade: a regra da Sprint 1 proíbe framework web, banco e injeção de dependência.
> O MUSI vai além, com DTOs no adaptador web e o domínio em Kotlin puro.

📖 [ArchUnit — User Guide](https://www.archunit.org/userguide/html/000_Index.html)
