# Demonstrações de Kotlin

Onze arquivos curtos, cada um com um `main`, que acompanham a leitura "Kotlin para quem vem
do Java" e os slides da mesma aula. Não dependem do projeto: rodam no
[Kotlin Playground](https://play.kotlinlang.org/), no navegador.

Como rodar: abra o Playground, apague o código que estiver lá, cole o conteúdo do arquivo e
clique em **Run**. A leitura e os slides trazem, para cada demonstração, um link que já abre
o Playground com o código carregado.

Cada arquivo termina com três experimentos, escritos como comentários no fim do `main`.

| Arquivo | O que mostra |
|---|---|
| `01-expressoes-e-nulidade.kt` | `if` e `when` como expressão; `?.`, `?:`, `!!` |
| `02-classes.kt` | `data class`, `copy`, argumentos nomeados, `object`, `companion object` |
| `03-lambdas.kt` | tipos de função, `it`, lambda no fim da chamada |
| `04-colecoes.kt` | `filter`, `map`, `find`; listas somente leitura |
| `05-extensoes.kt` | funções e propriedades de extensão |
| `06-lambda-com-receptor.kt` | `Tipo.() -> Unit`; `apply`, `let`, `also`, `run`, `with` |
| `07-mini-dsl-rotas.kt` | um Ktor em miniatura: `routing { get("/tarefas") { ... } }` |
| `08-mini-dsl-telas.kt` | um Compose em miniatura: `Coluna { Texto("...") }` e eventos por lambda |
| `09-delegacao.kt` | `by`, `by lazy` e um delegado que observa mudanças |
| `10-suspend.kt` | `suspend`, `async` e dez mil corrotinas |
| `11-idiomatico.kt` | o mesmo programa em estilo Java e em estilo Kotlin |

Conferidas com Kotlin 2.4.10. A demonstração 10 usa a biblioteca `kotlinx.coroutines`, que o
Playground já oferece.
