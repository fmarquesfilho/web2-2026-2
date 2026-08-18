# Stack Tecnológica — DIM0547 Desenvolvimento de Sistemas Web II

Documento de referência das tecnologias adotadas no curso, das razões da escolha e das instruções de configuração. Complementa o [Plano de Curso](PLANO_DE_CURSO.md).

Todas as ferramentas e serviços listados são gratuitos e não exigem cartão de crédito.

---

## 1. Arquitetura de referência

```
                     Cliente web ou aplicativo móvel
                                  │
                                  │ HTTPS · REST · OpenAPI
                                  ▼
   ┌──────────────────────────────────────────────────────────┐
   │  api — Spring Boot 3 · Java 21+                          │
   │  Web · Aplicação · Domínio · Infraestrutura              │
   │  ArchUnit verifica a regra de dependência no CI          │
   └───────┬──────────────────────────┬───────────────────────┘
           │ gRPC                     │ gRPC
   ┌───────▼────────┐        ┌────────▼────────┐     ┌────────────┐
   │ worker — Go    │        │ cache — Go      │     │ PostgreSQL │
   │ arch-go no CI  │        │ TTL, invalidação│◄────┤   (Neon)   │
   └────────────────┘        └─────────────────┘     └────────────┘

   Contratos:    /protos/*.proto → buf lint · buf breaking · buf generate
   Documentação: /docs → site estático e referência de API, públicos
```

| Camada | Tecnologia | Papel no sistema |
|--------|-----------|------------------|
| Serviço de CRUD | Java 21+ · Spring Boot 3 | Entidades de domínio, persistência, autenticação, orquestração |
| Microsserviços | Go · gRPC | Trabalho de I/O intensivo, processamento, agendamento, cache |
| Contratos | Protocol Buffers · Buf | Interface versionada entre serviços, validada no CI |
| Banco de dados | PostgreSQL · Neon | Persistência relacional gerenciada |
| Documentação | Site estático + referência de API | Publicada a partir do repositório |

---

## 2. Escolhas e justificativas

### Java com Spring Boot no serviço de CRUD

Spring Boot concentra a maior demanda de mercado entre os frameworks Java e tem o ecossistema mais completo para as necessidades do curso: Spring Data JPA para persistência, Spring Security para autenticação e autorização, springdoc para OpenAPI e integração nativa com Testcontainers. Java 21 introduziu virtual threads, records e pattern matching, que reduzem consideravelmente o código cerimonial de versões anteriores.

Alternativas consideradas: Quarkus, que produz imagens menores e inicia mais rápido em containers, mas tem ecossistema e material didático menores; e Micronaut, com injeção de dependências em tempo de compilação, porém com menor presença no mercado brasileiro.

### Go nos microsserviços

Go é adequado a serviços de I/O intensivo pelo modelo de concorrência baseado em goroutines, pelo binário estático que produz imagens de container pequenas e pelo tempo de partida próximo de zero. A separação entre um serviço de CRUD em Java e microsserviços em Go reproduz uma decisão arquitetural comum na indústria: usar cada linguagem no domínio em que é mais produtiva.

### gRPC e Protocol Buffers entre serviços

O contrato entre serviços é definido em arquivos `.proto` versionados e verificados no CI. `buf lint` valida o estilo do contrato e `buf breaking` detecta alterações incompatíveis antes que cheguem à branch principal. Os stubs de Go e Java são gerados a partir do contrato, nunca escritos manualmente.

A alternativa seria REST interno com OpenAPI como contrato, que exigiria menos ferramental novo, mas não oferece a mesma verificação automática de compatibilidade nem streaming bidirecional.

### Clean Architecture com verificação automática

A regra de dependência — camadas internas não conhecem camadas externas — é verificada por *fitness functions* no pipeline: ArchUnit no Java e arch-go no Go. Se uma classe de domínio importar um pacote de infraestrutura, o CI falha. A regra deixa de depender de disciplina individual e passa a ser garantida pela automação.

