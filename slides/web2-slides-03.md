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

## Introdução a linguagem Go e ao Projeto de Exemplo

DIM0547 — Turma 01 · Aulas 03–04 (Sprint 0) · 31/08 e 02/09

Prof. Fernando · UFRN · 2026.2

---

# Roteiro da semana

**Segunda, 31/08 — Fundamentos de Go**

| Bloco | O que vemos |
|---|---|
| A arquitetura do MUSI | Componentes, como conversam, dois diagramas de sequência |
| Ktor × Quarkus | A mesma fronteira, nos dois stacks |
| Go essencial | Pacotes, structs, interfaces, erros e `context` |
| O esqueleto e os testes | `net/http`, JSON, e os casos de `contratos/` |

**Quarta, 02/09 — Monorepo e CI**

| Bloco | O que vemos |
|---|---|
| Estrutura | As camadas em pacotes; um repo, três stacks |
| Ambiente | Tasks do `mise` e Docker Compose |
| CI | O workflow que compila tudo a cada push |
| Oficina | Montar o esqueleto do monorepo do grupo |

---

# Onde paramos

Nas duas primeiras aulas fechamos a fronteira HTTP:

```
  Recurso, método, status e erro em problem+json
  Cache: Cache-Control, ETag e o 304
  REST: as restrições de Fielding, nível 2 de Richardson
  Clean Architecture: a regra de dependência
```

Hoje descemos uma camada: o serviço interno que a API chama. No projeto de referência ele é escrito em Go, e a escolha da linguagem é uma decisão de arquitetura.

---

<!-- _class: lead -->

# A arquitetura do MUSI

Os componentes, e como conversam.

---

# Os componentes

```
                     ┌──────────────┐
                     │  app (móvel) │  Compose · DIM0524
                     └──────┬───────┘
                    HTTP+JSON│
   ┌──────────────┐         ▼            um dos dois, à escolha do grupo
   │  contratos/  │◄─── api-ktor  OU  api-quarkus ───┐
   │ schema+proto │        (serviço principal)        │ chamada interna
   │ fonte da     │                                   ▼
   │ verdade      │◄──────────── services/busca (Go) ─┘
   └──────────────┘                     │
                          services/conciliacao (Go) ──► MusicBrainz
                                        │                (fora da leitura)
                                        ▼
                          acervo (memória → banco na Sprint 3)
```

Cada componente é um processo com um papel. `contratos/` não roda: descreve o domínio que todos seguem.

> Vocês escolhem um serviço principal (Ktor ou Quarkus) e pelo menos um serviço Go. Essa divisão é a decisão que a proposta registra.

---

# O papel de cada um

| Componente | Faz | Não faz |
|---|---|---|
| `app` | Interface móvel (DIM0524) | Regra de negócio pesada |
| `api-ktor` / `api-quarkus` | Fronteira pública: HTTP, validação, orquestração | Ficar acoplado a um serviço |
| `services/busca` | A busca no acervo | Falar com terceiros |
| `services/conciliacao` | Casar obras com o MusicBrainz, em lote | Rodar no caminho da leitura |
| `contratos/` | Schema JSON e `.proto` | Depender de linguagem |

> As duas APIs respondem a mesma coisa, contra o mesmo serviço Go. É a comparação Java × Kotlin, lado a lado, no mesmo produto.

---

# Sequência: uma busca

Do cliente ao acervo e de volta, uma leitura:

```
 Cliente        API (Ktor/Quarkus)      services/busca (Go)     Acervo
   │  GET /obras?dimensao=ritmo&valor=baiao                        │
   │──────────────►│                                               │
   │               │  POST /buscar  { "tipo":"tem", ... }          │
   │               │──────────────────────────►│                   │
   │               │                            │ filtra o acervo  │
   │               │                            │─────────────────►│
   │               │                            │◄─────────────────│
   │               │◄──────── [ obras ] ────────│                   │
   │◄─ 200 + Cache-Control/ETag ─│                                   │
```

A API traduz a query em uma árvore de filtro, delega ao Go e devolve com os cabeçalhos de cache da aula 02.

---

# Sequência: a conciliação

Fora do ciclo de leitura, na importação, com escolha humana:

