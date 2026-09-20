# Guia da Sprint 1 — DIM0547

Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral): entrega em **02/10 (sexta), 23:59**, com apresentações em 28/09 (Coorte B, online) e 30/09 (Coorte A, em sala). O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-1). Os enunciados, prontos para virar cartões no quadro, estão em [SPRINT-1-TAREFAS.md](SPRINT-1-TAREFAS.md).

A Sprint 1 é a sprint do **serviço de CRUD**: o serviço principal, em Kotlin/Ktor ou Java/Quarkus conforme a decisão da Sprint 0, passa a ter entidades persistidas em PostgreSQL, esquema versionado por migrações, arquitetura verificada por teste, testes de integração com banco real e documentação OpenAPI gerada do código.

---

## O que entregar

| Critério da rubrica | Peso | Em uma frase |
|---|---|---|
| CRUD completo | 25% | ≥ 2 entidades com relacionamento, todas as operações, paginação e filtros, com verbos e status HTTP coerentes |
| Clean Architecture + verificação | 25% | Camadas separadas e um teste de arquitetura, rodando no CI, que falha se o domínio importar framework ou infraestrutura |
| Persistência e migrações | 20% | Flyway com migrações versionadas; banco sobe via `docker compose`; esquema só por migração |
| Testes local e remoto | 20% | Unitários do domínio e integração com Testcontainers, passando no Docker Desktop e no CI sem mudar configuração |
| Validação, erros e OpenAPI | 10% | Validação em todas as entradas, erros em *problem details* (RFC 9457), OpenAPI gerado e acessível |

Além da entrega técnica, a nota da sprint tem a atividade no repositório (30%) e a comunicação (20%): ver [AVALIACAO.md](AVALIACAO.md#2-nota-de-cada-sprint).

---

## Material de apoio

- Leituras da sprint: [`leituras/web2-s1-pte1.md`](../leituras/web2-s1-pte1.md) (serviço de CRUD em Ktor × Quarkus, 14/09) e [`leituras/web2-s1-pte2.md`](../leituras/web2-s1-pte2.md) (persistência, migrações, testes e OpenAPI, 21/09).
- Exemplos: `exemplos/ktor-tarefas/` e `exemplos/quarkus-tarefas/`, passos 1 a 9 de cada `PASSOS.md`. A mesma API nos dois stacks, com PostgreSQL, Flyway, testes com Testcontainers (Ktor) e Dev Services (Quarkus) e OpenAPI.
- Projeto de referência: `github.com/fmarquesfilho/musi` (`api-ktor/`, `api-quarkus/`).
- Ambiente sem instalação: o `.devcontainer/` deste repositório abre um Codespace com Java 25, Maven, Go e Docker, onde os exemplos, o `docker compose`, o Testcontainers e o Dev Services funcionam. É também um modelo para o `.devcontainer/` do projeto do grupo.

---

## CRUD completo

- Pelo menos duas entidades do domínio da Sprint 0, com um relacionamento entre elas (por exemplo, `Projeto` 1:N `Tarefa`). O relacionamento aparece no banco (chave estrangeira) e na API (rota aninhada como `/projetos/{id}/tarefas`, ou filtro por `projetoId`).
- Todas as operações: listar, buscar por id, criar, atualizar e remover, com os métodos e status do guia de 14/09 (`201` com `Location` na criação, `204` na remoção, `404` para id inexistente).
- Paginação na listagem (`?pagina=0&tamanho=20`, ou `limit`/`offset`), com um tamanho máximo, e ao menos um filtro por campo.
- A paginação vai para o SQL (`LIMIT`/`OFFSET` no Exposed, `page(...)` no Panache): não carreguem tudo para paginar em memória.

---

## Clean Architecture + verificação

- O domínio (entidades e regras) não importa Ktor, Quarkus, Exposed, Hibernate nem JDBC. Os adaptadores (rotas ou recursos, repositórios com banco) dependem do domínio, nunca o contrário.
- Um teste de arquitetura verifica a regra de dependência: ArchUnit no Java; ArchUnit ou Konsist no Kotlin. Ele deve **falhar** se alguém importar framework no domínio — confiram isso uma vez, de propósito.
- O teste roda no CI junto com os demais.

---

## Persistência e migrações

- Migrações Flyway em `src/main/resources/db/migration`, `V1__...`, `V2__...`. Nunca editem uma migração já aplicada: criem a próxima.
- Nada de `ddl-auto`, `schema-management.strategy` diferente de `none` ou `SchemaUtils.create`: o esquema vem só das migrações.
- `docker compose up` sobe o banco (e, quando existir, a API). URL, usuário e senha por variável de ambiente, nunca no código.

---

## Testes local e remoto

- Unitários do domínio e dos casos de uso, sem banco, com repositórios falsos ou em memória: rápidos, muitos.
- Integração com banco real: Testcontainers (Ktor) ou `@QuarkusTest` com Dev Services (Quarkus). Verificam migração, SQL, relacionamento e restrições.
- A mesma suíte roda no Docker Desktop e no GitHub Actions sem mudar configuração: os runners Linux do Actions têm Docker. Nada de URL de banco fixa nos testes.
- `mise run test` e o workflow executam a suíte inteira. Com Quarkus 3.28 e Docker Engine 29, importem o BOM do Testcontainers 1.21.4 (ver o `pom.xml` do exemplo).

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
| 1 min | A arquitetura: camadas e o teste de arquitetura falhando e passando |
| 1 min | Os testes (unitários e com Testcontainers) e o CI verde; o que ficou para a Sprint 2 |

Todos os integrantes devem falar. Link no `README.md`.

---

## Como o grupo é avaliado nesta sprint

- **Entrega técnica (50%)**: a rubrica da Sprint 1, sobre o estado da branch principal no prazo (hash do último commit).
- **Atividade no repositório (30%)**: CI verde, commits distribuídos pelas semanas, ao menos um PR integrado por integrante, PRs revisados por outro integrante e cartões do quadro ligados a PRs. O Fator de Participação individual segue [AVALIACAO.md](AVALIACAO.md#32-fator-de-participação).
- **Comunicação (20%)**: média entre o vídeo e a apresentação da coorte.
