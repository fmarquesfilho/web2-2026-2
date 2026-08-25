# Stack Tecnológica — DIM0547 Desenvolvimento de Sistemas Web II

Documento de referência das tecnologias adotadas no curso, das razões da escolha e das instruções de configuração. Complementa o [Plano de Curso](PLANO_DE_CURSO.md).

Todas as ferramentas e serviços listados são gratuitos e não exigem cartão de crédito.

---

## 1. Arquitetura de referência

| Camada | Tecnologia | Papel no sistema |
|--------|-----------|------------------|
| Serviço de CRUD | Kotlin/Ktor ou Java/Quarkus (escolha do grupo) | Entidades de domínio, persistência, autenticação, orquestração |
| Microsserviços | Go · gRPC | Trabalho de I/O intensivo, processamento, agendamento, cache |
| Contratos | Protocol Buffers · Buf | Interface versionada entre serviços, validada no CI |
| Banco de dados | PostgreSQL · Neon | Persistência relacional gerenciada |
| Documentação | Site estático + referência de API | Publicada a partir do repositório |

---

## 2. Escolhas e justificativas

### Kotlin com Ktor

Kotlin é uma linguagem moderna, interoperável com Java e com sintaxe mais concisa. Ktor é um framework assíncrono nativo, baseado em corrotinas, que facilita a construção de serviços leves e reativos. A escolha é adequada para equipes que desejam explorar uma stack mais contemporânea e produtiva.

### Java com Quarkus

Quarkus é um framework Kubernetes-native com suporte a GraalVM, que produz imagens menores e inicia mais rápido em containers. Oferece CDI, RESTEasy Reactive e integração com Hibernate Panache, mantendo a familiaridade do ecossistema Java com um modelo mais enxuto.

### Go nos microsserviços

Go é adequado a serviços de I/O intensivo pelo modelo de concorrência baseado em goroutines, pelo binário estático que produz imagens de container pequenas e pelo tempo de partida próximo de zero. A separação entre um serviço de CRUD em Kotlin/Java e microsserviços em Go reproduz uma decisão arquitetural comum na indústria.

### gRPC e Protocol Buffers entre serviços

O contrato entre serviços é definido em arquivos `.proto` versionados e verificados no CI. `buf lint` valida o estilo do contrato e `buf breaking` detecta alterações incompatíveis antes que cheguem à branch principal. Os stubs de Go, Java e Kotlin são gerados a partir do contrato, nunca escritos manualmente.

### Clean Architecture com verificação automática

A regra de dependência — camadas internas não conhecem camadas externas — é verificada por *fitness functions* no pipeline: ArchUnit no Java, ou testes de arquitetura no Kotlin (ex.: `archunit-kotlin`), e arch-go no Go.

---

## 3. Ferramentas

| Categoria | Ferramenta | Observação |
|-----------|-----------|------------|
| Versionamento e CI | GitHub e GitHub Actions | Minutos ilimitados em repositórios públicos |
| Versões e tasks | `mise` | Gerencia versões de Go, Java, Kotlin e Node; substitui makefiles |
| Containers locais | Docker Desktop e Docker Compose | Ambiente de desenvolvimento e execução de testes |
| Registro de imagens | GitHub Container Registry | Gratuito para repositórios públicos |
| Banco de dados | Neon | PostgreSQL serverless com branches de banco |
| Contratos | Buf CLI | `buf lint`, `buf breaking`, `buf generate` |
| Arquitetura | ArchUnit, arch-go, archunit-kotlin | Verificação da regra de dependência no CI |
| Migrações | Flyway | Migrações versionadas e idempotentes |
| Testes | JUnit 5, kotlin-test, Testcontainers | Executam local e remotamente |
| Segurança | Semgrep e Renovate | Análise estática e atualização de dependências |
| Documentação | Docusaurus, Rspress, MkDocs ou VitePress | Escolha livre do grupo |
| Referência de API | Scalar, Redoc ou Swagger UI | Gerada a partir do OpenAPI |

---

## 4. Hospedagem gratuita

O sistema deve estar acessível publicamente a partir da Sprint 3.

| Plataforma | Situação em 2026 | Recomendação |
|-----------|------------------|--------------|
| Northflank | Dois serviços com CPU e RAM dedicadas, volumes persistentes, sem suspensão por inatividade | Recomendada |
| Render | 750 horas mensais, com suspensão após inatividade | Adequada para demonstração |
| Railway | Crédito mensal de US$ 5, renovável | Suficiente para um serviço |
| Fly.io | Camada gratuita encerrada para contas novas | Não utilizar |
| Koyeb | Plano Starter fechado para novos usuários | Não utilizar |

Para reduzir o consumo de memória do serviço Java/Kotlin, use imagens com JRE slim e build multiestágio. No Quarkus, a compilação nativa com GraalVM reduz ainda mais o consumo e o tempo de partida — considerado diferencial na avaliação. No Ktor, o uso de corrotinas e o pacote nativo também ajudam.

---

## 5. Configuração do ambiente

### Pré-requisitos

- Docker Desktop instalado e em execução
- `mise` instalado: https://mise.jdx.dev/getting-started.html
- Conta no GitHub e conta no Neon


### Tasks mínimas do `mise`

O `mise.toml` do projeto deve expor, no mínimo:

| Task | Função |
|------|--------|
| `mise run build` | Compila todos os serviços |
| `mise run test` | Executa toda a suíte de testes |
| `mise run lint` | Executa linters e verificações de arquitetura |
| `mise run up` | Sobe o ambiente completo com Docker Compose |
| `mise run ci` | Reproduz localmente o pipeline de integração contínua |

O comando `mise run ci` deve produzir o mesmo resultado que o pipeline no GitHub Actions. Executá-lo antes de abrir um pull request evita retrabalho.

### Verificações do pipeline

O pipeline de CI, construído ao longo do semestre, deve conter ao final:

| Verificação | A partir da |
|-------------|-------------|
| Build do serviço principal e Go | Sprint 0 |
| Testes unitários e de integração | Sprint 1 |
| Teste de arquitetura (ArchUnit ou equivalente) | Sprint 1 |
| `buf lint` e `buf breaking` | Sprint 2 |
| Sincronia dos stubs gerados | Sprint 2 |
| arch-go | Sprint 2 |
| Publicação de imagens no GHCR | Sprint 3 |
| Aplicação de migrações | Sprint 3 |
| Semgrep e secret scanning | Bloco final |
| Renovate | Bloco final |
| Verificação de defasagem da documentação | Bloco final |

