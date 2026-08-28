# Tarefas da Sprint 0 — DIM0547

Estes são os enunciados das tarefas da Sprint 0, prontos para virar cartões no GitHub
Projects. Cada um tem um objetivo, o que fazer, e o *pronto quando* alinhado à rubrica.

O **como** (templates, exemplos e a estrutura da proposta e do vídeo) está em
[SPRINT-0.md](SPRINT-0.md) — as tarefas apontam para a seção certa em vez de repeti-la. Os
pesos vêm de [RUBRICAS.md](RUBRICAS.md#sprint-0). A arquitetura de referência está em
[STACK.md](STACK.md). Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral).

| # | Tarefa | Critério da rubrica |
|---|---|---|
| T1 | Criar o monorepo e a estrutura de pastas | Estrutura do monorepo (25%) |
| T2 | Escrever a visão do produto | Proposta e decisão de arquitetura (30%) |
| T3 | Definir o MVP e as entidades do domínio | Proposta e decisão de arquitetura (30%) |
| T4 | Decidir e justificar Kotlin/Ktor × Java/Quarkus | Proposta e decisão de arquitetura (30%) |
| T5 | Definir a divisão com o serviço Go | Proposta e decisão de arquitetura (30%) |
| T6 | Montar o backlog no GitHub Projects | Configuração do processo (20%) |
| T7 | Deixar o CI verde (passando) nos dois stacks | CI passando (25%) |
| T8 | Consolidar `docs/proposta.md` | Todos |
| T9 | Gravar o vídeo de 5 minutos | Todos |

> Projeto de referência: `github.com/fmarquesfilho/musi`. Ele mostra o monorepo, o CI e a
> divisão entre o serviço principal e o serviço Go já funcionando.

---

## T1 — Criar o monorepo e a estrutura de pastas

**Objetivo.** Ter o monorepo público com o esqueleto onde os serviços vão morar.

**O que fazer.**
- [ ] Criar o repositório **público** e escrever o `README.md` (equipe, matrículas, coorte, como rodar)
- [ ] Criar a estrutura: `api/`, `services/`, `protos/`, `docs/`
- [ ] Adicionar `mise.toml` com as tasks `build` e `test`, e o `docker-compose.yml`
- [ ] Conferir que `mise run build` e `mise run test` rodam localmente

**Pronto quando.** As pastas e os arquivos de configuração existem e são coerentes, e as tasks do `mise` passam.

