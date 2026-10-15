// Comando avisos: o servidor gRPC do serviço de avisos.
//
//	go run ./cmd/avisos                # escuta na porta 50051
//	AVISOS_PORTA=6000 go run ./cmd/avisos
package main

import (
	"log/slog"
	"net"
	"os"
	"os/signal"
	"syscall"

	"google.golang.org/grpc"
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
