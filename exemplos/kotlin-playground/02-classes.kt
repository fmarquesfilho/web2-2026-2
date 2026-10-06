// Demonstração 2 — classes, data classes e object
// Uma linha de Kotlin declara o que em Java pede construtor, getters, equals, hashCode e toString.

// Construtor primário: os parâmetros com `val` já são propriedades.
// `feita` tem valor padrão: quem cria a tarefa pode omitir.
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// Classe comum: sem `data`, equals compara referências e toString mostra o endereço.
class TarefaComum(val id: Int, val titulo: String)

// `object`: uma única instância, criada pela linguagem (o singleton do Java, sem código).
object Contador {
    private var ultimo = 0
    fun proximo(): Int = ++ultimo
}

// Kotlin não tem `static`. O que pertence à classe, e não à instância, vai no companion object.
data class ConfigBanco(val url: String, val usuario: String) {
    companion object {
        fun local() = ConfigBanco(url = "jdbc:postgresql://localhost:5432/tarefas", usuario = "tarefas")
    }
}

fun main() {
    // Não existe `new`. Argumentos nomeados deixam a chamada legível e permitem trocar a ordem.
    val a = Tarefa(1, "Estudar Kotlin")
    val b = Tarefa(titulo = "Estudar Kotlin", id = 1)

    println(a)                       // toString gerado
    println(a == b)                  // equals gerado: compara os valores (== chama equals)
    println(a === b)                 // === compara referências, como o == do Java

    // copy: uma cópia com parte dos valores trocada. O original não muda.
    val feita = a.copy(feita = true)
    println(feita)
    println(a)

    // Desestruturação: uma variável para cada propriedade, na ordem do construtor.
    val (id, titulo) = feita
    println("$id: $titulo")

    println(TarefaComum(1, "x") == TarefaComum(1, "x"))   // false: não é data class

    println(Contador.proximo())
    println(Contador.proximo())
    println(ConfigBanco.local())

    // Experimente:
    // 1. Tente a.feita = true. O que o compilador diz? (val não aceita reatribuição)
    // 2. Tire a palavra `data` de Tarefa e rode de novo: quais linhas mudam?
    // 3. Crie uma tarefa passando só `titulo`. Por que não compila?
}
