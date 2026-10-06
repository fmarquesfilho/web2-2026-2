# Cronograma — DIM0547 Desenvolvimento de Sistemas Web II

**Período**: 17/08/2026 a 19/12/2026
**Horário**: Segundas e quartas, 13:00 às 14:40 (24T12)

As aulas de 10/08, 12/08 e 17/08 não foram realizadas. O curso inicia em 19/08.

**Ajuste de 11/09.** Por remanejamento de aulas no início de setembro, a Sprint 0 foi estendida e sua entrega passou para **16/09** (quarta), 23:59. A Sprint 1 foi condensada em duas aulas de conteúdo (**14 e 21/09**), com encontros de acompanhamento de projeto em **16 e 23/09**. As seções abaixo já refletem o ajuste.

**Ajuste de 03/10.** Para caber no que falta do semestre, o curso passa a ter **uma sprint a
mais, e não três**: a Sprint 2, de novembro, é a entrega final. A Sprint 3 e o bloco final
deixam de existir, e o conteúdo foi enxugado.
A entrega da Sprint 1 passa para **16/10** (sexta), 23:59. A prova escrita passa para **11/11** (quarta; em 06/10 foi adiada de 09/11 para 11/11)
e a de reposição, para **02/12**. No fim de cada sprint, no lugar das apresentações, o professor faz uma *daily meeting*
com cada grupo, online, pelo Google Meet, como as de 28 e 30/09; quem preferir conversar em
sala pode fazê-lo na aula presencial anterior. De 05/10 em diante são 7 aulas presenciais, 7
online e 1 a definir (30/11). As seções abaixo já refletem o ajuste, que substitui as datas dos ajustes anteriores.

---

## Legenda

| Símbolo | Significado |
|---------|-------------|
| 🟢 | Aula presencial |
| 🎤 | *Daily meeting* com cada grupo: online, pelo Google Meet; em sala para quem preferir |
| ⏳ | Aula a definir: reservada, alocada conforme a demanda |
| 🎥 | Aula em vídeo, publicada no SIGAA |
| 🔵 | Encontro online no Google Meet — aula ou apoio ao projeto |
| 🚀 | Entrega da sprint, às 23:59 |
| 📚 | Prova escrita, presencial, em laboratório |
| 🔴 | Feriado ou atividades suspensas |
| — | Sem encontro |

---

## Estrutura das sprints

Uma Sprint 0 de quatro semanas e duas sprints de projeto. A Sprint 2 é a entrega final. Cada sprint tem aulas de conteúdo no início, encontros de acompanhamento no meio e uma *daily meeting* com cada grupo na última semana.

Todos os grupos participam da *daily meeting* em todas as sprints, exceto na Sprint 0. As regras de nota estão em [AVALIACAO.md](AVALIACAO.md).

---

## Visão geral

| Bloco | Período | Tema | *Daily meetings* | Entrega |
|-------|---------|------|---------------|---------|
| Sprint 0 | 17/08 a 14/09 | Fundamentos e ambiente poliglota | — | 16/09 |
| Sprint 1 | 14/09 a 16/10 | Serviço de CRUD em Ktor ou Quarkus | 28 e 30/09, online | 16/10 |
| Sprint 2 (final) | 01/11 a 30/11, com o conteúdo em 19 e 21/10 | Serviço Go, gRPC e implantação | 23 e 25/11, online | 30/11 |
| Prova escrita | 11/11 | Sprints 0 e 1 e o conteúdo da Sprint 2 | — | — |
| Prova de reposição | 02/12 | Sprints 0 a 2, cumulativa e opcional | — | — |

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

## Daily meetings

| Sprint | Online, pelo Google Meet | Em sala, para quem preferir |
|--------|--------------------------|-----------------------------|
| Sprint 1 | 28 e 30/09 (realizadas) | — |
| Sprint 2 (final) | 23 e 25/11 | 18/11 |

No fim de cada sprint, o professor conversa com cada grupo no formato de uma *daily meeting*: uma conversa rápida, pelo Google Meet, em que o grupo mostra o que fez e o que pretende fazer até a entrega. Não é preciso preparar nada. O professor chama os grupos um a um pelo Discord, e não é preciso ficar na chamada antes da sua vez. O grupo que preferir conversar em sala avisa com antecedência e faz a sua *daily* na aula presencial de 18/11. As regras estão em [AVALIACAO.md](AVALIACAO.md#4-componente-c--comunicação).

---

## Aulas presenciais e online

| Semana | Segunda | Quarta |
|--------|---------|--------|
| 05 e 07/10 | 🟢 presencial | 🟢 presencial |
| 12 e 14/10 | 🔴 feriado | 🔵 online |
| 19 e 21/10 | 🟢 presencial | 🟢 presencial |
| 26 e 28/10 | 🔵 online | 🔴 feriado |
| 02 e 04/11 | 🔴 feriado | 🔵 online |
| 09 e 11/11 | 🔵 online | 🟢 presencial (prova) |
| 16 e 18/11 | 🔵 online | 🟢 presencial |
| 23 e 25/11 | 🔵 online (*daily meetings*) | 🔵 online (*daily meetings*) |
| 30/11 e 02/12 | ⏳ a definir | 🟢 presencial (reposição) |

---

## Sprint 0 — Fundamentos e ambiente poliglota

**17/08 a 14/09. Entrega: 16/09 (quarta), 23:59.**

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 17/08 | Seg | — | Não houve aula |
| 19/08 | Qua | 🟢 | Apresentação do curso, dos critérios de avaliação e da arquitetura de referência. HTTP: recurso e representação, URI, métodos, idempotência, códigos de status e cabeçalhos. Arquitetura de serviços: monólito, monólito modular e microsserviços. **Kotlin com Ktor OU Java com Quarkus — escolha do grupo** |
| 24/08 | Seg | 🟢 | Cache HTTP: frescor e validação, `Cache-Control`, `ETag` e concorrência otimista. REST: as restrições de Fielding e o modelo de maturidade de Richardson. Clean Architecture: regra de dependência, portas e adaptadores, e como ela é verificada. Formação de grupos, escolha de coorte e definição do domínio do projeto |
| 26/08 | Qua | 🔵 | Encontro online — dúvidas sobre a proposta e o domínio |
| 31/08 | Seg | 🔵 | Encontro online de acompanhamento de projetos (aula remanejada) |
| 02/09 | Qua | 🔴 | Aula cancelada — conteúdo remanejado para 09 e 14/09 |
| 07/09 | Seg | 🔴 | Independência do Brasil |
| 09/09 | Qua | 🟢 | Estrutura do monorepo MUSI e mapeamento das camadas em pacotes. O serviço principal em detalhe (Ktor/Quarkus): rotas, injeção de dependência, DTOs, cliente do serviço Go, erros e OpenAPI. |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-0). Guia com templates e exemplos: [SPRINT-0.md](SPRINT-0.md).

