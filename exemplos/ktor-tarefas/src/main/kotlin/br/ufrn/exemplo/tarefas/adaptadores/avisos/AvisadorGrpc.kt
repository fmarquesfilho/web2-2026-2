package br.ufrn.exemplo.tarefas.adaptadores.avisos

import br.ufrn.exemplo.tarefas.avisos.v1.AvisosServiceGrpcKt.AvisosServiceCoroutineStub
import br.ufrn.exemplo.tarefas.avisos.v1.avisarRequest
import br.ufrn.exemplo.tarefas.dominio.Avisador
import br.ufrn.exemplo.tarefas.dominio.Tarefa
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// Adaptador da porta Avisador: chama o serviço Go por gRPC. É o único lugar da API que
// conhece o contrato gerado; o domínio e as rotas só conhecem a interface Avisador.
class AvisadorGrpc(
    private val canal: ManagedChannel,
    private val prazo: Duration = 1.seconds,
) : Avisador {

    private val stub = AvisosServiceCoroutineStub(canal)
    private val log = LoggerFactory.getLogger(AvisadorGrpc::class.java)

    override suspend fun avisar(tarefa: Tarefa): Boolean = try {
        // Todo pedido sai com prazo. Sem ele, um serviço lento prende a requisição HTTP.
        val resposta = stub
            .withDeadlineAfter(prazo.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .avisar(avisarRequest {               // DSL gerada: lambda com receptor
                tarefaId = tarefa.id.toLong()
                titulo = tarefa.titulo
            })
        log.info("aviso da tarefa {}: {}", tarefa.id, resposta.entregasList.map { "${it.canal}=${it.entregue}" })
        true
    } catch (e: StatusException) {
        // UNAVAILABLE (serviço fora), DEADLINE_EXCEEDED (lento demais), INVALID_ARGUMENT...
        // A tarefa já foi criada: o aviso falhar não deve derrubar a resposta.
        log.warn("aviso da tarefa {} não enviado: {}", tarefa.id, e.status.code)
        false
    }

    fun fechar() {
        canal.shutdown().awaitTermination(1, TimeUnit.SECONDS)
    }

    companion object {
        // "localhost:50051" em desenvolvimento; "avisos:50051" no docker compose.
        fun para(endereco: String) = AvisadorGrpc(ManagedChannelBuilder.forTarget(endereco).usePlaintext().build())
    }
}
