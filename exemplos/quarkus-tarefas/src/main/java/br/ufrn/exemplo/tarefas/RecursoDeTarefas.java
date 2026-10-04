package br.ufrn.exemplo.tarefas;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/tarefas")
public class RecursoDeTarefas {

    @Inject
    RepositorioDeTarefas repositorio;

    @GET
    public List<Tarefa> listar() {
        return repositorio.listar();
    }

    @GET
    @Path("/{id}")
    public Tarefa buscar(@PathParam("id") int id) {
        return repositorio.buscar(id).orElseThrow(NotFoundException::new);
    }

    @POST
    public Response criar(NovaTarefa nova) {
        Tarefa criada = repositorio.adicionar(nova);
        return Response.status(Response.Status.CREATED).entity(criada).build();
    }
}
