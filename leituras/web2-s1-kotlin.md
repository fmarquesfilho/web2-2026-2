# Leitura — Kotlin para quem vem do Java (Sprint 1, apoio)

Guia de apoio para ler e escrever o Kotlin que aparece no exemplo de Tarefas, tanto na API
em Ktor (Web II) quanto no aplicativo em Compose Multiplatform (Dispositivos Móveis). O
ponto de partida é o Java que a turma já conhece: cada recurso é apresentado ao lado do
equivalente em Java, quando existe um, e depois localizado no código do exemplo.

A leitura dá mais espaço ao que não tem equivalente direto em Java e que mais aparece no
curso: lambdas, lambda no fim da chamada, funções de extensão, lambdas com receptor e
delegação com `by`. Variáveis, funções e classes entram de forma resumida, como revisão.

Como usar. Cada capítulo aponta uma demonstração: um arquivo curto, com `main`, que roda no
[Kotlin Playground](https://play.kotlinlang.org/) sem instalar nada. O link abre o código
já carregado; o botão **Run** executa. Cada demonstração termina com três experimentos, que
são os exercícios desta leitura. Os mesmos arquivos estão em `exemplos/kotlin-playground/`
do repositório.

As demonstrações foram compiladas e executadas com Kotlin 2.4.10, a versão do exemplo de
Tarefas. Os trechos identificados com o nome de um arquivo (`Rotas.kt`, `Telas.kt`) foram
copiados do exemplo.

## 1. Mapa: do que se vê no código ao nome do recurso

| O que aparece no código | Nome do recurso | Capítulo |
|---|---|---|
| `val`, `var`, `fun f(x: Int): String` | declarações | 2 |
| `= if (...) a else b`, `= when { ... }` | `if` e `when` como expressão | 3 |
| `Tarefa?`, `a?.b`, `a ?: c`, `a!!` | nulidade no tipo | 4 |
| `data class`, `copy(...)`, `object`, `companion object` | classes | 5 |
| `{ x -> ... }`, `{ it.id }`, `(Int) -> Unit` | lambdas e tipos de função | 6 |
| `nome { ... }`, `nome(args) { ... }` | lambda no fim da chamada | 6.4 |
| `filter { }`, `map { }`, `find { }` | coleções | 7 |
| `fun Application.rotas()`, `fun List<Tarefa>.alternando(...)` | função de extensão | 8 |
| `apply { url = ... }`, `routing { get(...) { call... } }`, `Column { Text(...) }` | lambda com receptor | 9 |
| `x?.let { }`, `apply`, `also`, `run`, `with` | funções de escopo | 9.3 |
| `val r by inject<...>()`, `var texto by remember { ... }` | delegação | 10 |
| `receive<NovaTarefa>()`, `"id" to "2"`, `return@Column` | outros símbolos | 11 |
| `suspend fun` | corrotinas | 12.2 |

## 2. Relembrando a escrita básica

```java
// Java
final String titulo = "Estudar";
int total = 0;
total = total + 1;

String situacao(Tarefa t) {
    return t.feita() ? "feita" : "pendente";
}

var t = new Tarefa(1, titulo, false);
System.out.println("Tarefa " + t.id() + ": " + t.titulo());
```

```kotlin
// Kotlin
val titulo = "Estudar"          // val: não aceita nova atribuição (o `final` do Java)
var total = 0                   // var: aceita
total = total + 1

fun situacao(t: Tarefa): String = if (t.feita) "feita" else "pendente"

val t = Tarefa(1, titulo)
println("Tarefa ${t.id}: ${t.titulo}")
```

O que muda:

- O tipo vem depois do nome, separado por dois-pontos: `t: Tarefa`, `fun f(): String`.
- O tipo pode ser omitido quando o compilador o deduz do valor: `val titulo = "Estudar"` é um `String`.
- Não há `new` nem ponto e vírgula.
- Uma função que só devolve uma expressão pode ser escrita com `=`, sem chaves e sem `return`.
- Dentro de um texto, `$nome` insere uma variável e `${expressão}` insere o resultado de uma expressão.
- `Unit` faz o papel do `void`; quando é o tipo de retorno, pode ser omitido.
- `t.titulo` lê a propriedade. Não se escreve `getTitulo()`.
- `==` compara os valores (chama `equals`); `===` compara as referências, como o `==` do Java.

📖 Ref. Kotlin — Basic syntax: <https://kotlinlang.org/docs/basic-syntax.html>

## 3. `if`, `when` e `try` devolvem valor

Em Java, `if` é um comando: para guardar o resultado, declara-se a variável antes e
atribui-se em cada ramo, ou usa-se o operador ternário. Em Kotlin, `if` é uma expressão:
tem um valor, que pode ir direto para um `val`. Por isso a linguagem não tem o operador
`? :`.

```kotlin
val situacao = if (tarefa.feita) "feita" else "pendente"
```

O `when` substitui o `switch` e também é uma expressão. Sem argumento, ele funciona como
uma sequência de `if`/`else if`: o primeiro ramo cuja condição é verdadeira dá o valor.

```kotlin
// Tarefa.kt (Web II)
fun violacoes(): List<String> = when {
    titulo.isBlank() -> listOf("titulo: não pode ficar em branco")
    titulo.length > 200 -> listOf("titulo: no máximo 200 caracteres")
    else -> emptyList()
}
```

O `try` segue a mesma regra: `val numero = try { texto.toInt() } catch (e: NumberFormatException) { 0 }`.
Kotlin não tem exceções verificadas: nenhuma função é obrigada a declarar `throws`, e
nenhuma chamada é obrigada a ter `try`.

📖 Ref. Kotlin — Conditions and loops: <https://kotlinlang.org/docs/control-flow.html>

▶ [Demonstração 1 — expressões e nulidade][demo1]

## 4. Nulidade faz parte do tipo

Em Java, toda referência pode ser `null`, e o erro aparece em execução, como
`NullPointerException`. Em Kotlin, o tipo diz se o valor pode faltar, e o compilador cobra
o tratamento.

| Escrita | Significado | Em Java |
|---|---|---|
| `Tarefa` | nunca é `null` | sem equivalente; depende de convenção ou de anotações |
| `Tarefa?` | pode ser `null` | toda referência |
| `a?.b` | chamada segura: `b` se `a` não for `null`; senão, `null` | `a != null ? a.b : null` |
| `a ?: c` | operador Elvis: `a` se não for `null`; senão, `c` | `a != null ? a : c` |
| `a!!` | afirma que não é `null`; se for, lança `NullPointerException` | o uso direto de uma referência |

O lado direito do `?:` pode ser um `return` ou um `throw`. É assim que a rota de busca
trata os dois casos de erro antes de chegar à resposta:

```kotlin
// Rotas.kt (Web II)
val id = call.parameters["id"]?.toIntOrNull()
    ?: throw BadRequestException("O id deve ser um número inteiro")
val tarefa = repositorio.buscar(id)
    ?: throw NotFoundException("A tarefa $id não existe")
call.respond(tarefa)
```

Lendo a primeira instrução da esquerda para a direita: `call.parameters["id"]` devolve um
`String?` (o parâmetro pode não existir); `?.toIntOrNull()` só converte se houver texto, e
devolve `Int?` (o texto pode não ser um número); `?: throw ...` encerra a rota quando o
resultado é `null`. Depois dessa linha, `id` é um `Int`, sem interrogação.

Esse ajuste de tipo se chama *smart cast*: depois de um teste que elimina o `null`, o
compilador passa a tratar a variável como não nula.

```kotlin
// Telas.kt (Móveis)
if (onVoltar != null) {
    TextButton(onClick = onVoltar) { Text("Voltar") }   // aqui onVoltar é () -> Unit
}
```

O `!!` troca um erro de compilação por um possível erro em execução. Quando ele aparecer,
vale procurar uma forma com `?.`, `?:` ou um teste de `null`.

📖 Ref. Kotlin — Null safety: <https://kotlinlang.org/docs/null-safety.html>
📖 Ref. Kotlin — Nullability in Java and Kotlin: <https://kotlinlang.org/docs/java-to-kotlin-nullability-guide.html>

▶ [Demonstração 1 — expressões e nulidade][demo1]

## 5. Classes

### 5.1 O construtor declara as propriedades

```java
// Java
public record Tarefa(int id, String titulo, boolean feita) {
    public Tarefa(int id, String titulo) {
        this(id, titulo, false);
    }
}
```

```kotlin
// Kotlin
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)
```

Os parênteses depois do nome da classe são o construtor primário. Um parâmetro com `val` ou
`var` é, ao mesmo tempo, parâmetro do construtor e propriedade da classe.

`data class` é o parente mais próximo do `record` do Java: o compilador gera `equals`,
`hashCode` e `toString` a partir das propriedades do construtor. Gera também `copy`, que
cria uma cópia com parte dos valores trocada:

```kotlin
val a = Tarefa(1, "Estudar")
val b = a.copy(feita = true)        // a continua igual; b é outra tarefa
```

### 5.2 Valores padrão e argumentos nomeados

`val feita: Boolean = false` define um valor padrão: quem chama pode omitir o argumento. É
o que em Java se resolve com construtores ou métodos sobrecarregados.

Na chamada, um argumento pode ser passado pelo nome. A ordem deixa de importar, e a leitura
fica clara quando há vários parâmetros do mesmo tipo:

```kotlin
// Telas.kt (Móveis)
CartaoTarefa(Tarefa(1, "Estudar Compose", feita = true), onAlternar = {}, onAbrir = {})
```

No Compose, quase toda função tem muitos parâmetros com valor padrão. Por isso as chamadas
costumam nomear os argumentos: `Button(onClick = onAdicionar, enabled = valido)`.

### 5.3 `object` e `companion object`

`object` declara uma classe e a sua única instância, de uma vez. É o padrão *singleton*,
feito pela linguagem.

```kotlin
// Banco.kt (Web II): a descrição da tabela existe uma vez só
object Tarefas : Table("tarefas") { ... }

// App.kt (Móveis): a rota da lista não tem argumentos, então basta uma instância
@Serializable
object Lista
```

Kotlin não tem `static`. O que pertence à classe, e não a cada instância, vai num
`companion object`, e é chamado pelo nome da classe:

```kotlin
// Banco.kt (Web II)
data class ConfigBanco(val url: String, val usuario: String, val senha: String) {
    companion object {
        fun doAmbiente() = ConfigBanco(...)
    }
}

ConfigBanco.doAmbiente()
```

Funções que não dependem de nenhuma classe podem ficar soltas no arquivo, como
`fun main()` e `fun tituloValido(texto: String)`. São as funções de nível superior.

📖 Ref. Kotlin — Data classes: <https://kotlinlang.org/docs/data-classes.html>
📖 Ref. Kotlin — Object declarations and expressions: <https://kotlinlang.org/docs/object-declarations.html>

▶ [Demonstração 2 — classes][demo2]

## 6. Lambdas

### 6.1 O que é

Uma lambda é um bloco de código guardado num valor: pode ser atribuída a uma variável,
passada como argumento e executada depois. Java tem lambdas desde a versão 8; a diferença
está no tipo e na escrita.

Em Java, o tipo de uma lambda é uma interface com um único método (`Runnable`,
`Predicate<T>`, `Function<T, R>`). Em Kotlin, existe um tipo próprio para funções, escrito
`(Entrada) -> Saída`:

| Tipo em Kotlin | Interface em Java | Onde aparece no curso |
|---|---|---|
| `() -> Unit` | `Runnable` | `onAdicionar`, `onVoltar` |
| `(Int) -> Unit` | `Consumer<Integer>` | `onAlternar`, `onAbrir` |
| `(String) -> Unit` | `Consumer<String>` | `onTextoChange` |
| `() -> T` | `Supplier<T>` | o bloco de `remember { ... }` e o de `lazy { ... }` |
| `(Tarefa) -> Boolean` | `Predicate<Tarefa>` | a condição de `filter` e de `find` |
| `(Tarefa) -> String` | `Function<Tarefa, String>` | a transformação de `map` |

Para executar, chama-se a variável como se fosse uma função: `onAlternar(2)`.

### 6.2 A escrita, da forma completa à mais curta

```kotlin
val feita: (Tarefa) -> Boolean = { tarefa: Tarefa -> tarefa.feita }   // completa
val feita: (Tarefa) -> Boolean = { tarefa -> tarefa.feita }           // tipo do parâmetro deduzido
val feita: (Tarefa) -> Boolean = { it.feita }                         // um parâmetro só: `it`
```

- As chaves delimitam a lambda. Em Java, a lambda `t -> t.feita()` não tem chaves em volta.
- A seta `->` separa os parâmetros do corpo.
- Quando há exatamente um parâmetro, ele pode ficar sem nome e é chamado de `it`.
- Quando não há parâmetros, não há seta: `{ println("clicou") }`.

Para ler em voz alta, `{ it.feita }` é "dada uma tarefa, a propriedade `feita` dela".

### 6.3 O valor de uma lambda

O valor de uma lambda é a última expressão do bloco. Não se escreve `return`:

```kotlin
val dobro: (Int) -> Int = { numero ->
    val resultado = numero * 2
    resultado                       // este é o valor devolvido
}
```

### 6.4 Lambda no fim da chamada

Esta regra explica a forma de quase todo o código do curso. Quando o último parâmetro de
uma função é uma função, a lambda pode ser escrita depois dos parênteses. Se ela for o
único argumento, os parênteses vazios somem. A mesma chamada, em quatro formas:

```kotlin
tarefas.filter({ tarefa -> !tarefa.feita })    // 1. lambda como argumento comum
tarefas.filter() { tarefa -> !tarefa.feita }   // 2. lambda depois dos parênteses
tarefas.filter { tarefa -> !tarefa.feita }     // 3. parênteses vazios somem
tarefas.filter { !it.feita }                   // 4. com `it`
```

As quatro compilam para o mesmo programa. A forma 4 é a que se escreve no dia a dia.

Consequência para a leitura: `nome { ... }` e `nome(argumentos) { ... }` são chamadas de
função, e o bloco é o último argumento. Não são comandos da linguagem.

```kotlin
install(ContentNegotiation) { json() }                  // Ktor: chamada a install, com 2 argumentos
Button(onClick = onAdicionar) { Text("Adicionar") }     // Compose: chamada a Button, com 2 argumentos
routing { ... }                                         // Ktor: chamada a routing, com 1 argumento
```

Uma função pode receber mais de uma lambda. Só a última sai dos parênteses; as outras
costumam ser passadas pelo nome:

```kotlin
// Telas.kt (Móveis): `key` é uma lambda nomeada; o conteúdo de cada item é a lambda no fim
items(tarefas, key = { it.id }) { tarefa ->
    CartaoTarefa(tarefa, onAlternar = { onAlternar(tarefa.id) }, onAbrir = { onAbrir(tarefa.id) })
}
```

### 6.5 Uma lambda enxerga as variáveis ao redor

Uma lambda pode ler e alterar as variáveis do lugar onde foi criada. Em Java, a lambda só
pode ler variáveis que não mudam (`final` ou efetivamente finais).

```kotlin
// App.kt (Móveis): as duas lambdas alteram o estado declarado acima delas
val adicionar = {
    tarefas = tarefas.comNova(proximoId, texto)
    proximoId++
    texto = ""
}
val alternar = { id: Int -> tarefas = tarefas.alternando(id) }
```

`adicionar` tem o tipo `() -> Unit`, e `alternar`, o tipo `(Int) -> Unit`. É por isso que
podem ser passadas às telas nos parâmetros `onAdicionar` e `onAlternar`.

### 6.6 Passar uma função que já existe

Uma função declarada com `fun` pode ser usada como valor com `::`. Uma variável que já
guarda uma função é passada diretamente, entre parênteses:

```kotlin
tarefas.filter(::estaFeita)          // referência a uma função declarada com fun
selecionada?.let(alternar)           // App.kt (Móveis): `alternar` já é um valor (Int) -> Unit
```

📖 Ref. Kotlin — Higher-order functions and lambdas: <https://kotlinlang.org/docs/lambdas.html>
📖 Ref. Kotlin — Passing trailing lambdas: <https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas>

▶ [Demonstração 3 — lambdas][demo3]

## 7. Coleções

As operações que em Java pedem `stream()` e um coletor são funções diretas da lista, e
recebem uma lambda no fim da chamada.

```java
// Java
List<String> pendentes = tarefas.stream()
    .filter(t -> !t.feita())
    .map(t -> t.titulo())
    .toList();
```

```kotlin
// Kotlin
val pendentes = tarefas.filter { !it.feita }.map { it.titulo }
```

| Função | Devolve |
|---|---|
| `filter { condição }` | uma lista com os elementos que passam na condição |
| `map { transformação }` | uma lista com cada elemento transformado |
| `find { condição }` | o primeiro elemento que passa, ou `null` |
| `any { condição }` | `true` se algum elemento passa |
| `count { condição }` | quantos passam |
| `sortedBy { chave }` | uma lista ordenada pela chave |
| `lista + elemento` | uma lista com o elemento a mais |

`List` é somente leitura: não tem `add` nem `remove`. Para alterar no lugar, o tipo é
`MutableList`, criado com `mutableListOf(...)`. O curso usa listas somente leitura, e
"alterar" significa criar uma lista nova:

```kotlin
// Tarefa.kt (Móveis): devolve outra lista, com uma tarefa trocada por uma cópia
fun List<Tarefa>.alternando(id: Int): List<Tarefa> =
    map { if (it.id == id) it.copy(feita = !it.feita) else it }
```

No Compose isso tem uma consequência prática: a tela se redesenha quando o estado recebe
um valor novo (`tarefas = tarefas.alternando(id)`). Alterar um objeto por dentro não avisa
ninguém.

📖 Ref. Kotlin — Collections overview: <https://kotlinlang.org/docs/collections-overview.html>
📖 Ref. Kotlin — Collections in Java and Kotlin: <https://kotlinlang.org/docs/java-to-kotlin-collections-guide.html>

▶ [Demonstração 4 — coleções][demo4]

## 8. Funções de extensão

### 8.1 O que é

Uma função de extensão acrescenta uma função a um tipo que já existe, sem herança e sem
alterar a classe. O tipo é escrito antes do nome da função, separado por um ponto, e se
chama receptor.

```java
// Java: método estático numa classe utilitária
class Tarefas {
    static List<Tarefa> alternando(List<Tarefa> lista, int id) { ... }
}

Tarefas.alternando(lista, 2);
```

```kotlin
// Kotlin: List<Tarefa> é o receptor
fun List<Tarefa>.alternando(id: Int): List<Tarefa> =
    map { if (it.id == id) it.copy(feita = !it.feita) else it }

lista.alternando(2)
```

Dentro da função, `this` é o receptor: a lista sobre a qual a função foi chamada. O `this`
pode ser omitido; `map { ... }` ali quer dizer `this.map { ... }`.

A chamada fica com a forma de um método (`lista.alternando(2)`), o que permite encadear:

```kotlin
val depois = tarefas.comNova(3, "Gravar o vídeo").alternando(1)
```

### 8.2 O que ela não faz

- Não altera a classe. É uma função comum; o compilador a transforma numa chamada que
  recebe o receptor como primeiro argumento.
- Não enxerga os membros privados do tipo. Só usa o que é público.
- Não substitui um membro: se a classe já tem uma função com o mesmo nome e os mesmos
  parâmetros, a da classe é a escolhida.
- Precisa ser importada quando está em outro pacote. É por isso que o `Rotas.kt` importa
  `io.ktor.server.routing.get` e `io.ktor.server.response.respond`: são extensões.

### 8.3 No exemplo de Tarefas

```kotlin
// Aplicacao.kt (Web II): extensões de Application organizam a configuração do servidor
fun Application.modulo(config: ConfigBanco = ConfigBanco.doAmbiente()) { ... }
fun Application.configurar(repositorio: RepositorioDeTarefas) {
    install(Koin) { ... }           // this.install(...): o this é a Application
    tratarErros()                   // outra extensão de Application, em Erros.kt
    rotas()                         // outra extensão de Application, em Rotas.kt
}

// Banco.kt (Web II): extensão privada, visível só neste arquivo
private fun ResultRow.paraTarefa() = Tarefa(this[Tarefas.id], this[Tarefas.titulo], this[Tarefas.feita])
```

Em `embeddedServer(CIO, port = 8080) { modulo() }`, o bloco é executado com a aplicação
como `this` (capítulo 9), e por isso `modulo()` pode ser chamado sem nada antes.

Grande parte da biblioteca padrão é feita de extensões: `filter` e `map` são extensões de
coleções; `isBlank` e `toIntOrNull`, de `String`. No Compose, `Modifier.padding(16.dp)` é
uma extensão de `Modifier`, e `16.dp` é uma propriedade de extensão de `Int`.

📖 Ref. Kotlin — Extensions: <https://kotlinlang.org/docs/extensions.html>

▶ [Demonstração 5 — funções de extensão][demo5]

## 9. Lambda com receptor

### 9.1 O que é

Este recurso junta os capítulos 6 e 8: é uma lambda que, por dentro, tem um `this`. O tipo
se escreve com o receptor antes dos parênteses: `Conexao.() -> Unit`.

```kotlin
// Lambda comum: (Conexao) -> Unit. O objeto chega como parâmetro e precisa ser citado.
configurar { conexao ->
    conexao.url = "jdbc:postgresql://localhost/tarefas"
    conexao.usuario = "tarefas"
}

// Lambda com receptor: Conexao.() -> Unit. O objeto é o this do bloco.
configurar {
    url = "jdbc:postgresql://localhost/tarefas"       // this.url
    usuario = "tarefas"                               // this.usuario
}
```

Quem define qual das duas formas vale é a função chamada, no tipo do parâmetro:

```kotlin
fun configurar(bloco: Conexao.() -> Unit): Conexao {
    val conexao = Conexao()
    conexao.bloco()                 // executa o bloco com `conexao` como this
    return conexao
}
```

Java não tem esse recurso. O parente mais próximo é o padrão *builder* com chamadas
encadeadas (`config.setUrl(...).setUsuario(...)`).

Ao ler um bloco, a pergunta útil é: quem é o `this` aqui? A resposta está na assinatura da
função chamada. Na IDE, basta apontar para o nome da função, ou ativar as dicas que mostram
`this: Tipo` no início de cada bloco.

### 9.2 Blocos dentro de blocos

Uma biblioteca que oferece várias funções desse tipo permite escrever uma estrutura
inteira com blocos aninhados. É o que a documentação chama de *type-safe builder* ou DSL
(linguagem específica de domínio). O Ktor usa essa técnica para rotas, e o Compose, para
telas; os capítulos 12 e 13 leem os dois casos linha a linha.

Dentro de um bloco aninhado, continuam visíveis os `this` dos blocos de fora. É por isso
que, dentro de `get("/{id}") { ... }`, o código consegue usar `repositorio`, declarado em
`Application.rotas()`.

### 9.3 Funções de escopo

A biblioteca padrão tem cinco funções que executam um bloco sobre um objeto. Diferem em
duas coisas: como o objeto aparece dentro do bloco (`it` ou `this`) e o que a chamada
devolve.

| Função | O objeto é | Devolve | Uso típico |
|---|---|---|---|
| `let` | `it` | o resultado do bloco | fazer algo só quando o valor não é `null`: `x?.let { ... }` |
| `apply` | `this` | o próprio objeto | configurar um objeto recém-criado |
| `also` | `it` | o próprio objeto | um efeito a mais, como um registro em log |
| `run` | `this` | o resultado do bloco | calcular algo a partir do objeto |
| `with(x)` | `this` | o resultado do bloco | várias chamadas sobre o mesmo objeto |

Os dois casos do exemplo de Tarefas:

```kotlin
// Banco.kt (Web II): cria, configura e devolve o HikariConfig, numa expressão só
HikariDataSource(
    HikariConfig().apply {
        jdbcUrl = config.url
        username = config.usuario
        password = config.senha
        maximumPoolSize = 5
    },
)

// App.kt (Móveis): `selecionada` é Int?. A função só é chamada quando há uma tarefa selecionada.
onAlternar = { selecionada?.let(alternar) }
```

Para começar, `let` e `apply` cobrem a maior parte dos casos. Blocos de escopo aninhados
ficam difíceis de ler; quando isso acontecer, uma variável com nome resolve melhor.

📖 Ref. Kotlin — Function literals with receiver: <https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver>
📖 Ref. Kotlin — Scope functions: <https://kotlinlang.org/docs/scope-functions.html>
📖 Ref. Kotlin — Type-safe builders: <https://kotlinlang.org/docs/type-safe-builders.html>

▶ [Demonstração 6 — lambda com receptor e funções de escopo][demo6]

## 10. Delegação com `by`

`by` entrega a outro objeto, o delegado, o trabalho de ler e gravar uma propriedade. O
compilador troca cada leitura por uma chamada a `getValue()` do delegado e cada gravação
por uma chamada a `setValue()`. Java não tem equivalente.

O caso mais simples é o `by lazy`, da biblioteca padrão: o valor é calculado no primeiro
uso e guardado.

```kotlin
val repositorio: Repositorio by lazy { Repositorio() }     // ainda não criou nada
repositorio.listar()                                       // cria aqui, uma vez
```

No Ktor com Koin, a dependência é buscada no primeiro uso:

```kotlin
// Rotas.kt (Web II)
val repositorio by inject<RepositorioDeTarefas>()
```

`inject` devolve um `Lazy<RepositorioDeTarefas>`. Sem o `by`, cada uso seria
`repositorio.value.listar()`. Com o `by`, a variável se comporta como um
`RepositorioDeTarefas`.

No Compose, o delegado é o estado observável:

```kotlin
// Sem by: `texto` é um MutableState<String>
val texto = remember { mutableStateOf("") }
texto.value = "Estudar"

// Com by: `texto` se comporta como um String
var texto by remember { mutableStateOf("") }
texto = "Estudar"
```

É nas chamadas a `getValue()` e `setValue()` que o Compose registra quem leu o estado e
avisa quando ele mudou. Dois detalhes práticos: com `by`, a variável precisa ser `var`,
porque há gravação; e o arquivo precisa dos imports `androidx.compose.runtime.getValue` e
`androidx.compose.runtime.setValue` (a IDE sugere os dois).

📖 Ref. Kotlin — Delegated properties: <https://kotlinlang.org/docs/delegated-properties.html>
📖 Ref. Android — State and Jetpack Compose: <https://developer.android.com/develop/ui/compose/state>

▶ [Demonstração 9 — delegação][demo9]

## 11. Outros símbolos que aparecem no exemplo

| No código | O que é | Em Java |
|---|---|---|
| `call.receive<NovaTarefa>()`, `inject<RepositorioDeTarefas>()`, `composable<Detalhe> { }` | o tipo é passado entre `< >`, na chamada | um argumento `NovaTarefa.class` |
| `"id" to "2"`, `Tarefas.id eq id` | função infixa: `a to b` é `a.to(b)` | chamada comum de método |
| `call.parameters["id"]`, `this[Tarefas.titulo]`, `it[titulo] = nova.titulo` | operador de índice: `a[i]` chama `a.get(i)`; `a[i] = v` chama `a.set(i, v)` | só existe para arrays |
| `val (id, titulo) = tarefa` | desestruturação: uma variável para cada propriedade da `data class` | sem equivalente |
| `1..3`, `0 until n` | intervalos, usados em `for (i in 1..3)` | `for (int i = 1; i <= 3; i++)` |
| `return@Column` | sai só do bloco passado a `Column`, não da função inteira | sem equivalente |
| ``fun `id inexistente devolve 404`()`` | nome entre crases: aceita espaços; usado em nomes de teste | sem equivalente |
| `@OptIn(ExperimentalKtorApi::class)` | aceita o uso de uma API marcada como experimental | sem equivalente direto |
| `Tarefa::class` | a referência à classe | `Tarefa.class` |

Sobre o `return@Column`, que aparece em `TelaDetalhe`:

```kotlin
// Telas.kt (Móveis)
Column(modifier.padding(16.dp)) {
    if (tarefa == null) {
        Text("Selecione uma tarefa")
        return@Column               // encerra o bloco do Column; nada abaixo é executado
    }
    Text(tarefa.titulo, ...)        // daqui em diante, tarefa não é null
}
```

📖 Ref. Kotlin — Returns and jumps: <https://kotlinlang.org/docs/returns.html#return-to-labels>
📖 Ref. Kotlin — Reified type parameters: <https://kotlinlang.org/docs/inline-functions.html#reified-type-parameters>
📖 Ref. Kotlin — Infix notation: <https://kotlinlang.org/docs/functions.html#infix-notation>

## 12. No Ktor: lendo o `Rotas.kt`

### 12.1 As rotas

```kotlin
// Rotas.kt (Web II), sem a parte do OpenAPI
fun Application.rotas() {                                   // (a)
    val repositorio by inject<RepositorioDeTarefas>()       // (b)

    routing {                                               // (c)
        route("/tarefas") {                                 // (d)
            get { call.respond(repositorio.listar()) }      // (e)

            post {
                val nova = call.receive<NovaTarefa>()       // (f)
                val violacoes = nova.violacoes()
                if (violacoes.isNotEmpty()) throw EntradaInvalida(violacoes)
                val criada = repositorio.adicionar(nova)
                call.response.header(HttpHeaders.Location, "/tarefas/${criada.id}")
                call.respond(HttpStatusCode.Created, criada)
            }
        }
    }
}
```

- (a) Função de extensão de `Application`. Dentro dela, `this` é a aplicação.
- (b) Delegação: o repositório vem do Koin, no primeiro uso.
- (c) `routing` é uma função, e o bloco é o seu único argumento (lambda no fim da chamada).
  O bloco é uma lambda com receptor: dentro dele, `this` é o objeto que registra rotas.
- (d) `route("/tarefas") { ... }` é outra chamada, com dois argumentos: o caminho e o bloco.
- (e) `get { ... }` registra uma rota. O bloco não é executado agora: fica guardado e roda a
  cada requisição. Dentro dele, o `this` é um `RoutingContext`, que tem a propriedade
  `call`. É por isso que `call` pode ser usado sem ter sido declarado.
- (f) O tipo entre `< >` diz em que classe o JSON deve ser convertido.

O mesmo começo, sem as abreviações da linguagem:

```kotlin
this.routing({
    this.route("/tarefas", {
        this.get({ this.call.respond(repositorio.listar()) })
    })
})
```

Como as rotas são chamadas de função comuns, cabem entre elas `if`, laços e funções
auxiliares. No Quarkus, as mesmas rotas são declaradas por anotações (`@GET`, `@Path`).

A demonstração 7 escreve um Ktor em miniatura, com `routing`, `get` e `call`, em cerca de
trinta linhas. Ela mostra que essa sintaxe é feita com recursos comuns da linguagem.

### 12.2 `suspend`

```kotlin
// Tarefa.kt (Web II)
interface RepositorioDeTarefas {
    suspend fun listar(): List<Tarefa>
    suspend fun buscar(id: Int): Tarefa?
    suspend fun adicionar(nova: NovaTarefa): Tarefa
}
```

`suspend` marca uma função que pode pausar e continuar depois. Enquanto ela espera a
resposta do banco, a thread fica livre para atender outras requisições. A unidade que
executa esse código se chama corrotina; muitas corrotinas se revezam em poucas threads.

A regra que o compilador cobra: uma função `suspend` só pode ser chamada de outra função
`suspend` ou de dentro de uma corrotina. No Ktor, o bloco de cada rota já é `suspend`, e
cada requisição já roda numa corrotina. Por isso `repositorio.buscar(id)` é chamado
diretamente, com a aparência de código sequencial.

Em Java, os caminhos para o mesmo resultado são `CompletableFuture` ou threads virtuais.
O Quarkus do exemplo segue outra estratégia: executa o método numa thread de trabalho.

📖 Ref. Ktor — Routing: <https://ktor.io/docs/server-routing.html>
📖 Ref. Kotlin — Coroutines basics: <https://kotlinlang.org/docs/coroutines-basics.html>

▶ [Demonstração 7 — um Ktor em miniatura][demo7] · ▶ [Demonstração 10 — corrotinas][demo10]

## 13. No Compose: lendo o `Telas.kt`

### 13.1 Os parâmetros de uma tela

```kotlin
// Telas.kt (Móveis)
@Composable
fun TelaDetalhe(
    tarefa: Tarefa?,                  // (a)
    onAlternar: () -> Unit,           // (b)
    onVoltar: (() -> Unit)?,          // (c)
    modifier: Modifier = Modifier,    // (d)
)
```

- (a) Tipo anulável: pode não haver tarefa selecionada.
- (b) Tipo de função sem parâmetros e sem resultado. A tela avisa que o usuário tocou; quem
  chamou decide o que fazer. Dados entram por parâmetros comuns, e eventos saem por
  parâmetros de tipo função.
- (c) Função opcional. Os parênteses externos são necessários: `(() -> Unit)?` é "uma
  função, ou `null`"; `() -> Unit?` seria "uma função que devolve `Unit` ou `null`".
- (d) Valor padrão: quem chama pode omitir. O valor `Modifier` é um `companion object` da
  interface `Modifier`, que representa "nenhum ajuste".

Na lista, `onAlternar` é `(Int) -> Unit`, porque a lista precisa dizer qual tarefa foi
tocada. No cartão, é `() -> Unit`, porque o cartão já é de uma tarefa. A ligação entre os
dois é uma lambda:

```kotlin
CartaoTarefa(tarefa, onAlternar = { onAlternar(tarefa.id) }, onAbrir = { onAbrir(tarefa.id) })
```

### 13.2 A árvore de componentes

```kotlin
// Telas.kt (Móveis), resumido
Column(modifier.padding(16.dp)) {                                      // (a)
    Text("Minhas tarefas")
    FormularioTarefa(texto, onTextoChange, tituloValido(texto), onAdicionar)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {     // (b)
        items(tarefas, key = { it.id }) { tarefa ->                    // (c)
            CartaoTarefa(tarefa, ...)
        }
    }
}
```

- (a) `Column` é uma função, não uma classe. Recebe dois argumentos: o `modifier` e o
  bloco de conteúdo (lambda no fim da chamada). Dentro do bloco, `this` é um `ColumnScope`.
- (b) `LazyColumn` recebe um argumento nomeado e o bloco. Dentro dele, `this` é um
  `LazyListScope`.
- (c) `items` é uma função do `LazyListScope`: só existe dentro do bloco de uma lista.
  Recebe a lista, uma lambda nomeada (`key`) e, no fim, a lambda que desenha cada item.

O receptor também limita o que pode ser chamado em cada lugar. `Modifier.weight(1f)`, usado
em `Row` e `Column` para dividir o espaço, é uma função do `RowScope` e do `ColumnScope`:
fora desses blocos, não compila.

Como a tela é feita de chamadas de função, cabem nela `if` e `for`:

```kotlin
if (onVoltar != null) {
    TextButton(onClick = onVoltar) { Text("Voltar") }
}
```

A demonstração 8 escreve um Compose em miniatura, que monta uma árvore de componentes com
essa mesma escrita. O Compose de verdade funciona de outro jeito por dentro: a anotação
`@Composable` ativa um plugin do compilador, que acompanha o estado e redesenha só o que
mudou.

### 13.3 O estado

```kotlin
// App.kt (Móveis)
var tarefas by remember { mutableStateOf(tarefasIniciais) }     // delegação (capítulo 10)
val alternar = { id: Int -> tarefas = tarefas.alternando(id) }  // lambda que reatribui o estado
```

Três recursos numa linha cada: `by` faz `tarefas` se comportar como uma lista; a lambda
enxerga e altera essa variável (seção 6.5); e `alternando` é uma extensão que devolve uma
lista nova (capítulo 8). A reatribuição é o que faz a tela se redesenhar.

📖 Ref. Android — Kotlin for Jetpack Compose: <https://developer.android.com/develop/ui/compose/kotlin>

▶ [Demonstração 8 — um Compose em miniatura][demo8]

## 14. Escrever de modo idiomático

Código traduzido do Java linha a linha compila em Kotlin, mas fica mais longo do que
precisa. A tabela reúne as trocas mais comuns:

| Hábito trazido do Java | Em Kotlin |
|---|---|
| declarar tudo com `var` | `val` por padrão; `var` só quando o valor muda |
| variável declarada antes do `if` e atribuída em cada ramo | `val x = if (...) a else b` |
| `"Tarefa " + id + ": " + titulo` | `"Tarefa $id: $titulo"` |
| classe com campos privados, construtor e getters | `data class` com as propriedades no construtor |
| construtores ou métodos sobrecarregados | parâmetros com valor padrão |
| `if (x != null) { x.f() }` | `x?.f()` |
| `x != null ? x : padrao` | `x ?: padrao` |
| laço com índice e `lista.get(i)` | `for (item in lista)`, ou `filter` e `map` |
| classe `AlgumaCoisaUtil` com métodos estáticos | funções de nível superior ou de extensão |
| `lista.stream().filter(...).toList()` | `lista.filter { ... }` |
| `x.equals(y)` | `x == y` |
| `!!` para "resolver" um erro de nulidade | `?.`, `?:` ou um teste de `null` |

A demonstração 11 traz o mesmo programa nas duas versões, lado a lado.

O Android Studio e o IntelliJ IDEA sugerem boa parte dessas trocas: um trecho sublinhado
com uma lâmpada ao lado costuma ter uma forma mais curta, aplicada com `Alt+Enter`.

📖 Ref. Kotlin — Idioms: <https://kotlinlang.org/docs/idioms.html>
📖 Ref. Kotlin — Coding conventions: <https://kotlinlang.org/docs/coding-conventions.html>
📖 Ref. Kotlin — Comparison to Java: <https://kotlinlang.org/docs/comparison-to-java.html>

▶ [Demonstração 11 — idiomático][demo11]

## 15. Exercícios

Cada demonstração termina com três experimentos, escritos como comentários no fim do
`main`. A sugestão é fazer na ordem:

| | Demonstração | Recursos |
|---|---|---|
| 1 | ▶ [Expressões e nulidade][demo1] | `if`, `when`, `?.`, `?:` |
| 2 | ▶ [Classes][demo2] | `data class`, `copy`, `object`, `companion object` |
| 3 | ▶ [Lambdas][demo3] | tipos de função, `it`, lambda no fim da chamada |
| 4 | ▶ [Coleções][demo4] | `filter`, `map`, `find`, listas somente leitura |
| 5 | ▶ [Funções de extensão][demo5] | receptor, `this`, propriedade de extensão |
| 6 | ▶ [Lambda com receptor][demo6] | `Tipo.() -> Unit`, `apply`, `let`, `also`, `run`, `with` |
| 7 | ▶ [Um Ktor em miniatura][demo7] | extensão, lambda com receptor e lambda no fim, juntas |
| 8 | ▶ [Um Compose em miniatura][demo8] | o mesmo, numa árvore de componentes com eventos |
| 9 | ▶ [Delegação][demo9] | `by`, `by lazy`, um delegado que observa mudanças |
| 10 | ▶ [Corrotinas][demo10] | `suspend`, `async`, dez mil corrotinas |
| 11 | ▶ [Idiomático][demo11] | o mesmo programa em estilo Java e em estilo Kotlin |

Para praticar no código do grupo:

1. Abra um arquivo do projeto e, para cada bloco `{ ... }`, responda: de qual função ele é
   argumento? Dentro dele, o objeto é `it` ou `this`? De que tipo?
2. Reescreva uma chamada com lambda no fim na forma sem abreviações (como nos capítulos 12
   e 13) e confira que compila.
3. Encontre uma função do projeto que receba uma lista e devolva outra, e transforme-a numa
   função de extensão.
4. Procure todos os `!!` do projeto e substitua cada um por `?.`, `?:` ou um teste de `null`.

## 16. Perguntas frequentes

**Preciso dominar tudo isto para entregar a Sprint 1?** Não. Para ler e adaptar o exemplo de
Tarefas, os capítulos 4, 6, 8 e 9 são os que mais ajudam. Os demais servem de consulta.

**Quando uso `it` e quando dou nome ao parâmetro?** `it` funciona bem em blocos de uma
linha. Em blocos maiores, ou quando há um bloco dentro de outro, um nome
(`{ tarefa -> ... }`) evita dúvida sobre a quem o `it` se refere.

**Como descubro quem é o `this` de um bloco?** Pela assinatura da função chamada: o tipo do
último parâmetro tem a forma `Tipo.() -> ...`. No Android Studio e no IntelliJ IDEA, aponte
o cursor para o nome da função, ou use `Ctrl+P` dentro dos parênteses.

**Por que `routing`, `get`, `Column` e `Button` não aparecem na lista de palavras da
linguagem?** Porque são funções de biblioteca. O que a linguagem oferece é a regra que
permite escrever o último argumento fora dos parênteses.

**Função de extensão ou função comum?** Extensão quando a função é, na leitura, uma
operação sobre um tipo (`lista.alternando(id)`). Função comum quando não há um objeto
principal (`tituloValido(texto)`).

**Posso misturar Java e Kotlin no mesmo projeto?** Pode: os dois compilam para a JVM, e um
chama o outro. As bibliotecas Java usadas no exemplo (HikariCP, Flyway) são chamadas do
Kotlin sem adaptação.

**O Playground roda o código do exemplo de Tarefas?** Não: ele não tem Ktor nem Compose.
Por isso as demonstrações 7 e 8 usam versões em miniatura, escritas só com a linguagem.
A biblioteca `kotlinx.coroutines`, usada na demonstração 10, está disponível lá.

## Referências

Todas as páginas foram consultadas em outubro de 2026. A documentação do Kotlin e a do
Android são publicadas sob a licença Apache 2.0.

Para estudar em sequência
- Kotlin — Tour of Kotlin (com código executável e exercícios): <https://kotlinlang.org/docs/kotlin-tour-welcome.html>
- Kotlin — Koans (exercícios no navegador, para quem vem do Java): <https://play.kotlinlang.org/koans>
- Android — Learn the Kotlin programming language: <https://developer.android.com/kotlin/learn>
- Android — Android Basics with Compose (unidades 1 a 3 têm trilhas de Kotlin): <https://developer.android.com/courses/android-basics-compose/course>
- JetBrains — Kotlin for Education (curso Programming in Kotlin, com slides): <https://kotlinlang.org/education/>

Kotlin e Java
- Kotlin — Comparison to Java: <https://kotlinlang.org/docs/comparison-to-java.html>
- Kotlin — Strings in Java and Kotlin: <https://kotlinlang.org/docs/java-to-kotlin-idioms-strings.html>
- Kotlin — Collections in Java and Kotlin: <https://kotlinlang.org/docs/java-to-kotlin-collections-guide.html>
- Kotlin — Nullability in Java and Kotlin: <https://kotlinlang.org/docs/java-to-kotlin-nullability-guide.html>
- Kotlin — Calling Java from Kotlin: <https://kotlinlang.org/docs/java-interop.html>

Linguagem
- Kotlin — Basic syntax: <https://kotlinlang.org/docs/basic-syntax.html>
- Kotlin — Conditions and loops: <https://kotlinlang.org/docs/control-flow.html>
- Kotlin — Null safety: <https://kotlinlang.org/docs/null-safety.html>
- Kotlin — Classes: <https://kotlinlang.org/docs/classes.html>
- Kotlin — Data classes: <https://kotlinlang.org/docs/data-classes.html>
- Kotlin — Object declarations and expressions: <https://kotlinlang.org/docs/object-declarations.html>
- Kotlin — Functions: <https://kotlinlang.org/docs/functions.html>
- Kotlin — Higher-order functions and lambdas: <https://kotlinlang.org/docs/lambdas.html>
- Kotlin — Collections overview: <https://kotlinlang.org/docs/collections-overview.html>
- Kotlin — Extensions: <https://kotlinlang.org/docs/extensions.html>
- Kotlin — Scope functions: <https://kotlinlang.org/docs/scope-functions.html>
- Kotlin — Type-safe builders: <https://kotlinlang.org/docs/type-safe-builders.html>
- Kotlin — Delegated properties: <https://kotlinlang.org/docs/delegated-properties.html>
- Kotlin — Returns and jumps: <https://kotlinlang.org/docs/returns.html>
- Kotlin — Idioms: <https://kotlinlang.org/docs/idioms.html>
- Kotlin — Coding conventions: <https://kotlinlang.org/docs/coding-conventions.html>
- Kotlin — Coroutines basics: <https://kotlinlang.org/docs/coroutines-basics.html>

Ktor e Compose
- Ktor — Routing: <https://ktor.io/docs/server-routing.html>
- Koin — Ktor: <https://insert-koin.io/docs/reference/koin-ktor/ktor>
- Android — Kotlin for Jetpack Compose: <https://developer.android.com/develop/ui/compose/kotlin>
- Android — State and Jetpack Compose: <https://developer.android.com/develop/ui/compose/state>

Ferramenta
- Kotlin Playground: <https://play.kotlinlang.org/>
- Kotlin — Run code snippets: <https://kotlinlang.org/docs/run-code-snippets.html>

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
