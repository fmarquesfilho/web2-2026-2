package br.ufrn.exemplo.tarefas;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.List;

// O recurso REST. As rotas são ANOTAÇÕES (compare com o DSL de código do Ktor).
// O controller só traduz HTTP em chamadas ao repositório.
@Path("/tarefas")
public class RecursoDeTarefas {

    // CDI injeta a implementação de RepositorioDeTarefas.
    @Inject
    RepositorioDeTarefas repositorio;

    @GET
    public List<Tarefa> listar() {
        return repositorio.listar();
    }

    @POST
    public Response criar(NovaTarefa nova) {
        Tarefa criada = repositorio.adicionar(nova);
        return Response.status(Response.Status.CREATED).entity(criada).build();
    }
}
