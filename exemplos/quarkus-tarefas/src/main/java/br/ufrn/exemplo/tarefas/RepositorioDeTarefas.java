package br.ufrn.exemplo.tarefas;

import java.util.List;

// A porta. O recurso depende dela, não da lista.
public interface RepositorioDeTarefas {
    List<Tarefa> listar();
    Tarefa adicionar(NovaTarefa nova);
}
