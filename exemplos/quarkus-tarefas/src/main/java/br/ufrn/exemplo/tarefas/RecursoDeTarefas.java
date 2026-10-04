package br.ufrn.exemplo.tarefas;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/tarefas")
public class RecursoDeTarefas {

    // O recurso fala com a interface. Por enquanto, quem constrói a implementação é ele mesmo.
    private final RepositorioDeTarefas repositorio = new RepositorioEmMemoria();

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
