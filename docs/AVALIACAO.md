# Sistemática de Avaliação — DIM0547 Desenvolvimento de Sistemas Web II

**Período**: 2026.2 · **Docente**: Fernando Figueira Filho

---

## 1. Nota final

Média aritmética das três unidades. Não há listas de exercícios: o domínio técnico é verificado no projeto e na prova.

| Unidade | Componente | Peso |
|---------|-----------|------|
| U1 | Sprint 0 | 20% |
| | Sprint 1 | 40% |
| | Sprint 2 | 40% |
| U2 | Sprint 3 | 60% |
| | Prova | 40% |
| U3 | Entrega final | 60% |
| | Prova | 40% |

São aplicadas duas provas: a prova escrita, obrigatória, e a prova de reposição, opcional e cumulativa. Vale a **maior das duas notas**, e é ela que entra na U2 e na U3. A U1 não tem prova, e fecha antes da prova de reposição, de modo que cada estudante conhece o próprio desempenho antes de decidir se vai refazer.

Datas de fechamento das unidades e de todas as entregas: [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral).

Aprovação conforme o Regulamento dos Cursos de Graduação da UFRN: média igual ou superior a 5,0, nota igual ou superior a 3,0 em cada unidade e frequência mínima de 75%.

---

## 2. Nota de cada sprint

| Componente | Peso |
|------------|------|
| A. Entrega técnica, conforme [RUBRICAS.md](RUBRICAS.md) | 50% |
| B. Atividade no repositório | 30% |
| C. Comunicação: vídeo e apresentação | 20% |

```
Nota do grupo   = 0,50·A + 0,30·B + 0,20·C
Nota individual = Nota do grupo × Fator de Participação × (1 + Bônus)   [máximo 10,0]
```

---

## 3. Componente B — Atividade no repositório

O repositório deve permanecer público durante o semestre. Ao final de cada sprint são consideradas as métricas do período.

### 3.1 Saúde do repositório

| Indicador | Pontos |
|-----------|--------|
| CI verde na branch principal no prazo | 2,0 |
| Commits em pelo menos metade das semanas da sprint, sem concentração num único dia | 2,0 |
| Ao menos 1 pull request por integrante, integrado | 2,0 |
| Ao menos metade dos PRs com aprovação de outro integrante | 2,0 |
| Itens movimentados no quadro e vinculados a PRs | 2,0 |

Em grupos de um integrante, os dois últimos indicadores viram uso de PRs com descrição e checklist (2,0) e issues fechadas por PR referenciado (2,0).

### 3.2 Fator de Participação

Quatro condições por integrante, no período da sprint:

1. Autoria de ao menos 15% dos commits do grupo, ou de 15% das linhas líquidas alteradas
2. Commits em ao menos metade das semanas
3. Ao menos um pull request autorado e integrado
4. Ao menos uma revisão em PR de outro integrante

| Condições atendidas | Fator |
|--------------------|-------|
| 4 | 1,00 |
| 3 | 0,85 |
| 2 | 0,70 |
| 1 | 0,50 |
| 0 | 0,00 |

O fator não eleva a nota individual acima da nota do grupo.

### 3.3 Integridade

- Contribuições devem ser autoradas com o e-mail da conta GitHub declarada na Sprint 0.
- Pair programming deve usar `Co-authored-by:`.
- Contestações em até 1 semana após a divulgação da nota.

---

## 4. Componente C — Comunicação

### 4.1 Vídeo

Obrigatório em todas as sprints. Link não listado ou público no YouTube, registrado no `README.md`. Todos os integrantes devem falar.

### 4.2 Apresentação

Todos os grupos apresentam ao final de cada sprint, exceto na Sprint 0.

| Coorte | Formato |
|--------|---------|
| B | Online, por Google Meet |
| A | Presencial, em sala de aula |

As datas de cada sessão estão em [CRONOGRAMA.md](CRONOGRAMA.md#apresentações).

A coorte é escolhida na Sprint 0 e vale para o semestre. Se a enquete indicar preferência majoritária pelo online, a sessão presencial também passa a ser online.

C é a média entre vídeo e apresentação. Na Sprint 0, C é a nota do vídeo. O docente pode dirigir perguntas a qualquer integrante sobre qualquer parte da entrega.

---

## 5. Bônus de integração

Grupos que integrarem este projeto com os de Sistemas Móveis (DIM0524) ou Processos de Software (DIM0510) recebem acréscimo de 15% na nota de cada sprint, limitado a 10,0.

Condições: declaração na Sprint 0; produto comum com repositórios vinculados nos READMEs; contribuição desta disciplina distinguível — o backend, os contratos e a infraestrutura são avaliados aqui; integração ativa durante toda a sprint avaliada.

---

## 6. Grupos

- De 1 a 4 integrantes. Cinco apenas mediante justificativa aprovada.
- Formação até a entrega da Sprint 0.
- Alterações de composição valem a partir da sprint seguinte, comunicadas antes do encerramento da sprint em curso.
- Não são aceitas alterações após a entrega da Sprint 3.

---

## 7. Provas escritas

| Prova | Conteúdo |
|-------|----------|
| Prova escrita, obrigatória | Sprints 0 a 2: HTTP, arquitetura de serviços, Clean Architecture, Kotlin/Ktor ou Java/Quarkus, JPA/Exposed, Flyway, testes com Testcontainers, Go, Protocol Buffers e gRPC |
| Prova de reposição, opcional | Todo o conteúdo da primeira prova, acrescido da Sprint 3: PostgreSQL gerenciado, modelagem e índices, estratégias de cache, implantação em containers, logs estruturados e health checks |

Ambas são individuais, com questões fechadas, no Multiprova, presenciais, em laboratório, aplicadas no horário da aula. Permitida consulta a uma folha A4 manuscrita, frente e verso.

A prova de reposição é aberta a qualquer estudante, inclusive a quem já obteve nota alta na primeira. Ela não substitui automaticamente a nota anterior: **vale a maior das duas**. Quem não comparecer permanece com a nota da primeira prova.

O conteúdo do bloco final não é objeto de nenhuma das provas; é avaliado na entrega final e na apresentação.

---

## 8. Prazos e revisão

- Entregas vencem às sextas-feiras, 23:59, nas datas do [cronograma](CRONOGRAMA.md#visão-geral).
- Atraso: 10% de desconto por dia corrido, até 3 dias. Após 72 horas a entrega não é aceita.
- Vale o estado do repositório no momento do prazo, pelo hash do último commit na branch principal.

---

## 9. Uso de ferramentas de IA

Permitido. Toda contribuição submetida deve ser compreendida pelo integrante que a submeteu, que pode ser questionado sobre qualquer trecho durante as apresentações. Submeter conteúdo que não consegue explicar caracteriza fraude acadêmica.

O grupo mantém em `docs/uso-de-ia.md` o registro das ferramentas usadas e das tarefas em que foram aplicadas.