package br.ufrn.exemplo.tarefas

import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.ExperimentalKtorApi
import org.koin.ktor.ext.inject

// As rotas são CÓDIGO, num DSL — não anotações. O controller só traduz HTTP em chamadas
// ao repositório. `.describe { }` alimenta o OpenAPI (API experimental do Ktor 3.5).
@OptIn(ExperimentalKtorApi::class)
fun Application.rotas() {
    val repositorio by inject<RepositorioDeTarefas>()

    routing {
        route("/tarefas") {
            get { call.respond(repositorio.listar()) }.describe {
                summary = "Lista as tarefas"
                responses { HttpStatusCode.OK { schema = jsonSchema<List<Tarefa>>() } }
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val tarefa = repositorio.buscar(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(tarefa)
            }.describe {
                summary = "Busca uma tarefa pelo id"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<Tarefa>() }
                    HttpStatusCode.NotFound { description = "Tarefa inexistente" }
                }
            }

            post {
                val nova = call.receive<NovaTarefa>()
                call.respond(HttpStatusCode.Created, repositorio.adicionar(nova))
            }.describe {
                summary = "Cria uma tarefa"
                requestBody { schema = jsonSchema<NovaTarefa>() }
                responses { HttpStatusCode.Created { schema = jsonSchema<Tarefa>() } }
            }
        }

        swaggerUI(path = "docs") {
            info = OpenApiInfo(title = "Tarefas", version = "1.0")
        }
    }
}
