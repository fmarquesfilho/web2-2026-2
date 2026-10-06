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

## Kotlin para quem vem do Java

DIM0547 — Turma 01 · Sprint 1 · apoio

Prof. Fernando · UFRN · 2026.2

---

# Aviso: prova adiada para 11/11

| | Antes | Agora |
|---|---|---|
| Prova escrita | 09/11 (segunda) | **11/11 (quarta)**, em laboratório, no horário da aula |
| 09/11 (segunda) | presencial: prova | 🔵 online: acompanhamento de projetos |
| 11/11 (quarta) | online: acompanhamento | 🟢 presencial: **prova escrita** |
| Prova de reposição | 02/12 | 02/12 (sem mudança) |

- Vale no lugar do calendário dos slides 05; o conteúdo e as regras da prova são os mesmos
- Entrega da Sprint 1: **16/10 (sexta), 23:59**

---

# O que esta aula responde

Um trecho do `Rotas.kt` do exemplo de Tarefas, e as perguntas que ele costuma levantar:

```kotlin
fun Application.rotas() {                                   // 1
    val repositorio by inject<RepositorioDeTarefas>()       // 2

    routing {                                               // 3
        get("/tarefas/{id}") {                              // 4
            val id = call.parameters["id"]?.toIntOrNull()   // 5
                ?: throw BadRequestException("O id deve ser um número inteiro")
            call.respond(repositorio.buscar(id) ?: throw NotFoundException("..."))
        }
    }
}
```

1. Por que há um tipo e um ponto antes do nome da função? → *função de extensão*
2. O que o `by` faz? → *delegação*
3. `routing` é palavra da linguagem? Cadê os parênteses? → *lambda no fim da chamada*
4. De onde vem `call`, se ninguém o declarou? → *lambda com receptor*
5. O que são `?.` e `?:` → *nulidade no tipo*

---

# Relembrando: o que muda na escrita

<div class="columns">
<div class="col">

**<span class="pill-red">Java</span>**

```java
final String titulo = "Estudar";
int total = 0;
total = total + 1;

String situacao(Tarefa t) {
    return t.feita() ? "feita" : "pendente";
}

var t = new Tarefa(1, titulo, false);
System.out.println("Tarefa " + t.id());
```

</div>
<div class="col">

**<span class="pill-blue">Kotlin</span>**

```kotlin
val titulo = "Estudar"     // não muda
var total = 0              // muda
total = total + 1

fun situacao(t: Tarefa): String =
    if (t.feita) "feita" else "pendente"

val t = Tarefa(1, titulo)
println("Tarefa ${t.id}")
```

</div>
</div>

- O tipo vem depois do nome (`t: Tarefa`) e pode ser omitido quando o compilador o deduz
- Sem `new`, sem ponto e vírgula; `Unit` faz o papel do `void`
- Função de uma expressão: `=` no lugar de `{ return ... }`

