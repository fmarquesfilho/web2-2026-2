# Guia da Sprint 0 — DIM0547

Prazo em [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral). O que entregar e como é avaliado: [RUBRICAS.md](RUBRICAS.md#sprint-0).

A Sprint 0 é a fase de planejamento: a equipe define o que vai construir, decide o que fica em Java e o que fica em Go, e deixa o monorepo com CI verde.

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

## A divisão entre Java e Go

Esta é a decisão arquitetural central da disciplina, e ela é avaliada. A regra de bolso:

| Vai para o serviço Java | Vai para um microsserviço Go |
|---|---|
| Entidades de domínio e suas regras | Coleta e integração com sistemas externos |
| Persistência e migrações | Processamento em lote ou agendado |
| Autenticação e autorização | Trabalho concorrente de I/O intensivo |
| Orquestração dos casos de uso | Cache e pré-computação |

O critério não é "qual linguagem eu prefiro", e sim **qual característica do trabalho** justifica cada escolha. Uma proposta que não souber dizer isso perde ponto.

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
| 1 min 30 s | MVP: o que entra, o que fica fora, critérios de sucesso |
| 1 min | **Divisão Java × Go**, com justificativa, e o monorepo com CI verde |
| 30 s | Backlog em visão geral e riscos identificados |

Todos os integrantes devem falar.

---

## Estrutura de `docs/proposta.md`

1. Visão do produto, no template
2. Definição do MVP: dentro e fora do escopo
3. Link para backlog inicial, com as histórias priorizadas (pode ser para o próprio repositório caso o backlog esteja registrado nele — o GitHub Projects mora dentro do repositório do GitHub)
4. Entidades principais do domínio
5. **Divisão de responsabilidades entre Java e Go, com justificativa**
6. Equipe: nome, matrícula e papel de cada integrante
7. Coorte de apresentação e, se houver, integração com outra disciplina

Máximo 3 páginas.
