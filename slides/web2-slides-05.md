---
marp: true
theme: default
paginate: true
backgroundColor: #ffffff
color: #1a1a2e
style: |
  section {
    font-family: 'Calibri', sans-serif;
    padding: 36px 48px;
    font-size: 1.3em;
  }
  h1 {
    font-family: 'Consolas', monospace;
    color: #1a56db;
    font-size: 1.6em;
    margin-bottom: 0.3em;
    border-bottom: 2px solid #e5e7eb;
    padding-bottom: 0.2em;
  }
  h2 {
    font-family: 'Consolas', monospace;
    color: #374151;
    font-size: 1.2em;
    margin-bottom: 0.25em;
  }
  h3 { color: #6b7280; font-size: 0.9em; margin: 0.2em 0; }
  strong { color: #b45309; }
  em { color: #6b7280; }
  code {
    font-family: 'Consolas', monospace;
    background: #e5e7eb;
    color: #1e3a5f;
    padding: 0.08em 0.3em;
    border-radius: 3px;
    font-size: 1.00em;
  }
  pre {
    background: #f3f4f6 !important;
    border: 1px solid #d1d5db;
    border-left: 3px solid #1a56db;
    border-radius: 6px;
    padding: 0.7em 1em;
    margin: 0.4em 0;
  }
  pre code {
    background: transparent;
    color: #1e3a5f;
    font-size: 0.85em;
    padding: 0;
    line-height: 1.5;
  }
  table { font-size: 0.95em; width: 100%; border-collapse: collapse; }
  th {
    background: #e5e7eb;
    color: #1a56db;
    font-family: 'Consolas', monospace;
    padding: 0.35em 0.7em;
    border: 1px solid #d1d5db;
  }
  td { background: #ffffff; padding: 0.28em 0.7em; border: 1px solid #d1d5db; color: #1a1a2e; }
  tr:nth-child(even) td { background: #f9fafb; }
  ul { margin: 0.25em 0; padding-left: 1.3em; }
  li { margin: 0.18em 0; font-size: 0.88em; line-height: 1.4; }
  blockquote {
    border-left: 3px solid #1a56db;
    background: #eff6ff;
    padding: 0.4em 0.9em;
    margin: 0.5em 0;
    font-style: normal;
    color: #1e3a5f;
    border-radius: 0 5px 5px 0;
    font-size: 1.00em;
  }
  .columns { display: flex; gap: 1.8em; }
  .col { flex: 1; }
  .pill-red   { display:inline-block; background:#fee2e2; border:1.5px solid #dc2626; color:#dc2626; font-family:'Consolas',monospace; font-weight:bold; font-size:0.85em; padding:0.12em 0.6em; border-radius:20px; }
  .pill-green { display:inline-block; background:#dcfce7; border:1.5px solid #16a34a; color:#16a34a; font-family:'Consolas',monospace; font-weight:bold; font-size:0.85em; padding:0.12em 0.6em; border-radius:20px; }
  .pill-blue  { display:inline-block; background:#dbeafe; border:1.5px solid #1a56db; color:#1a56db; font-family:'Consolas',monospace; font-weight:bold; font-size:0.85em; padding:0.12em 0.6em; border-radius:20px; }
  section.lead { justify-content: center; }
  section.lead h1 { font-size: 2.4em; border-bottom: none; }
  section.lead h2 { font-size: 1.5em; color: #6b7280; }
  .tag { display:inline-block; background:#f3f4f6; border:1px solid #d1d5db; color:#374151; font-size:0.85em; padding:0.1em 0.5em; border-radius:4px; font-family:'Consolas',monospace; }


---

# Desenvolvimento de Sistemas Web II

## Persistência, migrações, testes e OpenAPI

DIM0547 — Turma 01 · Sprint 1 · 21/09

Prof. Fernando · UFRN · 2026.2

---

# Roteiro da semana

**Segunda, 21/09 — Do repositório em memória ao banco**

| Bloco | O que vemos |
|---|---|
| Banco | JDBC, pool de conexões, PostgreSQL |
| Migrações | Flyway: versionadas, idempotentes |
| Exposed × Panache | O adaptador de persistência nos dois stacks |
| Testes | Rota sem banco · integração com Testcontainers e Dev Services |
| OpenAPI | Documentação gerada do código |

**Quarta, 23/09 — Acompanhamento online** (projeto)
**28 e 30/09 — Apresentações** · 🚀 **Entrega da Sprint 1: 02/10, 23:59**

---

# Onde paramos

Em 14/09 a API de tarefas ficou pronta nos dois stacks:

```
  Rotas (DSL) × recursos (anotações)
  JSON, POST, 201 com o corpo criado
  RepositorioDeTarefas: a porta
  RepositorioEmMemoria: a implementação
  Koin × CDI
```

Hoje só a implementação da porta muda. **As rotas não mudam.**

---

# O que a lista em memória não resolve

| Problema | Com PostgreSQL |
|---|---|
| Dados somem ao reiniciar | Tarefa criada continua lá após reiniciar |
| Cada instância vê os seus dados | Todas leem o mesmo banco |
| Concorrência | 2.000 `POST` simultâneos: **2.000 × `201`, ids únicos** |

> Em 14/09, os mesmos 2.000 `POST` perderam tarefas e repetiram ids. Quem garante agora é o banco: `SERIAL` e `INSERT` atômico.

---

# A porta, os adaptadores

```
            ┌───────────────┐        ┌──────────────────────┐
  HTTP ───▶ │ rotas/recurso │ ─────▶ │ RepositorioDeTarefas │
            └───────────────┘        └──────────┬───────────┘
                                                │ implementa
               RepositorioEmMemoria  ◀──────────┼──────────▶  RepositorioPostgres (Ktor)
               (testes de rota)                             RepositorioPanache (Quarkus)
```

- A separação de 14/09 paga agora: **uma classe nova**, a mesma interface
- A implementação em memória **fica**: é a dos testes sem Docker

---

<!-- _class: lead -->

# Banco

## JDBC, pool e onde fica a senha

---

# JDBC e pool de conexões

```
jdbc:postgresql://localhost:5432/tarefas
     └── driver ─┘ └─ host ─┘ └porta┘ └ banco ┘
```

<div class="columns">
<div class="col">

**Ktor** — o pool é escolha sua

```kotlin
HikariDataSource(HikariConfig().apply {
    jdbcUrl = config.url
    username = config.usuario
    password = config.senha
    maximumPoolSize = 5
})
```

</div>
<div class="col">

**Quarkus** — vem com a extensão

```properties
quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=\
  ${DB_URL:jdbc:postgresql://...}
```

Em dev e test, **sem URL**: Dev Services

</div>
</div>

> URL, usuário e senha em **variável de ambiente**. Nunca no repositório.

---

<!-- _class: lead -->

# Migrações

## O esquema do banco sob controle de versão

---

# Flyway

```
src/main/resources/db/migration/
  V1__cria_tarefas.sql
  V2__adiciona_prazo.sql
```

- Aplica em ordem; guarda versão e **checksum** em `flyway_schema_history`
- Primeira subida: `Migrating schema "public" to version "1 - cria tarefas"`
- Seguintes: `Schema "public" is up to date. No migration necessary.`

| Ktor | Quarkus |
|---|---|
| `Flyway.configure().dataSource(ds).load().migrate()` | `quarkus.flyway.migrate-at-start=true` |
| + `flyway-database-postgresql` | já incluso na extensão |

---

# O esquema é da migração, não do ORM

A rubrica: **esquema só por migração**, sem `ddl-auto` ou equivalente.

- Quarkus: `quarkus.hibernate-orm.schema-management.strategy=none`
- Ktor: o `object Tarefas` descreve a tabela para as consultas; **nunca** `SchemaUtils.create`

**Nunca edite uma migração aplicada.** O Flyway recusa subir:

```
Migration checksum mismatch for migration version 1
-> Applied to database : 1556295178
-> Resolved locally    : 1950589099
```

> Mudou o esquema? `V2__...`. No PostgreSQL, uma migração que falha é **desfeita inteira**.

---

<!-- _class: lead -->

# Exposed × Panache

## O mesmo adaptador, dois estilos

---

# Ktor: Exposed (DSL)

```kotlin
object Tarefas : Table("tarefas") {
    val id = integer("id").autoIncrement()
    val titulo = varchar("titulo", 200)
    val feita = bool("feita").default(false)
    override val primaryKey = PrimaryKey(id)
}

override suspend fun buscar(id: Int): Tarefa? = suspendTransaction(db) {
    Tarefas.selectAll().where { Tarefas.id eq id }.singleOrNull()?.paraTarefa()
}
```

- Toda operação dentro de uma **transação**; fora dela: `No transaction in context.`
- A porta ganhou **`suspend`**: JDBC bloqueia, `suspendTransaction` não trava a thread do servidor
- Exposed 1.x: pacotes `org.jetbrains.exposed.v1...`

---

# Quarkus: Hibernate com Panache

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
    public Tarefa adicionar(NovaTarefa nova) { /* new TarefaEntidade(); persist(e); */ }
}
```

- Escrita **sem** `@Transactional`: `TransactionRequiredException` → `500`
- A entidade não é o `record Tarefa`: JPA exige construtor vazio e campos mutáveis

---

# Lado a lado

| | Ktor + Exposed | Quarkus + Panache |
|---|---|---|
| Estilo | SQL em Kotlin | ORM: objeto ↔ tabela |
| Transação | `suspendTransaction { }` explícito | `@Transactional` |
| Pool | HikariCP, configurado por você | Agroal, pela extensão |
| Banco em dev/teste | Testcontainers, no teste | Dev Services, automático |
| Esquema | nunca `SchemaUtils.create` | `strategy=none` |

> Exposed: cada SQL visível. Panache: CRUD quase pronto — mas conheçam o que o Hibernate faz por baixo (N+1, carregamento preguiçoso).

---

# O banco como última barreira

Em 14/09, o Quarkus aceitava `POST {}` e criava tarefa com título `null`.

Agora a coluna é `NOT NULL`:

```
ERROR: null value in column "titulo" of relation "tarefas"
       violates not-null constraint
```

| Pedido | Ktor | Quarkus |
|---|---|---|
| `POST {}` | `400` (kotlinx exige `titulo`) | **`500`** |

> O dado ruim não entrou, mas o cliente recebeu erro **do servidor**. A primeira barreira é **validar** — critério da rubrica.

---

<!-- _class: lead -->

# Testes

## Da rota ao banco de verdade

---

# Duas camadas de teste

<div class="columns">
<div class="col">

**Rota sem banco** <span class="pill-green">ms</span>

```kotlin
testApplication {
  application {
    configurar(RepositorioEmMemoria())
  }
  client.get("/tarefas/abc") // 400
}
```

Quarkus: JUnit puro, `new RecursoDeTarefas()` + repositório em memória

</div>
<div class="col">

**Integração** <span class="pill-blue">banco real</span>

```kotlin
val postgres = PostgreSQLContainer(
    "postgres:17-alpine").apply { start() }

modulo(ConfigBanco(postgres.jdbcUrl,
  postgres.username, postgres.password))
```

Quarkus: `@QuarkusTest` + **Dev Services**, nenhuma linha sobre banco

</div>
</div>

> Rubrica: unitários + Testcontainers, **passando no Docker Desktop e no CI** sem mudar configuração.

---

# Testcontainers na prática

- Container por **classe** (no `companion object`): 4 testes em 2,6 s com a imagem baixada
- Primeira vez baixa a imagem (a `postgres:17` tem 158 MB)
- Testcontainers 2.x: `testcontainers-postgresql`, pacote `org.testcontainers.postgresql`

**Achado desta semana** — Docker Desktop atualizado:

```
Could not find a valid Docker environment
UnixSocketClientProviderStrategy: failed with exception BadRequestException (Status 400)
```

O Quarkus 3.28 traz o Testcontainers **1.21.3**, que não conversa com o Docker Engine 29. Correção: importar o BOM do Testcontainers **1.21.4** antes do BOM do Quarkus.

---

<!-- _class: lead -->

# OpenAPI

## A documentação que sai do código

---

# Ktor × Quarkus

<div class="columns">
<div class="col">

**Ktor** — `describe` (experimental no 3.5)

```kotlin
get("/{id}") { /* ... */ }.describe {
  summary = "Busca uma tarefa pelo id"
  parameters {
    path("id") { schema = jsonSchema<Int>() }
  }
}
swaggerUI(path = "docs") { /* ... */ }
```

`/docs` · `/docs/documentation.yaml`

</div>
<div class="col">

**Quarkus** — só a extensão

```java
@GET @Path("/{id}")
@Operation(summary = "Busca uma tarefa pelo id")
public Tarefa buscar(@PathParam("id") int id)
```

`/q/openapi` · `/q/swagger-ui` (dev/test; em prod, `always-include=true`)

</div>
</div>

> Sem declarar o tipo, o `{id}` do Ktor sai como `string`.

---

# Rodar tudo junto

```bash
docker compose up -d          # PostgreSQL 17, volume "dados"
./gradlew run                 # Ktor, :8080
mvn quarkus:dev               # Quarkus, :8081 (Dev Services)
```

```bash
curl -s -i -X POST localhost:8080/tarefas \
  -H 'Content-Type: application/json' -d '{"titulo":"Persistir"}'
```

Os exemplos: `exemplos/ktor-tarefas` e `exemplos/quarkus-tarefas`, **passos 6 a 9** do `PASSOS.md`.

---

# Entrega da Sprint 1 — 02/10, 23:59

| Critério | Peso |
|---|---|
| CRUD de ≥ 2 entidades com relacionamento, paginação e filtros | 25% |
| Camadas + teste de arquitetura no CI | 25% |
| Flyway, banco via `docker compose`, esquema só por migração | 20% |
| Unitários + Testcontainers, local **e** no CI | 20% |
| Validação, *problem details*, OpenAPI | 10% |

Guia e tarefas: `docs/SPRINT-1.md` e `docs/SPRINT-1-TAREFAS.md`. Apresentações: 28/09 (Coorte B, online) e 30/09 (Coorte A, em sala).

---

# Próximas aulas

- **23/09** — acompanhamento online: tragam o banco subindo e os testes rodando
- **28 e 30/09** — apresentações da Sprint 1
- **05/10** — Sprint 2: Go idiomático e Clean Architecture em Go, Protocol Buffers e Buf
- **07/10** — gRPC e a integração do serviço principal com o serviço Go

---

# Onde estudar depois

| Fonte | Foco |
|---|---|
| `leituras/web2-s1-pte2.md` | Esta aula, com erros comuns e exercícios |
| `jetbrains.com/help/exposed` | Exposed: DSL, transações |
| `quarkus.io/guides/hibernate-orm-panache` | Panache |
| `documentation.red-gate.com/flyway` | Flyway |
| `java.testcontainers.org` · `quarkus.io/guides/databases-dev-services` | Testcontainers e Dev Services |
| `ktor.io/docs/openapi-spec-generation.html` · `quarkus.io/guides/openapi-swaggerui` | OpenAPI |
