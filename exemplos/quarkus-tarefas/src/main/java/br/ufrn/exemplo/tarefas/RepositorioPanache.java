package br.ufrn.exemplo.tarefas;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RepositorioPanache
        implements RepositorioDeTarefas, PanacheRepositoryBase<TarefaEntidade, Integer> {

    @Override
    public List<Tarefa> listar() {
        return listAll(Sort.by("id")).stream().map(TarefaEntidade::paraTarefa).toList();
    }

    @Override
    public Optional<Tarefa> buscar(int id) {
        return findByIdOptional(id).map(TarefaEntidade::paraTarefa);
    }

    // Escrita precisa de transação; leitura, não.
    @Override
    @Transactional
    public Tarefa adicionar(NovaTarefa nova) {
        TarefaEntidade entidade = new TarefaEntidade();
        entidade.titulo = nova.titulo();
        persist(entidade);
        return entidade.paraTarefa();
    }
}
