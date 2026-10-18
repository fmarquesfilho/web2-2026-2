# Tarefas da Sprint 2 — DIM0547

Estes são os enunciados das tarefas da Sprint 2, prontos para virar cartões no GitHub Projects. Cada um tem um objetivo, o que fazer, e o *pronto quando* alinhado à rubrica.

O **como** está em [SPRINT-2.md](SPRINT-2.md) e na leitura da sprint ([`leituras/web2-s2.md`](../leituras/web2-s2.md)): as tarefas apontam para a seção certa em vez de repeti-la. Os pesos vêm de [RUBRICAS.md](RUBRICAS.md#sprint-2). Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral): **30/11, 23:59**.

| # | Tarefa | Critério da rubrica |
|---|---|---|
| T1 | Quebrar o trabalho em cartões da sprint | Atividade no repositório |
| T2 | Escolher e justificar a responsabilidade do serviço Go | Microsserviço Go (25%) · Documentação (10%) |
| T3 | Escrever o domínio do serviço Go, com testes | Microsserviço Go (25%) |
| T4 | Escrever o contrato `.proto` e gerar os stubs | Contrato Protobuf e integração gRPC (25%) |
| T5 | Expor o serviço por gRPC | Contrato Protobuf e integração gRPC (25%) |
| T6 | Chamar o serviço Go a partir da API | Contrato Protobuf e integração gRPC (25%) |
| T7 | Escrever os testes que saíram da Sprint 1 | Ambiente e testes (15%) |
| T8 | Subir tudo com `docker compose` | Ambiente e testes (15%) |
| T9 | Ampliar o CI | Contrato (25%) · Ambiente e testes (15%) |
| T10 | Pôr a API no ar com banco gerenciado | Sistema no ar (25%) |
| T11 | Documentar o sistema | Documentação (10%) |
| T12 | Gravar o vídeo de 5 minutos | Comunicação |

> Exemplo de referência: `exemplos/avisos-go/` deste repositório, passos 13 a 18 do [`PASSOS.md`](../exemplos/avisos-go/PASSOS.md). Projeto de referência: `github.com/fmarquesfilho/musi`, pastas `services/` e `contratos/`.

---

## T1 — Quebrar o trabalho em cartões da sprint

**Objetivo.** Planejar a sprint no quadro e dar um dono a cada cartão.

**O que fazer.**
- [ ] Criar os cartões das tarefas T2 a T12 que se aplicam ao produto
- [ ] Atribuir um responsável a cada cartão e ligá-lo ao PR que o resolve
- [ ] Revisar o quadro no encontro de acompanhamento de cada semana

**Pronto quando.** O quadro mostra o que está na sprint, com dono, e cada cartão fechado aponta para um PR integrado.

**Referência.** [AVALIACAO.md](AVALIACAO.md#31-saúde-do-repositório).

---

## T2 — Escolher e justificar a responsabilidade do serviço Go

**Objetivo.** Ter um serviço com motivo para existir fora da API.

**O que fazer.**
- [ ] Listar, no domínio do produto, trabalho de E/S que espera por terceiros, concorrência, processamento ou agendamento
- [ ] Escolher uma responsabilidade e escrever a ADR: o que o serviço faz, por que fica fora da API, o que aconteceria se ficasse dentro
- [ ] Confirmar que o serviço não divide tabelas com a API

**Pronto quando.** A ADR está em `docs/decisoes/` e a responsabilidade cabe numa frase.

**Referência.** [SPRINT-2.md](SPRINT-2.md) *Microsserviço Go* · `web2-s2.md`, capítulo 1.

---

## T3 — Escrever o domínio do serviço Go, com testes

**Objetivo.** A regra do serviço em Go puro, testada, sem rede.

**O que fazer.**
- [ ] Criar o módulo (`go.mod`) e o pacote de domínio
- [ ] Declarar interfaces pequenas para o que o domínio usa de fora
- [ ] Usar `context.Context` como primeiro parâmetro de toda função que pode esperar, e respeitar o prazo
- [ ] Declarar os erros com nome e usar `%w` ao acrescentar contexto
- [ ] Escrever os testes com `go test`, inclusive um que verifica o prazo

**Pronto quando.** `go test ./...` passa, e o domínio não importa gRPC nem HTTP.

**Referência.** `web2-s2.md`, capítulos 2 e 3 · exemplo, Passo 13 · [Curso básico de Go](https://www.youtube.com/playlist?list=PLTr2C2xXIaIqO7Yr7FHohhgG_OgONpYIx).

---

## T4 — Escrever o contrato `.proto` e gerar os stubs

**Objetivo.** Um contrato versionado, de onde as duas pontas geram o código.

**O que fazer.**
- [ ] Criar `protos/` com `buf.yaml` (regras `STANDARD` e `COMMENTS`) e `buf.gen.yaml`
- [ ] Escrever o serviço e as mensagens no pacote `<produto>.<serviço>.v1`, com todos os elementos comentados
- [ ] Rodar `buf lint` até ficar sem saída
- [ ] Gerar os stubs Go com `buf generate`

**Pronto quando.** `buf lint` passa, e os stubs gerados compilam.

**Referência.** `web2-s2.md`, capítulo 4 · exemplo, Passo 14.

---

## T5 — Expor o serviço por gRPC

**Objetivo.** O servidor gRPC do serviço Go, sobre o domínio.

**O que fazer.**
- [ ] Escrever o adaptador que traduz entre o contrato e o domínio, com o tipo `Unimplemented...` embutido
- [ ] Traduzir os erros do domínio em códigos gRPC (`INVALID_ARGUMENT`, `NOT_FOUND`...)
- [ ] Registrar o health check e o reflection; encerrar com `GracefulStop` no SIGTERM
- [ ] Testar o adaptador pela pilha gRPC (`bufconn`)
- [ ] Conferir com o `grpcurl`

**Pronto quando.** O `grpcurl` lista o serviço, a chamada responde, e o health check devolve `SERVING`.

**Referência.** `web2-s2.md`, capítulo 5 · exemplo, Passo 15.

---

## T6 — Chamar o serviço Go a partir da API

**Objetivo.** A API usa o serviço Go sem depender dele para continuar de pé.

**O que fazer.**
- [ ] Declarar a porta no domínio da API, sem nada de gRPC
- [ ] Gerar os stubs no build (plugin `com.google.protobuf` no Ktor; `quarkus-grpc` no Quarkus) a partir de `protos/`
- [ ] Escrever o adaptador: toda chamada com prazo; códigos de status tratados
- [ ] Decidir e implementar o que a API faz quando o Go falha
- [ ] Testar o adaptador contra um servidor gRPC (em memória ou num container)
- [ ] Acrescentar `io.grpc` e o pacote gerado às dependências proibidas no teste de arquitetura

**Pronto quando.** Com o Go no ar, a chamada acontece; com o Go parado, a API responde dentro do prazo; o teste do fluxo passa.

**Referência.** `web2-s2.md`, capítulo 6 · exemplo, Passo 16.

---

## T7 — Escrever os testes que saíram da Sprint 1

**Objetivo.** A suíte do serviço principal completa: unitários, integração e arquitetura.

**O que fazer.**
- [ ] Unitários do domínio e dos casos de uso, sem banco, com repositórios falsos ou em memória
- [ ] Integração com PostgreSQL: Testcontainers (Ktor) ou `@QuarkusTest` com Dev Services (Quarkus), cobrindo CRUD, relacionamento e paginação
- [ ] Teste de arquitetura (ArchUnit ou Konsist), que falha quando o domínio importa framework, banco ou gRPC; conferir uma vez, de propósito
- [ ] Rodar a suíte com `mise run test` (ou o comando do grupo)

**Pronto quando.** Os três tipos de teste existem e passam localmente, sem mudança de configuração.

**Referência.** `web2-s1-pte2.md`, capítulos 7 e 11 · exemplos, passos 8 e 12.

---

## T8 — Subir tudo com `docker compose`

**Objetivo.** O sistema inteiro sobe com um comando, numa máquina que só tem Docker.

**O que fazer.**
- [ ] Escrever o `Dockerfile` do serviço Go (dois estágios; imagem final mínima)
- [ ] Escrever o `Dockerfile` da API (dois estágios)
- [ ] Expor um health check em cada serviço
- [ ] Escrever o `compose.yaml` com banco, Go e API, com `healthcheck` e `depends_on: service_healthy`, e a configuração por variável de ambiente

**Pronto quando.** Num clone limpo, `docker compose up --build` sobe tudo, e `docker compose ps` mostra os serviços saudáveis.

**Referência.** `web2-s2.md`, capítulo 7 · exemplo, Passo 17.

---

## T9 — Ampliar o CI

**Objetivo.** O CI confere tudo o que a entrega promete.

**O que fazer.**
- [ ] Acrescentar `buf lint` (por exemplo, com `bufbuild/buf-action`)
- [ ] Acrescentar `go test ./...`
- [ ] Rodar a suíte completa do serviço principal, inclusive os testes com banco
- [ ] (Opcional) publicar as imagens no GHCR na branch principal

**Pronto quando.** Em push e PR, o CI roda `buf lint`, os testes do Go e os do serviço principal, e fica verde na branch principal.

**Referência.** `exemplos/implantacao/ci-e-imagens.yml` · `web2-s2.md`, capítulo 8.

---

## T10 — Pôr a API no ar com banco gerenciado

**Objetivo.** A API acessível pela internet, com o banco no Neon.

**O que fazer.**
- [ ] Criar o banco no Neon e guardar a connection string como segredo da plataforma
- [ ] Publicar a API numa plataforma de container (Render ou outra), com health check
- [ ] Aplicar as migrações por task ou pelo CI
- [ ] Conferir que a API responde sem o serviço Go
- [ ] Pôr a URL pública no README
- [ ] Escrever a ADR da implantação

**Pronto quando.** A URL do README responde, e o repositório não contém nenhum segredo.

**Referência.** [SPRINT-2.md](SPRINT-2.md) *Sistema no ar* · `web2-s2.md`, capítulo 8 · exemplo, Passo 18.

---

## T11 — Documentar o sistema

**Objetivo.** Qualquer pessoa entende, roda e avalia o sistema só com o repositório.

**O que fazer.**
- [ ] README com as seções do guia: o que é, arquitetura (diagrama dos serviços), no ar, pré-requisitos, como rodar, como testar, exemplos de chamada e o link do vídeo
- [ ] OpenAPI acessível por URL, local e no ar, com todas as rotas e as respostas de erro
- [ ] As duas ADRs novas em `docs/decisoes/` (o serviço Go e a implantação), com contexto, decisão, alternativas e consequências
- [ ] Arquivo `LICENSE` na raiz, citado no README
- [ ] Pedir a alguém de fora do grupo que siga o README e cronometrar

**Pronto quando.** Uma pessoa de fora roda o sistema em menos de 10 minutos, e o README, o OpenAPI, as ADRs e a licença estão no repositório.

**Referência.** [SPRINT-2.md](SPRINT-2.md) *Documentação* · [RUBRICAS.md](RUBRICAS.md#sprint-2).

---

## T12 — Gravar o vídeo de 5 minutos

**Objetivo.** Mostrar o produto final funcionando e explicar as escolhas.

**O que fazer.**
- [ ] Seguir o roteiro do guia (o serviço Go · o sistema no ar · o contrato e a chamada · o compose · os testes e o CI)
- [ ] Garantir que **todos os integrantes falam**
- [ ] Publicar o vídeo e linkar no `README.md`
- [ ] Mostrar o andamento na *daily meeting* do grupo (23 ou 25/11, ou 18/11 em sala)

**Pronto quando.** O vídeo tem cerca de 5 min, cobre o roteiro, todos falam, e está acessível pelo link.

**Referência.** [SPRINT-2.md](SPRINT-2.md) *Estrutura do vídeo* · [AVALIACAO.md](AVALIACAO.md#4-componente-c--comunicação).
