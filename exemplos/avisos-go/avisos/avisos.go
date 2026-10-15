// Package avisos avisa, por vários canais ao mesmo tempo, que uma tarefa foi criada.
//
// É o domínio do serviço: não conhece gRPC, HTTP nem banco. Quem chama passa um
// context.Context, que carrega o prazo (deadline) e o cancelamento.
package avisos

import (
	"context"
	"errors"
	"fmt"
	"strings"
	"sync"
)

// Aviso é o que se envia: qual tarefa foi criada e com que título.
type Aviso struct {
	TarefaID int64
	Titulo   string
}

// Canal é qualquer meio de entrega: e-mail, push, chat. A interface é pequena e
// declarada aqui, do lado de quem usa, como é costume em Go.
type Canal interface {
	Nome() string
	Enviar(ctx context.Context, aviso Aviso) error
}

// Entrega é o resultado de um canal: Erro nulo quer dizer entregue.
type Entrega struct {
	Canal string
	Erro  error
}

// ErrAvisoInvalido é devolvido quando o aviso não tem tarefa ou título.
// Quem recebe compara com errors.Is, não pelo texto da mensagem.
var ErrAvisoInvalido = errors.New("aviso inválido")

// Avisador envia um aviso a todos os canais.
type Avisador struct {
	canais []Canal
}

// NovoAvisador monta um Avisador com os canais dados.
func NovoAvisador(canais ...Canal) *Avisador {
	return &Avisador{canais: canais}
}

// Avisar envia o aviso a todos os canais ao mesmo tempo, uma goroutine por canal, e
// espera todos terminarem. O tempo total é o do canal mais lento, não a soma.
// Se o prazo do ctx vencer, os canais que ainda não terminaram desistem.
func (av *Avisador) Avisar(ctx context.Context, aviso Aviso) ([]Entrega, error) {
	if aviso.TarefaID <= 0 || strings.TrimSpace(aviso.Titulo) == "" {
		return nil, fmt.Errorf("%w: tarefa %d, título %q", ErrAvisoInvalido, aviso.TarefaID, aviso.Titulo)
	}

	entregas := make([]Entrega, len(av.canais))
	var wg sync.WaitGroup
	for i, canal := range av.canais {
		wg.Go(func() {
			// Cada goroutine escreve só na sua posição: não há disputa pela fatia.
			entregas[i] = Entrega{Canal: canal.Nome(), Erro: canal.Enviar(ctx, aviso)}
		})
	}
	wg.Wait()
	return entregas, nil
}
