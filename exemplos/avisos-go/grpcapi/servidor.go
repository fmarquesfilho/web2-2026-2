// Package grpcapi é o adaptador gRPC do serviço: traduz entre as mensagens geradas do
// contrato e o domínio (pacote avisos). O domínio não importa este pacote.
package grpcapi

import (
	"context"
	"errors"
	"time"

	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"

	"avisos/avisos"
	avisosv1 "avisos/gen/tarefas/avisos/v1"
)

// folga: o servidor encerra os envios um pouco antes do prazo de quem chamou, para que
// a resposta, com as entregas parciais, ainda chegue dentro do prazo.
const folga = 50 * time.Millisecond

// Servidor implementa avisosv1.AvisosServiceServer.
type Servidor struct {
	// Embutir o tipo "Unimplemented" é a recomendação do grpc-go: se o contrato ganhar
	// um método novo, o servidor continua compilando e responde UNIMPLEMENTED.
	avisosv1.UnimplementedAvisosServiceServer
	avisador *avisos.Avisador
}

// NovoServidor cria o adaptador sobre um Avisador.
func NovoServidor(avisador *avisos.Avisador) *Servidor {
	return &Servidor{avisador: avisador}
}

// Avisar recebe o pedido, chama o domínio e traduz o resultado. O ctx já traz o prazo
// definido por quem chamou (o deadline do gRPC vira o deadline do context).
func (s *Servidor) Avisar(ctx context.Context, req *avisosv1.AvisarRequest) (*avisosv1.AvisarResponse, error) {
	if prazo, ok := ctx.Deadline(); ok {
		var cancelar context.CancelFunc
		ctx, cancelar = context.WithDeadline(ctx, prazo.Add(-folga))
		defer cancelar()
	}
	entregas, err := s.avisador.Avisar(ctx, avisos.Aviso{TarefaID: req.GetTarefaId(), Titulo: req.GetTitulo()})
	if errors.Is(err, avisos.ErrAvisoInvalido) {
		// Erro do domínio vira um código gRPC; o texto vai junto, para quem depura.
		return nil, status.Error(codes.InvalidArgument, err.Error())
	}
	if err != nil {
		return nil, status.Error(codes.Internal, "falha ao avisar")
	}

	resp := &avisosv1.AvisarResponse{}
	for _, e := range entregas {
		entrega := &avisosv1.Entrega{Canal: e.Canal, Entregue: e.Erro == nil}
		if e.Erro != nil {
			entrega.Erro = e.Erro.Error()
		}
		resp.Entregas = append(resp.Entregas, entrega)
	}
	return resp, nil
}
