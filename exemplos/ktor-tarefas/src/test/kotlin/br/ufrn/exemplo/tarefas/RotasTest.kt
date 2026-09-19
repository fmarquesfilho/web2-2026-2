package br.ufrn.exemplo.tarefas

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
