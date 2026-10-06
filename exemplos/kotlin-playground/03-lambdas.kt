// Demonstração 3 — lambdas e tipos de função
// Uma lambda é um bloco de código guardado num valor. O tipo dela se escreve (Entrada) -> Saída.

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// Função de ordem superior: recebe outra função como parâmetro.
// Em Java, `condicao` seria um Predicate<Tarefa>.
fun contar(tarefas: List<Tarefa>, condicao: (Tarefa) -> Boolean): Int {
    var total = 0
    for (tarefa in tarefas) if (condicao(tarefa)) total++
    return total
}

// O último parâmetro é uma função: quem chama pode escrever a lambda depois dos parênteses.
fun repetir(vezes: Int, acao: (Int) -> Unit) {
    for (i in 1..vezes) acao(i)
}

// Uma função comum também pode ser passada como valor, com `::`.
fun estaFeita(tarefa: Tarefa): Boolean = tarefa.feita

fun main() {
    val tarefas = listOf(Tarefa(1, "Estudar Kotlin", feita = true), Tarefa(2, "Entender lambdas"))

    // A mesma lambda, da forma mais longa à mais curta:
    val forma1: (Tarefa) -> Boolean = { tarefa: Tarefa -> tarefa.feita }
    val forma2: (Tarefa) -> Boolean = { tarefa -> tarefa.feita }    // o tipo do parâmetro é inferido
    val forma3: (Tarefa) -> Boolean = { it.feita }                  // um só parâmetro: chama-se `it`

    println(contar(tarefas, forma1))
    println(contar(tarefas, forma2))
    println(contar(tarefas, forma3))

    // A mesma chamada, da forma mais longa à mais curta:
    println(contar(tarefas, { tarefa -> tarefa.feita }))   // lambda dentro dos parênteses
    println(contar(tarefas) { tarefa -> tarefa.feita })    // lambda no fim, fora dos parênteses
    println(contar(tarefas) { it.feita })                  // com `it`
    println(contar(tarefas, ::estaFeita))                  // referência a uma função

    // Quando a lambda é o único argumento, os parênteses somem.
    repetir(3) { numero -> println("volta $numero") }

    // O valor da lambda é a última expressão dela. Não se escreve `return`.
    val dobro: (Int) -> Int = { numero ->
        val resultado = numero * 2
        resultado
    }
    println(dobro(21))

    // Uma lambda enxerga (e pode alterar) as variáveis de onde foi criada.
    var cliques = 0
    val aoClicar: () -> Unit = { cliques++ }
    aoClicar()
    aoClicar()
    println("cliques: $cliques")

    // Experimente:
    // 1. Mude contar(tarefas) { it.feita } para contar as tarefas pendentes.
    // 2. Declare val aoAlternar: (Int) -> Unit = { id -> println("alternar $id") } e chame aoAlternar(2).
    //    É a forma do parâmetro onAlternar das telas do exemplo de Tarefas.
    // 3. Escreva `return resultado` dentro da lambda `dobro` e leia o erro.
}
