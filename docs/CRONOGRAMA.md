# Cronograma — DIM0547 Desenvolvimento de Sistemas Web II

**Período**: 17/08/2026 a 19/12/2026
**Horário**: Segundas e quartas, 13:00 às 14:40 (24T12)

As aulas de 10/08, 12/08 e 17/08 não foram realizadas. O curso inicia em 19/08.

---

## Legenda

| Símbolo | Significado |
|---------|-------------|
| 🟢 | Aula presencial |
| 🎤 | Apresentação dos grupos (presencial ou online, conforme a coorte) |
| 🔵 | Encontro online no Google Meet — aula ou apoio ao projeto |
| 🚀 | Entrega da sprint, sexta-feira às 23:59 |
| 📚 | Prova escrita, presencial, em laboratório |
| 🔴 | Feriado ou atividades suspensas |
| — | Sem encontro |

---

## Estrutura das sprints

Uma Sprint 0 de quatro semanas, três sprints de projeto e um bloco final. Cada sprint tem duas ou três aulas presenciais com o conteúdo, um ou dois encontros online no horário da aula, e dois dias de apresentação na última semana — uma sessão online e uma em sala.

Todos os grupos apresentam em todas as sprints, exceto na Sprint 0. A entrega vence na sexta-feira que encerra a sprint. As regras de nota estão em [AVALIACAO.md](AVALIACAO.md).

---

## Visão geral

| Bloco | Período | Tema | Apresentações | Entrega |
|-------|---------|------|---------------|---------|
| Sprint 0 | 17/08 a 11/09 | Fundamentos e ambiente poliglota | — | 11/09 |
| Sprint 1 | 14/09 a 02/10 | Serviço de CRUD em Ktor ou Quarkus | 28 e 30/09 | 02/10 |
| Sprint 2 | 05/10 a 23/10 | Microsserviço Go e gRPC | 14 e 19/10 | 23/10 |
| Sprint 3 | 26/10 a 20/11 | Dados, cache e implantação | 16 e 18/11 | 20/11 |
| Bloco final | 23/11 a 11/12 | Segurança, automação e documentação | 07 e 09/12 | 11/12 |
| Prova escrita | 21/10 | Sprints 0 a 2 | — | — |
| Prova de reposição | 30/11 | Sprints 0 a 3, cumulativa e opcional | — | — |

---

## Feriados e suspensões

| Data | Dia | Evento |
|------|-----|--------|
| 07/09 | Segunda | Independência do Brasil |
| 12/10 | Segunda | Nossa Senhora Aparecida |
| 28/10 | Quarta | Dia do Servidor Público |
| 02/11 | Segunda | Finados |

Fonte: Calendário Universitário UFRN 2026, Resolução nº 074/2025-CONSAD.

---

## Apresentações

| Sprint | Coorte B — online | Coorte A — presencial |
|--------|-------------------|------------------------|
| Sprint 1 | 28/09 | 30/09 |
| Sprint 2 | 19/10 | 14/10 |
| Sprint 3 | 16/11 | 18/11 |
| Entrega final | 07/12 | 09/12 |

Na Sprint 2 a ordem se inverte por causa do feriado de 12/10 e da prova de 21/10. A escolha de coorte e as regras de apresentação estão em [AVALIACAO.md](AVALIACAO.md#4-componente-c--comunicação).

---

## Sprint 0 — Fundamentos e ambiente poliglota

**17/08 a 11/09. Entrega: 11/09 (sexta), 23:59.**

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 17/08 | Seg | — | Não houve aula |
| 19/08 | Qua | 🟢 | Apresentação do curso, dos critérios de avaliação e da arquitetura de referência. HTTP: recurso e representação, URI, métodos, idempotência, códigos de status e cabeçalhos. Arquitetura de serviços: monólito, monólito modular e microsserviços. **Kotlin com Ktor OU Java com Quarkus — escolha do grupo** |
| 24/08 | Seg | 🟢 | Cache HTTP: frescor e validação, `Cache-Control`, `ETag` e concorrência otimista. REST: as restrições de Fielding e o modelo de maturidade de Richardson. Clean Architecture: regra de dependência, portas e adaptadores, e como ela é verificada. Formação de grupos, escolha de coorte e definição do domínio do projeto |
| 26/08 | Qua | 🔵 | Encontro online — dúvidas sobre a proposta e o domínio |
| 31/08 | Seg | 🟢 | Estrutura do monorepo MUSI e mapeamento das camadas em pacotes. O serviço principal em detalhe, nos dois stacks — Kotlin com Ktor e Java 25 com Quarkus: rotas, injeção de dependência, DTOs, cliente do serviço Go, erros e OpenAPI |
| 02/09 | Qua | 🟢 | Fundamentos de Go: pacotes, interfaces, erros e contexto. Os serviços Go do MUSI (busca e conciliação). Ambiente reproduzível: tasks do `mise`, Docker Compose e primeiro workflow de CI. Oficina de montagem do monorepo |
| 07/09 | Seg | 🔴 | Independência do Brasil |
| 09/09 | Qua | 🔵 | Encontro online — dúvidas sobre o pipeline e o ambiente |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-0). Guia com templates e exemplos: [SPRINT-0.md](SPRINT-0.md).

