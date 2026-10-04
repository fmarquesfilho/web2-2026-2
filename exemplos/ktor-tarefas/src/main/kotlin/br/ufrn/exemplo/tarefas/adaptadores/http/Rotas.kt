package br.ufrn.exemplo.tarefas.adaptadores.http

import br.ufrn.exemplo.tarefas.dominio.EntradaInvalida
import br.ufrn.exemplo.tarefas.dominio.NovaTarefa
import br.ufrn.exemplo.tarefas.dominio.RepositorioDeTarefas
import br.ufrn.exemplo.tarefas.dominio.Tarefa
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.header
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
// Erros não viram resposta aqui: as rotas lançam, e o StatusPages (Erros.kt) responde.
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
                    ?: throw BadRequestException("O id deve ser um número inteiro")
                val tarefa = repositorio.buscar(id)
                    ?: throw NotFoundException("A tarefa $id não existe")
                call.respond(tarefa)
            }.describe {
                summary = "Busca uma tarefa pelo id"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<Tarefa>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problema>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problema>() }
                }
            }

            post {
                val nova = call.receive<NovaTarefa>()
                val violacoes = nova.violacoes()
                if (violacoes.isNotEmpty()) throw EntradaInvalida(violacoes)
                val criada = repositorio.adicionar(nova)
                call.response.header(HttpHeaders.Location, "/tarefas/${criada.id}")
                call.respond(HttpStatusCode.Created, criada)
            }.describe {
                summary = "Cria uma tarefa"
                requestBody { schema = jsonSchema<NovaTarefa>() }
                responses {
                    HttpStatusCode.Created { schema = jsonSchema<Tarefa>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problema>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problema>() }
                }
            }
        }

        swaggerUI(path = "docs") {
            info = OpenApiInfo(title = "Tarefas", version = "1.0")
        }
    }
}
