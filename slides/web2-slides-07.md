---
marp: true
theme: default
paginate: true
backgroundColor: #ffffff
color: #1a1a2e
style: |
  section {
    font-family: 'Calibri', sans-serif;
    padding: 36px 48px;
    font-size: 1.3em;
  }
  h1 {
    font-family: 'Consolas', monospace;
    color: #1a56db;
    font-size: 1.6em;
    margin-bottom: 0.3em;
    border-bottom: 2px solid #e5e7eb;
    padding-bottom: 0.2em;
  }
  h2 {
    font-family: 'Consolas', monospace;
    color: #374151;
    font-size: 1.2em;
    margin-bottom: 0.25em;
  }
  h3 { color: #6b7280; font-size: 0.9em; margin: 0.2em 0; }
  strong { color: #b45309; }
  em { color: #6b7280; }
  code {
    font-family: 'Consolas', monospace;
    background: #e5e7eb;
    color: #1e3a5f;
    padding: 0.08em 0.3em;
    border-radius: 3px;
    font-size: 1.00em;
  }
  pre {
    background: #f3f4f6 !important;
    border: 1px solid #d1d5db;
    border-left: 3px solid #1a56db;
    border-radius: 6px;
    padding: 0.7em 1em;
    margin: 0.4em 0;
  }
  pre code {
    background: transparent;
    color: #1e3a5f;
    font-size: 0.85em;
    padding: 0;
    line-height: 1.5;
  }
  table { font-size: 0.95em; width: 100%; border-collapse: collapse; }
  th {
    background: #e5e7eb;
    color: #1a56db;
    font-family: 'Consolas', monospace;
    padding: 0.35em 0.7em;
    border: 1px solid #d1d5db;
  }
  td { background: #ffffff; padding: 0.28em 0.7em; border: 1px solid #d1d5db; color: #1a1a2e; }
  tr:nth-child(even) td { background: #f9fafb; }
  ul { margin: 0.25em 0; padding-left: 1.3em; }
  li { margin: 0.18em 0; font-size: 0.88em; line-height: 1.4; }
  blockquote {
    border-left: 3px solid #1a56db;
    background: #eff6ff;
    padding: 0.4em 0.9em;
    margin: 0.5em 0;
    font-style: normal;
    color: #1e3a5f;
    border-radius: 0 5px 5px 0;
    font-size: 1.00em;
  }
  .columns { display: flex; gap: 1.8em; }
  .col { flex: 1; }
  .pill-red   { display:inline-block; background:#fee2e2; border:1.5px solid #dc2626; color:#dc2626; font-family:'Consolas',monospace; font-weight:bold; font-size:0.85em; padding:0.12em 0.6em; border-radius:20px; }
  .pill-green { display:inline-block; background:#dcfce7; border:1.5px solid #16a34a; color:#16a34a; font-family:'Consolas',monospace; font-weight:bold; font-size:0.85em; padding:0.12em 0.6em; border-radius:20px; }
  .pill-blue  { display:inline-block; background:#dbeafe; border:1.5px solid #1a56db; color:#1a56db; font-family:'Consolas',monospace; font-weight:bold; font-size:0.85em; padding:0.12em 0.6em; border-radius:20px; }
  section.lead { justify-content: center; }
  section.lead h1 { font-size: 2.4em; border-bottom: none; }
  section.lead h2 { font-size: 1.5em; color: #6b7280; }
  .tag { display:inline-block; background:#f3f4f6; border:1px solid #d1d5db; color:#374151; font-size:0.85em; padding:0.1em 0.5em; border-radius:4px; font-family:'Consolas',monospace; }


---

# Desenvolvimento de Sistemas Web II

## Um serviço em Go, o contrato em Protobuf e o servidor gRPC

DIM0547 — Turma 01 · Sprint 2 · aula de 19/10

Prof. Fernando · UFRN · 2026.2

---

# A Sprint 2, a última

| | Data |
|---|---|
| Conteúdo | hoje (passos 13 a 15) e quarta, 21/10 (passos 16 a 18) |
| Acompanhamento online | 26/10, 09/11 e 16/11 |
| Prova escrita | **11/11 (quarta)**, em laboratório |
| *Daily meetings* online | 23 e 25/11 (presencial, se preferirem, em 18/11) |
| **Entrega final** | **30/11, 23:59** |

| Critério | Peso | Aula |
|---|---|---|
| Microsserviço Go | 25% | hoje |
| Contrato Protobuf e integração gRPC | 25% | hoje e quarta |
| Sistema no ar | 25% | quarta |
| Ambiente e testes (inclui os testes que saíram da Sprint 1) | 15% | quarta |
| Prontidão do repositório | 10% | — |

---

# Onde vamos chegar

```
                    ┌──────────────────────┐   gRPC, com prazo    ┌──────────────────────┐
  cliente HTTP ───► │  API (Ktor/Quarkus)  │ ───────────────────► │  serviço Go: avisos  │
                    │  REST + JSON, :8080  │  AvisosService/Avisar│  :50051              │
                    └──────────┬───────────┘                      └──────────────────────┘
                               │ JDBC                                 3 canais ao mesmo tempo
                               ▼                                      (e-mail, push, chat)
                         PostgreSQL (Neon)
```

- Ao criar uma tarefa, a API pede ao serviço Go que avise por três canais
- O contrato entre os dois é um arquivo `.proto`, versionado no repositório
- Se o serviço Go cair, a tarefa continua sendo criada

**Branch `aula`:** `./passo.sh 12` é onde a Sprint 1 terminou; hoje vamos até o 15.

---

# Por que um serviço à parte, e em Go

A rubrica pede um serviço com responsabilidade **justificada**. Bons motivos:

| Motivo | No exemplo |
|---|---|
| Trabalho de E/S que espera por terceiros | cada canal espera um servidor externo |
| Concorrência | os três canais ao mesmo tempo, uma goroutine por canal |
| Falha isolada | se o serviço cair, a API continua respondendo |
| Ritmo próprio de implantação | o serviço muda sem republicar a API |

Maus motivos: "microsserviço é moderno"; separar um CRUD em dois serviços que dividem o mesmo banco.

> Go cabe bem aqui: goroutines baratas, binário estático, imagem de container pequena e partida rápida.

📖 **Ref.** [Effective Go](https://go.dev/doc/effective_go)

---

# Passo 13

## O serviço Go: pacotes, interfaces, erros e `context`

---

# Go para quem vem de Kotlin e Java

| Assunto | Kotlin / Java | Go |
|---|---|---|
| Unidade de código | classe, pacote | pacote (uma pasta); o módulo é o `go.mod` |
| Visibilidade | `private`, `public` | inicial maiúscula é exportada (`Avisar`); minúscula, não (`canais`) |
| Objetos | classes, herança | `struct` com métodos; sem herança |
| Interfaces | `implements` explícito | satisfeitas implicitamente: quem tem os métodos implementa |
| Erros | exceções | valores: `func f() (T, error)`, e quem chama confere `if err != nil` |
| Concorrência | threads, corrotinas | goroutines (`go f()`) e canais |
| Nulo | `null` | o valor zero de cada tipo; `nil` para ponteiros, interfaces, fatias |

```go
func (av *Avisador) Avisar(ctx context.Context, aviso Aviso) ([]Entrega, error)
//    └ receptor (o "this")  └ prazo e cancelamento         └ dois resultados: valor e erro
```

Para começar em Go: o [Curso básico de Go](https://www.youtube.com/playlist?list=PLTr2C2xXIaIqO7Yr7FHohhgG_OgONpYIx), em vídeo, com os exercícios do *Aprenda Go com Testes*.

---

# O domínio: uma interface pequena e o Avisador

```go
// Canal é qualquer meio de entrega: e-mail, push, chat. A interface é pequena e
// declarada aqui, do lado de quem usa, como é costume em Go.
type Canal interface {
    Nome() string
    Enviar(ctx context.Context, aviso Aviso) error
}

type Avisador struct {
    canais []Canal
}

func NovoAvisador(canais ...Canal) *Avisador {        // "construtor": uma função comum
    return &Avisador{canais: canais}
}
```

- `CanalSimulado` tem `Nome()` e `Enviar(...)`: é um `Canal`, sem declarar nada
- O pacote `avisos` é o domínio: não importa gRPC, HTTP nem banco
- Em Go, interfaces costumam ter um ou dois métodos, e ficam com quem as usa

---

# Concorrência: uma goroutine por canal

```go
func (av *Avisador) Avisar(ctx context.Context, aviso Aviso) ([]Entrega, error) {
    if aviso.TarefaID <= 0 || strings.TrimSpace(aviso.Titulo) == "" {
        return nil, fmt.Errorf("%w: tarefa %d, título %q", ErrAvisoInvalido, aviso.TarefaID, aviso.Titulo)
    }
    entregas := make([]Entrega, len(av.canais))
    var wg sync.WaitGroup
    for i, canal := range av.canais {
        wg.Go(func() {              // dispara uma goroutine e a registra no WaitGroup
            entregas[i] = Entrega{Canal: canal.Nome(), Erro: canal.Enviar(ctx, aviso)}
        })
    }
    wg.Wait()                       // espera todas terminarem
    return entregas, nil
}
```

- Cada goroutine escreve só na sua posição da fatia: não há disputa
- Canais de 400, 50 e 150 ms: em sequência, 600 ms; ao mesmo tempo, **400 ms**, o do mais lento

📖 **Ref.** [Go — sync.WaitGroup](https://pkg.go.dev/sync#WaitGroup)

---

# `context`: prazo e cancelamento

```go
func (c CanalSimulado) Enviar(ctx context.Context, aviso Aviso) error {
    select {                                  // espera o que acontecer primeiro
    case <-time.After(c.demora):              // o canal terminou
        return nil
    case <-ctx.Done():                        // o prazo venceu ou alguém cancelou
        return fmt.Errorf("canal %s: %w", c.nome, ctx.Err())
    }
}
```

```go
ctx, cancelar := context.WithTimeout(context.Background(), 100*time.Millisecond)
defer cancelar()                              // defer: roda ao sair da função
entregas, err := av.Avisar(ctx, aviso)       // push (50 ms) entrega; email e chat desistem
```

- Toda função que pode esperar recebe `ctx context.Context` como **primeiro** parâmetro
- O prazo atravessa as camadas: quem chama decide quanto está disposto a esperar

📖 **Ref.** [Go — context](https://pkg.go.dev/context)

---

# Erros como valores

```go
var ErrAvisoInvalido = errors.New("aviso inválido")           // um erro com nome

return nil, fmt.Errorf("%w: tarefa %d", ErrAvisoInvalido, id) // %w embrulha, sem perder a origem

if errors.Is(err, avisos.ErrAvisoInvalido) { ... }            // compara pela identidade, não pelo texto
```

| Exceções (Kotlin, Java) | Erros como valores (Go) |
|---|---|
| saem da função por um caminho escondido | aparecem na assinatura: `(T, error)` |
| quem chama pode esquecer de tratar | quem chama recebe o erro e decide ali |
| `catch (e: Tipo)` | `errors.Is(err, ErrX)` ou `errors.As(err, &alvo)` |

**Demonstração**

```bash
cd exemplos/avisos-go
go test ./...          # 3 testes do domínio
go run ./cmd/demo      # prazo de 1 s: 3 entregues em 400 ms; prazo de 100 ms: só o push
```

📖 **Ref.** [Go — errors](https://pkg.go.dev/errors)

---

# Passo 14

## O contrato: Protocol Buffers e Buf

---

# O contrato `.proto`

```protobuf
syntax = "proto3";

package tarefas.avisos.v1;                 // a versão faz parte do nome

option go_package = "avisos/gen/tarefas/avisos/v1;avisosv1";
option java_package = "br.ufrn.exemplo.tarefas.avisos.v1";
option java_multiple_files = true;

// Envia avisos sobre tarefas.
service AvisosService {
  // Avisa, em todos os canais ao mesmo tempo, que uma tarefa foi criada. ...
  rpc Avisar(AvisarRequest) returns (AvisarResponse);
}

// O pedido de aviso.
message AvisarRequest {
  int64 tarefa_id = 1;                     // o número identifica o campo no binário
  string titulo = 2;
}
```

- Um arquivo, três linguagens: o Go, o Kotlin e o Java geram o código a partir dele
- Números de campo nunca mudam e nunca são reaproveitados: é o que mantém a compatibilidade
- Mudança incompatível vira `v2`, ao lado da `v1`

📖 **Ref.** [Protocol Buffers — proto3](https://protobuf.dev/programming-guides/proto3/)

---

# Buf: conferir e gerar

```yaml
# protos/buf.yaml                         # protos/buf.gen.yaml
version: v2                               version: v2
lint:                                     plugins:
  use:                                      - remote: buf.build/protocolbuffers/go:v1.36.12
    - STANDARD                                out: ../avisos-go/gen
    - COMMENTS   # tudo comentado             opt: paths=source_relative
                                            - remote: buf.build/grpc/go:v1.6.0
                                              out: ../avisos-go/gen
                                              opt: paths=source_relative
```

```bash
cd exemplos/protos
buf lint          # sem saída: o contrato segue as regras (nomes, pacote com versão, comentários)
buf generate      # escreve avisos-go/gen/tarefas/avisos/v1/*.pb.go
```

- Plugins remotos: nada de `protoc` instalado na máquina
- `buf lint` vai para o CI: a rubrica pede o contrato verde lá
- Os stubs de Kotlin e Java são gerados pelo Gradle e pelo Quarkus, no build

📖 **Ref.** [Buf — lint rules](https://buf.build/docs/lint/rules/)

---

# Passo 15

## O servidor gRPC: status, prazo, health e reflection

---

# O adaptador: do contrato para o domínio

```go
type Servidor struct {
    avisosv1.UnimplementedAvisosServiceServer   // métodos novos no contrato não quebram o build
    avisador *avisos.Avisador
}

func (s *Servidor) Avisar(ctx context.Context, req *avisosv1.AvisarRequest) (*avisosv1.AvisarResponse, error) {
    entregas, err := s.avisador.Avisar(ctx, avisos.Aviso{TarefaID: req.GetTarefaId(), Titulo: req.GetTitulo()})
    if errors.Is(err, avisos.ErrAvisoInvalido) {
        return nil, status.Error(codes.InvalidArgument, err.Error())   // erro do domínio → código gRPC
    }
    ...                                                                 // entregas → mensagens do contrato
}
```

| Situação | Código gRPC | Equivalente HTTP |
|---|---|---|
| entrada inválida | `INVALID_ARGUMENT` | 400 |
| prazo vencido | `DEADLINE_EXCEEDED` | 504 |
| serviço fora do ar | `UNAVAILABLE` | 503 |
| erro interno | `INTERNAL` | 500 |

📖 **Ref.** [gRPC — Status codes](https://grpc.io/docs/guides/status-codes/)

---

# O prazo atravessa a rede

```go
// O deadline do cliente chega no ctx do servidor. O servidor encerra os envios 50 ms
// antes, para que a resposta, com as entregas parciais, ainda chegue a tempo.
if prazo, ok := ctx.Deadline(); ok {
    var cancelar context.CancelFunc
    ctx, cancelar = context.WithDeadline(ctx, prazo.Add(-folga))
    defer cancelar()
}
```

```
cliente com prazo de 300 ms
  └─► servidor encerra em 250 ms ─► push (50 ms) e chat (150 ms) entregues; email (400 ms) não
        └─► resposta chega antes dos 300 ms: o cliente recebe a lista
```

- Sem a folga, o servidor esperaria até o fim e o cliente veria só `DEADLINE_EXCEEDED`
- O prazo viaja no cabeçalho `grpc-timeout`: dá para vê-lo no log do cliente

📖 **Ref.** [gRPC — Deadlines](https://grpc.io/docs/guides/deadlines/)

---

# O servidor e a demonstração

`cmd/avisos/main.go`: registra o serviço, o *health check* padrão e o *reflection*; encerra com `GracefulStop` ao receber SIGTERM.

```bash
go run ./cmd/avisos                                   # escuta na 50051

grpcurl -plaintext localhost:50051 list               # o reflection lista os serviços
grpcurl -plaintext -d '{"tarefa_id": 7, "titulo": "Estudar gRPC"}' \
  localhost:50051 tarefas.avisos.v1.AvisosService/Avisar
grpcurl -plaintext -max-time 0.3 -d '{"tarefa_id": 7, "titulo": "Estudar gRPC"}' \
  localhost:50051 tarefas.avisos.v1.AvisosService/Avisar      # email volta não entregue
grpcurl -plaintext -d '{"tarefa_id": 7, "titulo": " "}' \
  localhost:50051 tarefas.avisos.v1.AvisosService/Avisar      # Code: InvalidArgument
grpcurl -plaintext localhost:50051 grpc.health.v1.Health/Check   # SERVING
```

Testes do servidor: `bufconn`, uma conexão em memória que passa pela pilha gRPC inteira, sem abrir porta.

---

# gRPC ou REST?

| | REST com JSON | gRPC com Protobuf |
|---|---|---|
| Contrato | OpenAPI, gerado ou escrito | `.proto`, a fonte de onde o código é gerado |
| Formato | texto (JSON), legível | binário, compacto |
| Transporte | HTTP/1.1 ou 2 | HTTP/2 |
| Erros | status HTTP e *problem details* | códigos gRPC |
| Bom para | APIs públicas, navegador, apps | serviço falando com serviço, dentro do sistema |

> No projeto, as duas convivem: REST na API que o cliente usa; gRPC entre a API e o serviço Go.

📖 **Ref.** [gRPC — Introduction](https://grpc.io/docs/what-is-grpc/introduction/)

---

# Na quarta, 21/10

| Passo | Assunto |
|---|---|
| 16 | A API chama o Go: cliente gRPC no Ktor e no Quarkus, com prazo e degradação |
| 17 | Tudo em containers: Dockerfiles, health checks e `docker compose` |
| 18 | Implantação: imagens no GHCR, migração por comando, Render e Neon |

Até lá, no projeto do grupo:

- escolham a responsabilidade do serviço Go e escrevam a justificativa (uma ADR)
- escrevam o `.proto` e deixem o `buf lint` verde
- comecem o servidor com um teste

---

# Onde estudar depois

- Leitura da sprint: `leituras/web2-s2.md`, capítulos 1 a 5
- [Curso básico de Go](https://www.youtube.com/playlist?list=PLTr2C2xXIaIqO7Yr7FHohhgG_OgONpYIx) (vídeos do professor) e o [repositório dos exercícios](https://github.com/classrooms-fmarquesfilho/aprenda-go-com-testes): Olá, mundo; métodos e interfaces; ponteiros e erros; injeção de dependência
- [Aprenda Go com Testes](https://larien.gitbook.io/aprenda-go-com-testes/): [Concorrência](https://larien.gitbook.io/aprenda-go-com-testes/primeiros-passos-com-go/concorrencia) · [Select](https://larien.gitbook.io/aprenda-go-com-testes/primeiros-passos-com-go/select) · [Context](https://quii.gitbook.io/learn-go-with-tests/go-fundamentals/context) (em inglês)
- Passos: `exemplos/avisos-go/PASSOS.md` · branch `aula`, tags `passo-13` a `passo-15`
- [A Tour of Go](https://go.dev/tour/) · [Effective Go](https://go.dev/doc/effective_go)
- [Go — context](https://pkg.go.dev/context) · [Go — errors](https://pkg.go.dev/errors)
- [Protocol Buffers — proto3](https://protobuf.dev/programming-guides/proto3/) · [Buf — lint rules](https://buf.build/docs/lint/rules/)
- [gRPC — Deadlines](https://grpc.io/docs/guides/deadlines/) · [Status codes](https://grpc.io/docs/guides/status-codes/) · [Health checking](https://grpc.io/docs/guides/health-checking/)
