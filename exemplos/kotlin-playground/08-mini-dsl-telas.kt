// Demonstração 8 — um Compose em miniatura (Dispositivos Móveis)
// Monta uma árvore de componentes com a mesma forma de escrita do Telas.kt do exemplo.
// O Compose de verdade funciona de outro jeito por dentro (um plugin do compilador
// acompanha o estado e redesenha só o que mudou), mas a sintaxe sai das mesmas peças:
// lambda no fim da chamada, lambda com receptor, argumentos nomeados e valores padrão.

data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// Um nó da árvore: um nome, os filhos e, se for clicável, a ação do clique.
class No(val nome: String, val aoClicar: (() -> Unit)? = null) {
    val filhos = mutableListOf<No>()

    fun desenhar(nivel: Int = 0) {
        println("  ".repeat(nivel) + nome)
        filhos.forEach { it.desenhar(nivel + 1) }
    }

    // Todos os nós clicáveis da árvore, na ordem em que aparecem.
    fun botoes(): List<No> =
        (if (aoClicar != null) listOf(this) else emptyList()) + filhos.flatMap { it.botoes() }
}

// O `this` dos blocos de conteúdo: sabe em que nó os filhos devem entrar.
class Escopo(val no: No)

fun Escopo.Texto(texto: String) {
    no.filhos += No("Texto(\"$texto\")")
}

// O último parâmetro é o conteúdo: Coluna { ... }
fun Escopo.Coluna(conteudo: Escopo.() -> Unit) {
    val coluna = No("Coluna")
    no.filhos += coluna
    Escopo(coluna).conteudo()                 // executa o bloco com o novo escopo como this
}

// Um parâmetro com nome (onClick) e o conteúdo no fim: Botao(onClick = { ... }) { Texto("...") }
fun Escopo.Botao(onClick: () -> Unit, conteudo: Escopo.() -> Unit) {
    val botao = No("Botao", aoClicar = onClick)
    no.filhos += botao
    Escopo(botao).conteudo()
}

fun tela(conteudo: Escopo.() -> Unit): No = No("Tela").also { Escopo(it).conteudo() }

// Daqui para baixo, o código tem a forma do Telas.kt do exemplo: a tela não guarda estado,
// recebe os dados e devolve eventos (onAlternar).
fun Escopo.TelaLista(tarefas: List<Tarefa>, onAlternar: (Int) -> Unit) {
    Coluna {
        Texto("Minhas tarefas")
        for (tarefa in tarefas) {
            CartaoTarefa(tarefa, onAlternar = { onAlternar(tarefa.id) })
        }
    }
}

fun Escopo.CartaoTarefa(tarefa: Tarefa, onAlternar: () -> Unit) {
    Botao(onClick = onAlternar) {
        Texto(if (tarefa.feita) "[x] ${tarefa.titulo}" else "[ ] ${tarefa.titulo}")
    }
}

fun main() {
    // O estado fica fora da tela, como no App.kt do exemplo.
    var tarefas = listOf(Tarefa(1, "Estudar Compose"), Tarefa(2, "Entender estado elevado"))
    val alternar = { id: Int ->
        tarefas = tarefas.map { if (it.id == id) it.copy(feita = !it.feita) else it }
    }

    var arvore = tela { TelaLista(tarefas, onAlternar = alternar) }
    arvore.desenhar()

    // Simula o toque na segunda tarefa: o botão chama a lambda que recebeu.
    arvore.botoes()[1].aoClicar?.invoke()

    // O estado mudou. Montar a tela de novo é o que o Compose chama de recomposição.
    arvore = tela { TelaLista(tarefas, onAlternar = alternar) }
    arvore.desenhar()

    // Experimente:
    // 1. Acrescente uma terceira tarefa ao estado e rode de novo.
    // 2. Reescreva CartaoTarefa sem açúcar sintático:
    //        this.Botao(onAlternar, { this.Texto("...") })
    // 3. Crie fun Escopo.Linha(conteudo: Escopo.() -> Unit), copiando a ideia de Coluna,
    //    e use-a dentro do cartão.
}
