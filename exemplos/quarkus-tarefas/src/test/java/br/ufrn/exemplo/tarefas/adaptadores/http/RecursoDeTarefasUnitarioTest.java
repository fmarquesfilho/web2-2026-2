package br.ufrn.exemplo.tarefas.adaptadores.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufrn.exemplo.tarefas.adaptadores.memoria.AvisadorEmMemoria;
import br.ufrn.exemplo.tarefas.adaptadores.memoria.RepositorioEmMemoria;
import br.ufrn.exemplo.tarefas.dominio.NovaTarefa;
import br.ufrn.exemplo.tarefas.dominio.Tarefa;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// O recurso com o repositório em memória: JUnit puro, sem Quarkus, sem Docker, em
// milissegundos. Equivale ao RotasTest do Ktor.
class RecursoDeTarefasUnitarioTest {

    private RecursoDeTarefas recurso;
    private AvisadorEmMemoria avisador;

    @BeforeEach
    void montar() {
        recurso = new RecursoDeTarefas();
        recurso.repositorio = new RepositorioEmMemoria(); // o que o CDI faria
        avisador = new AvisadorEmMemoria();
        recurso.avisador = avisador;
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
    void criarAvisaOServicoDeAvisos() {
        recurso.criar(new NovaTarefa("Estudar gRPC"));
        assertEquals("Estudar gRPC", avisador.avisadas.get(0).titulo());
    }

    @Test
    void avisoQueFalhaNaoImpedeACriacao() {
        recurso.avisador = tarefa -> false; // Avisador que sempre falha
        assertEquals(201, recurso.criar(new NovaTarefa("Estudar gRPC")).getStatus());
    }

    @Test
    void idInexistenteViraNotFound() {
        assertThrows(NotFoundException.class, () -> recurso.buscar(9999));
    }
}
