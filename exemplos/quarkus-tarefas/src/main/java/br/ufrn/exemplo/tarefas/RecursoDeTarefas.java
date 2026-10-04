package br.ufrn.exemplo.tarefas;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

// Erros não viram resposta aqui: o recurso lança, e o Erros.java responde.
@Path("/tarefas")
public class RecursoDeTarefas {

    @Inject
    RepositorioDeTarefas repositorio;

    @GET
    @Operation(summary = "Lista as tarefas")
    public List<Tarefa> listar() {
        return repositorio.listar();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca uma tarefa pelo id")
    @APIResponse(responseCode = "200", description = "A tarefa")
    @APIResponse(responseCode = "404", description = "Tarefa inexistente",
            content = @Content(mediaType = Problema.MEDIA_TYPE, schema = @Schema(implementation = Problema.class)))
    public Tarefa buscar(@PathParam("id") int id) {
        return repositorio.buscar(id).orElseThrow(() -> new NotFoundException("A tarefa " + id + " não existe"));
    }

    @POST
    @Operation(summary = "Cria uma tarefa")
    @APIResponse(responseCode = "201", description = "Tarefa criada")
    @APIResponse(responseCode = "422", description = "Entrada inválida",
            content = @Content(mediaType = Problema.MEDIA_TYPE, schema = @Schema(implementation = Problema.class)))
    public Response criar(NovaTarefa nova) {
        if (nova == null) {
            throw new BadRequestException("O corpo da requisição é obrigatório");
        }
        List<String> violacoes = nova.violacoes();
        if (!violacoes.isEmpty()) {
            throw new EntradaInvalida(violacoes);
        }
        Tarefa criada = repositorio.adicionar(nova);
        return Response.created(URI.create("/tarefas/" + criada.id())).entity(criada).build();
    }
}
