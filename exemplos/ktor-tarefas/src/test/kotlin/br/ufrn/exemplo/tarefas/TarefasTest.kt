package br.ufrn.exemplo.tarefas

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.testcontainers.postgresql.PostgreSQLContainer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TarefasTest {

    companion object {
        // Um PostgreSQL de verdade, num container descartável, para a classe inteira.
        private val postgres = PostgreSQLContainer("postgres:17-alpine").apply { start() }
    }

    private fun ApplicationTestBuilder.appComBanco() = application {
        modulo(ConfigBanco(postgres.jdbcUrl, postgres.username, postgres.password))
    }

    @Test
    fun `lista as tarefas criadas pela migracao`() = testApplication {
        appComBanco()
        val cliente = createClient { install(ContentNegotiation) { json() } }
        val tarefas = cliente.get("/tarefas").body<List<Tarefa>>()
        assertTrue(tarefas.any { it.titulo == "Estudar Ktor" })
    }

    @Test
    fun `cria e depois busca pelo id`() = testApplication {
        appComBanco()
        val cliente = createClient { install(ContentNegotiation) { json() } }
        val resposta = cliente.post("/tarefas") {
            contentType(ContentType.Application.Json)
            setBody(NovaTarefa("Escrever testes"))
        }
        assertEquals(HttpStatusCode.Created, resposta.status)
        val criada = resposta.body<Tarefa>()
        assertEquals(criada, cliente.get("/tarefas/${criada.id}").body<Tarefa>())
    }

    @Test
    fun `id inexistente devolve 404`() = testApplication {
        appComBanco()
        assertEquals(HttpStatusCode.NotFound, client.get("/tarefas/9999").status)
    }

    @Test
    fun `openapi descreve as rotas`() = testApplication {
        appComBanco()
        val especificacao = client.get("/docs/documentation.yaml").bodyAsText()
        assertTrue("/tarefas/{id}:" in especificacao)
    }
}