📖 **Ref.** [Kotlin — Basic syntax](https://kotlinlang.org/docs/basic-syntax.html)

---

# `if` e `when` devolvem valor

```kotlin
// if no lugar do operador ternário
val situacao = if (tarefa.feita) "feita" else "pendente"

// when: o valor do ramo escolhido é o valor da função (Tarefa.kt, Web II)
fun violacoes(): List<String> = when {
    titulo.isBlank()    -> listOf("titulo: não pode ficar em branco")
    titulo.length > 200 -> listOf("titulo: no máximo 200 caracteres")
    else                -> emptyList()
}
```

- Em Java, `if` é comando: a variável é declarada antes e atribuída em cada ramo
- Em Kotlin, `if`, `when` e `try` são expressões: o resultado vai direto para o `val`
- Por isso a linguagem não tem o operador `? :`

📖 **Ref.** [Kotlin — Conditions and loops](https://kotlinlang.org/docs/control-flow.html) · ▶ [Demonstração 1][demo1]

---

# Nulidade faz parte do tipo

| Escrita | Significado | Em Java |
|---|---|---|
| `Tarefa` | nunca é `null` | sem equivalente; depende de convenção |
| `Tarefa?` | pode ser `null` | toda referência |
| `a?.b` | `b` se `a` não for `null`; senão `null` | `a != null ? a.b : null` |
| `a ?: c` | `a` se não for `null`; senão `c` | `a != null ? a : c` |
| `a!!` | "garanto que não é `null`"; falha em execução se for | uso direto, com risco de `NullPointerException` |

```kotlin
// Rotas.kt (Web II): cada ?: decide o que fazer quando o valor falta
val id = call.parameters["id"]?.toIntOrNull()
    ?: throw BadRequestException("O id deve ser um número inteiro")
val tarefa = repositorio.buscar(id)
    ?: throw NotFoundException("A tarefa $id não existe")
call.respond(tarefa)             // aqui `tarefa` já é Tarefa, não Tarefa?
```

📖 **Ref.** [Kotlin — Null safety](https://kotlinlang.org/docs/null-safety.html) · ▶ [Demonstração 1][demo1]

---

# Classes: o construtor declara as propriedades

<div class="columns">
<div class="col">

**<span class="pill-red">Java</span>**

```java
public record Tarefa(
    int id, String titulo, boolean feita) {

  public Tarefa(int id, String titulo) {
    this(id, titulo, false);
  }
}

var a = new Tarefa(1, "Estudar");
var b = new Tarefa(
    a.id(), a.titulo(), true);
```

</div>
<div class="col">

**<span class="pill-blue">Kotlin</span>**

```kotlin
data class Tarefa(
    val id: Int,
    val titulo: String,
    val feita: Boolean = false,
)



val a = Tarefa(1, "Estudar")
val b = a.copy(feita = true)
```

</div>
</div>

- `data class` gera `equals`, `hashCode`, `toString` e `copy`; `==` compara os valores
- Valor padrão (`= false`) substitui construtores sobrecarregados
- Argumento nomeado (`feita = true`) deixa claro o que cada valor é
- `object` declara uma instância única; `companion object` guarda o que em Java seria `static`

📖 **Ref.** [Kotlin — Data classes](https://kotlinlang.org/docs/data-classes.html) · ▶ [Demonstração 2][demo2]

---

# Entendendo: lambda

Uma lambda é um bloco de código guardado num valor. O tipo dela se escreve `(Entrada) -> Saída`.

```kotlin
val feita: (Tarefa) -> Boolean = { tarefa: Tarefa -> tarefa.feita }   // completa
val feita: (Tarefa) -> Boolean = { tarefa -> tarefa.feita }           // tipo do parâmetro deduzido
val feita: (Tarefa) -> Boolean = { it.feita }                         // um parâmetro só: `it`
```

| Tipo em Kotlin | Interface em Java | Exemplo no curso |
|---|---|---|
| `() -> Unit` | `Runnable` | `onAdicionar`, `onVoltar` |
| `(Int) -> Unit` | `Consumer<Integer>` | `onAlternar`, `onAbrir` |
| `(Tarefa) -> Boolean` | `Predicate<Tarefa>` | a condição do `filter` |
| `(Tarefa) -> String` | `Function<Tarefa, String>` | a transformação do `map` |

- As chaves delimitam a lambda; a seta separa parâmetros e corpo
- O valor da lambda é a última expressão; não se escreve `return`

📖 **Ref.** [Kotlin — Higher-order functions and lambdas](https://kotlinlang.org/docs/lambdas.html) · ▶ [Demonstração 3][demo3]

---

# Entendendo: lambda no fim da chamada

Quando o último parâmetro é uma função, a lambda pode sair dos parênteses. A mesma chamada, em quatro formas:

```kotlin
tarefas.filter({ tarefa -> !tarefa.feita })    // 1. lambda como argumento comum
tarefas.filter() { tarefa -> !tarefa.feita }   // 2. lambda depois dos parênteses
tarefas.filter { tarefa -> !tarefa.feita }     // 3. parênteses vazios somem
tarefas.filter { !it.feita }                   // 4. com `it`
```

É a regra que explica a forma de quase todo o código do curso:

```kotlin
install(ContentNegotiation) { json() }                  // Ktor
Button(onClick = onAdicionar) { Text("Adicionar") }     // Compose
items(tarefas, key = { it.id }) { tarefa -> ... }       // duas lambdas: uma nomeada, uma no fim
```

> Ao ler `nome { ... }` ou `nome(args) { ... }`: é uma chamada de função, e o bloco é o último argumento.

📖 **Ref.** [Kotlin — Passing trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas) · ▶ [Demonstração 3][demo3]

---

# Coleções: as operações são funções da lista

<div class="columns">
<div class="col">

**<span class="pill-red">Java</span>**

```java
List<String> pendentes = tarefas.stream()
    .filter(t -> !t.feita())
    .map(t -> t.titulo())
    .toList();

List<Tarefa> depois = tarefas.stream()
    .map(t -> t.id() == id
        ? new Tarefa(t.id(), t.titulo(),
                     !t.feita())
        : t)
    .toList();
```

</div>
<div class="col">

**<span class="pill-blue">Kotlin</span>**

```kotlin
val pendentes = tarefas
    .filter { !it.feita }
    .map { it.titulo }


val depois = tarefas.map {
    if (it.id == id)
        it.copy(feita = !it.feita)
    else it
}
```

</div>
</div>

- `List` é somente leitura; para alterar no lugar, o tipo é `MutableList`
- "Alterar" uma lista imutável é criar outra: `map`, `filter`, `+`
- No Compose, é a reatribuição da lista nova que avisa a tela para se redesenhar

📖 **Ref.** [Kotlin — Collections in Java and Kotlin](https://kotlinlang.org/docs/java-to-kotlin-collections-guide.html) · ▶ [Demonstração 4][demo4]

---

# Entendendo: função de extensão

Acrescenta uma função a um tipo que já existe, sem herança e sem alterar a classe.

<div class="columns">
<div class="col">

**<span class="pill-red">Java</span>** — método estático utilitário

```java
class Tarefas {
  static List<Tarefa> alternando(
      List<Tarefa> lista, int id) { ... }
}

Tarefas.alternando(lista, 2);
```

</div>
<div class="col">

**<span class="pill-blue">Kotlin</span>** — o tipo antes do ponto é o receptor

```kotlin
fun List<Tarefa>.alternando(id: Int) =
    map { if (it.id == id)
        it.copy(feita = !it.feita) else it }

lista.alternando(2)
```

</div>
</div>

- Dentro da função, `this` é o receptor, e pode ser omitido (`map` é `this.map`)
- Só enxerga a parte pública do tipo; é uma função comum, escolhida na compilação
- No curso: `fun Application.modulo()`, `fun Application.rotas()`, `fun List<Tarefa>.comNova(...)`

📖 **Ref.** [Kotlin — Extensions](https://kotlinlang.org/docs/extensions.html) · ▶ [Demonstração 5][demo5]

---

# Entendendo: lambda com receptor

Junta as duas ideias: uma lambda que, por dentro, tem um `this`.

<div class="columns">
<div class="col">

**Lambda comum:** `(Conexao) -> Unit`

```kotlin
configurar { conexao ->
    conexao.url = "jdbc:..."
    conexao.usuario = "tarefas"
}
```

</div>
<div class="col">

**Com receptor:** `Conexao.() -> Unit`

```kotlin
configurar {
    url = "jdbc:..."          // this.url
    usuario = "tarefas"       // this.usuario
}
```

</div>
</div>

```kotlin
// Banco.kt (Web II): apply recebe uma lambda com receptor e devolve o próprio objeto
HikariConfig().apply {
    jdbcUrl = config.url          // this.jdbcUrl
    maximumPoolSize = 5
}
```

> Dentro de um bloco, pergunte: quem é o `this` aqui? A resposta está no tipo do parâmetro: `Tipo.() -> Unit`.

📖 **Ref.** [Kotlin — Function literals with receiver](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver) · ▶ [Demonstração 6][demo6]

---

# Lendo o código do Ktor com essas três peças

```kotlin
fun Application.rotas() {                          // extensão de Application
    val repositorio by inject<RepositorioDeTarefas>()

    routing {                                      // lambda no fim; this: Routing
        route("/tarefas") {                        // this: Route
            get("/{id}") {                         // this: RoutingContext (é dele o `call`)
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("...")
                call.respond(repositorio.buscar(id) ?: throw NotFoundException("..."))
            }
        }
    }
}
```

O mesmo trecho, sem as abreviações da linguagem:

```kotlin
this.routing({ this.route("/tarefas", { this.get("/{id}", { this.call.respond(...) }) }) })
```

- Cada bloco é o último argumento de uma função, e cada função define o `this` do seu bloco
- As rotas são chamadas de função comuns: por isso cabem `if`, laços e funções auxiliares

📖 **Ref.** [Ktor — Routing](https://ktor.io/docs/server-routing.html) · [Kotlin — Type-safe builders](https://kotlinlang.org/docs/type-safe-builders.html) · ▶ [Demonstração 7][demo7]

---

# Funções de escopo

Cinco funções da biblioteca padrão que executam um bloco sobre um objeto.

| Função | O objeto é | Devolve | Uso típico | No curso |
|---|---|---|---|---|
| `let` | `it` | o resultado do bloco | fazer algo só se não for `null`: `x?.let { }` | `selecionada?.let(alternar)` |
| `apply` | `this` | o próprio objeto | configurar um objeto recém-criado | `HikariConfig().apply { }` |
| `also` | `it` | o próprio objeto | efeito extra, como um registro | — |
| `run` | `this` | o resultado do bloco | calcular algo a partir do objeto | — |
| `with(x)` | `this` | o resultado do bloco | várias chamadas sobre o mesmo objeto | — |

- Para ler: primeiro veja se o objeto é `it` ou `this`; depois, o que a chamada devolve
- Para escrever: comece por `let` e `apply`, que cobrem a maior parte dos casos

📖 **Ref.** [Kotlin — Scope functions](https://kotlinlang.org/docs/scope-functions.html) · ▶ [Demonstração 6][demo6]

---

# Delegação: o que o `by` faz

`by` entrega a outro objeto o trabalho de ler (e gravar) uma propriedade.

```kotlin
// Rotas.kt: a dependência é buscada no Koin no primeiro uso, e guardada
val repositorio by inject<RepositorioDeTarefas>()

repositorio.listar()         // usa-se como um RepositorioDeTarefas comum
```

```kotlin
// A mesma ideia na biblioteca padrão: calculado só no primeiro uso
val json by lazy { Json { explicitNulls = false } }
```

- Sem `by`, a variável guardaria um `Lazy<RepositorioDeTarefas>`, e cada uso seria `repositorio.value.listar()`
- Com `by`, o compilador troca cada leitura por uma chamada a `getValue()` do delegado
- `inject<RepositorioDeTarefas>()`: o tipo entre `< >` substitui o `RepositorioDeTarefas.class` do Java

📖 **Ref.** [Kotlin — Delegated properties](https://kotlinlang.org/docs/delegated-properties.html) · [Koin — Ktor](https://insert-koin.io/docs/reference/koin-ktor/ktor) · ▶ [Demonstração 9][demo9]

---

# `suspend`: esperar sem prender a thread

```kotlin
// Tarefa.kt: a porta do repositório
interface RepositorioDeTarefas {
    suspend fun listar(): List<Tarefa>
    suspend fun buscar(id: Int): Tarefa?
    suspend fun adicionar(nova: NovaTarefa): Tarefa
}
```

- `suspend` marca uma função que pode pausar (enquanto o banco responde) e continuar depois
- Enquanto ela espera, a thread atende outras requisições
- Uma função `suspend` só pode ser chamada de outra função `suspend` ou de dentro de uma corrotina
- No Ktor, o bloco de cada rota já é `suspend`: por isso `repositorio.buscar(id)` é chamado direto
- Em Java, o caminho equivalente passa por `CompletableFuture` ou por threads virtuais

| | Thread da JVM | Corrotina |
|---|---|---|
| Quem gerencia | o sistema operacional | a biblioteca `kotlinx.coroutines` |
| Custo | alguns MB cada | leve: milhares numa mesma thread |

📖 **Ref.** [Kotlin — Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html) · ▶ [Demonstração 10][demo10]

---

# Escrever de modo idiomático

<div class="columns">
<div class="col">

**Java escrito em Kotlin**

```kotlin
fun descrever(tarefa: Tarefa?): String {
    var texto: String
    if (tarefa == null) {
        texto = "nenhuma tarefa"
    } else {
        if (tarefa.feita) {
            texto = tarefa.titulo + " (feita)"
        } else {
            texto = tarefa.titulo + " (pendente)"
        }
    }
    return texto
}
```

</div>
<div class="col">

**Kotlin idiomático**

```kotlin
fun descrever(tarefa: Tarefa?) = when {
    tarefa == null -> "nenhuma tarefa"
    tarefa.feita -> "${tarefa.titulo} (feita)"
    else -> "${tarefa.titulo} (pendente)"
}
```

</div>
</div>

- Prefira `val`; use `var` só quando o valor muda
- Use `if` e `when` como expressão, e template de string no lugar de `+`
- Use `filter` e `map` no lugar de laço com índice; `data class` no lugar de getters escritos à mão
- Evite `!!`: prefira `?.`, `?:` ou um teste de `null`

📖 **Ref.** [Kotlin — Idioms](https://kotlinlang.org/docs/idioms.html) · [Coding conventions](https://kotlinlang.org/docs/coding-conventions.html) · ▶ [Demonstração 11][demo11]

---

# Demonstrações no Kotlin Playground

Cada link abre o código já carregado em [play.kotlinlang.org](https://play.kotlinlang.org/), e o botão **Run** executa. Cada demonstração termina com três experimentos. Os arquivos também estão em `exemplos/kotlin-playground/`.

| | Demonstração | O que mostra |
|---|---|---|
| 1 | ▶ [Expressões e nulidade][demo1] | `if`, `when`, `?.`, `?:` |
| 2 | ▶ [Classes][demo2] | `data class`, `copy`, `object`, `companion object` |
| 3 | ▶ [Lambdas][demo3] | tipos de função, `it`, lambda no fim da chamada |
| 4 | ▶ [Coleções][demo4] | `filter`, `map`, `find`, listas imutáveis |
| 5 | ▶ [Funções de extensão][demo5] | receptor, `this`, propriedade de extensão |
| 6 | ▶ [Lambda com receptor][demo6] | `Tipo.() -> Unit`, `apply`, `let`, `also`, `run`, `with` |
| 7 | ▶ [Um Ktor em miniatura][demo7] | `routing { get("/tarefas") { } }` em 30 linhas |
| 8 | ▶ [Um Compose em miniatura][demo8] | `Coluna { Texto("...") }` e eventos por lambda |
| 9 | ▶ [Delegação][demo9] | `by`, `by lazy`, um delegado que observa mudanças |
| 10 | ▶ [Corrotinas][demo10] | `suspend`, `async`, dez mil corrotinas |
| 11 | ▶ [Idiomático][demo11] | o mesmo programa em estilo Java e em estilo Kotlin |

---

# Para estudar

- Leitura: `leituras/web2-s1-kotlin.md` — o conteúdo desta aula por extenso, com os trechos do exemplo de Tarefas comentados e exercícios
- [Tour of Kotlin](https://kotlinlang.org/docs/kotlin-tour-welcome.html): trilha oficial com código executável e exercícios; a parte intermediária cobre extensões, funções de escopo e lambdas com receptor
- [Kotlin Koans](https://play.kotlinlang.org/koans): exercícios no navegador, pensados para quem vem do Java

## Fontes desta aula

- Kotlin — [Comparison to Java](https://kotlinlang.org/docs/comparison-to-java.html) · [Lambdas](https://kotlinlang.org/docs/lambdas.html) · [Extensions](https://kotlinlang.org/docs/extensions.html) · [Scope functions](https://kotlinlang.org/docs/scope-functions.html) · [Null safety](https://kotlinlang.org/docs/null-safety.html) · [Idioms](https://kotlinlang.org/docs/idioms.html)
- Kotlin — [Delegated properties](https://kotlinlang.org/docs/delegated-properties.html) · [Type-safe builders](https://kotlinlang.org/docs/type-safe-builders.html) · [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html)
- Ktor — [Routing](https://ktor.io/docs/server-routing.html) · JetBrains — [Kotlin for Education](https://kotlinlang.org/education/)

A documentação do Kotlin é publicada sob a licença Apache 2.0. Os trechos de código vêm do exemplo de Tarefas deste repositório.

[demo1]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyAxIOKAlCBleHByZXNzw7VlcyBlIG51bGlkYWRlXG4vLyBFbSBLb3RsaW4sIGBpZmAgZSBgd2hlbmAgZGV2b2x2ZW0gdmFsb3IsIGUgbyB0aXBvIGRpeiBzZSB1bWEgcmVmZXLDqm5jaWEgcG9kZSBzZXIgbnVsYS5cblxuZGF0YSBjbGFzcyBUYXJlZmEodmFsIGlkOiBJbnQsIHZhbCB0aXR1bG86IFN0cmluZywgdmFsIGZlaXRhOiBCb29sZWFuID0gZmFsc2UpXG5cbnZhbCB0YXJlZmFzID0gbGlzdE9mKFRhcmVmYSgxLCBcIkVzdHVkYXIgS290bGluXCIpLCBUYXJlZmEoMiwgXCJFbnRlbmRlciBsYW1iZGFzXCIsIGZlaXRhID0gdHJ1ZSkpXG5cbi8vIGBUYXJlZmE/YDogcG9kZSBkZXZvbHZlciB1bWEgdGFyZWZhIG91IG51bGwuIFNlbSBvIGA/YCwgbnVsbCBuw6NvIGNvbXBpbGEuXG5mdW4gYnVzY2FyKGlkOiBJbnQpOiBUYXJlZmE/ID0gdGFyZWZhcy5maW5kIHsgaXQuaWQgPT0gaWQgfVxuXG4vLyBgaWZgIMOpIGV4cHJlc3PDo286IHN1YnN0aXR1aSBvIG9wZXJhZG9yIHRlcm7DoXJpbyBkbyBKYXZhIChjb25kID8gYSA6IGIpLlxuZnVuIHNpdHVhY2FvKHRhcmVmYTogVGFyZWZhKTogU3RyaW5nID0gaWYgKHRhcmVmYS5mZWl0YSkgXCJmZWl0YVwiIGVsc2UgXCJwZW5kZW50ZVwiXG5cbi8vIGB3aGVuYCDDqSBleHByZXNzw6NvOiBvIHZhbG9yIGRvIHJhbW8gZXNjb2xoaWRvIMOpIG8gdmFsb3IgZGEgZnVuw6fDo28uXG5mdW4gdmlvbGFjb2VzKHRpdHVsbzogU3RyaW5nKTogTGlzdDxTdHJpbmc+ID0gd2hlbiB7XG4gICAgdGl0dWxvLmlzQmxhbmsoKSAtPiBsaXN0T2YoXCJ0aXR1bG86IG7Do28gcG9kZSBmaWNhciBlbSBicmFuY29cIilcbiAgICB0aXR1bG8ubGVuZ3RoID4gMjAwIC0+IGxpc3RPZihcInRpdHVsbzogbm8gbcOheGltbyAyMDAgY2FyYWN0ZXJlc1wiKVxuICAgIGVsc2UgLT4gZW1wdHlMaXN0KClcbn1cblxuLy8gTyBtZXNtbyByYWNpb2PDrW5pbyBkYSByb3RhIEdFVCAvdGFyZWZhcy97aWR9IGRvIGV4ZW1wbG8gZGUgVGFyZWZhcy5cbmZ1biByZXNwb25kZXIocGFyYW1ldHJvOiBTdHJpbmc/KTogU3RyaW5nIHtcbiAgICAvLyA/LiAgc8OzIGNoYW1hIHRvSW50T3JOdWxsKCkgc2UgYHBhcmFtZXRyb2AgbsOjbyBmb3IgbnVsbFxuICAgIC8vID86ICAob3BlcmFkb3IgRWx2aXMpIGTDoSBvIHZhbG9yIGRhIGRpcmVpdGEgcXVhbmRvIGEgZXNxdWVyZGEgw6kgbnVsbFxuICAgIHZhbCBpZCA9IHBhcmFtZXRybz8udG9JbnRPck51bGwoKSA/OiByZXR1cm4gXCI0MDA6IG8gaWQgZGV2ZSBzZXIgdW0gbsO6bWVybyBpbnRlaXJvXCJcbiAgICB2YWwgdGFyZWZhID0gYnVzY2FyKGlkKSA/OiByZXR1cm4gXCI0MDQ6IGEgdGFyZWZhICRpZCBuw6NvIGV4aXN0ZVwiXG4gICAgLy8gRGFxdWkgcGFyYSBiYWl4byBvIGNvbXBpbGFkb3Igc2FiZSBxdWUgYHRhcmVmYWAgbsOjbyDDqSBudWxsIChzbWFydCBjYXN0KS5cbiAgICByZXR1cm4gXCIyMDA6ICR7dGFyZWZhLnRpdHVsb30gKCR7c2l0dWFjYW8odGFyZWZhKX0pXCJcbn1cblxuZnVuIG1haW4oKSB7XG4gICAgcHJpbnRsbihyZXNwb25kZXIoXCIxXCIpKVxuICAgIHByaW50bG4ocmVzcG9uZGVyKFwiMlwiKSlcbiAgICBwcmludGxuKHJlc3BvbmRlcihcImFiY1wiKSlcbiAgICBwcmludGxuKHJlc3BvbmRlcihcIjlcIikpXG4gICAgcHJpbnRsbihyZXNwb25kZXIobnVsbCkpXG4gICAgcHJpbnRsbih2aW9sYWNvZXMoXCIgICBcIikpXG4gICAgcHJpbnRsbih2aW9sYWNvZXMoXCJFc3R1ZGFyXCIpKVxuXG4gICAgLy8gRXhwZXJpbWVudGU6XG4gICAgLy8gMS4gVHJvcXVlIGBUYXJlZmE/YCBwb3IgYFRhcmVmYWAgZW0gYnVzY2FyKCkgZSBsZWlhIG8gZXJybyBkZSBjb21waWxhw6fDo28uXG4gICAgLy8gMi4gQXBhZ3VlIHVtIGA/OmAgZGUgcmVzcG9uZGVyKCkgZSB2ZWphIG9uZGUgbyBjb21waWxhZG9yIHJlY2xhbWEuXG4gICAgLy8gMy4gRXNjcmV2YSBidXNjYXIoOSkhIS50aXR1bG8gZSByb2RlOiBgISFgIHRyb2NhIG8gZXJybyBkZSBjb21waWxhw6fDo28gcG9yIHVtXG4gICAgLy8gICAgTnVsbFBvaW50ZXJFeGNlcHRpb24gZW0gZXhlY3XDp8Ojby5cbn1cbiJ9
[demo2]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyAyIOKAlCBjbGFzc2VzLCBkYXRhIGNsYXNzZXMgZSBvYmplY3Rcbi8vIFVtYSBsaW5oYSBkZSBLb3RsaW4gZGVjbGFyYSBvIHF1ZSBlbSBKYXZhIHBlZGUgY29uc3RydXRvciwgZ2V0dGVycywgZXF1YWxzLCBoYXNoQ29kZSBlIHRvU3RyaW5nLlxuXG4vLyBDb25zdHJ1dG9yIHByaW3DoXJpbzogb3MgcGFyw6JtZXRyb3MgY29tIGB2YWxgIGrDoSBzw6NvIHByb3ByaWVkYWRlcy5cbi8vIGBmZWl0YWAgdGVtIHZhbG9yIHBhZHLDo286IHF1ZW0gY3JpYSBhIHRhcmVmYSBwb2RlIG9taXRpci5cbmRhdGEgY2xhc3MgVGFyZWZhKHZhbCBpZDogSW50LCB2YWwgdGl0dWxvOiBTdHJpbmcsIHZhbCBmZWl0YTogQm9vbGVhbiA9IGZhbHNlKVxuXG4vLyBDbGFzc2UgY29tdW06IHNlbSBgZGF0YWAsIGVxdWFscyBjb21wYXJhIHJlZmVyw6puY2lhcyBlIHRvU3RyaW5nIG1vc3RyYSBvIGVuZGVyZcOnby5cbmNsYXNzIFRhcmVmYUNvbXVtKHZhbCBpZDogSW50LCB2YWwgdGl0dWxvOiBTdHJpbmcpXG5cbi8vIGBvYmplY3RgOiB1bWEgw7puaWNhIGluc3TDom5jaWEsIGNyaWFkYSBwZWxhIGxpbmd1YWdlbSAobyBzaW5nbGV0b24gZG8gSmF2YSwgc2VtIGPDs2RpZ28pLlxub2JqZWN0IENvbnRhZG9yIHtcbiAgICBwcml2YXRlIHZhciB1bHRpbW8gPSAwXG4gICAgZnVuIHByb3hpbW8oKTogSW50ID0gKyt1bHRpbW9cbn1cblxuLy8gS290bGluIG7Do28gdGVtIGBzdGF0aWNgLiBPIHF1ZSBwZXJ0ZW5jZSDDoCBjbGFzc2UsIGUgbsOjbyDDoCBpbnN0w6JuY2lhLCB2YWkgbm8gY29tcGFuaW9uIG9iamVjdC5cbmRhdGEgY2xhc3MgQ29uZmlnQmFuY28odmFsIHVybDogU3RyaW5nLCB2YWwgdXN1YXJpbzogU3RyaW5nKSB7XG4gICAgY29tcGFuaW9uIG9iamVjdCB7XG4gICAgICAgIGZ1biBsb2NhbCgpID0gQ29uZmlnQmFuY28odXJsID0gXCJqZGJjOnBvc3RncmVzcWw6Ly9sb2NhbGhvc3Q6NTQzMi90YXJlZmFzXCIsIHVzdWFyaW8gPSBcInRhcmVmYXNcIilcbiAgICB9XG59XG5cbmZ1biBtYWluKCkge1xuICAgIC8vIE7Do28gZXhpc3RlIGBuZXdgLiBBcmd1bWVudG9zIG5vbWVhZG9zIGRlaXhhbSBhIGNoYW1hZGEgbGVnw612ZWwgZSBwZXJtaXRlbSB0cm9jYXIgYSBvcmRlbS5cbiAgICB2YWwgYSA9IFRhcmVmYSgxLCBcIkVzdHVkYXIgS290bGluXCIpXG4gICAgdmFsIGIgPSBUYXJlZmEodGl0dWxvID0gXCJFc3R1ZGFyIEtvdGxpblwiLCBpZCA9IDEpXG5cbiAgICBwcmludGxuKGEpICAgICAgICAgICAgICAgICAgICAgICAvLyB0b1N0cmluZyBnZXJhZG9cbiAgICBwcmludGxuKGEgPT0gYikgICAgICAgICAgICAgICAgICAvLyBlcXVhbHMgZ2VyYWRvOiBjb21wYXJhIG9zIHZhbG9yZXMgKD09IGNoYW1hIGVxdWFscylcbiAgICBwcmludGxuKGEgPT09IGIpICAgICAgICAgICAgICAgICAvLyA9PT0gY29tcGFyYSByZWZlcsOqbmNpYXMsIGNvbW8gbyA9PSBkbyBKYXZhXG5cbiAgICAvLyBjb3B5OiB1bWEgY8OzcGlhIGNvbSBwYXJ0ZSBkb3MgdmFsb3JlcyB0cm9jYWRhLiBPIG9yaWdpbmFsIG7Do28gbXVkYS5cbiAgICB2YWwgZmVpdGEgPSBhLmNvcHkoZmVpdGEgPSB0cnVlKVxuICAgIHByaW50bG4oZmVpdGEpXG4gICAgcHJpbnRsbihhKVxuXG4gICAgLy8gRGVzZXN0cnV0dXJhw6fDo286IHVtYSB2YXJpw6F2ZWwgcGFyYSBjYWRhIHByb3ByaWVkYWRlLCBuYSBvcmRlbSBkbyBjb25zdHJ1dG9yLlxuICAgIHZhbCAoaWQsIHRpdHVsbykgPSBmZWl0YVxuICAgIHByaW50bG4oXCIkaWQ6ICR0aXR1bG9cIilcblxuICAgIHByaW50bG4oVGFyZWZhQ29tdW0oMSwgXCJ4XCIpID09IFRhcmVmYUNvbXVtKDEsIFwieFwiKSkgICAvLyBmYWxzZTogbsOjbyDDqSBkYXRhIGNsYXNzXG5cbiAgICBwcmludGxuKENvbnRhZG9yLnByb3hpbW8oKSlcbiAgICBwcmludGxuKENvbnRhZG9yLnByb3hpbW8oKSlcbiAgICBwcmludGxuKENvbmZpZ0JhbmNvLmxvY2FsKCkpXG5cbiAgICAvLyBFeHBlcmltZW50ZTpcbiAgICAvLyAxLiBUZW50ZSBhLmZlaXRhID0gdHJ1ZS4gTyBxdWUgbyBjb21waWxhZG9yIGRpej8gKHZhbCBuw6NvIGFjZWl0YSByZWF0cmlidWnDp8OjbylcbiAgICAvLyAyLiBUaXJlIGEgcGFsYXZyYSBgZGF0YWAgZGUgVGFyZWZhIGUgcm9kZSBkZSBub3ZvOiBxdWFpcyBsaW5oYXMgbXVkYW0/XG4gICAgLy8gMy4gQ3JpZSB1bWEgdGFyZWZhIHBhc3NhbmRvIHPDsyBgdGl0dWxvYC4gUG9yIHF1ZSBuw6NvIGNvbXBpbGE/XG59XG4ifQ==
[demo3]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyAzIOKAlCBsYW1iZGFzIGUgdGlwb3MgZGUgZnVuw6fDo29cbi8vIFVtYSBsYW1iZGEgw6kgdW0gYmxvY28gZGUgY8OzZGlnbyBndWFyZGFkbyBudW0gdmFsb3IuIE8gdGlwbyBkZWxhIHNlIGVzY3JldmUgKEVudHJhZGEpIC0+IFNhw61kYS5cblxuZGF0YSBjbGFzcyBUYXJlZmEodmFsIGlkOiBJbnQsIHZhbCB0aXR1bG86IFN0cmluZywgdmFsIGZlaXRhOiBCb29sZWFuID0gZmFsc2UpXG5cbi8vIEZ1bsOnw6NvIGRlIG9yZGVtIHN1cGVyaW9yOiByZWNlYmUgb3V0cmEgZnVuw6fDo28gY29tbyBwYXLDom1ldHJvLlxuLy8gRW0gSmF2YSwgYGNvbmRpY2FvYCBzZXJpYSB1bSBQcmVkaWNhdGU8VGFyZWZhPi5cbmZ1biBjb250YXIodGFyZWZhczogTGlzdDxUYXJlZmE+LCBjb25kaWNhbzogKFRhcmVmYSkgLT4gQm9vbGVhbik6IEludCB7XG4gICAgdmFyIHRvdGFsID0gMFxuICAgIGZvciAodGFyZWZhIGluIHRhcmVmYXMpIGlmIChjb25kaWNhbyh0YXJlZmEpKSB0b3RhbCsrXG4gICAgcmV0dXJuIHRvdGFsXG59XG5cbi8vIE8gw7psdGltbyBwYXLDom1ldHJvIMOpIHVtYSBmdW7Dp8OjbzogcXVlbSBjaGFtYSBwb2RlIGVzY3JldmVyIGEgbGFtYmRhIGRlcG9pcyBkb3MgcGFyw6pudGVzZXMuXG5mdW4gcmVwZXRpcih2ZXplczogSW50LCBhY2FvOiAoSW50KSAtPiBVbml0KSB7XG4gICAgZm9yIChpIGluIDEuLnZlemVzKSBhY2FvKGkpXG59XG5cbi8vIFVtYSBmdW7Dp8OjbyBjb211bSB0YW1iw6ltIHBvZGUgc2VyIHBhc3NhZGEgY29tbyB2YWxvciwgY29tIGA6OmAuXG5mdW4gZXN0YUZlaXRhKHRhcmVmYTogVGFyZWZhKTogQm9vbGVhbiA9IHRhcmVmYS5mZWl0YVxuXG5mdW4gbWFpbigpIHtcbiAgICB2YWwgdGFyZWZhcyA9IGxpc3RPZihUYXJlZmEoMSwgXCJFc3R1ZGFyIEtvdGxpblwiLCBmZWl0YSA9IHRydWUpLCBUYXJlZmEoMiwgXCJFbnRlbmRlciBsYW1iZGFzXCIpKVxuXG4gICAgLy8gQSBtZXNtYSBsYW1iZGEsIGRhIGZvcm1hIG1haXMgbG9uZ2Egw6AgbWFpcyBjdXJ0YTpcbiAgICB2YWwgZm9ybWExOiAoVGFyZWZhKSAtPiBCb29sZWFuID0geyB0YXJlZmE6IFRhcmVmYSAtPiB0YXJlZmEuZmVpdGEgfVxuICAgIHZhbCBmb3JtYTI6IChUYXJlZmEpIC0+IEJvb2xlYW4gPSB7IHRhcmVmYSAtPiB0YXJlZmEuZmVpdGEgfSAgICAvLyBvIHRpcG8gZG8gcGFyw6JtZXRybyDDqSBpbmZlcmlkb1xuICAgIHZhbCBmb3JtYTM6IChUYXJlZmEpIC0+IEJvb2xlYW4gPSB7IGl0LmZlaXRhIH0gICAgICAgICAgICAgICAgICAvLyB1bSBzw7MgcGFyw6JtZXRybzogY2hhbWEtc2UgYGl0YFxuXG4gICAgcHJpbnRsbihjb250YXIodGFyZWZhcywgZm9ybWExKSlcbiAgICBwcmludGxuKGNvbnRhcih0YXJlZmFzLCBmb3JtYTIpKVxuICAgIHByaW50bG4oY29udGFyKHRhcmVmYXMsIGZvcm1hMykpXG5cbiAgICAvLyBBIG1lc21hIGNoYW1hZGEsIGRhIGZvcm1hIG1haXMgbG9uZ2Egw6AgbWFpcyBjdXJ0YTpcbiAgICBwcmludGxuKGNvbnRhcih0YXJlZmFzLCB7IHRhcmVmYSAtPiB0YXJlZmEuZmVpdGEgfSkpICAgLy8gbGFtYmRhIGRlbnRybyBkb3MgcGFyw6pudGVzZXNcbiAgICBwcmludGxuKGNvbnRhcih0YXJlZmFzKSB7IHRhcmVmYSAtPiB0YXJlZmEuZmVpdGEgfSkgICAgLy8gbGFtYmRhIG5vIGZpbSwgZm9yYSBkb3MgcGFyw6pudGVzZXNcbiAgICBwcmludGxuKGNvbnRhcih0YXJlZmFzKSB7IGl0LmZlaXRhIH0pICAgICAgICAgICAgICAgICAgLy8gY29tIGBpdGBcbiAgICBwcmludGxuKGNvbnRhcih0YXJlZmFzLCA6OmVzdGFGZWl0YSkpICAgICAgICAgICAgICAgICAgLy8gcmVmZXLDqm5jaWEgYSB1bWEgZnVuw6fDo29cblxuICAgIC8vIFF1YW5kbyBhIGxhbWJkYSDDqSBvIMO6bmljbyBhcmd1bWVudG8sIG9zIHBhcsOqbnRlc2VzIHNvbWVtLlxuICAgIHJlcGV0aXIoMykgeyBudW1lcm8gLT4gcHJpbnRsbihcInZvbHRhICRudW1lcm9cIikgfVxuXG4gICAgLy8gTyB2YWxvciBkYSBsYW1iZGEgw6kgYSDDumx0aW1hIGV4cHJlc3PDo28gZGVsYS4gTsOjbyBzZSBlc2NyZXZlIGByZXR1cm5gLlxuICAgIHZhbCBkb2JybzogKEludCkgLT4gSW50ID0geyBudW1lcm8gLT5cbiAgICAgICAgdmFsIHJlc3VsdGFkbyA9IG51bWVybyAqIDJcbiAgICAgICAgcmVzdWx0YWRvXG4gICAgfVxuICAgIHByaW50bG4oZG9icm8oMjEpKVxuXG4gICAgLy8gVW1hIGxhbWJkYSBlbnhlcmdhIChlIHBvZGUgYWx0ZXJhcikgYXMgdmFyacOhdmVpcyBkZSBvbmRlIGZvaSBjcmlhZGEuXG4gICAgdmFyIGNsaXF1ZXMgPSAwXG4gICAgdmFsIGFvQ2xpY2FyOiAoKSAtPiBVbml0ID0geyBjbGlxdWVzKysgfVxuICAgIGFvQ2xpY2FyKClcbiAgICBhb0NsaWNhcigpXG4gICAgcHJpbnRsbihcImNsaXF1ZXM6ICRjbGlxdWVzXCIpXG5cbiAgICAvLyBFeHBlcmltZW50ZTpcbiAgICAvLyAxLiBNdWRlIGNvbnRhcih0YXJlZmFzKSB7IGl0LmZlaXRhIH0gcGFyYSBjb250YXIgYXMgdGFyZWZhcyBwZW5kZW50ZXMuXG4gICAgLy8gMi4gRGVjbGFyZSB2YWwgYW9BbHRlcm5hcjogKEludCkgLT4gVW5pdCA9IHsgaWQgLT4gcHJpbnRsbihcImFsdGVybmFyICRpZFwiKSB9IGUgY2hhbWUgYW9BbHRlcm5hcigyKS5cbiAgICAvLyAgICDDiSBhIGZvcm1hIGRvIHBhcsOibWV0cm8gb25BbHRlcm5hciBkYXMgdGVsYXMgZG8gZXhlbXBsbyBkZSBUYXJlZmFzLlxuICAgIC8vIDMuIEVzY3JldmEgYHJldHVybiByZXN1bHRhZG9gIGRlbnRybyBkYSBsYW1iZGEgYGRvYnJvYCBlIGxlaWEgbyBlcnJvLlxufVxuIn0=
[demo4]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyA0IOKAlCBjb2xlw6fDtWVzXG4vLyBBcyBvcGVyYcOnw7VlcyBxdWUgZW0gSmF2YSBwZWRlbSBzdHJlYW0oKSBlIGNvbGxlY3QoKSBzw6NvIGZ1bsOnw7VlcyBkaXJldGFzIGRhIGxpc3RhLlxuXG5kYXRhIGNsYXNzIFRhcmVmYSh2YWwgaWQ6IEludCwgdmFsIHRpdHVsbzogU3RyaW5nLCB2YWwgZmVpdGE6IEJvb2xlYW4gPSBmYWxzZSlcblxuZnVuIG1haW4oKSB7XG4gICAgLy8gTGlzdCDDqSBzb21lbnRlIGxlaXR1cmE6IG7Do28gdGVtIGFkZCBuZW0gcmVtb3ZlLlxuICAgIHZhbCB0YXJlZmFzOiBMaXN0PFRhcmVmYT4gPSBsaXN0T2YoXG4gICAgICAgIFRhcmVmYSgxLCBcIkVzdHVkYXIgS290bGluXCIsIGZlaXRhID0gdHJ1ZSksXG4gICAgICAgIFRhcmVmYSgyLCBcIkVudGVuZGVyIGxhbWJkYXNcIiksXG4gICAgICAgIFRhcmVmYSgzLCBcIkVzY3JldmVyIG9zIHRlc3Rlc1wiKSxcbiAgICApXG5cbiAgICBwcmludGxuKHRhcmVmYXMuZmlsdGVyIHsgIWl0LmZlaXRhIH0pICAgICAgICAgICAgICAgICAgLy8gYXMgcXVlIHBhc3NhbSBuYSBjb25kacOnw6NvXG4gICAgcHJpbnRsbih0YXJlZmFzLm1hcCB7IGl0LnRpdHVsbyB9KSAgICAgICAgICAgICAgICAgICAgIC8vIHRyYW5zZm9ybWEgY2FkYSBlbGVtZW50b1xuICAgIHByaW50bG4odGFyZWZhcy5maWx0ZXIgeyAhaXQuZmVpdGEgfS5tYXAgeyBpdC50aXR1bG8udXBwZXJjYXNlKCkgfSlcbiAgICBwcmludGxuKHRhcmVmYXMuZmluZCB7IGl0LmlkID09IDIgfSkgICAgICAgICAgICAgICAgICAgLy8gbyBwcmltZWlybywgb3UgbnVsbFxuICAgIHByaW50bG4odGFyZWZhcy5maW5kIHsgaXQuaWQgPT0gOSB9KVxuICAgIHByaW50bG4odGFyZWZhcy5hbnkgeyBpdC5mZWl0YSB9KSAgICAgICAgICAgICAgICAgICAgICAvLyBleGlzdGUgYWxndW0/XG4gICAgcHJpbnRsbih0YXJlZmFzLmNvdW50IHsgaXQuZmVpdGEgfSlcbiAgICBwcmludGxuKHRhcmVmYXMuc29ydGVkQnkgeyBpdC50aXR1bG8gfS5tYXAgeyBpdC50aXR1bG8gfSlcbiAgICBwcmludGxuKHRhcmVmYXMuZ3JvdXBCeSB7IGl0LmZlaXRhIH0pICAgICAgICAgICAgICAgICAgLy8gTWFwPEJvb2xlYW4sIExpc3Q8VGFyZWZhPj5cblxuICAgIC8vIFwiQWx0ZXJhclwiIHVtYSBsaXN0YSBpbXV0w6F2ZWwgw6kgY3JpYXIgb3V0cmEuIMOJIGFzc2ltIHF1ZSBvIGV4ZW1wbG8gZGUgVGFyZWZhcyBtYXJjYVxuICAgIC8vIHVtYSB0YXJlZmEgY29tbyBmZWl0YTogbWFwIGRldm9sdmUgdW1hIGxpc3RhIG5vdmEsIGUgY29weSwgdW1hIHRhcmVmYSBub3ZhLlxuICAgIHZhbCBkZXBvaXMgPSB0YXJlZmFzLm1hcCB7IGlmIChpdC5pZCA9PSAyKSBpdC5jb3B5KGZlaXRhID0gIWl0LmZlaXRhKSBlbHNlIGl0IH1cbiAgICBwcmludGxuKGRlcG9pc1sxXSlcbiAgICBwcmludGxuKHRhcmVmYXNbMV0pICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLy8gYSBvcmlnaW5hbCBjb250aW51YSBpZ3VhbFxuXG4gICAgLy8gYCtgIHRhbWLDqW0gY3JpYSBvdXRyYSBsaXN0YS5cbiAgICB2YWwgY29tTm92YSA9IHRhcmVmYXMgKyBUYXJlZmEoNCwgXCJHcmF2YXIgbyB2w61kZW9cIilcbiAgICBwcmludGxuKGNvbU5vdmEuc2l6ZSlcbiAgICBwcmludGxuKHRhcmVmYXMuc2l6ZSlcblxuICAgIC8vIFF1YW5kbyDDqSBwcmVjaXNvIGFsdGVyYXIgbm8gbHVnYXIsIG8gdGlwbyBkaXo6IE11dGFibGVMaXN0LlxuICAgIHZhbCByYXNjdW5obyA9IG11dGFibGVMaXN0T2YoXCJhXCIsIFwiYlwiKVxuICAgIHJhc2N1bmhvLmFkZChcImNcIilcbiAgICBwcmludGxuKHJhc2N1bmhvKVxuXG4gICAgLy8gRXhwZXJpbWVudGU6XG4gICAgLy8gMS4gRXNjcmV2YSB0YXJlZmFzLmFkZChUYXJlZmEoNSwgXCJ4XCIpKS4gTyBxdWUgbyBjb21waWxhZG9yIGRpej9cbiAgICAvLyAyLiBMaXN0ZSBvcyB0w610dWxvcyBkYXMgdGFyZWZhcyBwZW5kZW50ZXMgZW0gb3JkZW0gYWxmYWLDqXRpY2EsIG51bWEgbGluaGEgc8OzLlxuICAgIC8vIDMuIFVzZSB0YXJlZmFzLnBhcnRpdGlvbiB7IGl0LmZlaXRhIH0gZSBkZXNlc3RydXR1cmU6IHZhbCAoZmVpdGFzLCBwZW5kZW50ZXMpID0gLi4uXG59XG4ifQ==
[demo5]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyA1IOKAlCBmdW7Dp8O1ZXMgZGUgZXh0ZW5zw6NvXG4vLyBBY3Jlc2NlbnRhbSB1bWEgZnVuw6fDo28gYSB1bSB0aXBvIHF1ZSBqw6EgZXhpc3RlLCBzZW0gaGVyYW7Dp2EgZSBzZW0gYWx0ZXJhciBhIGNsYXNzZS5cblxuZGF0YSBjbGFzcyBUYXJlZmEodmFsIGlkOiBJbnQsIHZhbCB0aXR1bG86IFN0cmluZywgdmFsIGZlaXRhOiBCb29sZWFuID0gZmFsc2UpXG5cbi8vIEVtIEphdmEsIGlzdG8gc2VyaWEgdW0gbcOpdG9kbyBlc3TDoXRpY28gbnVtYSBjbGFzc2UgdXRpbGl0w6FyaWE6XG4vLyAgICAgVGV4dG9VdGlsLmNvbW9UaXR1bG8odGV4dG8pXG5mdW4gY29tb1RpdHVsb0VzdGF0aWNvKHRleHRvOiBTdHJpbmcpOiBTdHJpbmcgPSB0ZXh0by50cmltKCkucmVwbGFjZUZpcnN0Q2hhciB7IGl0LnVwcGVyY2FzZSgpIH1cblxuLy8gRW0gS290bGluLCBvIHRpcG8gYW50ZXMgZG8gcG9udG8gw6kgbyBcInJlY2VwdG9yXCIuIERlbnRybyBkYSBmdW7Dp8OjbywgYHRoaXNgIMOpIG8gcmVjZXB0b3IuXG5mdW4gU3RyaW5nLmNvbW9UaXR1bG8oKTogU3RyaW5nID0gdGhpcy50cmltKCkucmVwbGFjZUZpcnN0Q2hhciB7IGl0LnVwcGVyY2FzZSgpIH1cblxuLy8gQXMgZHVhcyByZWdyYXMgZGEgdGVsYSBkbyBleGVtcGxvIGRlIFRhcmVmYXMgc8OjbyBleHRlbnPDtWVzIGRlIExpc3Q8VGFyZWZhPi5cbi8vIE8gYHRoaXNgIHBvZGUgc2VyIG9taXRpZG86IGBtYXBgIGFxdWkgw6kgYHRoaXMubWFwYC5cbmZ1biBMaXN0PFRhcmVmYT4uYWx0ZXJuYW5kbyhpZDogSW50KTogTGlzdDxUYXJlZmE+ID1cbiAgICBtYXAgeyBpZiAoaXQuaWQgPT0gaWQpIGl0LmNvcHkoZmVpdGEgPSAhaXQuZmVpdGEpIGVsc2UgaXQgfVxuXG5mdW4gTGlzdDxUYXJlZmE+LmNvbU5vdmEoaWQ6IEludCwgdGl0dWxvOiBTdHJpbmcpOiBMaXN0PFRhcmVmYT4gPSB0aGlzICsgVGFyZWZhKGlkLCB0aXR1bG8uY29tb1RpdHVsbygpKVxuXG4vLyBUYW1iw6ltIGV4aXN0ZSBwcm9wcmllZGFkZSBkZSBleHRlbnPDo28gKHNlbXByZSBjYWxjdWxhZGE7IG7Do28gZ3VhcmRhIHZhbG9yKS5cbnZhbCBMaXN0PFRhcmVmYT4ucGVuZGVudGVzOiBMaXN0PFRhcmVmYT5cbiAgICBnZXQoKSA9IGZpbHRlciB7ICFpdC5mZWl0YSB9XG5cbi8vIE8gcmVjZXB0b3IgcG9kZSBzZXIgYW51bMOhdmVsOiBhIGZ1bsOnw6NvIHRyYXRhIG8gbnVsbCBwb3IgZGVudHJvLlxuZnVuIFN0cmluZz8ub3VQYWRyYW8ocGFkcmFvOiBTdHJpbmcpOiBTdHJpbmcgPSBpZiAodGhpcy5pc051bGxPckJsYW5rKCkpIHBhZHJhbyBlbHNlIHRoaXNcblxuZnVuIG1haW4oKSB7XG4gICAgcHJpbnRsbihjb21vVGl0dWxvRXN0YXRpY28oXCIgIGVzdHVkYXIga290bGluIFwiKSlcbiAgICBwcmludGxuKFwiICBlc3R1ZGFyIGtvdGxpbiBcIi5jb21vVGl0dWxvKCkpICAgICAgICAgIC8vIGzDqi1zZSBkYSBlc3F1ZXJkYSBwYXJhIGEgZGlyZWl0YVxuXG4gICAgdmFsIHRhcmVmYXMgPSBsaXN0T2YoVGFyZWZhKDEsIFwiRXN0dWRhciBLb3RsaW5cIiksIFRhcmVmYSgyLCBcIkVudGVuZGVyIGxhbWJkYXNcIikpXG5cbiAgICAvLyBFbmNhZGVhbmRvOiBjYWRhIGNoYW1hZGEgZGV2b2x2ZSB1bWEgbGlzdGEgbm92YS5cbiAgICB2YWwgZGVwb2lzID0gdGFyZWZhc1xuICAgICAgICAuY29tTm92YSgzLCBcIiAgZ3JhdmFyIG8gdsOtZGVvXCIpXG4gICAgICAgIC5hbHRlcm5hbmRvKDEpXG4gICAgcHJpbnRsbihkZXBvaXMpXG4gICAgcHJpbnRsbihkZXBvaXMucGVuZGVudGVzLm1hcCB7IGl0LnRpdHVsbyB9KVxuXG4gICAgdmFsIGFwZWxpZG86IFN0cmluZz8gPSBudWxsXG4gICAgcHJpbnRsbihhcGVsaWRvLm91UGFkcmFvKFwic2VtIGFwZWxpZG9cIikpXG5cbiAgICAvLyBVbWEgZXh0ZW5zw6NvIHPDsyB1c2EgYSBwYXJ0ZSBww7pibGljYSBkbyB0aXBvOiBuw6NvIGVueGVyZ2EgbWVtYnJvcyBwcml2YWRvcy5cbiAgICAvLyBFIG7Do28gYWx0ZXJhIGEgY2xhc3NlOiDDqSB1bWEgZnVuw6fDo28gY29tdW0sIGVzY29saGlkYSBlbSB0ZW1wbyBkZSBjb21waWxhw6fDo29cbiAgICAvLyBwZWxvIHRpcG8gZGVjbGFyYWRvIGRhIHZhcmnDoXZlbC5cblxuICAgIC8vIEV4cGVyaW1lbnRlOlxuICAgIC8vIDEuIEVzY3JldmEgZnVuIFRhcmVmYS5yZXN1bW8oKTogU3RyaW5nLCBxdWUgZGV2b2x2ZSBcIlt4XSBUw610dWxvXCIgb3UgXCJbIF0gVMOtdHVsb1wiLFxuICAgIC8vICAgIGUgaW1wcmltYSBkZXBvaXMubWFwIHsgaXQucmVzdW1vKCkgfS5cbiAgICAvLyAyLiBFc2NyZXZhIGZ1biBJbnQuZWhQYXIoKTogQm9vbGVhbiBlIGNoYW1lIDQuZWhQYXIoKS5cbiAgICAvLyAzLiBDcmllIGEgcHJvcHJpZWRhZGUgZGUgZXh0ZW5zw6NvIHZhbCBMaXN0PFRhcmVmYT4uZmVpdGFzLlxufVxuIn0=
[demo6]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyA2IOKAlCBsYW1iZGEgY29tIHJlY2VwdG9yIGUgZnVuw6fDtWVzIGRlIGVzY29wb1xuLy8gSnVudGEgYXMgZHVhcyBpZGVpYXMgYW50ZXJpb3JlczogdW1hIGxhbWJkYSBxdWUsIHBvciBkZW50cm8sIHRlbSB1bSBgdGhpc2AuXG4vLyDDiSBvIHF1ZSBwZXJtaXRlIGVzY3JldmVyIGJsb2NvcyBjb21vIGFwcGx5IHsgfSwgcm91dGluZyB7IH0gZSBDb2x1bW4geyB9LlxuXG5jbGFzcyBDb25leGFvIHtcbiAgICB2YXIgdXJsOiBTdHJpbmcgPSBcIlwiXG4gICAgdmFyIHVzdWFyaW86IFN0cmluZyA9IFwiXCJcbiAgICB2YXIgbGltaXRlOiBJbnQgPSAxMFxuICAgIG92ZXJyaWRlIGZ1biB0b1N0cmluZygpID0gXCJDb25leGFvKHVybD0kdXJsLCB1c3VhcmlvPSR1c3VhcmlvLCBsaW1pdGU9JGxpbWl0ZSlcIlxufVxuXG4vLyBMYW1iZGEgY29tdW06IG8gb2JqZXRvIGNoZWdhIGNvbW8gcGFyw6JtZXRybywgZSDDqSBwcmVjaXNvIGNpdMOhLWxvIChpdC51cmwsIGl0LnVzdWFyaW8pLlxuZnVuIGNvbmZpZ3VyYXJDb21QYXJhbWV0cm8oYmxvY286IChDb25leGFvKSAtPiBVbml0KTogQ29uZXhhbyB7XG4gICAgdmFsIGNvbmV4YW8gPSBDb25leGFvKClcbiAgICBibG9jbyhjb25leGFvKVxuICAgIHJldHVybiBjb25leGFvXG59XG5cbi8vIExhbWJkYSBjb20gcmVjZXB0b3I6IG8gdGlwbyBhbnRlcyBkbyBwb250byB2aXJhIG8gYHRoaXNgIGRvIGJsb2NvLlxuLy8gRGVudHJvIGRlbGUsIGB1cmwgPSAuLi5gIHF1ZXIgZGl6ZXIgYHRoaXMudXJsID0gLi4uYC5cbmZ1biBjb25maWd1cmFyKGJsb2NvOiBDb25leGFvLigpIC0+IFVuaXQpOiBDb25leGFvIHtcbiAgICB2YWwgY29uZXhhbyA9IENvbmV4YW8oKVxuICAgIGNvbmV4YW8uYmxvY28oKVxuICAgIHJldHVybiBjb25leGFvXG59XG5cbi8vIEEgYmlibGlvdGVjYSBwYWRyw6NvIHVzYSBhIG1lc21hIHTDqWNuaWNhLiBFc3RhIMOpIGEgYXNzaW5hdHVyYSBkZSBidWlsZFN0cmluZzpcbi8vICAgICBmdW4gYnVpbGRTdHJpbmcoYWNhbzogU3RyaW5nQnVpbGRlci4oKSAtPiBVbml0KTogU3RyaW5nXG5mdW4gbGlzdGFFbVRleHRvKGl0ZW5zOiBMaXN0PFN0cmluZz4pOiBTdHJpbmcgPSBidWlsZFN0cmluZyB7XG4gICAgYXBwZW5kTGluZShcIlRhcmVmYXM6XCIpICAgICAgICAgICAgICAgICAgLy8gdGhpcy5hcHBlbmRMaW5lKC4uLik6IG8gdGhpcyDDqSB1bSBTdHJpbmdCdWlsZGVyXG4gICAgZm9yIChpdGVtIGluIGl0ZW5zKSBhcHBlbmRMaW5lKFwiLSAkaXRlbVwiKVxufVxuXG5kYXRhIGNsYXNzIFRhcmVmYSh2YWwgaWQ6IEludCwgdmFsIHRpdHVsbzogU3RyaW5nLCB2YWwgZmVpdGE6IEJvb2xlYW4gPSBmYWxzZSlcblxuZnVuIG1haW4oKSB7XG4gICAgcHJpbnRsbihjb25maWd1cmFyQ29tUGFyYW1ldHJvIHsgaXQudXJsID0gXCJqZGJjOnBvc3RncmVzcWw6Ly9sb2NhbGhvc3QvdGFyZWZhc1wiOyBpdC51c3VhcmlvID0gXCJ0YXJlZmFzXCIgfSlcblxuICAgIHByaW50bG4oY29uZmlndXJhciB7XG4gICAgICAgIHVybCA9IFwiamRiYzpwb3N0Z3Jlc3FsOi8vbG9jYWxob3N0L3RhcmVmYXNcIlxuICAgICAgICB1c3VhcmlvID0gXCJ0YXJlZmFzXCJcbiAgICAgICAgbGltaXRlID0gNVxuICAgIH0pXG5cbiAgICBwcmludChsaXN0YUVtVGV4dG8obGlzdE9mKFwiRXN0dWRhciBLb3RsaW5cIiwgXCJFbnRlbmRlciBsYW1iZGFzXCIpKSlcblxuICAgIC8vIEFzIGZ1bsOnw7VlcyBkZSBlc2NvcG8gZGEgYmlibGlvdGVjYSBwYWRyw6NvIHPDo28gdmFyaWHDp8O1ZXMgZGVzc2EgaWRlaWEuXG5cbiAgICAvLyBhcHBseTogY29uZmlndXJhIG8gb2JqZXRvIGUgZGV2b2x2ZSBvIHByw7NwcmlvIG9iamV0by4gKHRoaXMpXG4gICAgdmFsIGNvbmV4YW8gPSBDb25leGFvKCkuYXBwbHkge1xuICAgICAgICB1cmwgPSBcImpkYmM6cG9zdGdyZXNxbDovL2xvY2FsaG9zdC90YXJlZmFzXCJcbiAgICAgICAgbGltaXRlID0gNVxuICAgIH1cbiAgICBwcmludGxuKGNvbmV4YW8pXG5cbiAgICAvLyBsZXQ6IHVzYSBvIG9iamV0byBjb21vIGBpdGAgZSBkZXZvbHZlIG8gcmVzdWx0YWRvIGRvIGJsb2NvLlxuICAgIC8vIENvbSA/LiwgbyBibG9jbyBzw7Mgcm9kYSBxdWFuZG8gbyB2YWxvciBuw6NvIMOpIG51bGwuXG4gICAgdmFsIHNlbGVjaW9uYWRhOiBJbnQ/ID0gMlxuICAgIHNlbGVjaW9uYWRhPy5sZXQgeyBwcmludGxuKFwidGFyZWZhIHNlbGVjaW9uYWRhOiAkaXRcIikgfVxuXG4gICAgdmFsIG5lbmh1bWE6IEludD8gPSBudWxsXG4gICAgbmVuaHVtYT8ubGV0IHsgcHJpbnRsbihcImVzdGEgbGluaGEgbsOjbyBhcGFyZWNlXCIpIH1cblxuICAgIC8vIGFsc286IGZheiBhbGdvIGEgbWFpcyBjb20gbyBvYmpldG8gKHVtIHJlZ2lzdHJvLCBwb3IgZXhlbXBsbykgZSBkZXZvbHZlIG8gcHLDs3ByaW8gb2JqZXRvLlxuICAgIHZhbCB0YXJlZmEgPSBUYXJlZmEoMSwgXCJFc3R1ZGFyIEtvdGxpblwiKS5hbHNvIHsgcHJpbnRsbihcImNyaWFkYTogJGl0XCIpIH1cblxuICAgIC8vIHdpdGg6IHbDoXJpYXMgY2hhbWFkYXMgc29icmUgbyBtZXNtbyBvYmpldG8sIHNlbSByZXBldGlyIG8gbm9tZS5cbiAgICB3aXRoKHRhcmVmYSkgeyBwcmludGxuKFwiJGlkIC0gJHRpdHVsbyAtICRmZWl0YVwiKSB9XG5cbiAgICAvLyBydW46IGNvbW8gd2l0aCwgZXNjcml0byBkZXBvaXMgZG8gcG9udG87IGRldm9sdmUgbyByZXN1bHRhZG8gZG8gYmxvY28uXG4gICAgdmFsIHRhbWFuaG8gPSB0YXJlZmEucnVuIHsgdGl0dWxvLmxlbmd0aCB9XG4gICAgcHJpbnRsbih0YW1hbmhvKVxuXG4gICAgLy8gRXhwZXJpbWVudGU6XG4gICAgLy8gMS4gRW0gY29uZmlndXJhciB7IH0sIGVzY3JldmEgdGhpcy51cmwgPSBcIi4uLlwiIGVtIHZleiBkZSB1cmwgPSBcIi4uLlwiLiBNdWRhIGFsZ28/XG4gICAgLy8gMi4gVHJvcXVlIENvbmV4YW8uKCkgLT4gVW5pdCBwb3IgKENvbmV4YW8pIC0+IFVuaXQgZW0gY29uZmlndXJhciBlIHZlamEgbyBxdWUgZGVpeGEgZGUgY29tcGlsYXIuXG4gICAgLy8gMy4gUmVlc2NyZXZhIGEgY3JpYcOnw6NvIGRlIGBjb25leGFvYCBzZW0gYXBwbHkuIFF1YW50YXMgdmV6ZXMgbyBub21lIGRhIHZhcmnDoXZlbCBhcGFyZWNlP1xufVxuIn0=
[demo7]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyA3IOKAlCB1bSBLdG9yIGVtIG1pbmlhdHVyYSAoV2ViIElJKVxuLy8gQ2VyY2EgZGUgMzAgbGluaGFzIGJhc3RhbSBwYXJhIGVzY3JldmVyIHJvdGFzIG5vIGZvcm1hdG8gZG8gZXhlbXBsbyBkZSBUYXJlZmFzLlxuLy8gTyBLdG9yIGRlIHZlcmRhZGUgZmF6IG11aXRvIG1haXMsIG1hcyBhIHNpbnRheGUgc2FpIGRhcyBtZXNtYXMgdHLDqnMgcGXDp2FzOlxuLy8gZnVuw6fDo28gZGUgZXh0ZW5zw6NvLCBsYW1iZGEgY29tIHJlY2VwdG9yIGUgbGFtYmRhIG5vIGZpbSBkYSBjaGFtYWRhLlxuXG5kYXRhIGNsYXNzIFRhcmVmYSh2YWwgaWQ6IEludCwgdmFsIHRpdHVsbzogU3RyaW5nLCB2YWwgZmVpdGE6IEJvb2xlYW4gPSBmYWxzZSlcblxudmFsIHRhcmVmYXMgPSBsaXN0T2YoVGFyZWZhKDEsIFwiRXN0dWRhciBLb3RsaW5cIiksIFRhcmVmYSgyLCBcIkVudGVuZGVyIGxhbWJkYXNcIikpXG5cbi8vIFVtYSByZXF1aXNpw6fDo28gZW0gYW5kYW1lbnRvOiBvcyBwYXLDom1ldHJvcyBxdWUgY2hlZ2FyYW0gZSBhIHJlc3Bvc3RhIHF1ZSB2YWkgc2Fpci5cbmNsYXNzIENoYW1hZGEodmFsIHBhcmFtZXRyb3M6IE1hcDxTdHJpbmcsIFN0cmluZz4pIHtcbiAgICB2YXIgcmVzcG9zdGE6IFN0cmluZyA9IFwiXCJcbiAgICBmdW4gcmVzcG9uZGVyKHRleHRvOiBTdHJpbmcpIHsgcmVzcG9zdGEgPSB0ZXh0byB9XG59XG5cbi8vIE8gYHRoaXNgIGRlIGNhZGEgdHJhdGFkb3IgZGUgcm90YS4gw4kgcG9yIGlzc28gcXVlLCBubyBLdG9yLCBgY2FsbGAgZXhpc3RlIGRlbnRybyBkZSBnZXQgeyB9LlxuY2xhc3MgQ29udGV4dG8odmFsIGNhbGw6IENoYW1hZGEpXG5cbmNsYXNzIFJvdGFzIHtcbiAgICBwcml2YXRlIHZhbCB0YWJlbGEgPSBtdXRhYmxlTWFwT2Y8U3RyaW5nLCBDb250ZXh0by4oKSAtPiBVbml0PigpXG5cbiAgICAvLyBPIMO6bHRpbW8gcGFyw6JtZXRybyDDqSB1bWEgbGFtYmRhIGNvbSByZWNlcHRvcjogZ2V0KFwiL2NhbWluaG9cIikgeyAuLi4gfVxuICAgIGZ1biBnZXQoY2FtaW5obzogU3RyaW5nLCB0cmF0YWRvcjogQ29udGV4dG8uKCkgLT4gVW5pdCkge1xuICAgICAgICB0YWJlbGFbXCJHRVQgJGNhbWluaG9cIl0gPSB0cmF0YWRvclxuICAgIH1cblxuICAgIGZ1biBhdGVuZGVyKGNhbWluaG86IFN0cmluZywgcGFyYW1ldHJvczogTWFwPFN0cmluZywgU3RyaW5nPiA9IGVtcHR5TWFwKCkpOiBTdHJpbmcge1xuICAgICAgICB2YWwgdHJhdGFkb3IgPSB0YWJlbGFbXCJHRVQgJGNhbWluaG9cIl0gPzogcmV0dXJuIFwiNDA0OiByb3RhIGluZXhpc3RlbnRlXCJcbiAgICAgICAgdmFsIGNvbnRleHRvID0gQ29udGV4dG8oQ2hhbWFkYShwYXJhbWV0cm9zKSlcbiAgICAgICAgY29udGV4dG8udHJhdGFkb3IoKSAgICAgICAgICAgICAgICAgICAgICAgLy8gZXhlY3V0YSBvIGJsb2NvIGNvbSBgY29udGV4dG9gIGNvbW8gdGhpc1xuICAgICAgICByZXR1cm4gY29udGV4dG8uY2FsbC5yZXNwb3N0YVxuICAgIH1cbn1cblxuY2xhc3MgQXBsaWNhY2FvIHtcbiAgICB2YWwgcm90YXMgPSBSb3RhcygpXG59XG5cbi8vIEV4dGVuc8OjbyBkZSBBcGxpY2FjYW8gcXVlIHJlY2ViZSB1bWEgbGFtYmRhIGNvbSByZWNlcHRvciBSb3Rhcy5cbmZ1biBBcGxpY2FjYW8ucm91dGluZyhibG9jbzogUm90YXMuKCkgLT4gVW5pdCkge1xuICAgIHJvdGFzLmJsb2NvKClcbn1cblxuLy8gRGFxdWkgcGFyYSBiYWl4bywgbyBjw7NkaWdvIHRlbSBhIGZvcm1hIGRvIFJvdGFzLmt0IGRvIGV4ZW1wbG8uXG5mdW4gQXBsaWNhY2FvLm1vZHVsbygpIHtcbiAgICByb3V0aW5nIHsgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLy8gdGhpczogUm90YXNcbiAgICAgICAgZ2V0KFwiL3RhcmVmYXNcIikgeyAgICAgICAgICAgICAgICAgICAgICAgICAvLyB0aGlzOiBDb250ZXh0b1xuICAgICAgICAgICAgY2FsbC5yZXNwb25kZXIodGFyZWZhcy5qb2luVG9TdHJpbmcgeyBpdC50aXR1bG8gfSlcbiAgICAgICAgfVxuICAgICAgICBnZXQoXCIvdGFyZWZhcy97aWR9XCIpIHtcbiAgICAgICAgICAgIHZhbCBpZCA9IGNhbGwucGFyYW1ldHJvc1tcImlkXCJdPy50b0ludE9yTnVsbCgpXG4gICAgICAgICAgICB2YWwgdGFyZWZhID0gdGFyZWZhcy5maW5kIHsgaXQuaWQgPT0gaWQgfVxuICAgICAgICAgICAgY2FsbC5yZXNwb25kZXIodGFyZWZhPy50aXR1bG8gPzogXCI0MDQ6IGEgdGFyZWZhIG7Do28gZXhpc3RlXCIpXG4gICAgICAgIH1cbiAgICB9XG59XG5cbmZ1biBtYWluKCkge1xuICAgIHZhbCBhcGxpY2FjYW8gPSBBcGxpY2FjYW8oKVxuICAgIGFwbGljYWNhby5tb2R1bG8oKVxuXG4gICAgcHJpbnRsbihhcGxpY2FjYW8ucm90YXMuYXRlbmRlcihcIi90YXJlZmFzXCIpKVxuICAgIHByaW50bG4oYXBsaWNhY2FvLnJvdGFzLmF0ZW5kZXIoXCIvdGFyZWZhcy97aWR9XCIsIG1hcE9mKFwiaWRcIiB0byBcIjJcIikpKVxuICAgIHByaW50bG4oYXBsaWNhY2FvLnJvdGFzLmF0ZW5kZXIoXCIvdGFyZWZhcy97aWR9XCIsIG1hcE9mKFwiaWRcIiB0byBcIjlcIikpKVxuICAgIHByaW50bG4oYXBsaWNhY2FvLnJvdGFzLmF0ZW5kZXIoXCIvdXN1YXJpb3NcIikpXG5cbiAgICAvLyBFeHBlcmltZW50ZTpcbiAgICAvLyAxLiBBY3Jlc2NlbnRlIGEgcm90YSBnZXQoXCIvc2F1ZGVcIikgeyBjYWxsLnJlc3BvbmRlcihcIm5vIGFyXCIpIH0gZSBjaGFtZS1hIG5vIG1haW4uXG4gICAgLy8gMi4gUmVlc2NyZXZhIGEgcHJpbWVpcmEgcm90YSBzZW0gYcOnw7pjYXIgc2ludMOhdGljbzpcbiAgICAvLyAgICAgICAgdGhpcy5nZXQoXCIvdGFyZWZhc1wiLCB7IHRoaXMuY2FsbC5yZXNwb25kZXIoXCIuLi5cIikgfSlcbiAgICAvLyAgICDDiSBleGF0YW1lbnRlIG8gbWVzbW8gcHJvZ3JhbWEuXG4gICAgLy8gMy4gQWNyZXNjZW50ZSBmdW4gcG9zdCguLi4pIMOgIGNsYXNzZSBSb3RhcywgY29waWFuZG8gYSBpZGVpYSBkZSBnZXQuXG59XG4ifQ==
[demo8]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyA4IOKAlCB1bSBDb21wb3NlIGVtIG1pbmlhdHVyYSAoRGlzcG9zaXRpdm9zIE3Ds3ZlaXMpXG4vLyBNb250YSB1bWEgw6Fydm9yZSBkZSBjb21wb25lbnRlcyBjb20gYSBtZXNtYSBmb3JtYSBkZSBlc2NyaXRhIGRvIFRlbGFzLmt0IGRvIGV4ZW1wbG8uXG4vLyBPIENvbXBvc2UgZGUgdmVyZGFkZSBmdW5jaW9uYSBkZSBvdXRybyBqZWl0byBwb3IgZGVudHJvICh1bSBwbHVnaW4gZG8gY29tcGlsYWRvclxuLy8gYWNvbXBhbmhhIG8gZXN0YWRvIGUgcmVkZXNlbmhhIHPDsyBvIHF1ZSBtdWRvdSksIG1hcyBhIHNpbnRheGUgc2FpIGRhcyBtZXNtYXMgcGXDp2FzOlxuLy8gbGFtYmRhIG5vIGZpbSBkYSBjaGFtYWRhLCBsYW1iZGEgY29tIHJlY2VwdG9yLCBhcmd1bWVudG9zIG5vbWVhZG9zIGUgdmFsb3JlcyBwYWRyw6NvLlxuXG5kYXRhIGNsYXNzIFRhcmVmYSh2YWwgaWQ6IEludCwgdmFsIHRpdHVsbzogU3RyaW5nLCB2YWwgZmVpdGE6IEJvb2xlYW4gPSBmYWxzZSlcblxuLy8gVW0gbsOzIGRhIMOhcnZvcmU6IHVtIG5vbWUsIG9zIGZpbGhvcyBlLCBzZSBmb3IgY2xpY8OhdmVsLCBhIGHDp8OjbyBkbyBjbGlxdWUuXG5jbGFzcyBObyh2YWwgbm9tZTogU3RyaW5nLCB2YWwgYW9DbGljYXI6ICgoKSAtPiBVbml0KT8gPSBudWxsKSB7XG4gICAgdmFsIGZpbGhvcyA9IG11dGFibGVMaXN0T2Y8Tm8+KClcblxuICAgIGZ1biBkZXNlbmhhcihuaXZlbDogSW50ID0gMCkge1xuICAgICAgICBwcmludGxuKFwiICBcIi5yZXBlYXQobml2ZWwpICsgbm9tZSlcbiAgICAgICAgZmlsaG9zLmZvckVhY2ggeyBpdC5kZXNlbmhhcihuaXZlbCArIDEpIH1cbiAgICB9XG5cbiAgICAvLyBUb2RvcyBvcyBuw7NzIGNsaWPDoXZlaXMgZGEgw6Fydm9yZSwgbmEgb3JkZW0gZW0gcXVlIGFwYXJlY2VtLlxuICAgIGZ1biBib3RvZXMoKTogTGlzdDxObz4gPVxuICAgICAgICAoaWYgKGFvQ2xpY2FyICE9IG51bGwpIGxpc3RPZih0aGlzKSBlbHNlIGVtcHR5TGlzdCgpKSArIGZpbGhvcy5mbGF0TWFwIHsgaXQuYm90b2VzKCkgfVxufVxuXG4vLyBPIGB0aGlzYCBkb3MgYmxvY29zIGRlIGNvbnRlw7pkbzogc2FiZSBlbSBxdWUgbsOzIG9zIGZpbGhvcyBkZXZlbSBlbnRyYXIuXG5jbGFzcyBFc2NvcG8odmFsIG5vOiBObylcblxuZnVuIEVzY29wby5UZXh0byh0ZXh0bzogU3RyaW5nKSB7XG4gICAgbm8uZmlsaG9zICs9IE5vKFwiVGV4dG8oXFxcIiR0ZXh0b1xcXCIpXCIpXG59XG5cbi8vIE8gw7psdGltbyBwYXLDom1ldHJvIMOpIG8gY29udGXDumRvOiBDb2x1bmEgeyAuLi4gfVxuZnVuIEVzY29wby5Db2x1bmEoY29udGV1ZG86IEVzY29wby4oKSAtPiBVbml0KSB7XG4gICAgdmFsIGNvbHVuYSA9IE5vKFwiQ29sdW5hXCIpXG4gICAgbm8uZmlsaG9zICs9IGNvbHVuYVxuICAgIEVzY29wbyhjb2x1bmEpLmNvbnRldWRvKCkgICAgICAgICAgICAgICAgIC8vIGV4ZWN1dGEgbyBibG9jbyBjb20gbyBub3ZvIGVzY29wbyBjb21vIHRoaXNcbn1cblxuLy8gVW0gcGFyw6JtZXRybyBjb20gbm9tZSAob25DbGljaykgZSBvIGNvbnRlw7pkbyBubyBmaW06IEJvdGFvKG9uQ2xpY2sgPSB7IC4uLiB9KSB7IFRleHRvKFwiLi4uXCIpIH1cbmZ1biBFc2NvcG8uQm90YW8ob25DbGljazogKCkgLT4gVW5pdCwgY29udGV1ZG86IEVzY29wby4oKSAtPiBVbml0KSB7XG4gICAgdmFsIGJvdGFvID0gTm8oXCJCb3Rhb1wiLCBhb0NsaWNhciA9IG9uQ2xpY2spXG4gICAgbm8uZmlsaG9zICs9IGJvdGFvXG4gICAgRXNjb3BvKGJvdGFvKS5jb250ZXVkbygpXG59XG5cbmZ1biB0ZWxhKGNvbnRldWRvOiBFc2NvcG8uKCkgLT4gVW5pdCk6IE5vID0gTm8oXCJUZWxhXCIpLmFsc28geyBFc2NvcG8oaXQpLmNvbnRldWRvKCkgfVxuXG4vLyBEYXF1aSBwYXJhIGJhaXhvLCBvIGPDs2RpZ28gdGVtIGEgZm9ybWEgZG8gVGVsYXMua3QgZG8gZXhlbXBsbzogYSB0ZWxhIG7Do28gZ3VhcmRhIGVzdGFkbyxcbi8vIHJlY2ViZSBvcyBkYWRvcyBlIGRldm9sdmUgZXZlbnRvcyAob25BbHRlcm5hcikuXG5mdW4gRXNjb3BvLlRlbGFMaXN0YSh0YXJlZmFzOiBMaXN0PFRhcmVmYT4sIG9uQWx0ZXJuYXI6IChJbnQpIC0+IFVuaXQpIHtcbiAgICBDb2x1bmEge1xuICAgICAgICBUZXh0byhcIk1pbmhhcyB0YXJlZmFzXCIpXG4gICAgICAgIGZvciAodGFyZWZhIGluIHRhcmVmYXMpIHtcbiAgICAgICAgICAgIENhcnRhb1RhcmVmYSh0YXJlZmEsIG9uQWx0ZXJuYXIgPSB7IG9uQWx0ZXJuYXIodGFyZWZhLmlkKSB9KVxuICAgICAgICB9XG4gICAgfVxufVxuXG5mdW4gRXNjb3BvLkNhcnRhb1RhcmVmYSh0YXJlZmE6IFRhcmVmYSwgb25BbHRlcm5hcjogKCkgLT4gVW5pdCkge1xuICAgIEJvdGFvKG9uQ2xpY2sgPSBvbkFsdGVybmFyKSB7XG4gICAgICAgIFRleHRvKGlmICh0YXJlZmEuZmVpdGEpIFwiW3hdICR7dGFyZWZhLnRpdHVsb31cIiBlbHNlIFwiWyBdICR7dGFyZWZhLnRpdHVsb31cIilcbiAgICB9XG59XG5cbmZ1biBtYWluKCkge1xuICAgIC8vIE8gZXN0YWRvIGZpY2EgZm9yYSBkYSB0ZWxhLCBjb21vIG5vIEFwcC5rdCBkbyBleGVtcGxvLlxuICAgIHZhciB0YXJlZmFzID0gbGlzdE9mKFRhcmVmYSgxLCBcIkVzdHVkYXIgQ29tcG9zZVwiKSwgVGFyZWZhKDIsIFwiRW50ZW5kZXIgZXN0YWRvIGVsZXZhZG9cIikpXG4gICAgdmFsIGFsdGVybmFyID0geyBpZDogSW50IC0+XG4gICAgICAgIHRhcmVmYXMgPSB0YXJlZmFzLm1hcCB7IGlmIChpdC5pZCA9PSBpZCkgaXQuY29weShmZWl0YSA9ICFpdC5mZWl0YSkgZWxzZSBpdCB9XG4gICAgfVxuXG4gICAgdmFyIGFydm9yZSA9IHRlbGEgeyBUZWxhTGlzdGEodGFyZWZhcywgb25BbHRlcm5hciA9IGFsdGVybmFyKSB9XG4gICAgYXJ2b3JlLmRlc2VuaGFyKClcblxuICAgIC8vIFNpbXVsYSBvIHRvcXVlIG5hIHNlZ3VuZGEgdGFyZWZhOiBvIGJvdMOjbyBjaGFtYSBhIGxhbWJkYSBxdWUgcmVjZWJldS5cbiAgICBhcnZvcmUuYm90b2VzKClbMV0uYW9DbGljYXI/Lmludm9rZSgpXG5cbiAgICAvLyBPIGVzdGFkbyBtdWRvdS4gTW9udGFyIGEgdGVsYSBkZSBub3ZvIMOpIG8gcXVlIG8gQ29tcG9zZSBjaGFtYSBkZSByZWNvbXBvc2nDp8Ojby5cbiAgICBhcnZvcmUgPSB0ZWxhIHsgVGVsYUxpc3RhKHRhcmVmYXMsIG9uQWx0ZXJuYXIgPSBhbHRlcm5hcikgfVxuICAgIGFydm9yZS5kZXNlbmhhcigpXG5cbiAgICAvLyBFeHBlcmltZW50ZTpcbiAgICAvLyAxLiBBY3Jlc2NlbnRlIHVtYSB0ZXJjZWlyYSB0YXJlZmEgYW8gZXN0YWRvIGUgcm9kZSBkZSBub3ZvLlxuICAgIC8vIDIuIFJlZXNjcmV2YSBDYXJ0YW9UYXJlZmEgc2VtIGHDp8O6Y2FyIHNpbnTDoXRpY286XG4gICAgLy8gICAgICAgIHRoaXMuQm90YW8ob25BbHRlcm5hciwgeyB0aGlzLlRleHRvKFwiLi4uXCIpIH0pXG4gICAgLy8gMy4gQ3JpZSBmdW4gRXNjb3BvLkxpbmhhKGNvbnRldWRvOiBFc2NvcG8uKCkgLT4gVW5pdCksIGNvcGlhbmRvIGEgaWRlaWEgZGUgQ29sdW5hLFxuICAgIC8vICAgIGUgdXNlLWEgZGVudHJvIGRvIGNhcnTDo28uXG59XG4ifQ==
[demo9]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyA5IOKAlCBkZWxlZ2HDp8OjbyBkZSBwcm9wcmllZGFkZXMgY29tIGBieWBcbi8vIGBieWAgZW50cmVnYSBhIG91dHJvIG9iamV0byBvIHRyYWJhbGhvIGRlIGxlciBlIGdyYXZhciB1bWEgcHJvcHJpZWRhZGUuXG4vLyDDiSBvIHF1ZSBow6EgcG9yIHRyw6FzIGRlIGB2YXIgdGV4dG8gYnkgcmVtZW1iZXIgeyBtdXRhYmxlU3RhdGVPZihcIlwiKSB9YCAoQ29tcG9zZSlcbi8vIGUgZGUgYHZhbCByZXBvc2l0b3JpbyBieSBpbmplY3Q8UmVwb3NpdG9yaW9EZVRhcmVmYXM+KClgIChLb2luLCBubyBLdG9yKS5cblxuaW1wb3J0IGtvdGxpbi5yZWZsZWN0LktQcm9wZXJ0eVxuXG4vLyBVbSBkZWxlZ2FkbyDDqSBxdWFscXVlciBvYmpldG8gY29tIG9zIG9wZXJhZG9yZXMgZ2V0VmFsdWUgZSAocGFyYSB2YXIpIHNldFZhbHVlLlxuLy8gRXN0ZSBhdmlzYSBhIGNhZGEgbXVkYW7Dp2E6IMOpIGEgaWRlaWEsIGJlbSBzaW1wbGlmaWNhZGEsIGRvIGVzdGFkbyBvYnNlcnbDoXZlbCBkbyBDb21wb3NlLlxuY2xhc3MgT2JzZXJ2YXZlbDxUPihwcml2YXRlIHZhciB2YWxvcjogVCwgcHJpdmF0ZSB2YWwgYW9NdWRhcjogKFN0cmluZywgVCwgVCkgLT4gVW5pdCkge1xuICAgIG9wZXJhdG9yIGZ1biBnZXRWYWx1ZShkb25vOiBBbnk/LCBwcm9wcmllZGFkZTogS1Byb3BlcnR5PCo+KTogVCA9IHZhbG9yXG5cbiAgICBvcGVyYXRvciBmdW4gc2V0VmFsdWUoZG9ubzogQW55PywgcHJvcHJpZWRhZGU6IEtQcm9wZXJ0eTwqPiwgbm92bzogVCkge1xuICAgICAgICB2YWwgYW50aWdvID0gdmFsb3JcbiAgICAgICAgdmFsb3IgPSBub3ZvXG4gICAgICAgIGFvTXVkYXIocHJvcHJpZWRhZGUubmFtZSwgYW50aWdvLCBub3ZvKVxuICAgIH1cbn1cblxuY2xhc3MgUmVwb3NpdG9yaW8ge1xuICAgIGluaXQgeyBwcmludGxuKFwiICAocmVwb3NpdMOzcmlvIGNyaWFkbyBhZ29yYSlcIikgfVxuICAgIGZ1biBsaXN0YXIoKSA9IGxpc3RPZihcIkVzdHVkYXIgS290bGluXCIsIFwiRW50ZW5kZXIgbGFtYmRhc1wiKVxufVxuXG5mdW4gbWFpbigpIHtcbiAgICAvLyBTZW0gYGJ5YDogYSB2YXJpw6F2ZWwgZ3VhcmRhIG8gb2JqZXRvIG9ic2VydsOhdmVsLCBlIMOpIHByZWNpc28gcGFzc2FyIHBvciBlbGUuXG4gICAgLy8gQ29tIGBieWA6IGEgdmFyacOhdmVsIHNlIGNvbXBvcnRhIGNvbW8gdW0gU3RyaW5nIGNvbXVtOyBsZXIgZSBncmF2YXIgcGFzc2FtIHBlbG8gZGVsZWdhZG8uXG4gICAgdmFyIHRleHRvOiBTdHJpbmcgYnkgT2JzZXJ2YXZlbChcIlwiKSB7IG5vbWUsIGFudGlnbywgbm92byAtPlxuICAgICAgICBwcmludGxuKFwiICAkbm9tZSBtdWRvdSBkZSBcXFwiJGFudGlnb1xcXCIgcGFyYSBcXFwiJG5vdm9cXFwiXCIpXG4gICAgfVxuXG4gICAgdGV4dG8gPSBcIkVzdHVkYXJcIiAgICAgICAgICAgIC8vIGNoYW1hIHNldFZhbHVlXG4gICAgdGV4dG8gPSBcIkVzdHVkYXIgS290bGluXCJcbiAgICBwcmludGxuKHRleHRvLmxlbmd0aCkgICAgICAgIC8vIGNoYW1hIGdldFZhbHVlOyBvIHRpcG8gw6kgU3RyaW5nLCBuw6NvIE9ic2VydmF2ZWw8U3RyaW5nPlxuXG4gICAgLy8gYGJ5IGxhenlgOiBvIHZhbG9yIHPDsyDDqSBjYWxjdWxhZG8gbm8gcHJpbWVpcm8gdXNvLCBlIHVtYSB2ZXogc8OzLlxuICAgIC8vIE8gYGJ5IGluamVjdGAgZG8gS29pbiBzZWd1ZSBhIG1lc21hIGlkZWlhOiBidXNjYSBhIGRlcGVuZMOqbmNpYSBubyBwcmltZWlybyB1c28uXG4gICAgcHJpbnRsbihcImFudGVzIGRlIHVzYXIgbyByZXBvc2l0w7NyaW9cIilcbiAgICB2YWwgcmVwb3NpdG9yaW86IFJlcG9zaXRvcmlvIGJ5IGxhenkgeyBSZXBvc2l0b3JpbygpIH1cbiAgICBwcmludGxuKFwicHJpbWVpcm8gdXNvOlwiKVxuICAgIHByaW50bG4ocmVwb3NpdG9yaW8ubGlzdGFyKCkpXG4gICAgcHJpbnRsbihcInNlZ3VuZG8gdXNvOlwiKVxuICAgIHByaW50bG4ocmVwb3NpdG9yaW8ubGlzdGFyKCkpXG5cbiAgICAvLyBFeHBlcmltZW50ZTpcbiAgICAvLyAxLiBUcm9xdWUgYHZhciB0ZXh0bzogU3RyaW5nIGJ5IE9ic2VydmF2ZWwoXCJcIikuLi5gIHBvciBgdmFsYC4gTyBxdWUgZGVpeGEgZGUgY29tcGlsYXI/XG4gICAgLy8gMi4gQ29tZW50ZSBhcyBkdWFzIGNoYW1hZGFzIGEgcmVwb3NpdG9yaW8ubGlzdGFyKCkuIEEgbWVuc2FnZW0gXCIocmVwb3NpdMOzcmlvIGNyaWFkbyBhZ29yYSlcIlxuICAgIC8vICAgIGFpbmRhIGFwYXJlY2U/XG4gICAgLy8gMy4gRmHDp2EgbyBkZWxlZ2FkbyByZWN1c2FyIHRleHRvIGVtIGJyYW5jbzogZW0gc2V0VmFsdWUsIHPDsyB0cm9xdWUgbyB2YWxvciBzZSBub3ZvLmlzTm90QmxhbmsoKVxuICAgIC8vICAgIChkaWNhOiByZXN0cmluamEgYSBjbGFzc2UgYSBTdHJpbmcgcGFyYSBwb2RlciBjaGFtYXIgaXNOb3RCbGFuaykuXG59XG4ifQ==
[demo10]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyAxMCDigJQgZnVuw6fDtWVzIHN1c3BlbmQgZSBjb3Jyb3RpbmFzXG4vLyBgc3VzcGVuZGAgbWFyY2EgdW1hIGZ1bsOnw6NvIHF1ZSBwb2RlIGVzcGVyYXIgc2VtIHByZW5kZXIgYSB0aHJlYWQuXG4vLyBObyBleGVtcGxvIGRlIFRhcmVmYXMsIG8gcmVwb3NpdMOzcmlvIMOpIHN1c3BlbmQgcG9ycXVlIGNvbnN1bHRhIG8gYmFuY28uXG5cbmltcG9ydCBrb3RsaW54LmNvcm91dGluZXMuYXN5bmNcbmltcG9ydCBrb3RsaW54LmNvcm91dGluZXMuY29yb3V0aW5lU2NvcGVcbmltcG9ydCBrb3RsaW54LmNvcm91dGluZXMuZGVsYXlcbmltcG9ydCBrb3RsaW54LmNvcm91dGluZXMubGF1bmNoXG5pbXBvcnQga290bGlueC5jb3JvdXRpbmVzLnJ1bkJsb2NraW5nXG5pbXBvcnQga290bGluLnRpbWUubWVhc3VyZVRpbWVcblxuZGF0YSBjbGFzcyBUYXJlZmEodmFsIGlkOiBJbnQsIHZhbCB0aXR1bG86IFN0cmluZylcblxuLy8gZGVsYXkoKSBzdXNwZW5kZSBhIGNvcnJvdGluYSBwb3IgdW0gdGVtcG8sIHNlbSBibG9xdWVhciBhIHRocmVhZCAoZGlmZXJlbnRlIGRlIFRocmVhZC5zbGVlcCkuXG4vLyBBcXVpIGVsZSBmYXogbyBwYXBlbCBkYSBjb25zdWx0YSBhbyBiYW5jby5cbnN1c3BlbmQgZnVuIGJ1c2NhcihpZDogSW50KTogVGFyZWZhIHtcbiAgICBkZWxheSg1MDApXG4gICAgcmV0dXJuIFRhcmVmYShpZCwgXCJUYXJlZmEgJGlkXCIpXG59XG5cbi8vIFVtYSBmdW7Dp8OjbyBzdXNwZW5kIHPDsyBwb2RlIHNlciBjaGFtYWRhIGRlIG91dHJhIGZ1bsOnw6NvIHN1c3BlbmQgb3UgZGUgZGVudHJvIGRlIHVtYSBjb3Jyb3RpbmEuXG5zdXNwZW5kIGZ1biBidXNjYXJFbVNlcXVlbmNpYSgpOiBMaXN0PFRhcmVmYT4gPSBsaXN0T2YoYnVzY2FyKDEpLCBidXNjYXIoMiksIGJ1c2NhcigzKSlcblxuLy8gYXN5bmMgaW5pY2lhIGNhZGEgYnVzY2Egc2VtIGVzcGVyYXIgYSBhbnRlcmlvcjsgYXdhaXQoKSBwZWdhIG8gcmVzdWx0YWRvLlxuLy8gY29yb3V0aW5lU2NvcGUgc8OzIHRlcm1pbmEgcXVhbmRvIHRvZGFzIGFzIGNvcnJvdGluYXMgY3JpYWRhcyBkZW50cm8gZGVsZSB0ZXJtaW5hbS5cbnN1c3BlbmQgZnVuIGJ1c2NhckFvTWVzbW9UZW1wbygpOiBMaXN0PFRhcmVmYT4gPSBjb3JvdXRpbmVTY29wZSB7XG4gICAgdmFsIHBlZGlkb3MgPSBsaXN0T2YoMSwgMiwgMykubWFwIHsgaWQgLT4gYXN5bmMgeyBidXNjYXIoaWQpIH0gfVxuICAgIHBlZGlkb3MubWFwIHsgaXQuYXdhaXQoKSB9XG59XG5cbi8vIHJ1bkJsb2NraW5nIGNyaWEgdW1hIGNvcnJvdGluYSBlIGVzcGVyYSBwb3IgZWxhOiDDqSBhIHBvbnRlIGVudHJlIG8gbWFpbiBjb211bSBlIG8gbXVuZG8gc3VzcGVuZC5cbi8vIE5vIEt0b3IsIGNhZGEgcmVxdWlzacOnw6NvIGrDoSByb2RhIG51bWEgY29ycm90aW5hOiBuw6NvIHNlIGVzY3JldmUgcnVuQmxvY2tpbmcgbmFzIHJvdGFzLlxuZnVuIG1haW4oKSA9IHJ1bkJsb2NraW5nIHtcbiAgICB2YWwgdGVtcG8xID0gbWVhc3VyZVRpbWUgeyBwcmludGxuKGJ1c2NhckVtU2VxdWVuY2lhKCkpIH1cbiAgICBwcmludGxuKFwiZW0gc2VxdcOqbmNpYTogJHt0ZW1wbzEuaW5XaG9sZU1pbGxpc2Vjb25kc30gbXNcIilcblxuICAgIHZhbCB0ZW1wbzIgPSBtZWFzdXJlVGltZSB7IHByaW50bG4oYnVzY2FyQW9NZXNtb1RlbXBvKCkpIH1cbiAgICBwcmludGxuKFwiYW8gbWVzbW8gdGVtcG86ICR7dGVtcG8yLmluV2hvbGVNaWxsaXNlY29uZHN9IG1zXCIpXG5cbiAgICAvLyBDb3Jyb3RpbmFzIHPDo28gbGV2ZXM6IGRleiBtaWwgZXNwZXJhbmRvIGFvIG1lc21vIHRlbXBvLCBlbSBwb3VjYXMgdGhyZWFkcy5cbiAgICB2YWwgdGVtcG8zID0gbWVhc3VyZVRpbWUge1xuICAgICAgICBjb3JvdXRpbmVTY29wZSB7XG4gICAgICAgICAgICByZXBlYXQoMTBfMDAwKSB7IGxhdW5jaCB7IGRlbGF5KDUwMCkgfSB9XG4gICAgICAgIH1cbiAgICB9XG4gICAgcHJpbnRsbihcIjEwLjAwMCBjb3Jyb3RpbmFzOiAke3RlbXBvMy5pbldob2xlTWlsbGlzZWNvbmRzfSBtc1wiKVxuXG4gICAgLy8gRXhwZXJpbWVudGU6XG4gICAgLy8gMS4gVGlyZSBhIHBhbGF2cmEgc3VzcGVuZCBkZSBidXNjYXIoKSBlIGxlaWEgbyBlcnJvIGVtIGRlbGF5KCkuXG4gICAgLy8gMi4gQ2hhbWUgYnVzY2FyKDEpIGRlbnRybyBkZSB1bWEgZnVuw6fDo28gY29tdW0gKHNlbSBzdXNwZW5kKSBlIGxlaWEgbyBlcnJvLlxuICAgIC8vIDMuIFRyb3F1ZSBkZWxheSg1MDApIHBvciBUaHJlYWQuc2xlZXAoNTAwKSBkZW50cm8gZG8gbGF1bmNoIGUgY29tcGFyZSBvIHRlbXBvIGRhcyAxMC4wMDAuXG59XG4ifQ==
[demo11]: https://play.kotlinlang.org/#eyJwbGF0Zm9ybSI6ICJqYXZhIiwgImFyZ3MiOiAiIiwgIm5vbmVNYXJrZXJzIjogdHJ1ZSwgInRoZW1lIjogImlkZWEiLCAiY29kZSI6ICIvLyBEZW1vbnN0cmHDp8OjbyAxMSDigJQgbyBtZXNtbyBwcm9ncmFtYSwgcHJpbWVpcm8gXCJKYXZhIGVzY3JpdG8gZW0gS290bGluXCIsIGRlcG9pcyBLb3RsaW4gaWRpb23DoXRpY29cbi8vIEFzIGR1YXMgdmVyc8O1ZXMgY29tcGlsYW0gZSBkw6NvIG8gbWVzbW8gcmVzdWx0YWRvLiBBIHNlZ3VuZGEgdXNhIG8gcXVlIGEgbGluZ3VhZ2VtIG9mZXJlY2UuXG5cbi8vIC0tLS0tLS0tLS0gVmVyc8OjbyAxOiB0cmFkdXppZGEgZG8gSmF2YSwgbGluaGEgYSBsaW5oYSAtLS0tLS0tLS0tXG5cbmNsYXNzIFRhcmVmYVYxIHtcbiAgICBwcml2YXRlIHZhciBpZDogSW50XG4gICAgcHJpdmF0ZSB2YXIgdGl0dWxvOiBTdHJpbmdcbiAgICBwcml2YXRlIHZhciBmZWl0YTogQm9vbGVhblxuXG4gICAgY29uc3RydWN0b3IoaWQ6IEludCwgdGl0dWxvOiBTdHJpbmcsIGZlaXRhOiBCb29sZWFuKSB7XG4gICAgICAgIHRoaXMuaWQgPSBpZFxuICAgICAgICB0aGlzLnRpdHVsbyA9IHRpdHVsb1xuICAgICAgICB0aGlzLmZlaXRhID0gZmVpdGFcbiAgICB9XG5cbiAgICBmdW4gZ2V0SWQoKTogSW50IHsgcmV0dXJuIGlkIH1cbiAgICBmdW4gZ2V0VGl0dWxvKCk6IFN0cmluZyB7IHJldHVybiB0aXR1bG8gfVxuICAgIGZ1biBpc0ZlaXRhKCk6IEJvb2xlYW4geyByZXR1cm4gZmVpdGEgfVxufVxuXG5jbGFzcyBUYXJlZmFVdGlsIHtcbiAgICBjb21wYW5pb24gb2JqZWN0IHtcbiAgICAgICAgZnVuIHRpdHVsb3NQZW5kZW50ZXModGFyZWZhczogTGlzdDxUYXJlZmFWMT4/KTogTGlzdDxTdHJpbmc+IHtcbiAgICAgICAgICAgIHZhbCByZXN1bHRhZG8gPSBBcnJheUxpc3Q8U3RyaW5nPigpXG4gICAgICAgICAgICBpZiAodGFyZWZhcyAhPSBudWxsKSB7XG4gICAgICAgICAgICAgICAgZm9yIChpIGluIDAgdW50aWwgdGFyZWZhcy5zaXplKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhbCB0YXJlZmEgPSB0YXJlZmFzLmdldChpKVxuICAgICAgICAgICAgICAgICAgICBpZiAodGFyZWZhLmlzRmVpdGEoKSA9PSBmYWxzZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgcmVzdWx0YWRvLmFkZCh0YXJlZmEuZ2V0VGl0dWxvKCkudXBwZXJjYXNlKCkpXG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICByZXR1cm4gcmVzdWx0YWRvXG4gICAgICAgIH1cblxuICAgICAgICBmdW4gZGVzY3JldmVyKHRhcmVmYTogVGFyZWZhVjE/KTogU3RyaW5nIHtcbiAgICAgICAgICAgIHZhciB0ZXh0bzogU3RyaW5nXG4gICAgICAgICAgICBpZiAodGFyZWZhID09IG51bGwpIHtcbiAgICAgICAgICAgICAgICB0ZXh0byA9IFwibmVuaHVtYSB0YXJlZmFcIlxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICBpZiAodGFyZWZhLmlzRmVpdGEoKSkge1xuICAgICAgICAgICAgICAgICAgICB0ZXh0byA9IHRhcmVmYS5nZXRUaXR1bG8oKSArIFwiIChmZWl0YSlcIlxuICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHRleHRvID0gdGFyZWZhLmdldFRpdHVsbygpICsgXCIgKHBlbmRlbnRlKVwiXG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcmV0dXJuIHRleHRvXG4gICAgICAgIH1cbiAgICB9XG59XG5cbi8vIC0tLS0tLS0tLS0gVmVyc8OjbyAyOiBLb3RsaW4gaWRpb23DoXRpY28gLS0tLS0tLS0tLVxuXG4vLyBkYXRhIGNsYXNzIGNvbSBwcm9wcmllZGFkZXMgbm8gY29uc3RydXRvcjsgdmFsb3IgcGFkcsOjbyBlbSB2ZXogZGUgc29icmVjYXJnYS5cbmRhdGEgY2xhc3MgVGFyZWZhKHZhbCBpZDogSW50LCB2YWwgdGl0dWxvOiBTdHJpbmcsIHZhbCBmZWl0YTogQm9vbGVhbiA9IGZhbHNlKVxuXG4vLyBFeHRlbnPDo28gZW0gdmV6IGRlIGNsYXNzZSB1dGlsaXTDoXJpYTsgZmlsdGVyIGUgbWFwIGVtIHZleiBkZSBsYcOnbyBjb20gw61uZGljZTtcbi8vIGEgbGlzdGEgbsOjbyDDqSBhbnVsw6F2ZWw6IHF1ZW0gbsOjbyB0ZW0gdGFyZWZhcyBwYXNzYSB1bWEgbGlzdGEgdmF6aWEuXG5mdW4gTGlzdDxUYXJlZmE+LnRpdHVsb3NQZW5kZW50ZXMoKTogTGlzdDxTdHJpbmc+ID0gZmlsdGVyIHsgIWl0LmZlaXRhIH0ubWFwIHsgaXQudGl0dWxvLnVwcGVyY2FzZSgpIH1cblxuLy8gRnVuw6fDo28gZGUgdW1hIGV4cHJlc3PDo287IHdoZW4gZSBpZiBjb21vIGV4cHJlc3PDtWVzOyB0ZW1wbGF0ZSBkZSBzdHJpbmcuXG5mdW4gZGVzY3JldmVyKHRhcmVmYTogVGFyZWZhPyk6IFN0cmluZyA9IHdoZW4ge1xuICAgIHRhcmVmYSA9PSBudWxsIC0+IFwibmVuaHVtYSB0YXJlZmFcIlxuICAgIGVsc2UgLT4gXCIke3RhcmVmYS50aXR1bG99ICgke2lmICh0YXJlZmEuZmVpdGEpIFwiZmVpdGFcIiBlbHNlIFwicGVuZGVudGVcIn0pXCJcbn1cblxuZnVuIG1haW4oKSB7XG4gICAgdmFsIHYxID0gbGlzdE9mKFRhcmVmYVYxKDEsIFwiRXN0dWRhciBLb3RsaW5cIiwgdHJ1ZSksIFRhcmVmYVYxKDIsIFwiRW50ZW5kZXIgbGFtYmRhc1wiLCBmYWxzZSkpXG4gICAgcHJpbnRsbihUYXJlZmFVdGlsLnRpdHVsb3NQZW5kZW50ZXModjEpKVxuICAgIHByaW50bG4oVGFyZWZhVXRpbC5kZXNjcmV2ZXIodjEuZ2V0KDApKSlcbiAgICBwcmludGxuKFRhcmVmYVV0aWwuZGVzY3JldmVyKG51bGwpKVxuXG4gICAgdmFsIHYyID0gbGlzdE9mKFRhcmVmYSgxLCBcIkVzdHVkYXIgS290bGluXCIsIGZlaXRhID0gdHJ1ZSksIFRhcmVmYSgyLCBcIkVudGVuZGVyIGxhbWJkYXNcIikpXG4gICAgcHJpbnRsbih2Mi50aXR1bG9zUGVuZGVudGVzKCkpXG4gICAgcHJpbnRsbihkZXNjcmV2ZXIodjJbMF0pKVxuICAgIHByaW50bG4oZGVzY3JldmVyKG51bGwpKVxuXG4gICAgLy8gRXhwZXJpbWVudGU6XG4gICAgLy8gMS4gQ29udGUgYXMgbGluaGFzIGRlIGNhZGEgdmVyc8Ojby5cbiAgICAvLyAyLiBJbXByaW1hIHYxLmdldCgwKSBlIHYyWzBdLiBRdWFsIGRhcyBkdWFzIG1vc3RyYSBvcyBkYWRvcz8gUG9yIHF1w6o/XG4gICAgLy8gMy4gUmVlc2NyZXZhIGRlc2NyZXZlcigpIHVzYW5kbyB0YXJlZmE/LmxldCB7IC4uLiB9ID86IFwibmVuaHVtYSB0YXJlZmFcIi5cbn1cbiJ9
