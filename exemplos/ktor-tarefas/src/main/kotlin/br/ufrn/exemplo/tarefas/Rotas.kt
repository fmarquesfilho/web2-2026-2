package br.ufrn.exemplo.tarefas

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

// As rotas são CÓDIGO, num DSL — não anotações. A árvore inteira se lê de cima
// para baixo. O controller só traduz HTTP em chamadas ao repositório.
fun Application.rotas() {
    val repositorio by inject<RepositorioDeTarefas>() // Koin resolve a implementação

    routing {
        get("/") { call.respond(mapOf("status" to "no ar")) }

        route("/tarefas") {
            // GET /tarefas → lista tudo
            get { call.respond(repositorio.listar()) }

            // POST /tarefas → cria e devolve 201 com a tarefa criada
            post {
                val nova = call.receive<NovaTarefa>()
                call.respond(HttpStatusCode.Created, repositorio.adicionar(nova))
            }
        }
    }
}
