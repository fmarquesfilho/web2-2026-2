# Passos — API de Tarefas em Ktor

Esta pasta contém o estado final (depois de concluído o Passo 9); use os passos para reconstruir
ao vivo a partir de um projeto limpo. Passos 1 a 5: aula de 14/09. Passos 6 a 9: aula de 21/09.

Para partir de um repositório limpo, gere em [start.ktor.io](https://start.ktor.io) com os
plugins *Content Negotiation*, *kotlinx.serialization* e o engine *CIO* — ou comece
copiando só `build.gradle.kts`, `settings.gradle.kts` e o `gradlew` desta pasta.

Para rodar a qualquer momento: `./gradlew run` (sobe em `http://localhost:8080`). Do Passo 6
em diante, suba antes o banco: `docker compose up -d`.

---

## Passo 1 — Um servidor web mínimo

`src/main/kotlin/br/ufrn/exemplo/tarefas/Aplicacao.kt`:

```kotlin
fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        routing { get("/") { call.respondText("no ar") } }
    }.start(wait = true)
}
```

Testar: `curl localhost:8080` → `no ar`.

> O servidor é código: `embeddedServer` + engine (CIO). Não há é necessário instanciar 
> contêiner algum de aplicação, e não é necessário configurar nenhum XML.

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
