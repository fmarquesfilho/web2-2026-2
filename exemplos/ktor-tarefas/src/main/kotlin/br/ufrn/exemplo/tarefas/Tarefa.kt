package br.ufrn.exemplo.tarefas

import kotlinx.serialization.Serializable

// O domínio: uma tarefa simples. @Serializable deixa o JSON sair de graça.
@Serializable
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// O que chega no corpo do POST — sem id, que é o servidor quem atribui.
@Serializable
data class NovaTarefa(val titulo: String)

// A porta. `suspend`: a implementação com banco não pode travar a thread do servidor.
interface RepositorioDeTarefas {
    suspend fun listar(): List<Tarefa>
    suspend fun buscar(id: Int): Tarefa?
    suspend fun adicionar(nova: NovaTarefa): Tarefa
}

// Implementação em memória: continua útil nos testes de rota, sem banco (RotasTest).
class RepositorioEmMemoria : RepositorioDeTarefas {
    private val tarefas = mutableListOf(Tarefa(1, "Estudar Ktor"), Tarefa(2, "Estudar Quarkus"))
    private var proximoId = 3

    override suspend fun listar(): List<Tarefa> = tarefas.toList()
    override suspend fun buscar(id: Int): Tarefa? = tarefas.find { it.id == id }
    override suspend fun adicionar(nova: NovaTarefa): Tarefa =
        Tarefa(proximoId++, nova.titulo).also { tarefas.add(it) }
}
