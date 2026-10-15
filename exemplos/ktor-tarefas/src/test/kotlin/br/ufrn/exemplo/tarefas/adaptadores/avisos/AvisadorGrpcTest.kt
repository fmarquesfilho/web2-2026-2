package br.ufrn.exemplo.tarefas.adaptadores.avisos

import br.ufrn.exemplo.tarefas.avisos.v1.AvisarRequest
import br.ufrn.exemplo.tarefas.avisos.v1.AvisarResponse
import br.ufrn.exemplo.tarefas.avisos.v1.AvisosServiceGrpcKt.AvisosServiceCoroutineImplBase
import br.ufrn.exemplo.tarefas.avisos.v1.avisarResponse
import br.ufrn.exemplo.tarefas.avisos.v1.entrega
import br.ufrn.exemplo.tarefas.dominio.Tarefa
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

// O cliente gRPC contra um servidor falso, em memória: sem rede e sem o serviço Go.
class AvisadorGrpcTest {

    private val servidores = mutableListOf<Server>()

    // Sobe um servidor em memória com a implementação dada e devolve um cliente para ele.
    private fun avisadorPara(nome: String, servico: AvisosServiceCoroutineImplBase): AvisadorGrpc {
        servidores += InProcessServerBuilder.forName(nome).directExecutor().addService(servico).build().start()
        return AvisadorGrpc(InProcessChannelBuilder.forName(nome).directExecutor().build(), prazo = 300.milliseconds)
    }

    @AfterTest
    fun parar() = servidores.forEach { it.shutdownNow() }

    @Test
    fun `aviso entregue devolve true`() = runBlocking {
        val avisador = avisadorPara("rapido", object : AvisosServiceCoroutineImplBase() {
            override suspend fun avisar(request: AvisarRequest): AvisarResponse =
                avisarResponse { entregas += entrega { canal = "push"; entregue = true } }
        })
        assertTrue(avisador.avisar(Tarefa(1, "Estudar gRPC")))
    }

    @Test
    fun `servico lento estoura o prazo e devolve false sem esperar`() = runBlocking {
        val avisador = avisadorPara("lento", object : AvisosServiceCoroutineImplBase() {
            override suspend fun avisar(request: AvisarRequest): AvisarResponse {
                delay(5_000)
                return avisarResponse { }
            }
        })
        var resultado = true
        val duracao = measureTime { resultado = avisador.avisar(Tarefa(1, "Estudar gRPC")) }
        assertFalse(resultado)
        assertTrue(duracao < 2_000.milliseconds, "esperou $duracao: o prazo de 300 ms não foi aplicado")
    }

    @Test
    fun `servico fora do ar devolve false`() = runBlocking {
        val avisador = AvisadorGrpc(InProcessChannelBuilder.forName("ninguem").build(), prazo = 300.milliseconds)
        assertFalse(avisador.avisar(Tarefa(1, "Estudar gRPC")))
    }
}
