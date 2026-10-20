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

## Introdução a Go, com testes, e o domínio do serviço Go

DIM0547 — Turma 01 · Sprint 2 · aula em vídeo

Prof. Fernando · UFRN · 2026.2

---

# Sprint 2

| | Data |
|---|---|
| Conteúdo | hoje: introdução a Go e o Passo 13 · segunda, 26/10: passos 14 a 18 |
| Acompanhamento online | 09/11 e 16/11 |
| Prova escrita | **11/11 (quarta)**, no laboratório |
| *Daily meetings* online | 23 e 25/11 |
| **Entrega final** | **30/11, 23:59** |

| Critério | Peso | Aula |
|---|---|---|
| Microsserviço Go | 25% | hoje |
| Contrato Protobuf e integração gRPC | 25% | 26/10 |
| Sistema no ar | 25% | 26/10 |
| Ambiente e testes (inclui os testes que saíram da Sprint 1) | 15% | 26/10 |
| Documentação | 10% | — |

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

**Branch `aula`:** `./passo.sh 12` é onde a Sprint 1 terminou; hoje vamos até o 13.

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

# Roteiro de hoje

| Min | Bloco | Exemplo |
|---|---|---|
| 10 | A Sprint 2 e por que um serviço em Go | a figura do sistema |
| 10 | Ferramentas e tipos | `go mod`, `go run`, `go test` |
| 20 | Olá, Mundo com testes; funções como valores | `2-hello-world` |
| 15 | Métodos e interfaces | `3-metodos-interfaces` |
| 15 | Ponteiros e erros | `4-ponteiros-erros` |
| 10 | Injeção de dependência | `5-injecao-de-dependencia` |
| 20 | Passo 13: o domínio do serviço de avisos | `exemplos/avisos-go` |

