# Passos — API de Tarefas em Quarkus

Esta pasta contém o estado final (depois de concluído o Passo 12); use os passos para reconstruir
ao vivo a partir de um projeto limpo. Passos 1 a 5: aula de 14/09. Passos 6 a 12: segunda parte
da Sprint 1, em vídeo. Até o Passo 11, todo o código fica no pacote `br.ufrn.exemplo.tarefas`;
no Passo 12 ele se divide em `dominio` e `adaptadores`. Os passos são numerados igual aos do Ktor, para a
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
package br.ufrn.exemplo.tarefas;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/tarefas")
public class RecursoDeTarefas {
    @GET
    public String listar() { return "no ar"; }
}
```

E a porta, em `src/main/resources/application.properties` — sem ela, o Quarkus sobe na
8080, a mesma do exemplo Ktor:

```properties
# Porta 8081 para rodar lado a lado com o exemplo Ktor (8080).
quarkus.http.port=8081
# Porta dos testes fora da 8081, para testar com o dev mode aberto.
quarkus.http.test-port=8083
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

---

## Passo 10 — o ambiente em tasks

Os comandos dos nove passos viram nomes curtos no [`mise.toml`](../../mise.toml) da raiz do
repositório, que também fixa as versões do JDK e do Maven:

```toml
[tools]
java  = "temurin-25"
maven = "3.9"

[tasks."quarkus:dev"]
description = "Sobe a API Quarkus na 8081, em modo dev (banco pelo Dev Services)"
dir = "exemplos/quarkus-tarefas"
run = "mvn -B quarkus:dev"
```

```bash
mise install            # uma vez: JDK 25 e Maven
mise tasks              # a lista
mise run quarkus:dev    # == mvn -B quarkus:dev
mise run quarkus:test   # == mvn -B -ntp test
mise run quarkus:pacote # empacota e roda como em produção (java -jar)
mise run quarkus:demo   # cria e lista uma tarefa, com curl
```

> O mesmo `mise.toml` serve aos dois exemplos, com os prefixos `ktor:` e `quarkus:`. Repare
> que a versão do Java deixa de depender do que está instalado na máquina — é a mesma ideia
> do `maven.compiler.release` no `pom.xml`, agora para a ferramenta.

📖 [mise — tasks](https://mise.jdx.dev/tasks/)


---

## Passo 11 — Validação e erros em *problem details*

A regra fica no domínio, em Java puro; o recurso só lança; os `@ServerExceptionMapper`
decidem o status.

```java
// NovaTarefa.java — `null` também conta: com Jackson, `{}` chega como título nulo
public record NovaTarefa(String titulo) {
    public List<String> violacoes() {
        if (titulo == null || titulo.isBlank()) return List.of("titulo: não pode ficar em branco");
        if (titulo.length() > 200) return List.of("titulo: no máximo 200 caracteres");
        return List.of();
    }
}

// RecursoDeTarefas.java
@POST
public Response criar(NovaTarefa nova) {
    if (nova == null) throw new BadRequestException("O corpo da requisição é obrigatório");
    List<String> violacoes = nova.violacoes();
    if (!violacoes.isEmpty()) throw new EntradaInvalida(violacoes);
    Tarefa criada = repositorio.adicionar(nova);
    return Response.created(URI.create("/tarefas/" + criada.id())).entity(criada).build();
}

// Erros.java — o equivalente do StatusPages
@ServerExceptionMapper
public Response entradaInvalida(EntradaInvalida e) { return problema(422, /* ... */); }
```

`Erros.java` também traduz `WebApplicationException` (o `404` e o `400` do próprio Jakarta
REST), `MismatchedInputException` (campo com tipo errado) e qualquer `RuntimeException`
(`500`, sem detalhe interno), sempre em `application/problem+json`.

| Pedido | Resposta |
|---|---|
| `POST {"titulo":"   "}` | `422` |
| `POST {}` | `422` — antes deste passo, `500` (o `NULL` chegava ao banco) |
| `POST` sem corpo | `400` |
| `GET /tarefas/abc` | `404` — a especificação Jakarta REST manda |
| `GET /tarefas/9999` | `404` |
| `POST` válido | `201`, com `Location` |

> Alternativa comum no Quarkus: Bean Validation (`quarkus-hibernate-validator`,
> `@NotBlank` no record, `@Valid` no parâmetro). Aqui a regra fica numa função do domínio,
> igual à do Ktor, para o domínio não depender de anotação de framework (Passo 12). O MUSI
> usa as duas: Bean Validation para a forma, regras do domínio para o conteúdo.

📖 [RFC 9457 — Problem Details](https://www.rfc-editor.org/rfc/rfc9457) · [Quarkus REST — exception mapping](https://quarkus.io/guides/rest#exception-mapping)

---

## Passo 12 — Camadas em pacotes e o teste de arquitetura

```
br.ufrn.exemplo.tarefas
├── dominio/       Tarefa, NovaTarefa, EntradaInvalida, RepositorioDeTarefas
└── adaptadores/
    ├── http/      RecursoDeTarefas, Erros, Problema
    ├── banco/     TarefaEntidade, RepositorioPanache
    └── memoria/   RepositorioEmMemoria
```

```java
// src/test/java/.../arquitetura/ArquiteturaTest.java (ArchUnit 1.5.0)
static final ArchRule REGRA_DO_DOMINIO = noClasses().that().resideInAPackage("..dominio..")
        .should().dependOnClassesThat().resideInAnyPackage(
                "jakarta..", "io.quarkus..", "org.hibernate..", "com.fasterxml..", "java.sql..",
                "..adaptadores..");
```

Dependência nova: `com.tngtech.archunit:archunit:1.5.0`, escopo `test`.

Confira que a regra **falha** quando deve: dê à `Tarefa` um método que devolva
`jakarta.ws.rs.core.Response` e rode `mvn test` — `Architecture Violation ... was violated (3 times)`.

> Mudou de pacote e o `mvn quarkus:dev` acusou `GET /tarefas is declared by` duas classes?
> São `.class` antigos no `target/`: `mvn clean` resolve.

📖 [ArchUnit — User Guide](https://www.archunit.org/userguide/html/000_Index.html)
