package br.ufrn.exemplo.tarefas

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") { modulo() }.start(wait = true)
}

fun Application.modulo() {
    install(ContentNegotiation) { json() }

    val repositorio: RepositorioDeTarefas = RepositorioEmMemoria()

    routing {
        get("/tarefas") { call.respond(repositorio.listar()) }

        post("/tarefas") {
            val nova = call.receive<NovaTarefa>()
            call.respond(HttpStatusCode.Created, repositorio.adicionar(nova))
        }
    }
}
