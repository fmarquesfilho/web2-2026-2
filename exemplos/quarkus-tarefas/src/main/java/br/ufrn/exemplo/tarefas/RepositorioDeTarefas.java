package br.ufrn.exemplo.tarefas;

import java.util.List;
import java.util.Optional;

// A porta. O recurso depende dela, não do Panache.
public interface RepositorioDeTarefas {
    List<Tarefa> listar();
    Optional<Tarefa> buscar(int id);
    Tarefa adicionar(NovaTarefa nova);
}
