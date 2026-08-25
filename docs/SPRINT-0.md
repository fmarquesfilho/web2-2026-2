# Guia da Sprint 0 — DIM0547

Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral). O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-0).

A Sprint 0 é a fase de planejamento: a equipe define o que vai construir, decide entre Kotlin/Ktor e Java/Quarkus, define a divisão de responsabilidades com o(s) microsserviço(s) Go, e deixa o monorepo com CI verde (passando).

---

## Visão do produto

Declaração que responde por que o produto existe e qual problema resolve. Use este template:

```
Para [usuários-alvo]
Que [problema ou necessidade]
O [nome do produto] é um [categoria]
Que [benefício principal]
Diferente de [alternativa existente]
Nosso produto [diferencial único]
```

**Exemplo:**

```
Para estudantes que acompanham editais de bolsas e auxílios
Que precisam visitar dezenas de páginas para não perder prazo
O Radar é uma API de agregação de editais
Que coleta, normaliza e notifica oportunidades por perfil
Diferente de listas manuais em grupos de mensagem
Nosso produto atualiza sozinho e responde busca em milissegundos
```

Verifique se a visão define o usuário, nomeia um problema específico, explicita o valor único e é realista para um semestre com equipe de até quatro pessoas.

---

## A escolha entre Kotlin/Ktor e Java/Quarkus

Esta é a primeira decisão arquitetural da disciplina. O grupo deve justificá-la com base em:

- **Perfil da equipe**: familiaridade prévia, interesse em aprender
- **Características do domínio**: necessidade de desempenho, reatividade, integração com ferramentas
- **Mercado e ecossistema**: empregabilidade, bibliotecas disponíveis
- **Diferenciação didática**: explorar uma stack menos usual

**Não há escolha certa ou errada** — a qualidade da justificativa é que será avaliada.

**Exemplo de justificativa (Kotlin/Ktor):**

> Optamos por Kotlin com Ktor porque a equipe já tem experiência em Java e deseja explorar uma linguagem mais moderna. O domínio do sistema envolve várias chamadas a serviços externos (I/O), e as corrotinas do Kotlin facilitam a concorrência sem a complexidade de threads. Além disso, a interoperabilidade com Java permite usar bibliotecas maduras como Flyway e Testcontainers.

**Exemplo de justificativa (Java/Quarkus):**

> Escolhemos Java com Quarkus porque ele é otimizado para containers e oferece um tempo de partida muito baixo, o que é importante para o deploy na nuvem. A equipe tem mais afinidade com Java e quer consolidar os fundamentos da JVM. O ecossistema do Quarkus é rico e bem documentado, com suporte nativo a OpenAPI, Testcontainers e GraalVM.

---

## A divisão entre o serviço principal e os microsserviços Go

A segunda decisão arquitetural: o que fica no serviço principal (Ktor/Quarkus) e o que vai para Go.

| Vai para o serviço principal (Ktor/Quarkus) | Vai para um microsserviço Go |
|---|---|
| Entidades de domínio e suas regras | Coleta e integração com sistemas externos |
| Persistência e migrações | Processamento em lote ou agendado |
| Autenticação e autorização | Trabalho concorrente de I/O intensivo |
| Orquestração dos casos de uso | Cache e pré-computação |


---

## Definição do MVP

O MVP é o escopo mínimo que entrega valor. Declare explicitamente o que fica **fora**.

**Exemplo, para o Radar:**

| No MVP | Fora do MVP |
|---|---|
| CRUD de editais e de fontes monitoradas | Recomendação por perfil com IA |
| Coletor que varre as fontes periodicamente | Envio de e-mail e push |
| Busca por texto, área e prazo | Área administrativa web |
| Autenticação e perfis de interesse | Histórico e estatísticas |
| Cache das buscas mais frequentes | Importação de PDF de edital |

Enuncie também a hipótese de valor: *acreditamos que [usuários] vão [comportamento] porque [benefício]*.

---

## Backlog inicial

Formato de história de usuário: **como [papel], quero [ação] para [benefício]**. Priorize e estime.

| Prio | História | Critérios de aceitação | Sprint |
|---|---|---|---|
| P1 | Como estudante, quero listar editais abertos para achar oportunidade | Paginado, ordenado por prazo; filtra por área | 1 |
| P1 | Como administrador, quero cadastrar fontes para ampliar a cobertura | CRUD com URL validada; fonte única por domínio | 1 |
| P1 | Como sistema, quero coletar as fontes periodicamente para manter dados atuais | Serviço Go agendado; deduplica por identificador da fonte | 2 |
| P2 | Como estudante, quero me autenticar para salvar meus interesses | JWT com refresh; rotas de interesse protegidas | Final |
| P2 | Como estudante, quero busca rápida para não esperar | Cache das consultas frequentes; métrica de acerto exposta | 3 |

P1 é essencial ao MVP, P2 é importante, P3 é desejável.

---

## Estrutura do vídeo — 5 minutos

| Tempo | Conteúdo |
|---|---|
| 30 s | Equipe: nome, integrantes e o que cada um faz |
| 1 min 30 s | Visão do produto: problema, público e proposta de valor |
| 1 min | **Justificativa da escolha entre Kotlin/Ktor e Java/Quarkus** |
| 1 min | **Divisão de responsabilidades entre o serviço principal e Go**, com justificativa, e o monorepo com CI verde (passando) |
| 30 s | MVP: o que entra, o que fica fora, critérios de sucesso |
| 30 s | Backlog em visão geral e riscos identificados |

Todos os integrantes devem falar.

---

## Estrutura de `docs/proposta.md`

1. Visão do produto, no template
2. Definição do MVP: dentro e fora do escopo
3. Link para backlog inicial, com as histórias priorizadas (pode ser para o próprio repositório caso o backlog esteja registrado nele — o GitHub Projects mora dentro do repositório do GitHub)
4. Entidades principais do domínio
5. Decisão: Kotlin/Ktor ou Java/Quarkus — com breve justificativa
6. Divisão de responsabilidades entre o serviço principal e Go — com justificativa
7. Equipe: nome, matrícula e papel de cada integrante
8. Coorte de apresentação e, se houver, integração com outra disciplina

Máximo 3 páginas.