```
 Curador      services/conciliacao      MusicBrainz          Acervo
   │  conciliar as obras sem mbid                              │
   │──────────────►│                                           │
   │               │  para cada obra (≈1 por segundo):         │
   │               │──── busca candidatos ────►│               │
   │               │◄──── [ candidatos ] ──────│               │
   │◄── pendências para revisão ──│                            │
   │  escolhe um candidato                                     │
   │──────────────►│  grava o mbid escolhido ─────────────────►│
```

O serviço só coleta e apresenta; quem decide o vínculo é uma pessoa. A gravação do MBID é o passo seguinte (issue aberta no MUSI).

---

<!-- _class: lead -->

# Ktor × Quarkus

A mesma fronteira, dois jeitos. Escolham um.

---

# O mesmo conceito, dois stacks

| Aspecto | `api-ktor` (Kotlin) | `api-quarkus` (Java) |
|---|---|---|
| Framework | Ktor | Quarkus |
| Injeção de dependência | Koin, em execução | CDI, na compilação |
| Rotas | código, numa árvore | anotações em classe |
| Cliente do Go | classe com `HttpClient` | interface declarativa |
| Domínio | vem de `shared/` | reescrito em `dominio/` |

Nenhum é o certo. A qualidade da justificativa é o que a rubrica avalia.

> O MUSI mantém os dois para servir às duas trilhas. Abrir os dois lado a lado no IDE é o melhor jeito de escolher.

---

# Rotas: código × anotações

```kotlin
// Ktor — a árvore de rotas é código, e se lê de cima para baixo
routing {
    get("/health") { call.respond(mapOf("status" to "UP")) }
    route("/obras") {
        get  { /* busca simples pela query string */ }
        post { /* busca composta pelo corpo */ }
    }
}
```

```java
// Quarkus — o recurso é uma classe anotada
@Path("/obras")
public class ObraResource {
    @GET  public Response porFaceta(...) { ... }
    @POST public List<ObraDto> buscar(FiltroDto dto) { ... }
}
```

> O mesmo endpoint, duas filosofias: a árvore explícita do Ktor, e as anotações que o Quarkus varre para montar as rotas e o OpenAPI.

---

# Injeção e cliente do Go

```kotlin
// Ktor + Koin: a dependência é resolvida em execução
val buscarObras by inject<BuscarObras>()
// cliente: uma classe que usa o HttpClient do Ktor
class BuscaHttp(val cliente: HttpClient) : FonteDeObras { ... }
```

```java
// Quarkus + CDI: injeção verificada na compilação
@Inject ObraResource(BuscarObras buscarObras) { ... }
// cliente: uma interface; o Quarkus gera a implementação
@RegisterRestClient interface ClienteBusca {
    @POST List<ObraDto> buscar(FiltroDto filtro);
}
```

> Do lado Kotlin, a chamada ao Go é escrita à mão com o mesmo `HttpClient` que o app usa. Do lado Java, você declara a interface e o Quarkus preenche o código.

---

# Onde estudar depois

| Guia | Foco |
|---|---|
| A Tour of Go · `go.dev/tour` | A linguagem, no navegador |
| Effective Go · `go.dev/doc/effective_go` | Idioma e convenções |
| Go by Example · `gobyexample.com` | Receitas curtas por tópico |
| `pkg.go.dev/net/http` | A biblioteca padrão de HTTP |

O *Tour* roda no navegador, sem instalar nada. É a ordem que vamos seguir.

---

<!-- _class: lead -->

# Parte 1

## Por que Go para este serviço

---

# A pergunta é para qual trabalho

Go foi desenhado no Google para serviços de rede: compila rápido, gera um binário único e trata concorrência como recurso de primeira classe.

| Característica de Go | O que compra para um serviço |
|---|---|
| Binário único, estático | Imagem Docker minúscula, sobe em segundos |
| Compilação rápida | Ciclo curto, CI barata |
| Concorrência com *goroutines* | Muitas requisições sem *threads* caras |
| Biblioteca padrão forte | HTTP e JSON sem dependência externa |
| Simplicidade deliberada | Pouca sintaxe, código legível por quem chega |

> No MUSI, o serviço em Go não tem nenhuma dependência externa — só a biblioteca padrão. Por isso nem existe `go.sum`.

---

# Onde Go se encaixa bem

