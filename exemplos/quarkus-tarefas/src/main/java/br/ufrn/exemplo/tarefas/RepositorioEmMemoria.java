package br.ufrn.exemplo.tarefas;

import java.util.ArrayList;
import java.util.List;

// Implementação em memória. No Passo 7 entra outra, com PostgreSQL (Panache).
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
    public Tarefa adicionar(NovaTarefa nova) {
        Tarefa tarefa = new Tarefa(proximoId++, nova.titulo(), false);
        tarefas.add(tarefa);
        return tarefa;
    }
}