---

## 3. Ferramentas

| Categoria | Ferramenta | Observação |
|-----------|-----------|------------|
| Versionamento e CI | GitHub e GitHub Actions | Minutos ilimitados em repositórios públicos |
| Versões e tasks | `mise` | Gerencia versões de Go, Java e Node e substitui makefiles |
| Containers locais | Docker Desktop e Docker Compose | Ambiente de desenvolvimento e execução de testes |
| Registro de imagens | GitHub Container Registry | Gratuito para repositórios públicos |
| Banco de dados | Neon | PostgreSQL serverless com branches de banco |
| Contratos | Buf CLI | `buf lint`, `buf breaking`, `buf generate` |
| Arquitetura | ArchUnit e arch-go | Verificação da regra de dependência no CI |
| Migrações | Flyway | Migrações versionadas e idempotentes |
| Testes | JUnit 5, MockMvc, Testcontainers, `go test` | Executam local e remotamente |
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

Para reduzir o consumo de memória do serviço Java, use imagens com JRE slim e build multiestágio. A compilação nativa com GraalVM reduz ainda mais o consumo e o tempo de partida, e é considerada diferencial na avaliação.

---

## 5. Configuração do ambiente

### Pré-requisitos

- Docker Desktop instalado e em execução
- `mise` instalado: https://mise.jdx.dev/getting-started.html
- Conta no GitHub e conta no Neon

### Estrutura esperada do repositório

```
.
├── api/                    serviço Spring Boot
├── services/               microsserviços Go
├── protos/                 contratos Protocol Buffers
├── docs/                   documentação e site estático
├── .github/workflows/      pipelines de CI
├── mise.toml               versões e tasks
├── docker-compose.yml      ambiente local completo
├── renovate.json           atualização de dependências
└── arch-go.yml             regras de arquitetura do código Go
```

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
| Build de Java e Go | Sprint 0 |
| Testes unitários e de integração | Sprint 1 |
| ArchUnit | Sprint 1 |
| `buf lint` e `buf breaking` | Sprint 2 |
| Sincronia dos stubs gerados | Sprint 2 |
| arch-go | Sprint 2 |
| Publicação de imagens no GHCR | Sprint 3 |
| Aplicação de migrações | Sprint 3 |
| Semgrep e secret scanning | Bloco final |
| Renovate | Bloco final |
| Verificação de defasagem da documentação | Bloco final |

---

## 6. Ambientes de desenvolvimento online

Nenhuma etapa do curso exige máquina própria com Docker instalado.

| Ambiente | Cadastro | O que roda | Limite |
|---|---|---|---|
| **GitHub Codespaces** | sim | VS Code no navegador, Java, Go, Docker, testes, CI | 180 h por mês com o GitHub Student Pack |
| **Google Cloud Shell Editor** | conta Google | Java, Go, Docker, 5 GB persistentes | CPU modesta; sessão expira por inatividade |

**Codespaces é a opção recomendada.** Versionar um `.devcontainer/devcontainer.json` no monorepo, declarando as versões de Java, Go e as ferramentas do `mise`, resolve de vez o "na minha máquina funciona" — e é o mesmo princípio de reprodutibilidade que o `docker-compose.yml` aplica aos serviços.

O Cloud Shell serve como alternativa sem cadastro adicional, suficiente para compilar e rodar a suíte de testes, mas apertado para subir o Compose completo com banco e dois serviços.

---

## 7. Referências de configuração

- mise: https://mise.jdx.dev/
- Buf: https://buf.build/docs/
- ArchUnit: https://www.archunit.org/userguide/html/000_Index.html
- arch-go: https://github.com/arch-go/arch-go
- Testcontainers: https://testcontainers.com/guides/
- Neon: https://neon.com/docs/introduction
- Flyway: https://documentation.red-gate.com/flyway
- Renovate: https://docs.renovatebot.com/
- Semgrep: https://semgrep.dev/docs/
