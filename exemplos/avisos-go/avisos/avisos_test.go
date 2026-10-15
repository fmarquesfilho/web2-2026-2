package avisos

import (
	"context"
	"errors"
	"testing"
	"time"
)

func TestAvisarEntregaEmTodosOsCanaisAoMesmoTempo(t *testing.T) {
	av := NovoAvisador(CanaisPadrao()...)

	inicio := time.Now()
	entregas, err := av.Avisar(context.Background(), Aviso{TarefaID: 1, Titulo: "Estudar Go"})
	duracao := time.Since(inicio)

	if err != nil {
		t.Fatalf("erro inesperado: %v", err)
	}
	if len(entregas) != 3 {
		t.Fatalf("esperava 3 entregas, veio %d", len(entregas))
	}
	for _, e := range entregas {
		if e.Erro != nil {
			t.Errorf("canal %s falhou: %v", e.Canal, e.Erro)
		}
	}
	// Em sequência seriam 600 ms (400 + 50 + 150). Ao mesmo tempo, perto de 400 ms.
	if duracao > 550*time.Millisecond {
		t.Errorf("demorou %v: os canais não rodaram ao mesmo tempo", duracao)
	}
}

func TestAvisarRespeitaOPrazoDoContexto(t *testing.T) {
	av := NovoAvisador(CanaisPadrao()...)
	ctx, cancelar := context.WithTimeout(context.Background(), 100*time.Millisecond)
	defer cancelar()

	entregas, err := av.Avisar(ctx, Aviso{TarefaID: 1, Titulo: "Estudar Go"})
	if err != nil {
		t.Fatalf("erro inesperado: %v", err)
	}

	porCanal := map[string]error{}
	for _, e := range entregas {
		porCanal[e.Canal] = e.Erro
	}
	if porCanal["push"] != nil {
		t.Errorf("push (50 ms) deveria ter entregado antes do prazo: %v", porCanal["push"])
	}
	for _, lento := range []string{"email", "chat"} {
		if !errors.Is(porCanal[lento], context.DeadlineExceeded) {
			t.Errorf("%s deveria ter desistido por prazo, veio %v", lento, porCanal[lento])
		}
	}
}

func TestAvisarRecusaAvisoInvalido(t *testing.T) {
	av := NovoAvisador(CanaisPadrao()...)
	casos := []struct {
		nome  string
		aviso Aviso
	}{
		{"sem tarefa", Aviso{TarefaID: 0, Titulo: "Estudar"}},
		{"título em branco", Aviso{TarefaID: 1, Titulo: "   "}},
	}
	for _, c := range casos {
		t.Run(c.nome, func(t *testing.T) {
			_, err := av.Avisar(context.Background(), c.aviso)
			if !errors.Is(err, ErrAvisoInvalido) {
				t.Errorf("esperava ErrAvisoInvalido, veio %v", err)
			}
		})
	}
}
