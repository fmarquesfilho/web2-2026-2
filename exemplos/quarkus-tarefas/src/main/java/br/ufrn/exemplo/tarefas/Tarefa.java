package br.ufrn.exemplo.tarefas;

// O domínio: uma tarefa. `record` é o equivalente Java à data class do Kotlin.
// O Jackson serializa records em JSON sem configuração.
public record Tarefa(int id, String titulo, boolean feita) {}
