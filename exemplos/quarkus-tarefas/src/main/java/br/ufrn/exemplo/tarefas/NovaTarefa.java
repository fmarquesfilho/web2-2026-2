package br.ufrn.exemplo.tarefas;

// O que chega no corpo do POST — sem id, que é o servidor quem atribui.
public record NovaTarefa(String titulo) {}
