package br.ufrn.exemplo.tarefas;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

// @ApplicationScoped: um único bean gerenciado pelo CDI, injetado onde for pedido.
// É o equivalente Java ao `single { ... }` do Koin no lado Ktor.
// Na Sprint 1 (21/09) esta implementação vira Postgres com Panache.
@ApplicationScoped
public class RepositorioEmMemoria implements RepositorioDeTarefas {

    private final List<Tarefa> tarefas = new ArrayList<>(List.of(
            new Tarefa(1, "Estudar Ktor", false),
            new Tarefa(2, "Estudar Quarkus", false)));
    private int proximoId = 3;

    @Override
    public List<Tarefa> listar() {
        return tarefas;
    }

    @Override
    public Tarefa adicionar(NovaTarefa nova) {
        Tarefa tarefa = new Tarefa(proximoId++, nova.titulo(), false);
        tarefas.add(tarefa);
        return tarefa;
    }
}
