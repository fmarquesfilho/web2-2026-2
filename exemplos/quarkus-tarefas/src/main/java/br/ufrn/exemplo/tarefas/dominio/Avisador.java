package br.ufrn.exemplo.tarefas.dominio;

// Outra porta: avisar que uma tarefa foi criada. O domínio não sabe se o aviso vai por
// gRPC, fila ou e-mail; devolve false quando não conseguiu, sem lançar exceção.
public interface Avisador {
    boolean avisar(Tarefa tarefa);
}
