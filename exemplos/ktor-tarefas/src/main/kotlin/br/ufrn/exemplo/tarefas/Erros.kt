package br.ufrn.exemplo.tarefas

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Erro no formato da RFC 9457: `application/problem+json`. `type` identifica a CLASSE do
// erro; `detail`, esta ocorrência. `violacoes` é um membro de extensão (a RFC permite).
@Serializable
data class Problema(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String? = null,
    val violacoes: List<String>? = null,
)

private val json = Json { explicitNulls = false }

suspend fun ApplicationCall.responderProblema(
    status: HttpStatusCode,
    tipo: String,
    titulo: String,
    detalhe: String? = null,
    violacoes: List<String>? = null,
) {
    val problema = Problema("/problemas/$tipo", titulo, status.value, detalhe, violacoes)
    respondText(json.encodeToString(problema), ContentType.Application.ProblemJson, status)
}

// Exceção vira resposta, num lugar só. As rotas só lançam; aqui cada erro ganha um status.
fun Application.tratarErros() {
    install(StatusPages) {
        // A FORMA está errada: JSON malformado, campo faltando, id que não é número.
        exception<BadRequestException> { call, causa ->
            call.responderProblema(
                HttpStatusCode.BadRequest, "requisicao-malformada", "Requisição malformada",
                causa.cause?.message ?: causa.message,
            )
        }
        exception<NotFoundException> { call, causa ->
            call.responderProblema(HttpStatusCode.NotFound, "nao-encontrado", "Recurso inexistente", causa.message)
        }
        // A forma está certa, mas a REGRA do domínio não: 422.
        exception<EntradaInvalida> { call, causa ->
            call.responderProblema(
                HttpStatusCode.UnprocessableEntity, "entrada-invalida", "Entrada inválida",
                "A entrada viola ${causa.violacoes.size} regra(s).", causa.violacoes,
            )
        }
        // O resto é erro nosso: 500, sem vazar detalhe interno para o cliente.
        exception<Throwable> { call, causa ->
            call.application.environment.log.error("erro não tratado", causa)
            call.responderProblema(HttpStatusCode.InternalServerError, "interno", "Erro interno")
        }
    }
}