| Bom caso para Go | Caso em que outra escolha cabe |
|---|---|
| Trabalho limitado por rede ou por taxa | Regras de negócio ricas e ramificadas |
| Concorrência alta, muitos pedidos | Domínio que se beneficia de união etiquetada |
| Serviço pequeno, foco único | Time que já domina outra pilha |

No projeto de referência, o candidato natural para Go é a conciliação com o MusicBrainz — limitada a uma requisição por segundo, assíncrona, tolerante a falha parcial, fora do ciclo da requisição.

> A decisão de "o que vai para Go" é sua, e precisa de justificativa, como a divisão de serviços da proposta.

---

# A conciliação com o MusicBrainz <span class="tag">ADR-0003</span>

Já implementada no MUSI. Casa a obra local com a identidade global do MusicBrainz.

| Peça | Onde, no IDE |
|---|---|
| Campos `mbid` e `mbidComposicao` na `Obra` | `dominio` — nas três linguagens |
| Porta `CatalogoExterno` (o domínio não conhece o MusicBrainz) | `services/conciliacao/conciliacao.go` |
| Cliente com limite de 1 req/s | `services/conciliacao/musicbrainz.go` |
| Testes sem rede (fake em memória) | `services/conciliacao/conciliacao_test.go` |

> É Go por característica: limitado por taxa, tolerante a falha parcial, fora do ciclo de leitura. A escolha entre candidatos é humana — o serviço só coleta. Ver o diagrama de sequência da conciliação.

---

# Uma diferença que vamos sentir hoje

Kotlin e Java 21 têm união etiquetada (`sealed`): o compilador verifica se um `when` tratou todos os casos.

Go não tem. Um `switch` sobre a árvore de filtro que esqueça um caso compila, e falha só ao rodar.

```
  Kotlin/Java   when exaustivo   → erro de compilação se faltar caso
  Go            switch + default → erro em tempo de execução
```

O que se ganha em troca: simplicidade e compilação veloz.

> As duas abordagens são válidas. Comparar essa escolha entre as três linguagens é conteúdo da disciplina — e aparece no domínio do MUSI, escrito nas três.

---

<!-- _class: lead -->

# Parte 2

## Go essencial

O suficiente para escrever um serviço. Abram `go.dev/tour`.

---

# O primeiro programa

```go
package main

import "fmt"

func main() {
    fmt.Println("Olá, turma")
}
```

| Elemento | Nota |
|---|---|
| `package main` | Todo arquivo pertence a um pacote |
| `import "fmt"` | Formatação e impressão, da biblioteca padrão |
| `func main()` | Ponto de entrada do executável |
| sem `;` | O compilador insere os pontos e vírgula |

> `gofmt` formata o código por você, e a CI recusa o que estiver fora do padrão. O estilo é decidido pela ferramenta, não em revisão.

---

# Variáveis e tipos

```go
var titulo string = "Ponteio"   // explícito
ano := 1967                     // curto, com tipo inferido
const limite = 20               // constante

var pronto bool                 // zero value: false
var total int                   // zero value: 0
var nome string                 // zero value: ""
```

Toda variável nasce com um *zero value* — não existe `null` para tipos básicos.

| Tipo | Exemplo |
|---|---|
| `int`, `int32`, `int64` | `42` |
| `float64` | `3.14` |
| `bool` | `true` |
| `string` | `"texto"` |

> `:=` só dentro de função; `var`/`const` valem também no nível do pacote. Declarar e não usar é erro de compilação: Go não deixa variável morta.

---

# Structs: os dados

```go
type Faceta struct {
    Dimensao string `json:"dimensao"`
    Valor    string `json:"valor"`
}

type Obra struct {
    ID      string   `json:"id"`
    Titulo  string   `json:"titulo"`
    Ano     int      `json:"ano"`
    Facetas []Faceta `json:"facetas"`
}
```

- Sem classe e sem herança: `struct` agrupa campos
- Maiúscula inicial significa exportado; minúscula, privado ao pacote
- A *tag* `json:"..."` diz como o campo vira JSON

> É o mesmo `Obra` do domínio Kotlin e Java. O nome do campo em JSON é o contrato; o nome em Go é interno.

---

# Funções, múltiplos retornos e erro

Go não tem exceção. O erro é um valor, devolvido junto com o resultado:

