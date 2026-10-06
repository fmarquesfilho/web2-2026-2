// Demonstração 6 — lambda com receptor e funções de escopo
// Junta as duas ideias anteriores: uma lambda que, por dentro, tem um `this`.
// É o que permite escrever blocos como apply { }, routing { } e Column { }.

class Conexao {
    var url: String = ""
    var usuario: String = ""
    var limite: Int = 10
    override fun toString() = "Conexao(url=$url, usuario=$usuario, limite=$limite)"
}

// Lambda comum: o objeto chega como parâmetro, e é preciso citá-lo (it.url, it.usuario).
fun configurarComParametro(bloco: (Conexao) -> Unit): Conexao {
    val conexao = Conexao()
    bloco(conexao)
    return conexao
}

// Lambda com receptor: o tipo antes do ponto vira o `this` do bloco.
// Dentro dele, `url = ...` quer dizer `this.url = ...`.
fun configurar(bloco: Conexao.() -> Unit): Conexao {
    val conexao = Conexao()
    conexao.bloco()
    return conexao
}

// A biblioteca padrão usa a mesma técnica. Esta é a assinatura de buildString:
//     fun buildString(acao: StringBuilder.() -> Unit): String
fun listaEmTexto(itens: List<String>): String = buildString {
    appendLine("Tarefas:")                  // this.appendLine(...): o this é um StringBuilder
    for (item in itens) appendLine("- $item")
}

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

fun main() {
    println(configurarComParametro { it.url = "jdbc:postgresql://localhost/tarefas"; it.usuario = "tarefas" })

    println(configurar {
        url = "jdbc:postgresql://localhost/tarefas"
        usuario = "tarefas"
        limite = 5
    })

    print(listaEmTexto(listOf("Estudar Kotlin", "Entender lambdas")))

    // As funções de escopo da biblioteca padrão são variações dessa ideia.

    // apply: configura o objeto e devolve o próprio objeto. (this)
    val conexao = Conexao().apply {
        url = "jdbc:postgresql://localhost/tarefas"
        limite = 5
    }
    println(conexao)

    // let: usa o objeto como `it` e devolve o resultado do bloco.
    // Com ?., o bloco só roda quando o valor não é null.
    val selecionada: Int? = 2
    selecionada?.let { println("tarefa selecionada: $it") }

    val nenhuma: Int? = null
    nenhuma?.let { println("esta linha não aparece") }

    // also: faz algo a mais com o objeto (um registro, por exemplo) e devolve o próprio objeto.
    val tarefa = Tarefa(1, "Estudar Kotlin").also { println("criada: $it") }

    // with: várias chamadas sobre o mesmo objeto, sem repetir o nome.
    with(tarefa) { println("$id - $titulo - $feita") }

    // run: como with, escrito depois do ponto; devolve o resultado do bloco.
    val tamanho = tarefa.run { titulo.length }
    println(tamanho)

    // Experimente:
    // 1. Em configurar { }, escreva this.url = "..." em vez de url = "...". Muda algo?
    // 2. Troque Conexao.() -> Unit por (Conexao) -> Unit em configurar e veja o que deixa de compilar.
    // 3. Reescreva a criação de `conexao` sem apply. Quantas vezes o nome da variável aparece?
}
