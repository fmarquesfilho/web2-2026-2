package br.ufrn.exemplo.tarefas.adaptadores.avisos;

import br.ufrn.exemplo.tarefas.avisos.v1.AvisarRequest;
import br.ufrn.exemplo.tarefas.avisos.v1.AvisarResponse;
import br.ufrn.exemplo.tarefas.avisos.v1.AvisosServiceGrpc.AvisosServiceBlockingStub;
import br.ufrn.exemplo.tarefas.dominio.Avisador;
import br.ufrn.exemplo.tarefas.dominio.Tarefa;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;

// Adaptador da porta Avisador: chama o serviço Go por gRPC. O Quarkus cria o stub a
// partir da configuração quarkus.grpc.clients.avisos.* (host e porta).
@ApplicationScoped
public class AvisadorGrpc implements Avisador {

    private static final Logger LOG = Logger.getLogger(AvisadorGrpc.class);

    @GrpcClient("avisos")
    AvisosServiceBlockingStub stub;

    @Override
    public boolean avisar(Tarefa tarefa) {
        try {
            // Stub bloqueante: o recurso já roda numa thread de trabalho. Sempre com prazo.
            AvisarResponse resposta = stub.withDeadlineAfter(1, TimeUnit.SECONDS)
                    .avisar(AvisarRequest.newBuilder()
                            .setTarefaId(tarefa.id())
                            .setTitulo(tarefa.titulo())
                            .build());
            LOG.infof("aviso da tarefa %d: %d entregas", tarefa.id(), resposta.getEntregasCount());
            return true;
        } catch (StatusRuntimeException e) {
            // A tarefa já foi criada: o aviso falhar não deve derrubar a resposta.
            LOG.warnf("aviso da tarefa %d não enviado: %s", tarefa.id(), e.getStatus().getCode());
            return false;
        }
    }
}
