package avisos

import (
	"context"
	"fmt"
	"log/slog"
	"time"
)

// CanalSimulado faz o papel de um canal real (e-mail, push, chat): espera um tempo,
// como esperaria a resposta de um servidor externo, e registra o envio no log.
type CanalSimulado struct {
	nome   string
	demora time.Duration
}

// NovoCanalSimulado cria um canal que leva `demora` para entregar.
func NovoCanalSimulado(nome string, demora time.Duration) CanalSimulado {
	return CanalSimulado{nome: nome, demora: demora}
}

// Nome devolve o nome do canal.
func (c CanalSimulado) Nome() string { return c.nome }

// Enviar espera a demora do canal, ou desiste quando o ctx vence ou é cancelado.
func (c CanalSimulado) Enviar(ctx context.Context, aviso Aviso) error {
	select {
	case <-time.After(c.demora):
		slog.Info("aviso enviado", "canal", c.nome, "tarefa", aviso.TarefaID, "titulo", aviso.Titulo)
		return nil
	case <-ctx.Done():
		// %w mantém o erro original na cadeia: quem chama pode testar com errors.Is.
		return fmt.Errorf("canal %s: %w", c.nome, ctx.Err())
	}
}

// CanaisPadrao são os três canais do exemplo, com demoras diferentes.
func CanaisPadrao() []Canal {
	return []Canal{
		NovoCanalSimulado("email", 400*time.Millisecond),
		NovoCanalSimulado("push", 50*time.Millisecond),
		NovoCanalSimulado("chat", 150*time.Millisecond),
	}
}
