package br.ufrn.exemplo.tarefas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// O recurso com o repositório em memória: JUnit puro, sem Quarkus, sem Docker, em
// milissegundos. Equivale ao RotasTest do Ktor.
class RecursoDeTarefasUnitarioTest {

    private RecursoDeTarefas recurso;

    @BeforeEach
    void montar() {
        recurso = new RecursoDeTarefas();
        recurso.repositorio = new RepositorioEmMemoria(); // o que o CDI faria
    }

    @Test
    void listaAsTarefasIniciais() {
        assertEquals(2, recurso.listar().size());
    }

    @Test
    void criarDevolve201ComATarefa() {
        var resposta = recurso.criar(new NovaTarefa("Escrever testes"));
        assertEquals(201, resposta.getStatus());
        assertEquals("Escrever testes", ((Tarefa) resposta.getEntity()).titulo());
    }

    @Test
    void idInexistenteViraNotFound() {
        assertThrows(NotFoundException.class, () -> recurso.buscar(9999));
    }
}
