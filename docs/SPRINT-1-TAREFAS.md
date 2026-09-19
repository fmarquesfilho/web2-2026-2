# Tarefas da Sprint 1 — DIM0547

Estes são os enunciados das tarefas da Sprint 1, prontos para virar cartões no GitHub
Projects. Cada um tem um objetivo, o que fazer, e o *pronto quando* alinhado à rubrica.

O **como** está em [SPRINT-1.md](SPRINT-1.md) e nas leituras da sprint
([`leituras/web2-s1-pte1.md`](../leituras/web2-s1-pte1.md) e
[`leituras/web2-s1-pte2.md`](../leituras/web2-s1-pte2.md)) — as tarefas apontam para a
seção certa em vez de repeti-la. Os pesos vêm de [RUBRICAS.md](RUBRICAS.md#sprint-1). Prazo
em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral): **02/10, 23:59**.

| # | Tarefa | Critério da rubrica |
|---|---|---|
| T1 | Quebrar as histórias P1 em cartões da sprint | Atividade no repositório |
| T2 | Modelar as entidades e escrever as migrações | Persistência e migrações (20%) |
| T3 | Subir o banco com `docker compose` | Persistência e migrações (20%) |
| T4 | Implementar os repositórios com banco | CRUD completo (25%) |
| T5 | Completar o CRUD com paginação e filtros | CRUD completo (25%) |
| T6 | Separar as camadas e escrever o teste de arquitetura | Clean Architecture + verificação (25%) |
| T7 | Validar as entradas e responder erros em *problem details* | Validação, erros e OpenAPI (10%) |
| T8 | Gerar e publicar o OpenAPI | Validação, erros e OpenAPI (10%) |
| T9 | Escrever os testes e rodá-los no CI | Testes local e remoto (20%) |
| T10 | Gravar o vídeo de 5 minutos e preparar a apresentação | Comunicação |

> Exemplos de referência: `exemplos/ktor-tarefas/` e `exemplos/quarkus-tarefas/` deste
> repositório, passos 6 a 9 de cada `PASSOS.md`. Projeto de referência:
> `github.com/fmarquesfilho/musi`.

---

## T1 — Quebrar as histórias P1 em cartões da sprint

**Objetivo.** Levar para a sprint só o que cabe em duas semanas e dar um dono a cada cartão.

**O que fazer.**
- [ ] Mover para a coluna da sprint as histórias P1 que dependem do serviço de CRUD
- [ ] Criar os cartões das tarefas T2 a T10
- [ ] Atribuir um responsável a cada cartão e ligá-lo ao PR que o resolve

**Pronto quando.** O quadro mostra o que está na sprint, com dono, e cada cartão fechado aponta para um PR integrado.