---

## Sprint 1 — Serviço de CRUD (Ktor ou Quarkus)

**14/09 a 02/10. Entrega: 02/10 (sexta), 23:59.**

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 14/09 | Seg | 🟢 | **Visão comparativa**: Kotlin/Ktor × Java/Quarkus. Kotlin: corrotinas, DSL de roteamento, Koin. Java: CDI, RESTEasy Reactive, Panache. |
| 16/09 | Qua | 🟢 | **Persistência e migrações**: Exposed (Kotlin) × Hibernate/Panache (Java). Flyway em ambos. Testes com Testcontainers |
| 21/09 | Seg | 🔵 | Encontro online — dúvidas sobre o projeto |
| 23/09 | Qua | 🟢 | **Testes e documentação**: JUnit 5 × kotlin-test; OpenAPI com `ktor-openapi` × `quarkus-smallrye-openapi`. Oficina sobre o projeto |
| 28/09 | Seg | 🎤 | Apresentações da Coorte B, online |
| 30/09 | Qua | 🎤 | Apresentações da Coorte A, em sala de aula |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-1).

---

## Sprint 2 — Microsserviço Go e gRPC

**05/10 a 23/10. Entrega: 23/10 (sexta), 23:59.**

A prova escrita ocorre dentro desta sprint, em 21/10. O conteúdo fica concentrado em 05 e 07/10, e as apresentações se antecipam para 14 e 19/10.

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 05/10 | Seg | 🟢 | Go idiomático para serviços e Clean Architecture em Go, com verificação por arch-go. Protocol Buffers: mensagens, serviços, evolução de esquema e compatibilidade. Buf: lint, breaking e geração de stubs |
| 07/10 | Qua | 🟢 | gRPC unário e streaming, deadlines, interceptors e health checking. Integração entre o serviço principal (Ktor/Quarkus) e o serviço Go |
| 12/10 | Seg | 🔴 | Nossa Senhora Aparecida |
| 14/10 | Qua | 🎤 | Apresentações da Coorte A, em sala de aula |
| 19/10 | Seg | 🎤 | Apresentações da Coorte B, online |
| 21/10 | Qua | 📚 | **Prova escrita** — presencial, em laboratório. Conteúdo das Sprints 0 a 2 |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-2).

---

## Sprint 3 — Dados, cache e implantação

**26/10 a 20/11. Entrega: 20/11 (sexta), 23:59.**

Esta sprint tem quatro semanas por causa dos feriados de 28/10 e 02/11.

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 26/10 | Seg | — | Sem encontro |
| 28/10 | Qua | 🔴 | Dia do Servidor Público |
| 02/11 | Seg | 🔴 | Finados |
| 04/11 | Qua | 🟢 | PostgreSQL gerenciado com Neon: branches de banco, connection pooling e limites de uso. Modelagem, índices e planos de execução |
| 09/11 | Seg | 🟢 | Estratégias de cache: cache-aside, write-through, TTL, invalidação e cache stampede. Serviço de cache em Go com métricas de acerto |
| 11/11 | Qua | 🟢 | Implantação em plataformas de container gratuitas, publicação de imagens no GHCR, logs estruturados e health checks |
| 16/11 | Seg | 🎤 | Apresentações da Coorte B, online |
| 18/11 | Qua | 🎤 | Apresentações da Coorte A, em sala de aula |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-3).

---

## Bloco final — Segurança, automação e documentação

**23/11 a 11/12. Entrega final: 11/12 (sexta), 23:59.**

O bloco final não tem entrega própria: o conteúdo apresentado aqui é avaliado na entrega final do projeto e na apresentação.

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 23/11 | Seg | 🟢 | OAuth 2.0 e OpenID Connect. JWT, refresh tokens e rotação de chaves. Comparativo: Ktor Auth × Quarkus OIDC. Autorização por rota e por método |
| 25/11 | Qua | 🔵 | Aula online — OWASP API Security Top 10 e gestão de segredos. Pipeline completo: matriz de jobs, cache de dependências, Semgrep, Renovate, hooks de pre-push e tasks do `mise` |
| 30/11 | Seg | 📚 | **Prova de reposição** — presencial, em laboratório. Cumulativa, Sprints 0 a 3. Opcional |
| 02/12 | Qua | 🔵 | Aula online — documentação como parte do produto: site estático, ADRs, referência de API gerada do OpenAPI e verificação de defasagem no CI. GraphQL e concorrência |
| 07/12 | Seg | 🎤 | Apresentações finais da Coorte B, online |
| 09/12 | Qua | 🎤 | Apresentações finais da Coorte A, em sala de aula |
| 11/12 | Sex | 🚀 | **Entrega final**, 23:59 |
| 14/12 e 16/12 | Seg e Qua | — | Sem encontro. Divulgação das notas e do retorno escrito no SIGAA |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#entrega-final).