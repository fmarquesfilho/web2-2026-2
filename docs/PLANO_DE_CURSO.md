# Plano de Curso — DIM0547 Desenvolvimento de Sistemas Web II

**Curso**: Bacharelado em Engenharia de Software — UFRN/DIMAp
**Carga horária**: 60 horas
**Horário**: Segundas e quartas, 13:00 às 14:40 (24T12)
**Período**: 17/08/2026 a 19/12/2026
**Docente**: Fernando Figueira Filho — fernando.figueira@ufrn.br

As aulas de 10/08, 12/08 e 17/08 não foram realizadas. O curso inicia em 19/08.

---

## Ementa

Desenvolvimento de aplicações web em arquitetura de serviços poliglota. Microsserviços em Go com Clean Architecture e serviço de CRUD em **Kotlin com Ktor** ou **Java com Quarkus** — a critério do grupo. Comunicação entre serviços com gRPC e Protocol Buffers. Persistência relacional gerenciada, estratégias de cache, autenticação e autorização, segurança de APIs. Testes automatizados executáveis local e remotamente, containerização, integração contínua com verificações arquiteturais e documentação publicada como site estático.

---

## Objetivos

### Geral

Capacitar o estudante a projetar, implementar, testar e implantar sistemas web distribuídos com múltiplas linguagens, aplicando Clean Architecture, contratos versionados entre serviços e automação verificável de qualidade.

### Específicos

Ao final do curso, o estudante será capaz de:

1. Decidir a distribuição de responsabilidades entre serviços de um sistema web e justificar a decisão
2. Implementar um serviço de CRUD em **Kotlin com Ktor** ou **Java com Quarkus**
3. Implementar microsserviços em Go idiomático com Clean Architecture e gRPC
4. Definir, versionar e evoluir contratos com Protocol Buffers, detectando quebras de compatibilidade automaticamente
5. Modelar e evoluir esquemas relacionais com migrações versionadas em PostgreSQL gerenciado
6. Projetar e medir estratégias de cache
7. Implementar autenticação e autorização com OAuth 2.0, OpenID Connect e JWT, e mitigar as ameaças do OWASP API Security Top 10
8. Escrever testes em múltiplos níveis que executem localmente com Docker Desktop e remotamente em integração contínua
9. Configurar um pipeline de CI com build, testes, lint, verificações de arquitetura, análise estática de segurança e atualização automática de dependências
10. Publicar documentação e referência de API como site estático a partir do repositório

---

## Mudanças em relação a 2026.1

O curso deixa de ser construído exclusivamente em Go e passa a adotar uma arquitetura poliglota. O serviço principal pode ser implementado em **Kotlin com Ktor** ou **Java com Quarkus**, a critério do grupo. A comunicação entre serviços é feita via gRPC. As listas de exercícios são extintas: o domínio técnico é verificado no projeto e em uma prova individual. O conteúdo passa a ser apresentado presencialmente, e os vídeos de 2026.1 permanecem como material de apoio.

---

## Organização do curso

### Estrutura das sprints

Uma Sprint 0 de quatro semanas, três sprints de projeto e um bloco final, com apresentações ao fim de cada sprint. Datas, conteúdo de cada aula e prazos: [CRONOGRAMA.md](CRONOGRAMA.md).



### Projeto integrador

Equipes de 1 a 4 estudantes desenvolvem um sistema ao longo do semestre, em um único repositório público contendo todos os serviços, a infraestrutura e a documentação. A atividade no repositório compõe a nota.

Equipes podem, opcionalmente, integrar este projeto com os de Sistemas Móveis (DIM0524) e Processos de Software (DIM0510), com bônus na nota. Ver [AVALIACAO.md](AVALIACAO.md).

---

## Arquitetura de referência

Os projetos adotam uma arquitetura poliglota orientada a serviços: um serviço de CRUD em **Kotlin com Ktor** ou **Java com Quarkus** (escolha do grupo), microsserviços em Go comunicando-se por gRPC, PostgreSQL gerenciado e documentação publicada como site estático. O domínio de aplicação é de escolha livre.