```go
func dividir(a, b int) (int, error) {
    if b == 0 {
        return 0, fmt.Errorf("divisão por zero")
    }
    return a / b, nil
}

resultado, err := dividir(10, 2)
if err != nil {
    return                       // trate aqui, sem ignorar
}
fmt.Println(resultado)
```

> `if err != nil` é o ritmo do Go. Verboso de propósito: o caminho de erro fica visível na leitura, em vez de escondido num `catch` distante.

---

# Slices e maps

```go
ritmos := []string{"ijexa", "baiao", "maracatu"}
ritmos = append(ritmos, "coco")
fmt.Println(len(ritmos))          // 4

anos := map[string]int{
    "Ponteio":    1967,
    "Asa Branca": 1947,
}
v, ok := anos["Ponteio"]          // v=1967, ok=true
_, existe := anos["Inexistente"]  // existe=false
```

| Tipo | Papel |
|---|---|
| `[]T` | Sequência de tamanho variável |
| `map[K]V` | Dicionário chave→valor |

> O segundo retorno do `map` (`ok`) diz se a chave existia. É como Go separa "ausente" de "zero".

---

# `for` é o único laço

```go
for i := 0; i < 3; i++ { fmt.Println(i) }   // clássico

for i, r := range ritmos {                   // índice e valor
    fmt.Println(i, r)
}

for _, o := range acervo {                   // só o valor
    fmt.Println(o.Titulo)
}

for total < 100 { total++ }                  // enquanto
```

`range` percorre slices, maps e canais. O `_` descarta o que não interessa.

> Não há `while` nem `foreach` no nome. Um `for`, várias formas.

---

# Interfaces: um método basta

Uma interface é um conjunto de métodos. Quem os tem, satisfaz — sem declarar que implementa.

```go
type Filtro interface {
    isFiltro()
}

type Tem struct{ Dimensao, Valor string }
type Ate struct{ Ano int }

func (Tem) isFiltro() {}
func (Ate) isFiltro() {}
```

O método não exportado `isFiltro()` fecha a hierarquia: só tipos deste pacote implementam `Filtro`. É o mais perto de `sealed` que Go oferece.

> Repare: `Tem` não diz `implements Filtro`. Ter o método já basta. É *duck typing* verificado na compilação.

---

# `switch` sobre o tipo, e o `default`

```go
func Satisfaz(o Obra, f Filtro) bool {
    switch t := f.(type) {
    case Tem:
        // ... t já é Tem aqui
    case Ate:
        return o.Ano <= t.Ano
    default:
        panic(fmt.Sprintf("filtro não tratado: %T", f))
    }
}
```

O `default` existe porque o compilador não verifica exaustividade. Nas versões Kotlin e Java ele não aparece — o `when` cobre todos os casos.

> Esse `panic` é a rede de segurança de uma decisão de linguagem. É exatamente o tipo de comparação que a prova cobra.

---

# `context`: prazo e cancelamento

Todo serviço precisa saber quando desistir. O `context.Context` carrega prazo e cancelamento, e é o primeiro parâmetro por convenção:

```go
func buscar(ctx context.Context, f Filtro) ([]Obra, error) {
    select {
    case <-ctx.Done():
        return nil, ctx.Err()      // cliente desistiu ou estourou o prazo
    default:
        // segue a busca
    }
}
```

- `context.Background()` — a raiz, no `main`
- `context.WithTimeout(ctx, 2*time.Second)` — deriva com prazo
- O `net/http` já entrega um `ctx` por requisição: `r.Context()`

> Sem `context`, uma chamada externa lenta trava a requisição inteira. Ele evita que o serviço fique esperando por algo que não vem.

---

<!-- _class: lead -->

# Parte 3

## O esqueleto no monorepo

---

# A estrutura de pastas

```
services/
├── go.mod                       módulo e versão do Go
├── cmd/
│   ├── servidor/main.go         o serviço de busca
│   └── conciliar/main.go        o lote de conciliação (ADR-0003)
├── busca/dominio/
│   ├── dominio.go               Obra, Filtro, Buscar
│   └── dominio_test.go          os testes do domínio
├── conciliacao/                 a porta e o cliente do MusicBrainz
└── arch-go.yml                  a regra de dependência do domínio
```

