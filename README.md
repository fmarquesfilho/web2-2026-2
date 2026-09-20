# DIM0547 — Desenvolvimento de Sistemas Web II

**Bacharelado em Engenharia de Software — UFRN/DIMAp**
**Período letivo 2026.2 — 17/08/2026 a 19/12/2026**
**Turma 01 · Segundas e quartas, 13:00 às 14:40 (24T12)**
**Docente**: Fernando Figueira Filho — fernando.figueira@ufrn.br

As aulas de 10/08, 12/08 e 17/08 não foram realizadas. O curso inicia em 19/08.

---

## Documentos da disciplina

Cada informação vive em um único documento. Em caso de divergência, vale o documento indicado abaixo.

| Documento | Conteúdo |
|-----------|----------|
| [Plano de Curso](docs/PLANO_DE_CURSO.md) | Ementa, objetivos, conteúdo programático e bibliografia |
| [Cronograma](docs/CRONOGRAMA.md) | **Todas as datas** e o conteúdo de cada aula |
| [Sistemática de Avaliação](docs/AVALIACAO.md) | **Pesos e regras de nota**, bônus, grupos, provas e integridade acadêmica |
| [Rúbricas](docs/RUBRICAS.md) | **O que entregar** em cada sprint e como é avaliado |
| [Guia da Sprint 0](docs/SPRINT-0.md) | Templates de visão do produto, MVP e da divisão Java × Go |
| [Guia da Sprint 1](docs/SPRINT-1.md) · [tarefas](docs/SPRINT-1-TAREFAS.md) | Serviço de CRUD: persistência, migrações, arquitetura verificada, testes e OpenAPI |
| [Leituras](leituras/) | Guias de leitura de cada aula (`web2-s1-pte1.md`, `web2-s1-pte2.md`, ...) |
| [Stack Tecnológica](docs/STACK.md) | **Arquitetura de referência**, tecnologias, hospedagem gratuita e ambiente |

---

## Projeto

Equipes de 1 a 4 estudantes desenvolvem um sistema ao longo do semestre, em um único monorepo **público** contendo todos os serviços, a infraestrutura e a documentação. Não há listas de exercícios: o domínio técnico é verificado no projeto e nas provas escritas.

O semestre é organizado em uma Sprint 0, três sprints de projeto e um bloco final, com apresentação de todos os grupos ao fim de cada sprint. Datas em [docs/CRONOGRAMA.md](docs/CRONOGRAMA.md); composição das notas em [docs/AVALIACAO.md](docs/AVALIACAO.md).

---

## Exemplos das aulas

Construídos do zero em aula, passo a passo. Cada `PASSOS.md` reconstrói o exemplo do
primeiro comando ao último, e a pasta contém o estado final.

| Exemplo | Stack | Passos |
|---|---|---|
| [`exemplos/ktor-tarefas`](exemplos/ktor-tarefas) | Kotlin · Ktor · Koin · Exposed | [`PASSOS.md`](exemplos/ktor-tarefas/PASSOS.md) |
| [`exemplos/quarkus-tarefas`](exemplos/quarkus-tarefas) | Java 25 · Quarkus · CDI · Panache | [`PASSOS.md`](exemplos/quarkus-tarefas/PASSOS.md) |

Os dois têm os mesmos nove passos, numerados igual, para a leitura lado a lado. Do Passo 6
em diante é preciso ter o **Docker** aberto: o Ktor sobe o PostgreSQL com `docker compose`,
e o Quarkus, com o Dev Services.

### Como rodar

O [`mise.toml`](mise.toml) da raiz fixa as versões (JDK 25, Maven 3.9) e dá um nome curto a
cada comando dos passos:

```bash
mise install                 # uma vez: baixa o JDK e o Maven
mise tasks                   # a lista completa

mise run ktor:banco          # PostgreSQL do exemplo Ktor
mise run ktor:run            # API Ktor na 8080
mise run ktor:test           # testes (o de integração precisa de Docker)

mise run quarkus:dev         # API Quarkus na 8081, com banco do Dev Services
mise run quarkus:test
```

O `mise` é conveniência, não requisito: cada task mostra, no `mise.toml`, o comando completo
(`./gradlew run`, `mvn quarkus:dev`) que continua valendo. Instalação:
[mise.jdx.dev](https://mise.jdx.dev/getting-started.html).

**Sem instalar nada:** crie um Codespace (Code → Codespaces). O
[`.devcontainer/`](.devcontainer) traz Java 25, Maven, Go, Docker e o próprio `mise`.

---

## Licença

Material licenciado sob [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/).
