// Demonstração 7 — um Ktor em miniatura (Web II)
// Cerca de 30 linhas bastam para escrever rotas no formato do exemplo de Tarefas.
// O Ktor de verdade faz muito mais, mas a sintaxe sai das mesmas três peças:
// função de extensão, lambda com receptor e lambda no fim da chamada.

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

val tarefas = listOf(Tarefa(1, "Estudar Kotlin"), Tarefa(2, "Entender lambdas"))

// Uma requisição em andamento: os parâmetros que chegaram e a resposta que vai sair.
class Chamada(val parametros: Map<String, String>) {
    var resposta: String = ""
    fun responder(texto: String) { resposta = texto }
}

// O `this` de cada tratador de rota. É por isso que, no Ktor, `call` existe dentro de get { }.
class Contexto(val call: Chamada)

class Rotas {
    private val tabela = mutableMapOf<String, Contexto.() -> Unit>()

    // O último parâmetro é uma lambda com receptor: get("/caminho") { ... }
    fun get(caminho: String, tratador: Contexto.() -> Unit) {
        tabela["GET $caminho"] = tratador
    }

    fun atender(caminho: String, parametros: Map<String, String> = emptyMap()): String {
        val tratador = tabela["GET $caminho"] ?: return "404: rota inexistente"
        val contexto = Contexto(Chamada(parametros))
        contexto.tratador()                       // executa o bloco com `contexto` como this
        return contexto.call.resposta
    }
}

class Aplicacao {
    val rotas = Rotas()
}

// Extensão de Aplicacao que recebe uma lambda com receptor Rotas.
fun Aplicacao.routing(bloco: Rotas.() -> Unit) {
    rotas.bloco()
}

// Daqui para baixo, o código tem a forma do Rotas.kt do exemplo.
fun Aplicacao.modulo() {
    routing {                                     // this: Rotas
        get("/tarefas") {                         // this: Contexto
            call.responder(tarefas.joinToString { it.titulo })
        }
        get("/tarefas/{id}") {
            val id = call.parametros["id"]?.toIntOrNull()
            val tarefa = tarefas.find { it.id == id }
            call.responder(tarefa?.titulo ?: "404: a tarefa não existe")
        }
    }
}

fun main() {
    val aplicacao = Aplicacao()
    aplicacao.modulo()

    println(aplicacao.rotas.atender("/tarefas"))
    println(aplicacao.rotas.atender("/tarefas/{id}", mapOf("id" to "2")))
    println(aplicacao.rotas.atender("/tarefas/{id}", mapOf("id" to "9")))
    println(aplicacao.rotas.atender("/usuarios"))

    // Experimente:
    // 1. Acrescente a rota get("/saude") { call.responder("no ar") } e chame-a no main.
    // 2. Reescreva a primeira rota sem açúcar sintático:
    //        this.get("/tarefas", { this.call.responder("...") })
    //    É exatamente o mesmo programa.
    // 3. Acrescente fun post(...) à classe Rotas, copiando a ideia de get.
}
