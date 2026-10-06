// Demonstração 1 — expressões e nulidade
// Em Kotlin, `if` e `when` devolvem valor, e o tipo diz se uma referência pode ser nula.

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

val tarefas = listOf(Tarefa(1, "Estudar Kotlin"), Tarefa(2, "Entender lambdas", feita = true))

// `Tarefa?`: pode devolver uma tarefa ou null. Sem o `?`, null não compila.
fun buscar(id: Int): Tarefa? = tarefas.find { it.id == id }

// `if` é expressão: substitui o operador ternário do Java (cond ? a : b).
fun situacao(tarefa: Tarefa): String = if (tarefa.feita) "feita" else "pendente"

// `when` é expressão: o valor do ramo escolhido é o valor da função.
fun violacoes(titulo: String): List<String> = when {
    titulo.isBlank() -> listOf("titulo: não pode ficar em branco")
    titulo.length > 200 -> listOf("titulo: no máximo 200 caracteres")
    else -> emptyList()
}

// O mesmo raciocínio da rota GET /tarefas/{id} do exemplo de Tarefas.
fun responder(parametro: String?): String {
    // ?.  só chama toIntOrNull() se `parametro` não for null
    // ?:  (operador Elvis) dá o valor da direita quando a esquerda é null
    val id = parametro?.toIntOrNull() ?: return "400: o id deve ser um número inteiro"
    val tarefa = buscar(id) ?: return "404: a tarefa $id não existe"
    // Daqui para baixo o compilador sabe que `tarefa` não é null (smart cast).
    return "200: ${tarefa.titulo} (${situacao(tarefa)})"
}

fun main() {
    println(responder("1"))
    println(responder("2"))
    println(responder("abc"))
    println(responder("9"))
    println(responder(null))
    println(violacoes("   "))
    println(violacoes("Estudar"))

    // Experimente:
    // 1. Troque `Tarefa?` por `Tarefa` em buscar() e leia o erro de compilação.
    // 2. Apague um `?:` de responder() e veja onde o compilador reclama.
    // 3. Escreva buscar(9)!!.titulo e rode: `!!` troca o erro de compilação por um
    //    NullPointerException em execução.
}
