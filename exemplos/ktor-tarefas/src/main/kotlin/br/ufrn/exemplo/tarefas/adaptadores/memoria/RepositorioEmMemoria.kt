package br.ufrn.exemplo.tarefas.adaptadores.memoria

import br.ufrn.exemplo.tarefas.dominio.NovaTarefa
import br.ufrn.exemplo.tarefas.dominio.RepositorioDeTarefas
import br.ufrn.exemplo.tarefas.dominio.Tarefa

// Implementação em memória: continua útil nos testes de rota, sem banco (RotasTest).
class RepositorioEmMemoria : RepositorioDeTarefas {
    private val tarefas = mutableListOf(Tarefa(1, "Estudar Ktor"), Tarefa(2, "Estudar Quarkus"))
    private var proximoId = 3

    override suspend fun listar(): List<Tarefa> = tarefas.toList()
    override suspend fun buscar(id: Int): Tarefa? = tarefas.find { it.id == id }
    override suspend fun adicionar(nova: NovaTarefa): Tarefa =
        Tarefa(proximoId++, nova.titulo).also { tarefas.add(it) }
}