Diagrama, justificativas e configuração: [STACK.md](STACK.md#1-arquitetura-de-referência).

---

## Conteúdo programático

### Sprint 0 — Fundamentos e ambiente

HTTP em profundidade: métodos, códigos de status, cabeçalhos, negociação de conteúdo, cache. Arquitetura de serviços: monólito, monólito modular e microsserviços. Clean Architecture: regra de dependência, portas e adaptadores. Ambiente reproduzível: `mise`, Docker Compose e estrutura de monorepo poliglota.

### Sprint 1 — Serviço de CRUD (Ktor ou Quarkus)

**Kotlin com Ktor**: corrotinas, DSL de roteamento, injeção de dependência com Koin ou Kodein-DI, validação com Kotlinx.serialization e `kotlin-validation`, tratamento de erros com `StatusPages`, persistência com Exposed ou Ktorm, migrações com Flyway, testes com `kotlin-test` e Testcontainers. Documentação com OpenAPI via `ktor-openapi`.

**Java com Quarkus**: CDI, RESTEasy Reactive, Bean Validation, tratamento de erros com `ExceptionMapper`, Hibernate ORM com Panache ou JPA, migrações com Flyway, testes com JUnit 5 e Testcontainers. Documentação com `quarkus-smallrye-openapi`.

Ambos: Arquitetura verificada com **ArchUnit** no Java ou com testes de arquitetura customizados no Kotlin; testes executáveis local e remotamente.

### Sprint 2 — Microsserviços em Go e gRPC

Go idiomático para serviços: pacotes, interfaces, erros, contexto, concorrência. Clean Architecture em Go com verificação por arch-go. Protocol Buffers: mensagens, serviços, evolução de esquema e compatibilidade. gRPC unário e streaming, deadlines, interceptors, health checking. Buf: lint, detecção de quebras e geração de stubs para Go, Java e Kotlin. Integração entre o serviço principal (Ktor/Quarkus) e o serviço Go.

### Sprint 3 — Dados, cache e implantação

PostgreSQL gerenciado com Neon: branches de banco, connection pooling, limites de uso. Modelagem, índices e planos de execução. Estratégias de cache: cache-aside, write-through, TTL, invalidação, cache stampede. Serviço de cache em Go com métricas de acerto. Implantação em plataformas de container gratuitas e publicação de imagens no GitHub Container Registry. Logs estruturados, health checks e métricas.

### Bloco final — Segurança, automação e documentação

Documentação como parte do produto: site estático, registros de decisão de arquitetura, referência de API gerada do OpenAPI, verificação de defasagem de documentação no CI. GraphQL: aplicabilidade e comparação com REST e gRPC. Concorrência: goroutines e channels em Go, corrotinas em Kotlin, virtual threads em Java. OAuth 2.0 e OpenID Connect. JWT, refresh tokens e rotação de chaves. Spring Security 6 (para Quarkus via `quarkus-oidc`) ou Ktor com `ktor-auth` como resource server: filtros, autorização por rota e por método. OWASP API Security Top 10: BOLA, autenticação quebrada, exposição excessiva de dados, rate limiting. Gestão de segredos. Pipeline completo: matriz de jobs, cache de dependências, análise estática de segurança com Semgrep, atualização automática de dependências com Renovate, hooks de pre-push e tasks do `mise`.

---

## Avaliação

Nota final por média aritmética de três unidades, compostas pelas entregas das sprints e por duas provas escritas, valendo a maior das duas notas. Cada sprint é avaliada em entrega técnica, atividade no repositório e comunicação.

Composição, pesos e regras: [AVALIACAO.md](AVALIACAO.md). Critérios de cada entrega: [RUBRICAS.md](RUBRICAS.md).

---

## Bibliografia

### Básica

MARTIN, Robert C. *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall, 2017.

NEWMAN, Sam. *Building Microservices*. 2. ed. O'Reilly, 2021.

VALENTE, Marco Tulio. *Engenharia de Software Moderna*. UFMG, 2020. Disponível em: https://engsoftmoderna.info/

### Complementar

RICHARDS, Mark; FORD, Neal. *Fundamentals of Software Architecture*. O'Reilly, 2020.

OWASP. *API Security Top 10*. Disponível em: https://owasp.org/API-Security/

Quarkus Documentation. Disponível em: https://quarkus.io/guides/

Ktor Documentation. Disponível em: https://ktor.io/docs/

*Effective Go*. Disponível em: https://go.dev/doc/effective_go

gRPC Documentation. Disponível em: https://grpc.io/docs/

Protocol Buffers Language Guide. Disponível em: https://protobuf.dev/programming-guides/proto3/

Testcontainers Guides. Disponível em: https://testcontainers.com/guides/

PostgreSQL Documentation. Disponível em: https://www.postgresql.org/docs/

RFC 6749 (OAuth 2.0), RFC 7519 (JWT), RFC 9700 (OAuth 2.0 Security Best Current Practice), RFC 9457 (Problem Details for HTTP APIs).

Toda a bibliografia complementar é de acesso gratuito.