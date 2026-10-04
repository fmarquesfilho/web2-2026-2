package br.ufrn.exemplo.tarefas;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Mapeamento da tabela `tarefas` (criada pela migração V1) para o Hibernate.
// Campos públicos: o Panache gera getters e setters na compilação.
@Entity
@Table(name = "tarefas")
public class TarefaEntidade extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String titulo;

    public boolean feita;

    Tarefa paraTarefa() {
        return new Tarefa(id, titulo, feita);
    }
}
