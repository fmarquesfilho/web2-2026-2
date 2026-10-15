// Comando avisos: o servidor gRPC do serviço de avisos.
//
//	go run ./cmd/avisos                # escuta na porta 50051
//	AVISOS_PORTA=6000 go run ./cmd/avisos
//	avisos saude                       # pergunta ao serviço local se está de pé (healthcheck)
package main

import (
	"context"
	"log/slog"
	"net"
	"os"
	"os/signal"
	"syscall"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/health"
	healthpb "google.golang.org/grpc/health/grpc_health_v1"
	"google.golang.org/grpc/reflection"

	"avisos/avisos"
	avisosv1 "avisos/gen/tarefas/avisos/v1"
	"avisos/grpcapi"
)

func main() {
	porta := os.Getenv("AVISOS_PORTA")
	if porta == "" {
		porta = "50051"
	}
	if len(os.Args) > 1 && os.Args[1] == "saude" {
		os.Exit(verificarSaude(porta))
	}
	lis, err := net.Listen("tcp", ":"+porta)
	if err != nil {
		slog.Error("não abriu a porta", "porta", porta, "erro", err)
		os.Exit(1)
	}

	srv := grpc.NewServer()
	avisosv1.RegisterAvisosServiceServer(srv, grpcapi.NovoServidor(avisos.NovoAvisador(avisos.CanaisPadrao()...)))

	// Health check padrão do gRPC: plataformas e o docker compose perguntam se o serviço está de pé.
	saude := health.NewServer()
	healthpb.RegisterHealthServer(srv, saude)
	saude.SetServingStatus(avisosv1.AvisosService_ServiceDesc.ServiceName, healthpb.HealthCheckResponse_SERVING)

	// Reflection: permite que o grpcurl descubra os serviços sem ter o .proto em mãos.
	reflection.Register(srv)

	// Encerramento gracioso: ao receber SIGTERM (docker stop, nova versão na plataforma),
	// para de aceitar chamadas e termina as que estão em andamento.
	go func() {
		sinal := make(chan os.Signal, 1)
		signal.Notify(sinal, syscall.SIGINT, syscall.SIGTERM)
		<-sinal
		slog.Info("encerrando")
		saude.Shutdown()
		srv.GracefulStop()
	}()

	slog.Info("serviço de avisos no ar", "porta", porta)
	if err := srv.Serve(lis); err != nil {
		slog.Error("servidor parou", "erro", err)
		os.Exit(1)
	}
}

// verificarSaude chama o health check gRPC do próprio serviço. A imagem final não tem
// shell nem curl; o docker compose usa este subcomando como healthcheck.
func verificarSaude(porta string) int {
	conn, err := grpc.NewClient("localhost:"+porta, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		return 1
	}
	defer conn.Close()
	ctx, cancelar := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancelar()
	resp, err := healthpb.NewHealthClient(conn).Check(ctx, &healthpb.HealthCheckRequest{})
	if err != nil || resp.GetStatus() != healthpb.HealthCheckResponse_SERVING {
		return 1
	}
	return 0
}