**Referência.** [SPRINT-0.md](SPRINT-0.md) · [RUBRICAS.md](RUBRICAS.md#sprint-0) *Estrutura do monorepo*.

---

## T2 — Escrever a visão do produto

**Objetivo.** Deixar claro por que o produto existe e que problema resolve.

**O que fazer.**
- [ ] Preencher o template de visão (seção *Visão do produto* do guia)
- [ ] Conferir: usuário definido, problema específico, valor único, viável em um semestre
- [ ] Registrar na seção 1 de `docs/proposta.md`

**Pronto quando.** A visão nomeia um público e um problema delimitado, não genérico.

**Referência.** [SPRINT-0.md](SPRINT-0.md) *Visão do produto*.

---

## T3 — Definir o MVP e as entidades do domínio

**Objetivo.** Delimitar o escopo mínimo e nomear as entidades centrais.

**O que fazer.**
- [ ] Listar o que está **no MVP** e o que fica **fora**
- [ ] Enunciar a hipótese de valor: *acreditamos que [usuários] vão [comportamento] porque [benefício]*
- [ ] Listar as entidades principais do domínio e suas relações
- [ ] Registrar nas seções 2 e 4 de `docs/proposta.md`

**Pronto quando.** O MVP é viável em quatro sprints, tem o fora-de-escopo declarado, e as entidades principais estão nomeadas.

**Referência.** [SPRINT-0.md](SPRINT-0.md) *Definição do MVP*.

---

## T4 — Decidir e justificar Kotlin/Ktor × Java/Quarkus

**Objetivo.** Tomar a primeira decisão arquitetural da disciplina, com justificativa.

**O que fazer.**
- [ ] Escolher entre **Kotlin/Ktor** e **Java/Quarkus**
- [ ] Justificar a partir de perfil da equipe, características do domínio, ecossistema e diferenciação
- [ ] Registrar na seção 5 de `docs/proposta.md`

**Pronto quando.** A escolha está declarada e justificada com base nas características do trabalho — não por conveniência. Não há escolha certa; o que vale é a qualidade da justificativa.

**Referência.** [SPRINT-0.md](SPRINT-0.md) *A escolha entre Kotlin/Ktor e Java/Quarkus* · [RUBRICAS.md](RUBRICAS.md#sprint-0) *Proposta e decisão de arquitetura*.

---

## T5 — Definir a divisão com o serviço Go

**Objetivo.** Decidir o que fica no serviço principal e o que vai para um microsserviço Go.

**O que fazer.**
- [ ] Mapear responsabilidades: domínio/persistência/auth no serviço principal; coleta, lote, I/O concorrente, cache no Go
- [ ] Justificar a divisão pelas características do trabalho
- [ ] Registrar na seção 6 de `docs/proposta.md`

**Pronto quando.** A divisão é clara e justificada, e cada serviço tem um motivo de existir baseado no tipo de trabalho que faz.

**Referência.** [SPRINT-0.md](SPRINT-0.md) *A divisão entre o serviço principal e os microsserviços Go*.

---

## T6 — Montar o backlog no GitHub Projects

**Objetivo.** Transformar o MVP em histórias priorizadas e estimadas.

**O que fazer.**
- [ ] Criar no mínimo 5 itens no formato *como [papel], quero [ação] para [benefício]*
- [ ] Estimar ao menos 3 e priorizar todas (P1/P2/P3)
- [ ] Declarar a coorte de apresentação

**Pronto quando.** Há ≥ 5 histórias, ≥ 3 estimadas, todas priorizadas, no quadro do GitHub Projects.

**Referência.** [SPRINT-0.md](SPRINT-0.md) *Backlog inicial* · [RUBRICAS.md](RUBRICAS.md#sprint-0) *Configuração do processo*.

---

## T7 — Deixar o CI verde (passando) nos dois stacks

**Objetivo.** Ter um pipeline que compila o serviço principal e o serviço Go a cada push.

**O que fazer.**
- [ ] Criar o workflow do GitHub Actions que roda o build do serviço principal e do Go
- [ ] Disparar em todo push e pull request
- [ ] Garantir que fica verde em `main`

**Pronto quando.** O CI roda o build dos dois stacks em push e PR e está verde em `main`. Ainda não é necessário fazer o deploy da aplicação.

**Referência.** [RUBRICAS.md](RUBRICAS.md#sprint-0).

---

## T8 — Consolidar `docs/proposta.md`

**Objetivo.** Reunir tudo num documento de no máximo 5 páginas.

**O que fazer.**
- [ ] Montar as 8 seções na ordem do guia (visão, MVP, link do backlog, entidades, decisão da stack, divisão com Go, equipe, coorte/integração)

**Pronto quando.** `docs/proposta.md` tem as 8 seções, cabe em 5 páginas e aponta para o backlog.

**Referência.** [SPRINT-0.md](SPRINT-0.md) *Estrutura de `docs/proposta.md`*.

---

## T9 — Gravar o vídeo de 5 minutos

**Objetivo.** Apresentar a equipe, o produto e as decisões de arquitetura.

**O que fazer.**
- [ ] Seguir o roteiro do guia (equipe · visão · decisão da stack · divisão entre Java/Quarkus ou Kotlin/Ktor + Go · MVP · backlog)
- [ ] Garantir que **todos os integrantes falam**
- [ ] Mostrar o monorepo com o CI verde
- [ ] Publicar o vídeo e linkar no `README.md` ou na proposta

**Pronto quando.** O vídeo tem ~5 min, cobre o roteiro, todos falam, e mostra o CI verde (passando).

**Referência.** [SPRINT-0.md](SPRINT-0.md) *Estrutura do vídeo*.
