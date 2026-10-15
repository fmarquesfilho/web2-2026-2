# Rúbricas — DIM0547 Desenvolvimento de Sistemas Web II

**Período**: 2026.2

Os critérios de todas as entregas estão disponíveis desde o início do semestre, o que permite adiantar trabalho.

Prazos e datas: [CRONOGRAMA.md](CRONOGRAMA.md#visão-geral). Pesos e regras de nota: [AVALIACAO.md](AVALIACAO.md).

---

## Como ler as rúbricas

| Nível | Nota | Significado |
|-------|------|-------------|
| Excelente | 10 | Atende plenamente e demonstra domínio |
| Bom | 8 | Atende, com lacunas menores |
| Suficiente | 6 | Atende no mínimo aceitável |
| Insuficiente | 0 a 4 | Não atende ou está ausente |

O Componente A (entrega técnica, 50%) é a média ponderada dos critérios da sprint. O Componente B (30%) segue [AVALIACAO.md §3](AVALIACAO.md#3-componente-b--atividade-no-repositório). O Componente C (20%) usa a rúbrica de comunicação ao final deste documento.

Critérios marcados com ⚙️ têm resultado binário e podem ser conferidos localmente antes da entrega.

### Portão de qualidade

O pipeline mínimo exigido em todas as sprints está em [STACK.md](STACK.md#verificações-do-pipeline). Pipeline com falha no momento do prazo limita o Componente A à nota 6.

---

## Sprint 0

Templates, exemplos e estrutura do vídeo e da proposta: [SPRINT-0.md](SPRINT-0.md).

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| ⚙️ **Estrutura do monorepo** | 25% | `api/`, `services/`, `protos/`, `docs/`, `mise.toml`, `docker-compose.yml` presentes e coerentes; `mise run build` e `mise run test` passam | Estrutura presente, alguma task não funciona | Estrutura ausente ou não builda |
| ⚙️ **CI passando** | 25% | Workflow roda build dos dois stacks (serviço principal e Go) em todo push e PR, verde em `main` | CI roda, cobre só um stack | Sem CI ou vermelho |
| **Proposta e decisão de arquitetura** | 30% | Domínio delimitado, MVP viável em 4 sprints, escolha justificada entre Kotlin/Ktor e Java/Quarkus e divisão clara de responsabilidades entre o serviço principal e Go, com base em características do trabalho | Proposta plausível, decisão arbitrária | Proposta vaga ou sem decisão |
| **Configuração do processo** | 20% | Repositório público, README completo, GitHub Projects com ≥ 5 itens, coorte declarada | Repositório e quadro criados, incompletos | Repositório privado ou sem quadro |

---

## Sprint 1

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| **CRUD completo** | 30% | ≥ 2 entidades com relacionamento, todas as operações, paginação e filtros; respostas coerentes com os verbos e status HTTP | CRUD de 1 entidade, semântica HTTP imprecisa | Incompleto ou não roda |
| **Clean Architecture** | 30% | Camadas separadas em pacotes; o domínio não importa framework nem infraestrutura; os adaptadores (rotas ou recursos, repositórios com banco) dependem do domínio, por interfaces | Camadas separadas, com dependências pontuais do domínio para framework ou infraestrutura | Camadas misturadas |
| ⚙️ **Persistência e migrações** | 25% | Flyway com migrações versionadas e idempotentes; banco sobe via `docker compose`; schema gerado apenas por migrações (sem `ddl-auto` ou equivalente) | Migrações presentes, com ajustes manuais | Esquema gerado automaticamente ou ausente |
| **Validação, erros e OpenAPI** | 15% | Validação em todas as entradas, erros no formato *problem details* (RFC 9457) ou similar, OpenAPI gerado e acessível | Validação parcial, erros inconsistentes | Ausente |

Os testes saíram desta sprint (ajuste de 13/10, combinado com a turma): o critério *Testes local e remoto*, de 20%, e o teste de arquitetura, que fazia parte de *Clean Architecture*, passam a ser avaliados na Sprint 2. O peso foi distribuído entre os quatro critérios restantes. O CI da Sprint 0 (build dos dois stacks) continua valendo na atividade no repositório.

---

## Sprint 2

Última sprint do semestre (ajuste de 03/10): substitui a Sprint 2, a Sprint 3 e a Entrega Final previstas no início do período. O que for entregue aqui é o produto final.

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| **Microsserviço Go** | 25% | Serviço com responsabilidade única e **justificada** (I/O intensivo, concorrência, processamento); Go idiomático, uso correto de `context` e erros | Serviço funciona, responsabilidade arbitrária | Ausente ou não roda |
| ⚙️ **Contrato Protobuf e integração gRPC** | 25% | `.proto` comentado, com `buf lint` verde no CI e stubs **gerados**; o serviço principal (Ktor/Quarkus) chama o Go por gRPC com deadline e tratamento de erro, e um teste de integração automatizado cobre o fluxo | Chamada funciona com stubs gerados, sem teste automatizado ou sem `buf lint` | Não integra, ou stubs escritos à mão |
| ⚙️ **Sistema no ar** | 25% | API e banco gerenciado no ar numa plataforma de container, com URL pública no README; migrações aplicadas por task ou CI; connection string em segredo, nunca no repositório; health check; a API continua respondendo quando o serviço Go não está acessível (prazo e degradação). O serviço Go no ar é opcional (ver a nota abaixo) | No ar, com deploy ou migração manual | Não está no ar |
| ⚙️ **Ambiente e testes** | 15% | `docker compose up` sobe API, serviço Go e banco do zero, sem passos manuais; suíte do serviço principal com testes unitários do domínio, integração com banco real (Testcontainers ou Dev Services) e **teste de arquitetura** que falha se o domínio importar framework ou infraestrutura; tudo verde local e no CI, sem alteração de configuração | Sobe com ajustes manuais documentados; ou testes só de um tipo; ou suíte verde em um ambiente só | Não sobe, ou sem testes, ou suíte quebrada |
| **Prontidão do repositório** | 10% | README permite a terceiros rodar em menos de 10 min; OpenAPI acessível por URL; ao menos 2 ADRs novas (o serviço Go e a implantação); licença definida | README funcional com lacunas | Não é possível rodar |

Nota sobre o serviço Go no ar (ajuste de 15/10): nas plataformas gratuitas consultadas, a comunicação gRPC entre dois serviços não está disponível (no Render, serviço privado é pago, e um web service gratuito não recebe tráfego pela rede privada nem gRPC pelo endereço público). Por isso, o critério *Sistema no ar* exige a API e o banco no ar, e o serviço Go é avaliado rodando junto da API no `docker compose` (critérios *Contrato Protobuf e integração gRPC* e *Ambiente e testes*). O grupo que puser o serviço Go no ar, em qualquer plataforma, registra como fez numa ADR.

---

## Rúbrica de Comunicação (Componente C, 20% de toda sprint)

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| **Clareza e objetividade** | 25% | Mensagem direta, dentro do tempo | Compreensível, tempo mal usado | Confusa ou muito fora do tempo |
| **Demonstração ao vivo** | 35% | Mostra o sistema rodando: requisição real, teste executando, pipeline verde | Demonstração parcial ou gravada de forma seletiva | Só slides |
| **Justificativa técnica** | 25% | Explica **por que** cada decisão de arquitetura foi tomada, com alternativa descartada | Descreve o que foi feito, sem justificar | Sem justificativa |
| **Participação da equipe** | 15% | Todos falam sobre o que fizeram | Maioria participa | Um só fala pelo grupo |

A rubrica vale para o vídeo e para a *daily meeting*, que não exige slides nem preparação: conta o que o grupo mostra e explica. Nas *daily meetings*, o docente pode solicitar a execução de um teste, a abertura de um arquivo ou a explicação de um trecho específico. A incapacidade de explicar a própria contribuição afeta o Fator de Participação individual.

---

## Checklist por sprint

Pode ser copiado para o `README.md` do repositório.

```markdown
### Sprint 0
- [ ] Monorepo público: api/ services/ protos/ docs/ mise.toml docker-compose.yml
- [ ] mise run build && mise run test passam localmente
- [ ] CI verde (build dos dois stacks)
- [ ] docs/proposta.md com justificativa Ktor×Quarkus e serviço principal×Go
- [ ] Coorte (A/B) e integração declaradas
- [ ] Vídeo 5 min

### Sprint 1
- [ ] CRUD de ≥2 entidades com relacionamento
- [ ] Camadas separadas: o domínio não importa framework nem infraestrutura
- [ ] Migrações Flyway (sem geração automática de schema)
- [ ] Validação + problem details + OpenAPI
- [ ] Vídeo 5 min

### Sprint 2 (final)
- [ ] Microsserviço Go com responsabilidade justificada
- [ ] protos/ com buf lint no CI e stubs gerados
- [ ] Teste de integração gRPC ponta a ponta
- [ ] docker compose up sobe tudo do zero
- [ ] API no ar, com URL pública no README e banco gerenciado; a API funciona mesmo sem o serviço Go
- [ ] Migrações por task ou CI; nenhum segredo no repositório
- [ ] Testes unitários, de integração (Testcontainers ou Dev Services) e de arquitetura, verdes local E no CI
- [ ] README roda em menos de 10 min; OpenAPI acessível; 2 ADRs novas
- [ ] Vídeo 5 min
```
