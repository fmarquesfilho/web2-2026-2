# Guia da Sprint 2 — DIM0547

Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral): entrega final em **30/11 (segunda), 23:59**. Conteúdo em vídeo, publicado no SIGAA até 23/10, e na aula presencial de 26/10 (as aulas de 19 e 21/10 foram canceladas); acompanhamento online em 09/11 e 16/11; *daily meetings* online em 23 e 25/11 (presencial em 18/11, para quem avisar antes). O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-2). Os enunciados, prontos para virar cartões no quadro, estão em [SPRINT-2-TAREFAS.md](SPRINT-2-TAREFAS.md).

A Sprint 2 é a última do semestre: o que for entregue em 30/11 é o produto final. O serviço principal ganha um **segundo serviço, em Go**, com uma responsabilidade justificada, ligado a ele por **gRPC**, com o contrato em Protocol Buffers. Tudo sobe com `docker compose`, a API vai para o ar com banco gerenciado, e os testes que saíram da Sprint 1 voltam, verdes no CI.

---

## O que entregar

| Critério da rubrica | Peso | Em uma frase |
|---|---|---|
| Microsserviço Go | 25% | Um serviço com responsabilidade única e justificada; Go idiomático, com `context` e erros usados corretamente |
| Contrato Protobuf e integração gRPC | 25% | `.proto` comentado, `buf lint` verde no CI, stubs gerados; a API chama o Go com prazo e erro tratado, e um teste automatizado cobre o fluxo |
| Sistema no ar | 25% | API e banco gerenciado no ar, com URL no README, migração por task ou CI, segredos fora do repositório, health check e API que funciona sem o Go |
| Ambiente e testes | 15% | `docker compose up` sobe API, Go e banco do zero; testes unitários, de integração com banco e de arquitetura, verdes local e no CI |
| Documentação | 10% | README completo, que permite rodar o sistema em menos de 10 min; OpenAPI por URL; duas ADRs novas; licença |