| Convenção | Significa |
|---|---|
| `cmd/<nome>` | Um executável por subpasta |
| pacote `dominio` | A camada de domínio, sem HTTP nem banco |
| `go.mod` | Nome do módulo e versão mínima do Go |

> `dominio` não importa HTTP. No MUSI isso é verificado pelo `arch-go` na CI — deixou de ser só um comentário.

---

# `go.mod`: o módulo

```go
module github.com/fmarquesfilho/musi/services

go 1.25
```

- O caminho do módulo é o prefixo de todos os `import` internos
- `go 1.25` é a versão mínima da linguagem
- Sem dependência externa, sem `go.sum`. `go build` funciona de imediato

```go
import "github.com/fmarquesfilho/musi/services/busca/dominio"
```

> Um módulo Go é a unidade de versionamento. No monorepo, os serviços Go vivem sob `services/`, com um `go.mod` só.

---

# Um servidor HTTP, sem framework

```go
func main() {
    mux := http.NewServeMux()
    mux.HandleFunc("/buscar", buscar)
    mux.HandleFunc("/health", func(w http.ResponseWriter, _ *http.Request) {
        w.Write([]byte(`{"status":"UP"}`))
    })

    srv := &http.Server{
        Addr:              ":9090",
        Handler:           mux,
        ReadHeaderTimeout: 5 * time.Second,
    }
    srv.ListenAndServe()
}
```

`net/http` é a biblioteca padrão. O roteador (`ServeMux`) e o servidor já vêm na linguagem.

> O `ReadHeaderTimeout` não é enfeite: sem um prazo, um cliente lento segura uma conexão indefinidamente. Servidor de verdade declara limites.

---

# Um handler

A assinatura é sempre a mesma: recebe onde escrever e o que foi pedido.

```go
func buscar(w http.ResponseWriter, r *http.Request) {
    if r.Method != http.MethodPost {
        problema(w, http.StatusMethodNotAllowed, "metodo", "Use POST.")
        return
    }

    var dto filtroDTO
    if err := json.NewDecoder(r.Body).Decode(&dto); err != nil {
        problema(w, http.StatusBadRequest, "json-invalido", "Corpo inválido.")
        return
    }

    w.Header().Set("Content-Type", "application/json")
    w.Header().Set("Cache-Control", "public, max-age=60")
    json.NewEncoder(w).Encode(dominio.Buscar(acervo, dto.paraDominio()))
}
```

> Reconhecem os cabeçalhos? `Cache-Control` é o assunto da aula 02, aqui do lado do servidor. O status certo e o `problem+json` também.

---

# JSON: decode e encode

```go
var dto filtroDTO
json.NewDecoder(r.Body).Decode(&dto)   // JSON → struct
json.NewEncoder(w).Encode(resultado)   // struct → JSON
```

O pacote `encoding/json` usa as *tags* do struct para mapear os nomes:

```go
type filtroDTO struct {
    Tipo     string      `json:"tipo"`
    Dimensao string      `json:"dimensao,omitempty"`
    Opcoes   []filtroDTO `json:"opcoes,omitempty"`
}
```

`omitempty` some com o campo quando ele está no zero value.

> Por que um `filtroDTO` separado do `Filtro` do domínio? Porque `Filtro` é interface, e `encoding/json` não sabe instanciar interface. É o mesmo motivo do DTO em Java e Kotlin.

---

# Erro no formato certo — RFC 9457

```go
func problema(w http.ResponseWriter, status int, tipo, detalhe string) {
    w.Header().Set("Content-Type", "application/problem+json")
    w.WriteHeader(status)
    json.NewEncoder(w).Encode(map[string]any{
        "type":   "https://musi.ufrn.br/erros/" + tipo,
        "status": status,
        "detail": detalhe,
    })
}
```

O mesmo `application/problem+json` da aula 02, agora emitido pelo serviço Go.

> Três serviços, três linguagens, um formato de erro. O contrato não muda com a implementação — é o que mantém o cliente simples.

---

# Como as camadas se mapeiam

| Camada (aula 02) | No serviço Go | Nunca contém |
|---|---|---|
| Domínio | pacote `busca/dominio` | HTTP, JSON, banco |
| Adaptador | o handler em `main.go` | regra de negócio |
| Infra | `http.Server`, `ServeMux` | regra de negócio |

