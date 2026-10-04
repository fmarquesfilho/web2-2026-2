package br.ufrn.exemplo.tarefas.dominio

import kotlinx.serialization.Serializable

// O domínio: uma tarefa simples. @Serializable deixa o JSON sair de graça.
@Serializable
data class Tarefa(val id: Int, val titulo: String, val feita: Boolean = false)

// O que chega no corpo do POST — sem id, que é o servidor quem atribui.
@Serializable
data class NovaTarefa(val titulo: String) {
    // A regra do domínio, em Kotlin puro: devolve TUDO o que está errado, não só o primeiro.
    fun violacoes(): List<String> = when {
        titulo.isBlank() -> listOf("titulo: não pode ficar em branco")
        titulo.length > 200 -> listOf("titulo: no máximo 200 caracteres")
        else -> emptyList()
    }
}

// Erro do domínio, com nome. Quem decide o status HTTP é o adaptador (Erros.kt).
class EntradaInvalida(val violacoes: List<String>) : RuntimeException(violacoes.joinToString("; "))

// A porta. `suspend`: a implementação com banco não pode travar a thread do servidor.
interface RepositorioDeTarefas {
    suspend fun listar(): List<Tarefa>
    suspend fun buscar(id: Int): Tarefa?
    suspend fun adicionar(nova: NovaTarefa): Tarefa
}
