package br.ufrn.exemplo.tarefas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Sem @ApplicationScoped desde 21/09: o bean agora é o RepositorioPanache. Com os dois
// anotados, o CDI não saberia qual injetar (dependência ambígua). Fica para testes
// de unidade, que o constroem com `new`.
public class RepositorioEmMemoria implements RepositorioDeTarefas {

    private final List<Tarefa> tarefas = new ArrayList<>(List.of(
            new Tarefa(1, "Estudar Ktor", false),
            new Tarefa(2, "Estudar Quarkus", false)));
    private int proximoId = 3;

    @Override
    public List<Tarefa> listar() {
        return List.copyOf(tarefas);
    }

    @Override
    public Optional<Tarefa> buscar(int id) {
        return tarefas.stream().filter(t -> t.id() == id).findFirst();
    }

    @Override
    public Tarefa adicionar(NovaTarefa nova) {
        Tarefa tarefa = new Tarefa(proximoId++, nova.titulo(), false);
        tarefas.add(tarefa);
        return tarefa;
    }
}