O handler traduz HTTP em chamada ao domínio: decodifica, chama `dominio.Buscar`, codifica. Um `if` que decide algo sobre obras dentro do handler está no lugar errado.

> A mesma regra de dependência das três aulas. A linguagem muda; a arquitetura não.

---

<!-- _class: lead -->

# Parte 4

## Testes

---

# `go test` e tabela de casos

O teste mora ao lado do código, em `_test.go`. O idioma é a tabela de casos:

```go
func TestSatisfaz(t *testing.T) {
    casos := []struct {
        nome     string
        filtro   Filtro
        esperado bool
    }{
        {"tem a faceta", Tem{"ritmo", "ijexa"}, true},
        {"não tem", Tem{"ritmo", "baiao"}, false},
    }
    for _, c := range casos {
        t.Run(c.nome, func(t *testing.T) { /* verifica c.esperado */ })
    }
}
```

> Um subteste por caso, com nome. Quando quebra, a mensagem já diz qual caso, sem depender de assert mágico.

---

# Os casos vêm de `contratos/`, carregados

O MUSI foi além: os testes das três linguagens *carregam* os mesmos arquivos de `contratos/exemplos/`, em vez de copiá-los.

```
  contratos/exemplos/casos-de-busca.json   ◄── único ponto de verdade
        ▲            ▲            ▲
       Go          Java        Kotlin       carregam o mesmo arquivo
```

- Um caso novo no JSON alcança as três de uma vez
- Se Go, Java e Kotlin discordarem, o CI acusa — a concordância virou garantia, não convenção

> Abram `dominio_test.go`, `DominioTest.java` e `CasosCompartilhadosTest.kt`: três linguagens, o mesmo arquivo de casos. É o que impede os domínios de divergirem.

---

# O que a CI cobra no serviço Go

```
  gofmt -l .        → recusa código fora do padrão
  go vet ./...      → apanha erros comuns antes de rodar
  arch-go           → o domínio não importa HTTP/JSON/banco
  go test -race     → testes com detector de corrida
```

| Passo | Por que existe |
|---|---|
| `gofmt` | Estilo decidido pela ferramenta |
| `go vet` | Formatos de `Printf` errados, laços suspeitos |
| `arch-go` | A regra de dependência do domínio, verificada |
| `-race` | Concorrência com bug some sem o detector |

> `arch-go.yml` proíbe o domínio de depender de `net/http` e `encoding/json`. Adicionar esse import ao domínio deixa o CI vermelho.

---

# Rodando localmente

```bash
cd services

go run ./cmd/servidor      # sobe na porta 9090
go test ./...              # roda os testes
gofmt -l .                 # lista o que está fora do padrão
```

E a checagem completa, como no CI, via `mise`:

```bash
mise run test:go           # testes
mise run lint:go           # go vet + gofmt + arch-go
```

> `go run` compila e executa num passo. Para o dia a dia, é o comando que vocês mais vão usar.

---

<!-- _class: lead -->

# Parte 5

## Mãos à obra

---

# Oficina — 20 minutos

Com o serviço de referência aberto (`services/` do MUSI):

```
  1. Suba o serviço:   go run ./cmd/servidor
  2. Em outro terminal, chame /health com curl
  3. Faça um POST /buscar com um filtro `tem`
  4. Rode go test ./... e leia a saída
  5. Acrescente um caso a contratos/exemplos/casos-de-busca.json
     e veja o teste carregá-lo
```

Exemplo de corpo para o passo 3:

```json
{ "tipo": "tem", "dimensao": "ritmo", "valor": "baiao" }
```

> Para explorar sem `curl`, a pasta `http/` do MUSI traz coleções de Bruno, Postman e um `.http`.

---

# Erros que aparecem sempre

| Erro | O que acontece |
|---|---|
| Ignorar o `err` | O programa segue com dado inválido e quebra longe |
| Esquecer o `return` após responder erro | O handler continua e escreve a resposta duas vezes |
| Ponteiro no `Decode`: `&dto` | Sem o `&`, o JSON não preenche nada |
| `dominio` importando HTTP | Fura a regra; o `arch-go` reprova |
| Não rodar `gofmt` | A CI fica vermelha por formatação |

