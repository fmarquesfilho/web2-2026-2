package br.ufrn.exemplo.tarefas.arquitetura.violacao.dominio

import io.ktor.http.HttpStatusCode

// Fixture do ArquiteturaTest: um "domínio" que importa Ktor. Não imite.
class Contaminado {
    fun status(): HttpStatusCode = HttpStatusCode.OK
}
