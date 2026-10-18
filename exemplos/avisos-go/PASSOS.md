# Passos 13 a 18 — o serviço de avisos em Go, o contrato gRPC e a implantação

Continuação da API de Tarefas (passos 1 a 12, em `ktor-tarefas/` e `quarkus-tarefas/`). Na Sprint 2, a API ganha um segundo serviço, escrito em Go: quando uma tarefa é criada, a API pede ao serviço de avisos que avise por três canais (e-mail, push e chat, simulados). A conversa entre os dois é por gRPC, com o contrato em Protocol Buffers.

Por que um serviço à parte, e em Go: avisar é trabalho de E/S que espera por terceiros (servidores de e-mail, de push). O serviço avisa os canais ao mesmo tempo, com uma goroutine por canal, respeita o prazo de quem chamou e, se cair, não derruba a API: a tarefa continua sendo criada.

```
exemplos/
├── protos/                      o contrato, compartilhado pelos três projetos
│   ├── buf.yaml, buf.gen.yaml
│   └── tarefas/avisos/v1/avisos.proto
├── avisos-go/                   o serviço Go
│   ├── avisos/                  domínio: Avisador, Canal, erros (sem gRPC)
│   ├── grpcapi/                 adaptador: o servidor gRPC, sobre o domínio
│   ├── gen/                     stubs gerados pelo buf (não editar)
│   └── cmd/avisos, cmd/demo     os executáveis
├── ktor-tarefas/                a API, agora cliente gRPC (Passo 16)
├── quarkus-tarefas/             idem
├── compose.yaml                 tudo junto (Passo 17)
└── implantacao/                 modelos de CI e de Render (Passo 18)
```

Versões: Go 1.26, grpc-go 1.84.0, protobuf-go 1.36.12, Buf CLI 1.71, grpc-java 1.85.0, grpc-kotlin 1.5.0, protobuf 4.36.2 (Kotlin), plugin Gradle `com.google.protobuf` 0.10.0, extensão `quarkus-grpc` do Quarkus 3.28.2.

---

## Passo 13 — O serviço Go: pacotes, interfaces, erros e `context`

Só Go, sem rede. O pacote `avisos` é o domínio:

```go
type Canal interface {                    // pequena, declarada do lado de quem usa
	Nome() string
	Enviar(ctx context.Context, aviso Aviso) error
}

var ErrAvisoInvalido = errors.New("aviso inválido")   // erro com nome: errors.Is

func (av *Avisador) Avisar(ctx context.Context, aviso Aviso) ([]Entrega, error) {
	if aviso.TarefaID <= 0 || strings.TrimSpace(aviso.Titulo) == "" {
		return nil, fmt.Errorf("%w: tarefa %d, título %q", ErrAvisoInvalido, aviso.TarefaID, aviso.Titulo)
	}
	entregas := make([]Entrega, len(av.canais))
	var wg sync.WaitGroup
	for i, canal := range av.canais {
		wg.Go(func() {                     // uma goroutine por canal
			entregas[i] = Entrega{Canal: canal.Nome(), Erro: canal.Enviar(ctx, aviso)}
		})
	}
	wg.Wait()
	return entregas, nil
}
```

O canal simulado espera a sua demora ou desiste quando o `ctx` vence:

```go
select {
case <-time.After(c.demora):
	return nil
case <-ctx.Done():
	return fmt.Errorf("canal %s: %w", c.nome, ctx.Err())   // %w mantém o erro na cadeia
}
```

```bash
cd exemplos/avisos-go
go test ./...          # 3 testes: ao mesmo tempo, prazo do context, aviso inválido
go run ./cmd/demo
```

```
prazo 1s, levou 400ms           ← o canal mais lento (400 ms), não a soma (600 ms)
  email entregue
  push  entregue
  chat  entregue
prazo 100ms, levou 100ms
  email não entregue: canal email: context deadline exceeded
  push  entregue
  chat  não entregue: canal chat: context deadline exceeded
aviso vazio: aviso inválido: tarefa 0, título ""
```

> Interfaces em Go são satisfeitas sem `implements`: qualquer tipo com `Nome()` e `Enviar(...)` é um `Canal`. O `context.Context` vai como primeiro parâmetro de toda função que pode esperar; é por ele que chegam o prazo e o cancelamento.