> O segundo é o mais comum: em Go, responder um erro não encerra o handler. O `return` é você quem escreve.

---

# Fecho de segunda

O serviço em Go responde a uma requisição e roda `go test` verde. Ele ainda vive sozinho.

Quarta juntamos as peças: um repositório, os três stacks, um comando que sobe tudo, e um CI que guarda o portão.

> Entre hoje e quarta: deixem o esqueleto do serviço Go compilando no repo do grupo, com uma rota `/health` e um teste.

---

<!-- _class: lead -->

# Quarta · 02/09

## O monorepo e a CI

Um repositório, três stacks, um pipeline.

---

# Por que um monorepo

Todos os serviços, os contratos e a documentação no mesmo repositório, versionados juntos.

| Ganho | Por quê |
|---|---|
| Mudança atômica | Alterar o contrato e os dois consumidores num PR só |
| Um histórico | O que mudou junto, aparece junto |
| Um CI | O portão de qualidade cobre tudo de uma vez |

> Um repo por serviço espalha uma mudança de contrato por três PRs em três lugares, e nada garante que cheguem juntos. Foi assim que a ADR-0003 tocou as três linguagens de uma vez.

---

# A estrutura do MUSI — abram no IDE

```
musi/
├── api-ktor/       Kotlin · Ktor · Koin     → serviço principal (opção A)
├── api-quarkus/    Java · Quarkus           → serviço principal (opção B)
├── services/       Go                        → busca e conciliação
├── contratos/      JSON Schema + .proto      → a fonte da verdade do domínio
├── shared/         domínio em Kotlin (KMP)   → usado por api-ktor e pelo app
├── http/           coleções para testar as APIs
├── docs/           ADRs, domínio, guias
├── mise.toml       versões e tasks
├── docker-compose.yml
└── .github/workflows/ci.yml
```

> Vocês escolhem um serviço principal (Ktor ou Quarkus), não os dois. O MUSI mantém ambos para servir às duas trilhas da disciplina.

---

# Um repo, ciclos de build diferentes

Nem tudo usa a mesma ferramenta de build, e tudo bem:

| Stack | Build | Onde |
|---|---|---|
| Kotlin (`api-ktor`, `shared`) | Gradle | `settings.gradle.kts` inclui os módulos |
| Java (`api-quarkus`) | Maven | ciclo próprio, `pom.xml` |
| Go (`services`) | Go modules | ciclo próprio, `go.mod` |

No MUSI, o `settings.gradle.kts` diz que `services/` e `api-quarkus/` ficam de fora, com ciclo próprio.

> Monorepo não quer dizer um build único. Quer dizer um repositório único, com cada componente construído pela ferramenta que faz sentido.

---

# As camadas viram pacotes

A regra de dependência da aula 02 vira estrutura de diretórios, a mesma nos três serviços:

```
  br/ufrn/musi/                    services/busca/
    dominio/       ← entidades       dominio/     ← Obra, Filtro, Buscar
    aplicacao/     ← casos de uso    ...
    adaptadores/   ← HTTP, busca
```

`dominio` não importa HTTP nem banco. No MUSI isso é verificado: `arch-go` no serviço Go, e o compilador do módulo `shared` no lado Kotlin.

> Abrir `services/busca/dominio/dominio.go` ao lado de `api-ktor/.../dominio` mostra a mesma regra em duas linguagens.

---

# Ambiente reproduzível: `mise`

O `mise.toml` fixa a versão de cada ferramenta e dá nome às tarefas, para o mesmo comando rodar na sua máquina e no CI.

```toml
[tools]
java = "temurin-25"
go   = "1.25"
gradle = "8.14"

[tasks.build]
run = [
  "./gradlew :api-ktor:build -x test",
  "cd services && go build ./...",
]
```

```bash
mise run build      # constrói os stacks
mise run test       # roda os testes
mise run ci         # reproduz o pipeline localmente
```

> Com a versão do Go e do Java declarada, "funciona na minha máquina" deixa de ser um problema: o ambiente está no arquivo, não combinado à parte.

---

# Subir tudo junto: Docker Compose

Um comando levanta os serviços e a rede entre eles:

```bash
docker compose up --build
```

```yaml
services:
  musi-api-ktor:   { ports: ["8080:8080"], depends_on: [musi-busca] }
  musi-busca:      { ports: ["9090:9090"] }   # o serviço Go
```

