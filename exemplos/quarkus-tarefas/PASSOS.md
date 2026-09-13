# Passos — API de Tarefas em Quarkus

Esta pasta contém o estado final (depois de concluído o Passo 5); use os passos para reconstruir
ao vivo a partir de um projeto limpo. Os passos são numerados igual aos do Ktor, para a
comparação ficar lado a lado.

Para partir de um repositório limpo, gere com
`quarkus create app br.ufrn.exemplo:quarkus-tarefas --extension='rest,rest-jackson'`
(ou em [code.quarkus.io](https://code.quarkus.io)) — ou comece copiando só o `pom.xml` desta pasta.

Para rodar a qualquer momento: `mvn quarkus:dev` (sobe em `http://localhost:8081`).

---

## Passo 1 — Um recurso REST mínimo

`src/main/java/br/ufrn/exemplo/tarefas/RecursoDeTarefas.java`:

```java
@Path("/tarefas")
public class RecursoDeTarefas {
    @GET
    public String listar() { return "no ar"; }
}
```

Testar: `curl localhost:8081/tarefas` → `no ar`.

> As rotas são anotações (`@Path`, `@GET`); o framework descobre o recurso. Não há uma
> função `main` subindo o servidor — compare com o `embeddedServer` do Ktor.

📖 [Quarkus — Writing REST services](https://quarkus.io/guides/rest)

---

## Passo 2 — Modelo e `GET /tarefas` em JSON

```java
public record Tarefa(int id, String titulo, boolean feita) {}

// no recurso, com a extensão quarkus-rest-jackson:
@GET
public List<Tarefa> listar() {
    return List.of(new Tarefa(1, "Estudar Ktor", false),
                   new Tarefa(2, "Estudar Quarkus", false));
}
```

Testar: `curl localhost:8081/tarefas` → lista em JSON.

> `record` ≈ `data class`. Com `quarkus-rest-jackson`, devolver o objeto já é devolver
> JSON — sem anotar `@Produces`.

📖 [Quarkus — Writing JSON REST services](https://quarkus.io/guides/rest-json)

---

## Passo 3 — `POST /tarefas`

```java
public record NovaTarefa(String titulo) {}

// no recurso, com a lista e o proximoId como campos:
@POST
public Response criar(NovaTarefa nova) {
    Tarefa criada = new Tarefa(proximoId++, nova.titulo(), false);
    tarefas.add(criada);
    return Response.status(Response.Status.CREATED).entity(criada).build();
}
```

Testar:
```bash
curl -X POST localhost:8081/tarefas -H 'Content-Type: application/json' \
     -d '{"titulo":"Escrever teste"}'
```

> O corpo JSON é desserializado no parâmetro do método. O `Response` monta a resposta,
> com o status `201 Created` explícito.

📖 [Quarkus — REST](https://quarkus.io/guides/rest) · [Jakarta REST — `Response`](https://jakarta.ee/specifications/restful-ws/)

---

## Passo 4 — Refatoração: Separar o repositório

Extraia a interface e a implementação:

```java
public interface RepositorioDeTarefas {
    List<Tarefa> listar();
    Tarefa adicionar(NovaTarefa nova);
}

class RepositorioEmMemoria implements RepositorioDeTarefas { /* ...lista + proximoId... */ }
```

O recurso passa a falar com a **interface**, não com a lista.

> O recurso não sabe se os dados vêm de memória ou banco. Na Sprint 1
> troca-se a implementação por Postgres (Panache) **sem precisar mexer no recurso**.

---

## Passo 5 — Injeção de dependência com CDI

```java
@ApplicationScoped
public class RepositorioEmMemoria implements RepositorioDeTarefas { /* ... */ }

// no recurso:
@Inject
RepositorioDeTarefas repositorio;
```

> O contêiner CDI cria e injeta o bean. No lado Ktor (via Koin)
> o mesmo papel vem de código (`single { }` / `by inject()`).

📖 [Quarkus — Contexts and Dependency Injection](https://quarkus.io/guides/cdi)
