package br.ufrn.exemplo.tarefas.adaptadores.memoria;

import br.ufrn.exemplo.tarefas.dominio.Avisador;
import br.ufrn.exemplo.tarefas.dominio.Tarefa;
import java.util.ArrayList;
import java.util.List;

// Avisador que só anota o que recebeu, para os testes de unidade (sem CDI, sem rede).
public class AvisadorEmMemoria implements Avisador {

    public final List<Tarefa> avisadas = new ArrayList<>();

    @Override
    public boolean avisar(Tarefa tarefa) {
        avisadas.add(tarefa);
        return true;
    }
}