**Referência.** [AVALIACAO.md](AVALIACAO.md#31-saúde-do-repositório) *Itens movimentados no quadro e vinculados a PRs*.

---

## T2 — Modelar as entidades e escrever as migrações

**Objetivo.** Ter o esquema do banco versionado, com ≥ 2 entidades relacionadas.

**O que fazer.**
- [ ] Escolher ≥ 2 entidades da proposta, com um relacionamento (1:N ou N:N)
- [ ] Escrever `V1__...sql` (e as seguintes) com tabelas, chaves estrangeiras, `NOT NULL` e índices necessários
- [ ] Configurar o Flyway para migrar na subida (Ktor: `Flyway.migrate()`; Quarkus: `quarkus.flyway.migrate-at-start=true`)
- [ ] Desligar a geração de esquema pelo ORM

**Pronto quando.** Um banco vazio chega ao esquema completo só pelas migrações, e rodar de novo não muda nada.

**Referência.** [SPRINT-1.md](SPRINT-1.md) *Persistência e migrações* · `web2-s1-pte2.md`, capítulo 3.

---

## T3 — Subir o banco com `docker compose`

**Objetivo.** Qualquer pessoa sobe o ambiente do zero, sem passos manuais.

**O que fazer.**
- [ ] Declarar o PostgreSQL no `docker-compose.yml`, com volume nomeado
- [ ] Ler URL, usuário e senha de variáveis de ambiente, com padrões para desenvolvimento
- [ ] Documentar no `README.md` e na task `mise run up`

**Pronto quando.** `docker compose up` (ou `mise run up`) sobe o banco e a API conecta sem ajuste manual.

**Referência.** `web2-s1-pte2.md`, capítulo 9.

---

## T4 — Implementar os repositórios com banco

**Objetivo.** Trocar a implementação em memória por uma com PostgreSQL, sem mudar as rotas.

**O que fazer.**
- [ ] Implementar as portas de repositório com Exposed (Ktor) ou Panache (Quarkus)
- [ ] Converter entre linhas/entidades JPA e objetos de domínio num lugar só
- [ ] Ktor: `suspendTransaction` em toda operação; Quarkus: `@Transactional` nas escritas
- [ ] Manter a implementação em memória para os testes sem banco

**Pronto quando.** A API grava e lê do PostgreSQL, os dados sobrevivem a um reinício, e as rotas só conhecem as interfaces.

**Referência.** `web2-s1-pte2.md`, capítulos 4 a 6.

---

## T5 — Completar o CRUD com paginação e filtros

**Objetivo.** Ter todas as operações das entidades, com semântica HTTP correta.

**O que fazer.**
- [ ] Listar, buscar por id, criar, atualizar e remover, para cada entidade
- [ ] Expor o relacionamento (rota aninhada ou filtro)
- [ ] Paginar a listagem no SQL, com tamanho máximo
- [ ] Oferecer ao menos um filtro por campo
- [ ] Responder `201` com `Location`, `204`, `404` e `409` onde couber

**Pronto quando.** Todas as operações existem para ≥ 2 entidades, a listagem pagina e filtra, e os status seguem os verbos.

**Referência.** [SPRINT-1.md](SPRINT-1.md) *CRUD completo* · `web2-s1-pte1.md`, capítulos 1, 2 e 6.

---

## T6 — Separar as camadas e escrever o teste de arquitetura

**Objetivo.** Garantir, por teste, que o domínio não depende de framework nem de infraestrutura.

**O que fazer.**
- [ ] Organizar os pacotes em domínio, casos de uso e adaptadores
- [ ] Escrever o teste de arquitetura (ArchUnit no Java; ArchUnit ou Konsist no Kotlin)
- [ ] Confirmar que ele falha ao importar, de propósito, uma classe de framework no domínio
- [ ] Rodar o teste no CI

**Pronto quando.** As camadas estão separadas, e o teste de arquitetura falha quando a regra é violada e passa no CI.

**Referência.** [SPRINT-1.md](SPRINT-1.md) *Clean Architecture + verificação* · `web2-s1-pte1.md`, capítulo 7 · [STACK.md](STACK.md#clean-architecture-com-verificação-automática).

---

## T7 — Validar as entradas e responder erros em *problem details*

**Objetivo.** Recusar entrada inválida com `400`/`422` claro, nunca `500`.

**O que fazer.**
- [ ] Validar todos os corpos e parâmetros (Bean Validation no Quarkus; validação explícita no Ktor)
- [ ] Tratar erros num lugar só (`StatusPages` ou `ExceptionMapper`)
- [ ] Responder `application/problem+json` com `type`, `title`, `status` e `detail`
- [ ] Conferir que violações de restrição do banco não chegam ao cliente como `500`

**Pronto quando.** Toda entrada inválida recebe `400`/`422` em *problem details*, com mensagem útil.

**Referência.** `web2-s1-pte1.md`, capítulo 6 · `web2-s1-pte2.md`, seção 5.6.

---

## T8 — Gerar e publicar o OpenAPI

**Objetivo.** Documentação da API que sai do código e não fica desatualizada.

**O que fazer.**
- [ ] Ktor: `ktor-server-routing-openapi` com `describe`, e Swagger UI (ou o `ktor-openapi` do MUSI); Quarkus: `quarkus-smallrye-openapi`
- [ ] Documentar todas as rotas, com os tipos certos nos parâmetros e as respostas de erro
- [ ] Deixar a especificação e a Swagger UI acessíveis e o endereço no `README.md`

**Pronto quando.** A especificação é gerada do código, cobre todas as rotas e abre na Swagger UI.

**Referência.** `web2-s1-pte2.md`, capítulo 8.

---

## T9 — Escrever os testes e rodá-los no CI

**Objetivo.** Uma suíte que roda igual no Docker Desktop e no GitHub Actions.

**O que fazer.**
- [ ] Unitários do domínio e dos casos de uso, sem banco
- [ ] Testes de rota sem banco, com o repositório em memória
- [ ] Integração com PostgreSQL: Testcontainers (Ktor) ou `@QuarkusTest` com Dev Services (Quarkus), cobrindo CRUD, relacionamento e paginação
- [ ] Rodar a suíte em `mise run test` e no workflow, em push e PR

**Pronto quando.** Unitários e integração passam localmente e no CI, sem mudança de configuração, e o CI está verde na branch principal.

**Referência.** [SPRINT-1.md](SPRINT-1.md) *Testes local e remoto* · `web2-s1-pte2.md`, capítulo 7.

---

## T10 — Gravar o vídeo de 5 minutos e preparar a apresentação

**Objetivo.** Mostrar o incremento funcionando e explicar as escolhas.

**O que fazer.**
- [ ] Seguir o roteiro do guia (entidades e CRUD · migrações e compose · arquitetura · testes e CI)
- [ ] Garantir que **todos os integrantes falam**
- [ ] Publicar o vídeo e linkar no `README.md`
- [ ] Ensaiar a apresentação da coorte (28/09 online ou 30/09 em sala)

**Pronto quando.** O vídeo tem ~5 min, cobre o roteiro, todos falam, e está acessível pelo link.

**Referência.** [SPRINT-1.md](SPRINT-1.md) *Estrutura do vídeo* · [AVALIACAO.md](AVALIACAO.md#4-componente-c--comunicação).