---

## Sprint 1 — Serviço de CRUD (Ktor ou Quarkus)

**14/09 a 16/10. Entrega: 16/10 (sexta), 23:59.**

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 14/09 | Seg | 🟢 | Conclui as pendências da Sprint 0 (Go e ambiente reproduzível). Início da Sprint 1 — **visão comparativa**: Kotlin/Ktor × Java/Quarkus. Kotlin: corrotinas, DSL de roteamento, Koin. Java: CDI e Quarkus REST |
| 16/09 | Qua | 🔵 | Encontro online de acompanhamento de projetos. **Entrega da Sprint 0, 23:59** |
| 21/09 | Seg | 🎥 | Aula em vídeo (parte 1): a API de tarefas em Ktor × Quarkus, do servidor ao repositório por interface (passos 1 a 4 do exemplo) |
| 23/09 | Qua | 🔴 | Aula cancelada (professor doente) |
| 28/09 | Seg | 🎤 | *Daily meetings* da Sprint 1, online pelo Google Meet: uma conversa rápida com cada grupo |
| 30/09 | Qua | 🎤 | *Daily meetings* da Sprint 1, online pelo Google Meet |
| 05/10 | Seg | 🟢 | Persistência e migrações (PostgreSQL, Flyway, Exposed × Panache) e testes sem banco e com banco real — passos 5 a 8 do exemplo |
| 07/10 | Qua | 🟢 | OpenAPI, validação com *problem details* e teste de arquitetura — passos 9 a 12 do exemplo |
| 12/10 | Seg | 🔴 | Nossa Senhora Aparecida |
| 14/10 | Qua | 🔵 | Encontro online de acompanhamento de projetos |
| 16/10 | Sex | 🚀 | **Entrega da Sprint 1, 23:59** |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-1). Guia: [SPRINT-1.md](SPRINT-1.md).

---

## Sprint 2 — Serviço Go, gRPC e implantação

**01/11 a 30/11. Entrega final: 30/11 (segunda), 23:59.**

Esta é a última sprint: o que for entregue em 30/11 é o produto final do semestre.

| Data | Dia | Tipo | Atividade |
|------|-----|------|-----------|
| 19/10 | Seg | 🟢 | Go idiomático para serviços: pacotes, interfaces, erros e `context`. Protocol Buffers e Buf: mensagens, serviços, `buf lint` e geração de stubs. gRPC unário, deadlines e tratamento de erro |
| 21/10 | Qua | 🟢 | Integração do serviço principal (Ktor/Quarkus) com o serviço Go, e `docker compose` com tudo. Implantação: banco gerenciado, plataforma de container, imagens no GHCR, migrações por task ou CI, health checks |
| 26/10 | Seg | 🔵 | Encontro online de acompanhamento de projetos |
| 28/10 | Qua | 🔴 | Dia do Servidor Público |
| 02/11 | Seg | 🔴 | Finados |
| 04/11 | Qua | 🔵 | Encontro online: revisão para a prova e dúvidas |
| 09/11 | Seg | 🔵 | Encontro online de acompanhamento de projetos |
| 11/11 | Qua | 📚 | **Prova escrita** — presencial, em laboratório. Sprints 0 e 1 e o conteúdo da Sprint 2 |
| 16/11 | Seg | 🔵 | Encontro online de acompanhamento de projetos |
| 18/11 | Qua | 🟢 | Oficina de projeto em sala. *Daily meeting* dos grupos que preferirem o presencial |
| 23/11 | Seg | 🎤 | *Daily meetings* da Sprint 2, online pelo Google Meet |
| 25/11 | Qua | 🎤 | *Daily meetings* da Sprint 2, online pelo Google Meet |
| 30/11 | Seg | ⏳ | A definir: aula reservada, a ser alocada conforme a demanda. 🚀 **Entrega final, 23:59** |
| 02/12 | Qua | 📚 | **Prova de reposição** — presencial, em laboratório. Cumulativa, Sprints 0 a 2. Opcional |
| 07/12 a 16/12 | — | — | Sem encontro. Divulgação das notas e do retorno escrito no SIGAA |

O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-2).
