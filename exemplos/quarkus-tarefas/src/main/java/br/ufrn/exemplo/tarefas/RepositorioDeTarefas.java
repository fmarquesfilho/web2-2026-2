package br.ufrn.exemplo.tarefas;

import java.util.List;

// A "porta": o que o recurso precisa, sem dizer quem fornece (regra de dependência).
public interface RepositorioDeTarefas {
    List<Tarefa> listar();
    Tarefa adicionar(NovaTarefa nova);
}
