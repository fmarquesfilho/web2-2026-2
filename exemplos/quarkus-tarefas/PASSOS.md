# Passos — API de Tarefas em Quarkus

Esta pasta contém o estado final (depois de concluído o Passo 9); use os passos para reconstruir
ao vivo a partir de um projeto limpo. Passos 1 a 5: aula de 14/09. Passos 6 a 9: aula de 21/09. Os passos são numerados igual aos do Ktor, para a
comparação ficar lado a lado.

Para partir de um repositório limpo, gere com
`quarkus create app br.ufrn.exemplo:quarkus-tarefas --extension='rest,rest-jackson'`
(ou em [code.quarkus.io](https://code.quarkus.io)) — ou comece copiando só o `pom.xml` desta pasta.

Para rodar a qualquer momento: `mvn quarkus:dev` (sobe em `http://localhost:8081`). Do Passo 6
em diante, o modo dev e os testes sobem um PostgreSQL sozinhos (Dev Services): precisa de
Docker aberto.

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

---

## Passo 6 — Banco e migração

Extensões `quarkus-hibernate-orm-panache`, `quarkus-jdbc-postgresql` e `quarkus-flyway`;
a mesma migração do Ktor em `src/main/resources/db/migration/V1__cria_tarefas.sql`.

```properties
quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=${DB_URL:jdbc:postgresql://localhost:5432/tarefas}
quarkus.hibernate-orm.schema-management.strategy=none
quarkus.flyway.migrate-at-start=true
```

> Em dev e test, sem URL, o **Dev Services** sobe o PostgreSQL no Docker. O Hibernate
> **não** cria tabelas (`strategy=none`): o esquema é da migração.

📖 [Quarkus — Flyway](https://quarkus.io/guides/flyway) · [Dev Services for Databases](https://quarkus.io/guides/databases-dev-services)

---

## Passo 7 — Repositório com Panache

```java
@Entity @Table(name = "tarefas")
public class TarefaEntidade extends PanacheEntityBase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Integer id;
    public String titulo;
    public boolean feita;
}

@ApplicationScoped
public class RepositorioPanache
        implements RepositorioDeTarefas, PanacheRepositoryBase<TarefaEntidade, Integer> {
    @Transactional
    public Tarefa adicionar(NovaTarefa nova) { /* new TarefaEntidade(); persist(entidade); */ }
}
```

> Escrita precisa de `@Transactional`; sem ela, `TransactionRequiredException` e `500`.
> O `RepositorioEmMemoria` perdeu o `@ApplicationScoped`: dois beans da mesma interface
> deixariam a injeção ambígua. **O recurso não mudou** além do `GET /tarefas/{id}`.

📖 [Quarkus — Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)

---

## Passo 8 — Testes com `@QuarkusTest`

```java
@QuarkusTest
class RecursoDeTarefasTest {
    @Test
    void idInexistenteDevolve404() {
        given().when().get("/tarefas/9999").then().statusCode(404);
    }
}
```

Rodar: `mvn test` (precisa de Docker). A porta de teste é 8083 (`quarkus.http.test-port`),
então dá para testar com o `quarkus:dev` aberto na 8081.

`RecursoDeTarefasUnitarioTest` testa o recurso sem Quarkus nem Docker: monta
`new RecursoDeTarefas()` com um `RepositorioEmMemoria` (o equivalente ao `RotasTest` do Ktor).

> O `pom.xml` importa o BOM do Testcontainers 1.21.4 antes do BOM do Quarkus: o 1.21.3
> que vem com o Quarkus 3.28 não reconhece o Docker Engine 29
> (`Could not find a valid Docker environment`).

📖 [Quarkus — Testing your application](https://quarkus.io/guides/getting-started-testing)

---

## Passo 9 — OpenAPI e Swagger UI

Extensão `quarkus-smallrye-openapi`. A especificação sai das anotações JAX-RS; o
`@Operation(summary = "...")` acrescenta a descrição.

Abra `http://localhost:8081/q/swagger-ui` (modo dev); a especificação fica em `/q/openapi`.

> No `java -jar`, a Swagger UI não vem por padrão; para incluí-la,
> `quarkus.swagger-ui.always-include=true`.

📖 [Quarkus — OpenAPI and Swagger UI](https://quarkus.io/guides/openapi-swaggerui)
