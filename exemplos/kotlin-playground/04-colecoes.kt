// Demonstração 4 — coleções
// As operações que em Java pedem stream() e collect() são funções diretas da lista.

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

fun main() {
    // List é somente leitura: não tem add nem remove.
    val tarefas: List<Tarefa> = listOf(
        Tarefa(1, "Estudar Kotlin", feita = true),
        Tarefa(2, "Entender lambdas"),
        Tarefa(3, "Escrever os testes"),
    )

    println(tarefas.filter { !it.feita })                  // as que passam na condição
    println(tarefas.map { it.titulo })                     // transforma cada elemento
    println(tarefas.filter { !it.feita }.map { it.titulo.uppercase() })
    println(tarefas.find { it.id == 2 })                   // o primeiro, ou null
    println(tarefas.find { it.id == 9 })
    println(tarefas.any { it.feita })                      // existe algum?
    println(tarefas.count { it.feita })
    println(tarefas.sortedBy { it.titulo }.map { it.titulo })
    println(tarefas.groupBy { it.feita })                  // Map<Boolean, List<Tarefa>>

    // "Alterar" uma lista imutável é criar outra. É assim que o exemplo de Tarefas marca
    // uma tarefa como feita: map devolve uma lista nova, e copy, uma tarefa nova.
    val depois = tarefas.map { if (it.id == 2) it.copy(feita = !it.feita) else it }
    println(depois[1])
    println(tarefas[1])                                    // a original continua igual

    // `+` também cria outra lista.
    val comNova = tarefas + Tarefa(4, "Gravar o vídeo")
    println(comNova.size)
    println(tarefas.size)

    // Quando é preciso alterar no lugar, o tipo diz: MutableList.
    val rascunho = mutableListOf("a", "b")
    rascunho.add("c")
    println(rascunho)

    // Experimente:
    // 1. Escreva tarefas.add(Tarefa(5, "x")). O que o compilador diz?
    // 2. Liste os títulos das tarefas pendentes em ordem alfabética, numa linha só.
    // 3. Use tarefas.partition { it.feita } e desestruture: val (feitas, pendentes) = ...
}
