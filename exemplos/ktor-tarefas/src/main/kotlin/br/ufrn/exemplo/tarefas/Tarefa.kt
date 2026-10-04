package br.ufrn.exemplo.tarefas

import kotlinx.serialization.Serializable

// O domínio: uma tarefa simples. @Serializable deixa o JSON sair de graça.
@Serializable
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// O que chega no corpo do POST — sem id, que é o servidor quem atribui.
@Serializable
data class NovaTarefa(val titulo: String)

// A porta: o que a aplicação precisa, sem dizer de onde os dados vêm.
interface RepositorioDeTarefas {
    fun listar(): List<Tarefa>
    fun adicionar(nova: NovaTarefa): Tarefa
}

// Implementação em memória. No Passo 7 entra outra, com PostgreSQL.
class RepositorioEmMemoria : RepositorioDeTarefas {
    private val tarefas = mutableListOf(Tarefa(1, "Estudar Ktor"), Tarefa(2, "Estudar Quarkus"))
    private var proximoId = 3

    override fun listar(): List<Tarefa> = tarefas.toList()

    override fun adicionar(nova: NovaTarefa): Tarefa =
        Tarefa(proximoId++, nova.titulo).also { tarefas.add(it) }
}
