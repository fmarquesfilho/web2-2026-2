package br.ufrn.exemplo.tarefas.adaptadores.http;

import br.ufrn.exemplo.tarefas.dominio.EntradaInvalida;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

// Exceção vira resposta, num lugar só: o equivalente do StatusPages do Ktor.
// `@ServerExceptionMapper` é a forma do Quarkus REST para `ExceptionMapper`: um método por tipo.
public class Erros {

    private static final Logger LOG = Logger.getLogger(Erros.class);

    // A forma está certa, mas a REGRA do domínio não: 422.
    @ServerExceptionMapper
    public Response entradaInvalida(EntradaInvalida e) {
        return problema(422, "entrada-invalida", "Entrada inválida",
                "A entrada viola " + e.violacoes().size() + " regra(s).", e.violacoes());
    }

    // Campo com tipo errado (`{"titulo":[1]}`): a FORMA está errada, 400. Sem este método,
    // o Quarkus responde com um JSON próprio, fora do formato problem details.
    @ServerExceptionMapper
    public Response jsonInvalido(MismatchedInputException e) {
        return problema(400, "requisicao-malformada", "Requisição malformada", e.getOriginalMessage(), null);
    }

    // O que o próprio Jakarta REST lança (JSON quebrado, 404, 405, 415...), no mesmo formato.
    @ServerExceptionMapper
    public Response web(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        return switch (status) {
            case 400 -> problema(400, "requisicao-malformada", "Requisição malformada", e.getMessage(), null);
            case 404 -> problema(404, "nao-encontrado", "Recurso inexistente", e.getMessage(), null);
            default -> problema(status, "http-" + status, e.getResponse().getStatusInfo().getReasonPhrase(), e.getMessage(), null);
        };
    }

    // O resto é erro nosso: 500, sem vazar detalhe interno para o cliente.
    @ServerExceptionMapper
    public Response inesperado(RuntimeException e) {
        LOG.error("erro não tratado", e);
        return problema(500, "interno", "Erro interno", null, null);
    }

    private static Response problema(int status, String tipo, String titulo, String detalhe, List<String> violacoes) {
        return Response.status(status)
                .type(Problema.MEDIA_TYPE)
                .entity(new Problema("/problemas/" + tipo, titulo, status, detalhe, violacoes))
                .build();
    }
}