📖 [Effective Go](https://go.dev/doc/effective_go) · [Go — context](https://pkg.go.dev/context) · [Go — errors](https://pkg.go.dev/errors)

---

## Passo 14 — O contrato: Protocol Buffers, `buf lint` e `buf generate`

`protos/tarefas/avisos/v1/avisos.proto`:

```protobuf
syntax = "proto3";

package tarefas.avisos.v1;

option go_package = "avisos/gen/tarefas/avisos/v1;avisosv1";
option java_multiple_files = true;
option java_package = "br.ufrn.exemplo.tarefas.avisos.v1";

// Envia avisos sobre tarefas.
service AvisosService {
  // Avisa, em todos os canais ao mesmo tempo, que uma tarefa foi criada. ...
  rpc Avisar(AvisarRequest) returns (AvisarResponse);
}

message AvisarRequest {
  int64 tarefa_id = 1;      // os números identificam o campo no binário: não mudam nunca
  string titulo = 2;
}

message AvisarResponse { repeated Entrega entregas = 1; }

message Entrega {
  string canal = 1;
  bool entregue = 2;
  string erro = 3;
}
```

`protos/buf.yaml` liga as regras `STANDARD` e `COMMENTS` (todo elemento comentado); `protos/buf.gen.yaml` gera os stubs Go com os plugins remotos do Buf, sem nada para instalar.

```bash
cd exemplos/protos
buf lint               # sem saída: o contrato segue as regras
buf generate           # escreve avisos-go/gen/tarefas/avisos/v1/*.pb.go
cd ../avisos-go && go mod tidy
```

> O pacote tem versão (`v1`) no nome e na pasta: uma mudança incompatível vira `v2`, ao lado, sem quebrar quem usa a `v1`. Os stubs de Kotlin e de Java não são gerados pelo buf: o Gradle e o Quarkus os geram no build, a partir do mesmo arquivo.

📖 [Protocol Buffers — proto3](https://protobuf.dev/programming-guides/proto3/) · [Buf — lint rules](https://buf.build/docs/lint/rules/)

---

## Passo 15 — O servidor gRPC em Go: status, deadline, health e reflection

`grpcapi/servidor.go` traduz entre o contrato e o domínio:

```go
func (s *Servidor) Avisar(ctx context.Context, req *avisosv1.AvisarRequest) (*avisosv1.AvisarResponse, error) {
	if prazo, ok := ctx.Deadline(); ok {           // o deadline do gRPC chega no ctx
		var cancelar context.CancelFunc
		ctx, cancelar = context.WithDeadline(ctx, prazo.Add(-folga))   // responde antes do prazo
		defer cancelar()
	}
	entregas, err := s.avisador.Avisar(ctx, avisos.Aviso{TarefaID: req.GetTarefaId(), Titulo: req.GetTitulo()})
	if errors.Is(err, avisos.ErrAvisoInvalido) {
		return nil, status.Error(codes.InvalidArgument, err.Error())   // erro do domínio → código gRPC
	}
	...
}
```

`cmd/avisos/main.go` registra o serviço, o *health check* padrão do gRPC e o *reflection* (para o `grpcurl` descobrir os serviços), e encerra com `GracefulStop` ao receber SIGTERM.

```bash
go test ./...                       # + 3 testes do servidor, pela pilha gRPC (bufconn)
go run ./cmd/avisos                 # escuta na 50051

grpcurl -plaintext localhost:50051 list
grpcurl -plaintext -d '{"tarefa_id": 7, "titulo": "Estudar gRPC"}' \
  localhost:50051 tarefas.avisos.v1.AvisosService/Avisar
grpcurl -plaintext -max-time 0.3 -d '{"tarefa_id": 7, "titulo": "Estudar gRPC"}' \
  localhost:50051 tarefas.avisos.v1.AvisosService/Avisar     # email volta não entregue
grpcurl -plaintext -d '{"tarefa_id": 7, "titulo": " "}' \
  localhost:50051 tarefas.avisos.v1.AvisosService/Avisar     # Code: InvalidArgument
grpcurl -plaintext localhost:50051 grpc.health.v1.Health/Check
```

> A folga de 50 ms: sem ela, o servidor esperaria até o fim do prazo do cliente, e a resposta, com as entregas parciais, chegaria tarde demais (o cliente veria `DeadlineExceeded`). Com ela, o cliente recebe a lista: dois canais entregues e um não.

📖 [gRPC — Deadlines](https://grpc.io/docs/guides/deadlines/) · [gRPC — Status codes](https://grpc.io/docs/guides/status-codes/) · [gRPC — Health checking](https://grpc.io/docs/guides/health-checking/)

---

## Passo 16 — O serviço principal chama o Go

O domínio da API ganha uma porta, sem nada de gRPC:

```kotlin
// ktor-tarefas: dominio/Tarefa.kt
fun interface Avisador {
    suspend fun avisar(tarefa: Tarefa): Boolean
}
```

```java
// quarkus-tarefas: dominio/Avisador.java
public interface Avisador {
    boolean avisar(Tarefa tarefa);
}
```

O adaptador chama o Go com prazo e, se falhar, registra e devolve `false`: a tarefa já foi criada, e o aviso não pode derrubar a resposta.

```kotlin
// ktor-tarefas: adaptadores/avisos/AvisadorGrpc.kt
val resposta = stub
    .withDeadlineAfter(prazo.inWholeMilliseconds, TimeUnit.MILLISECONDS)
    .avisar(avisarRequest {               // DSL gerada a partir do .proto
        tarefaId = tarefa.id.toLong()
        titulo = tarefa.titulo
    })
```

```java
// quarkus-tarefas: adaptadores/avisos/AvisadorGrpc.java
@GrpcClient("avisos")
AvisosServiceBlockingStub stub;
...
stub.withDeadlineAfter(1, TimeUnit.SECONDS).avisar(AvisarRequest.newBuilder()
        .setTarefaId(tarefa.id()).setTitulo(tarefa.titulo()).build());
```

Na rota, depois de criar: `avisador.avisar(criada)`.

Geração dos stubs:

- Ktor: plugin `com.google.protobuf` no `build.gradle.kts`, com `srcDir("../protos")` e os geradores `grpc` (Java) e `grpckt` (Kotlin, com corrotinas). O BOM `grpc-bom` alinha a versão de todos os módulos `io.grpc`.
- Quarkus: extensão `quarkus-grpc`; no `pom.xml`, a propriedade `quarkus.grpc.codegen.proto-directory` aponta para `../protos`. Endereço do serviço: `quarkus.grpc.clients.avisos.host` e `.port` no `application.properties`.

Testes novos: o cliente contra um servidor gRPC em memória (Ktor: `grpc-inprocess`; caso rápido, lento além do prazo e fora do ar), a rota com um `AvisadorEmMemoria`, e a regra de arquitetura, que agora também proíbe `io.grpc` e `com.google.protobuf` no domínio. Ktor: 16 testes; Quarkus: 13.

```bash
go run ./cmd/avisos                                  # terminal 1
cd ../ktor-tarefas && docker compose up -d && ./gradlew run      # terminal 2
curl -X POST localhost:8080/tarefas -H 'Content-Type: application/json' -d '{"titulo":"Estudar gRPC"}'
# no log da API: aviso da tarefa 5: [email=true, push=true, chat=true]
# pare o serviço Go e repita: 201 do mesmo jeito; no log, "não enviado: UNAVAILABLE"
```

📖 [gRPC — Kotlin quick start](https://grpc.io/docs/languages/kotlin/quickstart/) · [Quarkus — gRPC clients](https://quarkus.io/guides/grpc-service-consumption)

---

## Passo 17 — Tudo em containers: Dockerfiles, health checks e `docker compose`

- `avisos-go/Dockerfile`: compila com `golang:1.26` e copia só o binário estático para `distroless/static` (imagem de ~29 MB, sem shell).
- `ktor-tarefas/Dockerfile` e `quarkus-tarefas/Dockerfile`: compilam com o JDK 25 e rodam com o JRE 25. O contexto do build é `exemplos/`, porque os dois precisam de `protos/`.
- Health checks: `GET /health` no Ktor; `GET /q/health` no Quarkus (extensão `quarkus-smallrye-health`, que já verifica o banco); `avisos saude` no Go, um subcomando que chama o health check gRPC, porque a imagem não tem `curl`.
- `exemplos/compose.yaml`: banco, `avisos` e `api-ktor` (e `api-quarkus`, no perfil `quarkus`). Cada serviço espera os de que depende estarem saudáveis. Dentro da rede do compose, o endereço do serviço Go é o nome dele: `avisos:50051`.

```bash
cd exemplos
docker compose up --build                     # banco, avisos e a API Ktor (8080)
docker compose --profile quarkus up --build   # e também a API Quarkus (8081)
docker compose ps                             # todos healthy
```

📖 [Docker — multi-stage builds](https://docs.docker.com/build/building/multi-stage/) · [Compose — depends_on](https://docs.docker.com/reference/compose-file/services/#depends_on)

---

## Passo 18 — Implantação: imagens no GHCR, migração por comando e o Render

- `ktor-tarefas migrar`: aplica as migrações e termina. É o comando que uma plataforma roda antes de trocar a versão no ar. A porta vem de `PORT`, quando a plataforma a define.
- `implantacao/ci-e-imagens.yml`: modelo de workflow para o projeto do grupo. Em todo push e PR, `buf lint`, `go test` e `./gradlew test`; na `main`, publica as imagens em `ghcr.io/<usuário>/<repositório>/<serviço>`, com a etiqueta do commit e `latest`.
- `implantacao/render.yaml`: modelo de Blueprint. O banco fica no Neon; a connection string entra como segredo (`sync: false`), nunca no repositório.
- `mise.toml` ganhou `avisos:run`, `avisos:test`, `avisos:chamar`, `buf:lint`, `buf:gerar`, `tudo` e `tudo:parar`.

> Limites do plano gratuito do Render (consultados em 15/10/2026): serviço privado não tem instância gratuita, e um web service gratuito não recebe tráfego pela rede privada. Com gRPC entre a API e o Go, o serviço Go precisa do plano pago ou de outra plataforma com HTTP/2 de ponta a ponta. Ver os comentários no `render.yaml`.

📖 [GitHub — Publishing Docker images](https://docs.github.com/en/actions/tutorials/publish-packages/publish-docker-images) · [Render — Blueprint YAML](https://render.com/docs/blueprint-spec) · [Neon — Connect from any application](https://neon.com/docs/connect/connect-from-any-app)
