package br.ufrn.exemplo.tarefas.dominio;

import java.util.List;

// Erro do domínio, com nome. Quem decide o status HTTP é o adaptador (Erros.java).
public class EntradaInvalida extends RuntimeException {

    private final List<String> violacoes;

    public EntradaInvalida(List<String> violacoes) {
        super(String.join("; ", violacoes));
        this.violacoes = violacoes;
    }

    public List<String> violacoes() {
        return violacoes;
    }
}
