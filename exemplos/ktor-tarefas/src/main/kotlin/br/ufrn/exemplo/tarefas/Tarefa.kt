package br.ufrn.exemplo.tarefas

import kotlinx.serialization.Serializable

// O domínio: uma tarefa simples. @Serializable deixa o JSON sair de graça.
@Serializable
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// O que chega no corpo do POST — sem id, que é o servidor quem atribui.
@Serializable
data class NovaTarefa(val titulo: String)

// A "porta": o que as rotas precisam, sem dizer quem fornece (regra de dependência).
interface RepositorioDeTarefas {
    fun listar(): List<Tarefa>
    fun adicionar(nova: NovaTarefa): Tarefa
}

// Implementação em memória. Na Sprint 1 (21/09) ela vira Postgres — e as rotas
// não mudam, porque dependem da interface, não desta classe.
class RepositorioEmMemoria : RepositorioDeTarefas {
    private val tarefas = mutableListOf(
        Tarefa(1, "Estudar Ktor"),
        Tarefa(2, "Estudar Quarkus"),
    )
    private var proximoId = 3

    override fun listar(): List<Tarefa> = tarefas

    override fun adicionar(nova: NovaTarefa): Tarefa {
        val tarefa = Tarefa(proximoId++, nova.titulo)
        tarefas.add(tarefa)
        return tarefa
    }
}
