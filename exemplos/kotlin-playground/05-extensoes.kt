// Demonstração 5 — funções de extensão
// Acrescentam uma função a um tipo que já existe, sem herança e sem alterar a classe.

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// Em Java, isto seria um método estático numa classe utilitária:
//     TextoUtil.comoTitulo(texto)
fun comoTituloEstatico(texto: String): String = texto.trim().replaceFirstChar { it.uppercase() }

// Em Kotlin, o tipo antes do ponto é o "receptor". Dentro da função, `this` é o receptor.
fun String.comoTitulo(): String = this.trim().replaceFirstChar { it.uppercase() }

// As duas regras da tela do exemplo de Tarefas são extensões de List<Tarefa>.
// O `this` pode ser omitido: `map` aqui é `this.map`.
fun List<Tarefa>.alternando(id: Int): List<Tarefa> =
    map { if (it.id == id) it.copy(feita = !it.feita) else it }

fun List<Tarefa>.comNova(id: Int, titulo: String): List<Tarefa> = this + Tarefa(id, titulo.comoTitulo())

// Também existe propriedade de extensão (sempre calculada; não guarda valor).
val List<Tarefa>.pendentes: List<Tarefa>
    get() = filter { !it.feita }

// O receptor pode ser anulável: a função trata o null por dentro.
fun String?.ouPadrao(padrao: String): String = if (this.isNullOrBlank()) padrao else this

fun main() {
    println(comoTituloEstatico("  estudar kotlin "))
    println("  estudar kotlin ".comoTitulo())          // lê-se da esquerda para a direita

    val tarefas = listOf(Tarefa(1, "Estudar Kotlin"), Tarefa(2, "Entender lambdas"))

    // Encadeando: cada chamada devolve uma lista nova.
    val depois = tarefas
        .comNova(3, "  gravar o vídeo")
        .alternando(1)
    println(depois)
    println(depois.pendentes.map { it.titulo })

    val apelido: String? = null
    println(apelido.ouPadrao("sem apelido"))

    // Uma extensão só usa a parte pública do tipo: não enxerga membros privados.
    // E não altera a classe: é uma função comum, escolhida em tempo de compilação
    // pelo tipo declarado da variável.

    // Experimente:
    // 1. Escreva fun Tarefa.resumo(): String, que devolve "[x] Título" ou "[ ] Título",
    //    e imprima depois.map { it.resumo() }.
    // 2. Escreva fun Int.ehPar(): Boolean e chame 4.ehPar().
    // 3. Crie a propriedade de extensão val List<Tarefa>.feitas.
}