Além da entrega técnica, a nota da sprint tem a atividade no repositório (30%) e a comunicação (20%): ver [AVALIACAO.md](AVALIACAO.md#2-nota-de-cada-sprint).

---

## Material de apoio

- Leitura da sprint: [`leituras/web2-s2.md`](../leituras/web2-s2.md) — introdução a Go e o domínio do serviço Go (em vídeo); Protobuf, o servidor gRPC, a API chamando o Go, containers e implantação (26/10).
- Slides: `slides/web2-slides-07.md` (vídeo) e `slides/web2-slides-08.md` (26/10).
- Exemplo: `exemplos/avisos-go/` com o [`PASSOS.md`](../exemplos/avisos-go/PASSOS.md) dos passos 13 a 18, `exemplos/protos/`, `exemplos/compose.yaml` e os modelos em `exemplos/implantacao/`. No branch `aula`, as tags `passo-13` a `passo-18` guardam cada passo.
- Para aprender Go: o [Curso básico de Go](https://www.youtube.com/playlist?list=PLTr2C2xXIaIqO7Yr7FHohhgG_OgONpYIx), em vídeo, com os [exercícios](https://github.com/classrooms-fmarquesfilho/aprenda-go-com-testes) do *Aprenda Go com Testes* (<https://larien.gitbook.io/aprenda-go-com-testes/>).
- Os testes da Sprint 1 (agora avaliados aqui): capítulos 7 e 11 da leitura `web2-s1-pte2.md` e passos 8 e 12 dos exemplos.
- Ambiente sem instalação: o Codespace deste repositório traz Java 25, Maven, Go, Buf, grpcurl e Docker.

---

## Microsserviço Go

- Uma responsabilidade do domínio do produto, que justifique um processo à parte: trabalho de E/S que espera por terceiros, concorrência, processamento pesado, agendamento. Escrevam a justificativa numa ADR: o que o serviço faz, por que fica fora da API, e o que aconteceria se ficasse dentro.
- Não separem um CRUD em dois serviços que dividem as mesmas tabelas: os dois deixam de poder mudar de forma independente.
- Domínio em Go puro, sem gRPC: interfaces pequenas, declaradas por quem as usa; erros com nome (`errors.Is`) e `%w` para acrescentar contexto.
- `context.Context` como primeiro parâmetro de toda função que pode esperar, e o prazo respeitado (`select` com `ctx.Done()`).
- Testes com `go test`, ao lado do código, inclusive do comportamento sob prazo.

---

## Contrato Protobuf e integração gRPC

- O contrato em `protos/`, com o pacote versionado (`.../v1`) e todo elemento comentado. O `buf lint` (regras `STANDARD` e `COMMENTS`) roda no CI e fica verde.
- Stubs gerados, nunca escritos à mão: `buf generate` para o Go; no serviço principal, o plugin do Gradle (Ktor) ou a extensão `quarkus-grpc` (Quarkus).
- A API chama o Go **sempre com prazo** (`withDeadlineAfter`), trata os códigos de status (`UNAVAILABLE`, `DEADLINE_EXCEEDED`, `INVALID_ARGUMENT`) e decide o que fazer quando o Go falha. Se a operação do Go é acessória, a API continua respondendo.
- No servidor Go: os erros do domínio viram códigos gRPC; health check e reflection registrados; encerramento gracioso.
- Um teste automatizado cobre o fluxo: no mínimo, o cliente gRPC da API contra um servidor (em memória, como no exemplo, ou o serviço Go num container, com Testcontainers).

---

## Sistema no ar

- A API e o banco gerenciado (Neon) no ar, numa plataforma de container, com a URL pública no README.
- Migrações aplicadas por task ou pelo CI (o exemplo tem o comando `migrar`), e não à mão.
- Connection string e senhas como segredos da plataforma, nunca no repositório.
- Health check configurado na plataforma.
- A API continua respondendo quando o serviço Go não está acessível.
- **O serviço Go no ar é opcional.** Nas plataformas gratuitas consultadas, a comunicação gRPC entre dois serviços não está disponível (ver a nota em [RUBRICAS.md](RUBRICAS.md#sprint-2) e o capítulo 8 da leitura). O serviço Go é avaliado rodando junto da API no `docker compose`. Quem o puser no ar registra como fez numa ADR.

---

## Ambiente e testes

- `docker compose up --build` sobe API, serviço Go e banco do zero, numa máquina que só tem Docker: sem instalar a linguagem, sem passos manuais. Health checks e `depends_on` com `service_healthy`.
- A suíte do serviço principal tem os três tipos que saíram da Sprint 1: unitários do domínio, integração com banco real (Testcontainers ou Dev Services) e o teste de arquitetura (ArchUnit ou Konsist), que falha se o domínio importar framework, banco ou gRPC.
- A mesma suíte roda local e no CI, sem mudar configuração. O CI roda também o `go test` e o `buf lint`.

---

## Documentação

O critério avalia se outra pessoa consegue entender, rodar e avaliar o sistema só com o que está no repositório.

**README**, na raiz, com:

| Seção | O que contém |
|---|---|
| O que é | o problema que o sistema resolve, em dois ou três parágrafos |
| Arquitetura | um diagrama dos serviços (API, serviço Go, banco) e de como conversam (REST, gRPC, SQL) |
| No ar | a URL pública da API e da documentação OpenAPI |
| Pré-requisitos | o que instalar (Docker, e o que mais for necessário), com versões |
| Como rodar | `docker compose up --build` e o que esperar ao final; as variáveis de ambiente, com exemplo |
| Como testar | os comandos dos testes (serviço principal e Go) e o que cada suíte cobre |
| Exemplos | duas ou três chamadas à API (`curl` ou HTTP), com a resposta esperada |
| Vídeo | o link do vídeo da sprint |

O teste do README: uma pessoa de fora do grupo, seguindo só o README, roda o sistema em menos de 10 minutos.

**OpenAPI** acessível por URL, local e no ar, com todas as rotas, os parâmetros com os tipos certos e as respostas de erro (*problem details*).

**ADRs**, em `docs/decisoes/`, pelo menos duas novas, no formato contexto, decisão, alternativas consideradas e consequências:

- o serviço Go: a responsabilidade, por que fica fora da API, o que aconteceria se ficasse dentro;
- a implantação: a plataforma, o banco gerenciado, como os segredos são guardados, como as migrações são aplicadas, e o que ficou fora do ar (o serviço Go, se for o caso) e por quê.

**Licença**: um arquivo `LICENSE` na raiz (MIT, Apache 2.0 ou outra), citada no README.

---

## Estrutura do vídeo — 5 minutos

| Tempo | Conteúdo |
|---|---|
| 30 s | O que a sprint entregou, e qual é a responsabilidade do serviço Go |
| 1 min 30 s | O sistema no ar: uma requisição real pela URL pública, e a API respondendo sem o Go |
| 1 min | O contrato, o `buf lint` e uma chamada gRPC (pela API ou pelo `grpcurl`), com o prazo |
| 1 min | `docker compose up` do zero e os serviços saudáveis |
| 1 min | Os testes e o CI verde; o que o grupo faria a seguir |

Todos os integrantes devem falar. Link no `README.md`.

---

## Como o grupo é avaliado nesta sprint

- **Entrega técnica (50%)**: a rubrica da Sprint 2, sobre o estado da branch principal no prazo (hash do último commit).
- **Atividade no repositório (30%)**: CI verde, commits distribuídos pelas semanas, ao menos um PR integrado por integrante, PRs revisados por outro integrante e cartões do quadro ligados a PRs. O Fator de Participação individual segue [AVALIACAO.md](AVALIACAO.md#32-fator-de-participação).
- **Comunicação (20%)**: média entre o vídeo e a *daily meeting* (23 ou 25/11, online, ou 18/11, em sala).

O registro de uso de IA continua em `docs/uso-de-ia.md`.
