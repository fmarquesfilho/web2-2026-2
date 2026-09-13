# Passos — API de Tarefas em Ktor

Esta pasta contém o estado final (depois de concluído o Passo 5); use os passos para reconstruir
ao vivo a partir de um projeto limpo.

Para partir de um repositório limpo, gere em [start.ktor.io](https://start.ktor.io) com os
plugins *Content Negotiation*, *kotlinx.serialization* e o engine *CIO* — ou comece
copiando só `build.gradle.kts`, `settings.gradle.kts` e o `gradlew` desta pasta.

Para rodar a qualquer momento: `./gradlew run` (sobe em `http://localhost:8080`).

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

