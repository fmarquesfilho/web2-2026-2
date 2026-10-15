package grpcapi

import (
	"context"
	"net"
	"testing"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/status"
	"google.golang.org/grpc/test/bufconn"

	"avisos/avisos"
	avisosv1 "avisos/gen/tarefas/avisos/v1"
)

// cliente sobe o servidor numa conexão em memória (bufconn): o teste passa pela pilha
// gRPC inteira, com serialização e códigos de status, sem abrir porta de rede.
func cliente(t *testing.T) avisosv1.AvisosServiceClient {
	t.Helper()
	lis := bufconn.Listen(1024 * 1024)
	srv := grpc.NewServer()
	avisosv1.RegisterAvisosServiceServer(srv, NovoServidor(avisos.NovoAvisador(avisos.CanaisPadrao()...)))
	go func() { _ = srv.Serve(lis) }()
	t.Cleanup(srv.Stop)

	conn, err := grpc.NewClient("passthrough:///bufnet",
		grpc.WithContextDialer(func(context.Context, string) (net.Conn, error) { return lis.Dial() }),
		grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { _ = conn.Close() })
	return avisosv1.NewAvisosServiceClient(conn)
}

func TestAvisarPorGRPC(t *testing.T) {
	resp, err := cliente(t).Avisar(context.Background(), &avisosv1.AvisarRequest{TarefaId: 1, Titulo: "Estudar gRPC"})
	if err != nil {
		t.Fatalf("erro inesperado: %v", err)
	}
	if len(resp.GetEntregas()) != 3 {
		t.Fatalf("esperava 3 entregas, veio %d", len(resp.GetEntregas()))
	}
	for _, e := range resp.GetEntregas() {
		if !e.GetEntregue() {
			t.Errorf("canal %s não entregou: %s", e.GetCanal(), e.GetErro())
		}
	}
}

func TestAvisoInvalidoViraInvalidArgument(t *testing.T) {
	_, err := cliente(t).Avisar(context.Background(), &avisosv1.AvisarRequest{TarefaId: 1, Titulo: " "})
	if status.Code(err) != codes.InvalidArgument {
		t.Fatalf("esperava INVALID_ARGUMENT, veio %v", err)
	}
}

func TestPrazoDeQuemChamaChegaAoServidor(t *testing.T) {
	// 300 ms de prazo: o servidor encerra os envios em 250 ms (a folga), e push (50 ms) e
	// chat (150 ms) entregam; email (400 ms) volta como não entregue, dentro do prazo.
	ctx, cancelar := context.WithTimeout(context.Background(), 300*time.Millisecond)
	defer cancelar()

	resp, err := cliente(t).Avisar(ctx, &avisosv1.AvisarRequest{TarefaId: 1, Titulo: "Estudar gRPC"})
	if err != nil {
		t.Fatalf("erro inesperado: %v", err)
	}
	entregues := map[string]bool{}
	for _, e := range resp.GetEntregas() {
		entregues[e.GetCanal()] = e.GetEntregue()
	}
	if !entregues["push"] || !entregues["chat"] || entregues["email"] {
		t.Errorf("esperava push e chat entregues e email não, veio %v", entregues)
	}
}
