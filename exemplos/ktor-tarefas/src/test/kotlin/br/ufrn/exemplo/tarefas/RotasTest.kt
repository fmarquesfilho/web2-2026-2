package br.ufrn.exemplo.tarefas

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Rotas com o repositório em memória: sem Docker, em milissegundos.
class RotasTest {

    @Test
    fun `id que nao e numero devolve 400`() = testApplication {
        application { configurar(RepositorioEmMemoria()) }
        assertEquals(HttpStatusCode.BadRequest, client.get("/tarefas/abc").status)
    }

    @Test
    fun `id existente devolve 200`() = testApplication {
        application { configurar(RepositorioEmMemoria()) }
        assertEquals(HttpStatusCode.OK, client.get("/tarefas/1").status)
    }

    @Test
    fun `titulo em branco devolve 422 em problem details`() = testApplication {
        application { configurar(RepositorioEmMemoria()) }
        val resposta = client.post("/tarefas") {
            contentType(ContentType.Application.Json)
            setBody("""{"titulo":"   "}""")
        }
        assertEquals(HttpStatusCode.UnprocessableEntity, resposta.status)
        assertEquals(ContentType.Application.ProblemJson, resposta.contentType()?.withoutParameters())
        assertTrue("titulo: não pode ficar em branco" in resposta.bodyAsText())
    }

    @Test
    fun `corpo sem titulo devolve 400 em problem details`() = testApplication {
        application { configurar(RepositorioEmMemoria()) }
        val resposta = client.post("/tarefas") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, resposta.status)
        assertEquals(ContentType.Application.ProblemJson, resposta.contentType()?.withoutParameters())
    }
}
