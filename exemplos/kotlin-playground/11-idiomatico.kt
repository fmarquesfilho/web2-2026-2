// Demonstração 11 — o mesmo programa, primeiro "Java escrito em Kotlin", depois Kotlin idiomático
// As duas versões compilam e dão o mesmo resultado. A segunda usa o que a linguagem oferece.

// ---------- Versão 1: traduzida do Java, linha a linha ----------

class TarefaV1 {
    private var id: Int
    private var titulo: String
    private var feita: Boolean

    constructor(id: Int, titulo: String, feita: Boolean) {
        this.id = id
        this.titulo = titulo
        this.feita = feita
    }

    fun getId(): Int { return id }
    fun getTitulo(): String { return titulo }
    fun isFeita(): Boolean { return feita }
}

class TarefaUtil {
    companion object {
        fun titulosPendentes(tarefas: List<TarefaV1>?): List<String> {
            val resultado = ArrayList<String>()
            if (tarefas != null) {
                for (i in 0 until tarefas.size) {
                    val tarefa = tarefas.get(i)
                    if (tarefa.isFeita() == false) {
                        resultado.add(tarefa.getTitulo().uppercase())
                    }
                }
            }
            return resultado
        }

        fun descrever(tarefa: TarefaV1?): String {
            var texto: String
            if (tarefa == null) {
                texto = "nenhuma tarefa"
            } else {
                if (tarefa.isFeita()) {
                    texto = tarefa.getTitulo() + " (feita)"
                } else {
                    texto = tarefa.getTitulo() + " (pendente)"
                }
            }
            return texto
        }
    }
}

// ---------- Versão 2: Kotlin idiomático ----------

// data class com propriedades no construtor; valor padrão em vez de sobrecarga.
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// Extensão em vez de classe utilitária; filter e map em vez de laço com índice;
// a lista não é anulável: quem não tem tarefas passa uma lista vazia.
fun List<Tarefa>.titulosPendentes(): List<String> = filter { !it.feita }.map { it.titulo.uppercase() }

// Função de uma expressão; when e if como expressões; template de string.
fun descrever(tarefa: Tarefa?): String = when {
    tarefa == null -> "nenhuma tarefa"
    else -> "${tarefa.titulo} (${if (tarefa.feita) "feita" else "pendente"})"
}

fun main() {
    val v1 = listOf(TarefaV1(1, "Estudar Kotlin", true), TarefaV1(2, "Entender lambdas", false))
    println(TarefaUtil.titulosPendentes(v1))
    println(TarefaUtil.descrever(v1.get(0)))
    println(TarefaUtil.descrever(null))

    val v2 = listOf(Tarefa(1, "Estudar Kotlin", feita = true), Tarefa(2, "Entender lambdas"))
    println(v2.titulosPendentes())
    println(descrever(v2[0]))
    println(descrever(null))

    // Experimente:
    // 1. Conte as linhas de cada versão.
    // 2. Imprima v1.get(0) e v2[0]. Qual das duas mostra os dados? Por quê?
    // 3. Reescreva descrever() usando tarefa?.let { ... } ?: "nenhuma tarefa".
}