Os exemplos de Go vêm do repositório do [Curso básico de Go](https://www.youtube.com/playlist?list=PLTr2C2xXIaIqO7Yr7FHohhgG_OgONpYIx), que segue o livro *Aprenda Go com Testes*:

```bash
git clone https://github.com/classrooms-fmarquesfilho/aprenda-go-com-testes.git
cd aprenda-go-com-testes/2-hello-world && go test -v
```

---

# Parte 1

## Go com testes

---

# As ferramentas

```bash
go mod init exemplo.com/hello     # cria o módulo: o go.mod, com o nome e a versão do Go
go run .                          # compila e roda o pacote main
go test -v                        # roda os testes (arquivos *_test.go), mostrando cada um
go test -run TestHello/região     # só os testes cujo nome casa com o padrão
gofmt -l .                        # lista os arquivos fora do formato (o formato é um só)
go vet ./...                      # aponta erros comuns que compilam
```

| Termo | O que é |
|---|---|
| módulo | o projeto: uma pasta com `go.mod`, que declara o nome e as dependências |
| pacote | uma pasta de arquivos `.go` com o mesmo `package`; `main` gera um executável |
| teste | uma função `TestXxx(t *testing.T)` num arquivo `_test.go`, ao lado do código |

O teste vem com a linguagem: não há biblioteca de teste para escolher nem configurar.

---

# Tipagem: estática, forte e com inferência

```go
var nome string = "Maria"     // tipo declarado
idade := 20                   // tipo inferido (int), só dentro de funções
const HourNotSpecified = -1   // constante

total := idade + 2.5          // erro de compilação: int + float64
total := float64(idade) + 2.5 // conversão sempre explícita
```

| | Go | Java | Python |
|---|---|---|---|
| Quando os tipos são verificados | compilação (estática) | compilação | execução (dinâmica) |
| Conversão implícita entre tipos | não (forte) | em alguns casos (`int` → `double`) | não, mas os tipos só aparecem na execução |
| Precisa escrever o tipo? | não, quando o compilador deduz | `var` desde o Java 10 | não |

> Inferência não é tipagem dinâmica: `idade := 20` tem tipo `int` para sempre.

📖 **Ref.** Curso básico de Go, `2-hello-world/slides/slides-tipos-em-go.md`

---

# Olá, Mundo, com o teste primeiro

O ciclo do livro: **vermelho** (um teste que falha) → **verde** (o mínimo para passar) → **refatorar** (melhorar com os testes verdes).

```go
func assertMessage(t testing.TB, got, want string) {
    t.Helper()                       // a falha aponta a linha de quem chamou
    if got != want {
        t.Errorf("got %q want %q", got, want)
    }
}

func TestHello(t *testing.T) {
    t.Run("sem opções", func(t *testing.T) {          // subteste com nome
        assertMessage(t, Hello(), "Olá, Mundo")
    })
    t.Run("nome e hora", func(t *testing.T) {
        assertMessage(t, Hello(WithName("Maria"), WithHour(9)), "Bom dia, Maria")
    })
    t.Run("hora 24 - inválida", func(t *testing.T) {   // os limites também são casos
        assertMessage(t, Hello(WithHour(24)), "Olá, Mundo")
    })
}
```

**Na tela:** `cd 2-hello-world && go test -v`; quebrar um caso e ler a mensagem; `go test -run 'TestHello/região'`.

---

# Funções como valores: *functional options*

```go
type HelloOption func(*HelloConfig)          // um tipo que é uma função

func WithName(name string) HelloOption {
    return func(cfg *HelloConfig) { cfg.Name = name }   // devolve uma função que lembra de name
}

func Hello(options ...HelloOption) string {  // variádica: zero ou mais opções
    cfg := HelloConfig{Hour: HourNotSpecified}
    for _, option := range options {
        option(&cfg)                         // cada opção altera a configuração
    }
    ...
}

Hello()
Hello(WithName("João"), WithHour(20), WithRegion(RegionRN))
```

- Go não tem sobrecarga nem parâmetros com valor padrão: as opções resolvem as duas coisas
- `WithName` devolve uma *closure*: uma função que guarda o `name` recebido
- `&cfg` passa o endereço da configuração; é por ele que a opção consegue alterá-la (mais adiante, ponteiros)

📖 **Ref.** Curso básico de Go, `3-metodos-interfaces/slides/polimorfismo-em-go.md` (Overloading × Functional Options)

---

# Métodos e interfaces: satisfação implícita

```go
type Forma interface {
    Area() float64
}

type Retangulo struct {
    Largura float64
    Altura  float64
}

func (r Retangulo) Area() float64 {          // um método: a função tem um receptor
    return r.Largura * r.Altura
}

type Circulo struct{ Raio float64 }

func (c Circulo) Area() float64 { return math.Pi * c.Raio * c.Raio }
```

```go
func checarArea(t testing.TB, forma Forma, want float64) { ... }   // aceita qualquer Forma

checarArea(t, Retangulo{Largura: 12, Altura: 6}, 72.0)
checarArea(t, Circulo{Raio: 10}, 314.1592653589793)
```

Nenhum `implements`: quem tem o método `Area() float64` é uma `Forma`, e o compilador confere.

📖 **Ref.** Curso básico de Go, `3-metodos-interfaces/slides/polimorfismo-em-go.md`

---

# Interfaces em Java e em Go

<div class="columns">
<div class="col">

**<span class="pill-red">Java</span>**

```java
interface Forma { double area(); }

class Retangulo implements Forma {
    public double area() { ... }
}
```

A classe declara que implementa. A interface precisa existir antes, e a classe depende dela.

</div>
<div class="col">

**<span class="pill-green">Go</span>**

```go
type Forma interface { Area() float64 }

type Retangulo struct{ ... }

func (r Retangulo) Area() float64 { ... }
```

O tipo não sabe da interface. Quem usa declara a interface de que precisa.

</div>
</div>

Consequência: em Go, a interface fica **com quem usa**, e costuma ter um ou dois métodos. É o que vamos ver em `io.Writer` e no serviço de avisos (`Canal`).

---

# Ponteiros: o receptor por valor recebe uma cópia

```go
type Carteira struct {
    saldo int                                     // minúscula: invisível fora do pacote
}

func (c *Carteira) Depositar(quantia int) {        // *Carteira: o método altera a carteira original
    c.saldo += quantia
}
```

O que acontece sem o `*`:

```go
func (c Carteira) Depositar(quantia int) { c.saldo += quantia }   // c é uma cópia

carteira := Carteira{}
carteira.Depositar(10)
carteira.Saldo()          // 0: o depósito foi feito na cópia
```

| Receptor | Quando usar |
|---|---|
| por valor `(c Carteira)` | o método só lê; o tipo é pequeno |
| por ponteiro `(c *Carteira)` | o método altera o estado, ou o tipo é grande |

**Na tela:** em `4-ponteiros-erros`, tirar o `*` de `Depositar` e rodar `go test -v`.

---

# Erros: valores, com tipo próprio

```go
type ErroSaldoInsuficiente struct{ Solicitado, Disponivel int }

func (e ErroSaldoInsuficiente) Error() string {   // quem tem Error() string é um error
    return fmt.Sprintf("não é possível sacar %d, pois o saldo disponível é %d", e.Solicitado, e.Disponivel)
}

func (c *Carteira) Sacar(quantia int) error {
    if quantia > c.saldo {
        return ErroSaldoInsuficiente{Solicitado: quantia, Disponivel: c.saldo}
    }
    c.saldo -= quantia
    return nil                                     // nil: deu certo
}
```

```go
if err := carteira.Sacar(10); err != nil {
    var falta ErroSaldoInsuficiente
    if errors.As(err, &falta) {                    // recupera o erro com os campos
        fmt.Println("faltam", falta.Solicitado-falta.Disponivel)
    }
}
```

- `error` é uma interface com um método, `Error() string`; o erro aparece na assinatura e é tratado por quem chamou

📖 **Ref.** [Go — errors](https://pkg.go.dev/errors)

---

# Injeção de dependência: `io.Writer`

```go
type Writer interface { Write(p []byte) (n int, err error) }   // io.Writer: um método só

func Saudar(escritor io.Writer, options ...HelloOption) {
    fmt.Fprintf(escritor, "%s", Hello(options...))
}
```

| Quem chama passa | E a saudação vai para |
|---|---|
| `os.Stdout` | o terminal |
| `&bytes.Buffer{}` | a memória, para o teste conferir |
| o `http.ResponseWriter` de um servidor HTTP | a resposta da requisição |

```go
func TestSaudar(t *testing.T) {
    buffer := bytes.Buffer{}
    Saudar(&buffer, WithName("Maria"))
    if buffer.String() != "Olá, Maria" { t.Errorf(...) }
}
```

Depender de uma interface, e não de um destino concreto, é o que torna a função testável: a ideia das portas da API de Tarefas.

📖 **Ref.** Curso básico de Go, `5-injecao-de-dependencia/slides/5-injecao-dependencia-slides.md`

---

# Parte 2 — Passo 13

## O domínio do serviço de avisos

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
- Como o `io.Writer`: uma interface pequena, declarada por quem usa, com um método que pode falhar

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

# Erros com nome, e a demonstração

```go
var ErrAvisoInvalido = errors.New("aviso inválido")           // um erro com nome

return nil, fmt.Errorf("%w: tarefa %d", ErrAvisoInvalido, id) // %w embrulha, sem perder a origem

if errors.Is(err, avisos.ErrAvisoInvalido) { ... }            // compara pela identidade, não pelo texto
```

| | Quando usar | Como conferir |
|---|---|---|
| erro com nome (`ErrAvisoInvalido`) | quem chama só precisa saber **qual** erro foi | `errors.Is(err, ErrAvisoInvalido)` |
| tipo de erro (`ErroSaldoInsuficiente`) | quem chama precisa de **dados** do erro | `errors.As(err, &alvo)` |

**Demonstração**

```bash
cd exemplos/avisos-go
go test ./...          # 3 testes do domínio
go run ./cmd/demo      # prazo de 1 s: 3 entregues em 400 ms; prazo de 100 ms: só o push
```

📖 **Ref.** [Go — errors](https://pkg.go.dev/errors)

---

# Na segunda, 26/10

| Passo | Assunto |
|---|---|
| 14 | O contrato: Protocol Buffers e Buf |
| 15 | O servidor gRPC em Go: status, prazo, health e reflection |
| 16 | A API chama o Go: cliente gRPC no Ktor e no Quarkus |
| 17 | Tudo em containers: Dockerfiles, health checks e `docker compose` |
| 18 | Implantação: imagens no GHCR, migração por comando, Render e Neon |

Até lá, no projeto do grupo:

- fazer os exercícios do curso de Go que faltarem (`2-hello-world` a `5-injecao-de-dependencia`)
- escolher a responsabilidade do serviço Go e escrever a justificativa (uma ADR)
- escrever o domínio do serviço, com testes, como no Passo 13

---

# Onde estudar depois

- [Curso básico de Go](https://www.youtube.com/playlist?list=PLTr2C2xXIaIqO7Yr7FHohhgG_OgONpYIx) (vídeos do professor) e o [repositório dos exemplos](https://github.com/classrooms-fmarquesfilho/aprenda-go-com-testes)
- [Aprenda Go com Testes](https://larien.gitbook.io/aprenda-go-com-testes/): os capítulos de Olá, Mundo; estruturas, métodos e interfaces; ponteiros e erros; injeção de dependência; [concorrência](https://larien.gitbook.io/aprenda-go-com-testes/primeiros-passos-com-go/concorrencia) e [select](https://larien.gitbook.io/aprenda-go-com-testes/primeiros-passos-com-go/select)
- [Learn Go with Tests — Context](https://quii.gitbook.io/learn-go-with-tests/go-fundamentals/context) (em inglês)
- [A Tour of Go](https://go.dev/tour/) · [Effective Go](https://go.dev/doc/effective_go)
- Leitura da sprint: `leituras/web2-s2.md`, capítulos 1 a 3
- Passo a passo: `exemplos/avisos-go/PASSOS.md` · branch `aula`, tag `passo-13`
