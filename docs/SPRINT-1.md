# Guia da Sprint 1 — DIM0547

Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral): entrega em **16/10 (sexta), 23:59** (adiada; era 02/10). As *daily meetings* da sprint foram as reuniões online de 28 e 30/09. O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-1). Os enunciados, prontos para virar cartões no quadro, estão em [SPRINT-1-TAREFAS.md](SPRINT-1-TAREFAS.md).

A Sprint 1 é a sprint do **serviço de CRUD**: o serviço principal, em Kotlin/Ktor ou Java/Quarkus conforme a decisão da Sprint 0, passa a ter entidades persistidas em PostgreSQL, esquema versionado por migrações, arquitetura em camadas e documentação OpenAPI gerada do código.

---

## O que entregar

| Critério da rubrica | Peso | Em uma frase |
|---|---|---|
| CRUD completo | 30% | ≥ 2 entidades com relacionamento, todas as operações, paginação e filtros, com verbos e status HTTP coerentes |
| Clean Architecture | 30% | Camadas separadas em pacotes; o domínio não importa framework nem infraestrutura |
| Persistência e migrações | 25% | Flyway com migrações versionadas; banco sobe via `docker compose`; esquema só por migração |
| Validação, erros e OpenAPI | 15% | Validação em todas as entradas, erros em *problem details* (RFC 9457), OpenAPI gerado e acessível |

Além da entrega técnica, a nota da sprint tem a atividade no repositório (30%) e a comunicação (20%): ver [AVALIACAO.md](AVALIACAO.md#2-nota-de-cada-sprint).

---

## Material de apoio

- Leituras da sprint: [`leituras/web2-s1-pte1.md`](../leituras/web2-s1-pte1.md) (serviço de CRUD em Ktor × Quarkus, 14/09) e [`leituras/web2-s1-pte2.md`](../leituras/web2-s1-pte2.md) (persistência, migrações, testes, OpenAPI, validação com *problem details* e teste de arquitetura; segunda parte da sprint). Os capítulos de testes dessa leitura servem à Sprint 2.
- Exemplos: `exemplos/ktor-tarefas/` e `exemplos/quarkus-tarefas/`, passos 1 a 12 de cada `PASSOS.md`. A mesma API nos dois stacks, com PostgreSQL, Flyway, testes com Testcontainers (Ktor) e Dev Services (Quarkus), OpenAPI, validação com *problem details* (Passo 11) e teste de arquitetura com ArchUnit (Passo 12). O exemplo tem uma entidade só: duas entidades com relacionamento, `PUT`/`DELETE`, paginação e filtros estão no MUSI.
- Projeto de referência: `github.com/fmarquesfilho/musi` (`api-ktor/`, `api-quarkus/`).
- Ambiente sem instalação: o `.devcontainer/` deste repositório abre um Codespace com Java 25, Maven, Go e Docker, onde os exemplos, o `docker compose`, o Testcontainers e o Dev Services funcionam. É também um modelo para o `.devcontainer/` do projeto do grupo.

---

## CRUD completo

- Pelo menos duas entidades do domínio da Sprint 0, com um relacionamento entre elas (por exemplo, `Projeto` 1:N `Tarefa`). O relacionamento aparece no banco (chave estrangeira) e na API (rota aninhada como `/projetos/{id}/tarefas`, ou filtro por `projetoId`).
- Todas as operações: listar, buscar por id, criar, atualizar e remover, com os métodos e status do guia de 14/09 (`201` com `Location` na criação, `204` na remoção, `404` para id inexistente).
- Paginação na listagem (`?pagina=0&tamanho=20`, ou `limit`/`offset`), com um tamanho máximo, e ao menos um filtro por campo.
- A paginação vai para o SQL (`LIMIT`/`OFFSET` no Exposed, `page(...)` no Panache): não carreguem tudo para paginar em memória.

---

## Clean Architecture

- O domínio (entidades e regras) não importa Ktor, Quarkus, Exposed, Hibernate nem JDBC. Os adaptadores (rotas ou recursos, repositórios com banco) dependem do domínio, nunca o contrário.
- Os pacotes mostram as camadas: por exemplo, `dominio` e `adaptadores` (`http`, `banco`), como no Passo 12 do exemplo.
- As rotas ou recursos conhecem só as interfaces de repositório, declaradas no domínio.
- O teste de arquitetura (ArchUnit ou Konsist), que verifica essa regra automaticamente, passou para a Sprint 2. Nesta sprint, a regra é conferida pela leitura dos `import` do pacote de domínio.

---

## Persistência e migrações

- Migrações Flyway em `src/main/resources/db/migration`, `V1__...`, `V2__...`. Nunca editem uma migração já aplicada: criem a próxima.
- Nada de `ddl-auto`, `schema-management.strategy` diferente de `none` ou `SchemaUtils.create`: o esquema vem só das migrações.
- `docker compose up` sobe o banco (e, quando existir, a API). URL, usuário e senha por variável de ambiente, nunca no código.

---

## Testes: na Sprint 2

Os testes saíram desta sprint (ajuste de 13/10, combinado com a turma) e passam a ser avaliados na Sprint 2: os unitários do domínio, os de integração com banco real e o teste de arquitetura. Nesta sprint:

- O workflow da Sprint 0 (build dos dois stacks) continua rodando e deve estar verde na branch principal no prazo.
- Manter a implementação em memória dos repositórios e o domínio sem dependência de framework já deixa o código pronto para ser testado.
- Quem já escreveu testes pode mantê-los no repositório: eles contam na Sprint 2.

O conteúdo sobre testes continua disponível: capítulo 7 (testes) e capítulo 11 (teste de arquitetura) da leitura `web2-s1-pte2.md`, e passos 8 e 12 dos exemplos.

---

## Validação, erros e OpenAPI

- Validem todas as entradas: Bean Validation no Quarkus (`@NotBlank`, `@Valid`), validação explícita ou biblioteca de validação no Ktor. Entrada inválida é `400` (ou `422`), nunca `500`.
- Erros no formato *problem details* (`application/problem+json`), tratados num lugar só (`StatusPages` no Ktor, `ExceptionMapper` no Quarkus).
- OpenAPI gerado do código: `describe` do Ktor (o gerador nativo, usado no exemplo e no MUSI) ou o `ktor-openapi` do smiley4, com Swagger UI; `quarkus-smallrye-openapi` no Quarkus. Tipos corretos nos parâmetros e todas as respostas documentadas, inclusive as de erro.

---

## Estrutura do vídeo — 5 minutos

| Tempo | Conteúdo |
|---|---|
| 30 s | O que a sprint entregou |
| 1 min 30 s | As entidades e o relacionamento; o CRUD funcionando pela Swagger UI, com paginação e filtro |
| 1 min | As migrações e o banco subindo com `docker compose` |
| 1 min | A arquitetura: os pacotes de cada camada e os `import` do domínio |
| 1 min | Validação e erros em *problem details*; o CI verde; o que ficou para a Sprint 2 |

Todos os integrantes devem falar. Link no `README.md`.

---

## Como o grupo é avaliado nesta sprint

- **Entrega técnica (50%)**: a rubrica da Sprint 1, sobre o estado da branch principal no prazo (hash do último commit).
- **Atividade no repositório (30%)**: CI verde, commits distribuídos pelas semanas, ao menos um PR integrado por integrante, PRs revisados por outro integrante e cartões do quadro ligados a PRs. O Fator de Participação individual segue [AVALIACAO.md](AVALIACAO.md#32-fator-de-participação).
- **Comunicação (20%)**: média entre o vídeo e a *daily meeting* (a reunião online de 28 ou 30/09).