O `depends_on` e a rede do Compose deixam a api falar com o serviço Go por `http://musi-busca:9090`.

> É o mesmo `docker-compose.yml` do MUSI. Subir a stack inteira com um comando é o que permite testar a integração antes de entregar.

---

# CI: o portão de qualidade

O workflow roda a cada push e PR, e é o que a rubrica chama de "CI verde":

```
  push / PR ──► build dos stacks ──► testes ──► verde em main
                    │
                    └─ vermelho? o merge espera
```

No MUSI, `.github/workflows/ci.yml` tem um job por componente — `kotlin`, `java`, `go`, `contratos`, `docs` — e um job final que só passa se todos passarem.

> O build roda em todo componente a cada push, sem filtro de caminho: um filtro deixaria passar uma quebra num componente que você não tocou.

---

# Matriz ou job por componente?

| Estratégia | Quando |
|---|---|
| `matrix` | Os mesmos passos em várias versões/OS (ex.: Java 21 e 25) |
| Job por componente | Passos diferentes por stack (Gradle, Maven, Go) |

O MUSI usa job por componente, porque cada stack se constrói de um jeito. A entrega de vocês precisa do essencial: build dos dois stacks (principal e Go), verde em `main`.

> Comecem simples: um job que compila o serviço principal e um que compila o Go. Refinam nas próximas sprints.

---

# A entrega de vocês — o alvo de quarta

```
  [ ] Repositório público, com api/, services/, contratos/, docs/
  [ ] mise.toml com build e test; ambos passam localmente
  [ ] docker-compose.yml sobe o serviço principal e o Go
  [ ] CI verde em main, compilando os dois stacks
```

Vale metade da nota da Sprint 0 (estrutura do monorepo e CI passando). O resto é a proposta e o processo.

> Não precisa de deploy nem de banco ainda. Precisa compilar, subir com um comando, e o CI verde.

---

# Oficina — montar o esqueleto <span class="pill-blue">em grupo</span>

Partindo do MUSI como referência:

```
  1. Criar o repositório público e as pastas
  2. Copiar e adaptar o mise.toml: tasks build e test
  3. Escrever o docker-compose.yml com o serviço principal e o Go
  4. Criar .github/workflows/ci.yml: um job por stack
  5. Abrir o PR, ver o CI rodar, e só então dar merge
```

> Deixem o primeiro push vermelho de propósito e depois consertem. Ver o CI pegar o erro é metade do aprendizado.

---

# As tarefas da Sprint 0

O enunciado de cada entrega está em `docs/SPRINT-0-TAREFAS.md` — uma tarefa por cartão, com *pronto quando* e o peso na rubrica.

| Ajuste recente | Detalhe |
|---|---|
| Backlog: mínimo 5 histórias (antes 10) | ≥ 3 estimadas, todas priorizadas |
| `docs/proposta.md`: até 5 páginas (antes 3) | as 8 seções do guia |

> Criem uma issue por tarefa. O guia completo, com templates e exemplos, continua em `docs/SPRINT-0.md`.

---

# Próximos passos

**Até a entrega (11/09)**

- Monorepo público com a estrutura, `mise run build`/`test` passando
- `docker compose up` sobe o serviço principal e o Go
- CI verde nos dois stacks
- Proposta com a decisão da stack e a divisão com Go, e o backlog (≥ 5 histórias)

> 09/09 é encontro online, no horário da aula, para dúvidas sobre o pipeline e o ambiente.

---

# Referências da semana

**Go**

- *A Tour of Go* · `go.dev/tour` · *Effective Go* · `go.dev/doc/effective_go`
- *Go by Example* · `gobyexample.com` · `net/http`, `encoding/json`, `context` · `pkg.go.dev`

**Monorepo, ambiente e CI**

- `mise` · `mise.jdx.dev` · Docker Compose · `docs.docker.com/compose`
- GitHub Actions · `docs.github.com/actions` — *matrix* e *jobs*

**Disciplina e projeto de exemplo**

- `github.com/fmarquesfilho/web2-2026-2`
- MUSI · `github.com/fmarquesfilho/musi` — `services/`, `mise.toml`, `docker-compose.yml`, `.github/workflows/ci.yml`
