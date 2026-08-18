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
| ⚙️ **CI mínimo verde** | 25% | Workflow roda build dos dois stacks em todo push e PR, verde em `main` | CI roda, cobre só um stack | Sem CI ou vermelho |
| **Proposta e decisão de arquitetura** | 30% | Domínio delimitado, MVP viável em 4 sprints, **e justificativa clara do que fica em Java e do que fica em Go** com base em características do trabalho | Proposta plausível, divisão de responsabilidades arbitrária | Proposta vaga ou sem divisão |
| **Configuração do processo** | 20% | Repositório público, README completo, GitHub Projects com ≥ 10 itens, coorte declarada | Repositório e quadro criados, incompletos | Repositório privado ou sem quadro |

---

## Sprint 1

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| **CRUD completo** | 25% | ≥ 2 entidades com relacionamento, todas as operações, paginação e filtros; respostas coerentes com os verbos e status HTTP | CRUD de 1 entidade, semântica HTTP imprecisa | Incompleto ou não roda |
| ⚙️ **Clean Architecture + ArchUnit** | 25% | Camadas separadas; teste ArchUnit falha se o domínio importar framework/infra; roda no CI | Camadas separadas, sem teste de arquitetura | Camadas misturadas |
| ⚙️ **Persistência e migrações** | 20% | Flyway com migrações versionadas e idempotentes; banco sobe via `docker compose`; nenhum `ddl-auto: update` | Migrações presentes, com ajustes manuais | Esquema gerado pelo Hibernate ou ausente |
| ⚙️ **Testes local e remoto** | 20% | Unitários do domínio + integração com Testcontainers; **passam no Docker Desktop e no CI** sem alteração de configuração | Testes existem, só rodam em um dos ambientes | Sem testes ou falhando |
| **Validação, erros e OpenAPI** | 10% | Bean Validation em todas as entradas, erros no formato *problem details* (RFC 9457), OpenAPI gerado e acessível | Validação parcial, erros inconsistentes | Ausente |

---

## Sprint 2

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| **Microsserviço Go** | 25% | Serviço com responsabilidade única e **justificada** (I/O intensivo, concorrência, processamento); Go idiomático, uso correto de `context` e erros | Serviço funciona, responsabilidade arbitrária | Ausente ou não roda |
| ⚙️ **Clean Architecture + arch-go** | 20% | `arch-go.yml` protege `internal/domain` e as fronteiras entre serviços; `compliance: 100` no CI | `arch-go` configurado com regras triviais | Ausente |
| ⚙️ **Contratos Protobuf** | 25% | `.proto` bem modelado, com comentários; `buf lint` e `buf breaking` verdes no CI; stubs Go e Java **gerados**, com check de sincronia | `.proto` presente, `buf lint` roda | Stubs escritos à mão ou sem validação |
| ⚙️ **Integração gRPC ponta a ponta** | 20% | Java chama Go via gRPC com deadline e tratamento de erro; teste de integração automatizado cobre o fluxo | Chamada funciona, sem teste automatizado | Não integra |
| ⚙️ **Ambiente reproduzível** | 10% | `docker compose up` sobe API + serviço Go + banco, do zero, sem passos manuais | Sobe com ajustes manuais documentados | Não sobe |

---

## Sprint 3

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| ⚙️ **Banco gerenciado (Neon)** | 20% | Banco no Neon com migrações aplicadas automaticamente por task/CI; connection string em segredo, nunca no repo | Banco no Neon, migração manual | Só banco local |
| **Estratégia de cache** | 30% | Política declarada e justificada (o que cacheia, TTL, quando invalida); trata *stampede*; **métricas de hit/miss expostas e demonstradas com número real** | Cache funciona, sem métricas ou sem política de invalidação | Sem cache |
| ⚙️ **Deploy remoto** | 25% | Sistema no ar em Northflank/Render/Railway, URL pública no README, imagens no ghcr.io publicadas pelo CI | No ar, deploy manual | Não está no ar |
| ⚙️ **Testes continuam verdes local e remoto** | 15% | Toda a suíte roda no Docker Desktop e no CI, incluindo os testes de integração da Sprint 2 | Roda em um dos ambientes | Suíte quebrada |
| **Observabilidade** | 10% | Logs estruturados (JSON) com correlação de requisição, health check em todos os serviços | Logs e health parciais | Ausente |

---

## Entrega Final

Esta entrega absorve o conteúdo do bloco final: segurança de APIs, automação do pipeline e documentação.

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| ⚙️ **Sistema em produção** | 15% | URL pública estável, todos os fluxos do MVP funcionam, deploy automatizado a partir de `main` | No ar com falhas menores ou deploy manual | Fora do ar |
| **Autenticação** | 15% | JWT com refresh e rotação, ou OIDC com provedor externo; expiração e revogação tratadas; sem segredo hardcoded | Login funciona, sem refresh ou revogação | Ausente ou inseguro |
| ⚙️ **Autorização e BOLA** | 15% | Autorização por perfil e por recurso, com teste automatizado provando que um usuário não acessa recurso de outro | Autorização por perfil, sem teste | Rotas abertas |
| **Mitigação OWASP API Top 10** | 10% | `docs/seguranca.md` cobre as 10 ameaças, com a mitigação adotada e onde ela está no código; rate limiting ativo | Documento cobre parte das ameaças | Ausente ou genérico |
| ⚙️ **Pipeline completo e higiene de segredos** | 15% | Build, testes, lint, `arch-go`, ArchUnit, `buf lint`/`breaking`, SAST e check de docs verdes em `main`; `renovate.json` ativo; `mise run ci` reproduz o pipeline; nenhum segredo versionado | Maioria dos jobs verde, automação parcial | Pipeline incompleto ou segredo no histórico |
| ⚙️ **Site de documentação público** | 15% | Site estático no ar com visão geral, arquitetura com diagrama, guia de execução local, guia de contribuição e ao menos 3 ADRs; verificação de defasagem no CI | Site no ar, conteúdo incompleto | Sem site |
| ⚙️ **Referência de API pública** | 10% | Referência gerada do OpenAPI, acessível por URL, cobrindo todos os endpoints com exemplos | Referência publicada, incompleta | Ausente |
| **Prontidão do repositório** | 5% | README permite a terceiros rodar em menos de 10 min; licença definida; histórico limpo | README funcional com lacunas | Não é possível rodar |

---

## Rúbrica de Comunicação (Componente C, 20% de toda sprint)

| Critério | Peso | Excelente (10) | Suficiente (6) | Insuficiente (0–4) |
|----------|------|----------------|----------------|--------------------|
| **Clareza e objetividade** | 25% | Mensagem direta, dentro do tempo | Compreensível, tempo mal usado | Confusa ou muito fora do tempo |
| **Demonstração ao vivo** | 35% | Mostra o sistema rodando: requisição real, teste executando, pipeline verde | Demonstração parcial ou gravada de forma seletiva | Só slides |
| **Justificativa técnica** | 25% | Explica **por que** cada decisão de arquitetura foi tomada, com alternativa descartada | Descreve o que foi feito, sem justificar | Sem justificativa |
| **Participação da equipe** | 15% | Todos falam sobre o que fizeram | Maioria participa | Um só fala pelo grupo |

Nas apresentações, o docente pode solicitar a execução de um teste, a abertura de um arquivo ou a explicação de um trecho específico. A incapacidade de explicar a própria contribuição afeta o Fator de Participação individual.

---

## Checklist por sprint

Pode ser copiado para o `README.md` do repositório.

```markdown
### Sprint 0
- [ ] Monorepo público: api/ services/ protos/ docs/ mise.toml docker-compose.yml
- [ ] mise run build && mise run test passam localmente
- [ ] CI verde (build dos dois stacks)
- [ ] docs/proposta.md com justificativa Java × Go
- [ ] Coorte (A/B) e integração declaradas
- [ ] Vídeo 5 min

### Sprint 1
- [ ] CRUD de ≥2 entidades com relacionamento
- [ ] Teste ArchUnit da regra de dependência rodando no CI
- [ ] Migrações Flyway (sem ddl-auto)
- [ ] Testes com Testcontainers verdes local E no CI
- [ ] Bean Validation + problem details + OpenAPI
- [ ] Vídeo 5 min

### Sprint 2
- [ ] Microsserviço Go com responsabilidade justificada
- [ ] arch-go.yml com compliance 100 no CI
- [ ] protos/ com buf lint + buf breaking no CI
- [ ] Stubs Go e Java gerados (check de sincronia)
- [ ] Teste de integração gRPC ponta a ponta
- [ ] docker compose up sobe tudo do zero
- [ ] Vídeo 5 min

### Sprint 3
- [ ] Banco no Neon com migrações via CI/task
- [ ] Cache com política declarada + métricas hit/miss
- [ ] Deploy no ar (Northflank/Render/Railway) com URL no README
- [ ] Imagens publicadas no ghcr.io pelo CI
- [ ] Logs estruturados + health checks
- [ ] Suíte verde local E remoto
- [ ] Vídeo 5 min

### Entrega Final
- [ ] Sistema no ar, deploy automatizado de main
- [ ] Auth (JWT+refresh ou OIDC) + teste anti-BOLA
- [ ] docs/seguranca.md cobrindo OWASP API Top 10
- [ ] renovate.json ativo, Semgrep no CI, zero segredos versionados
- [ ] Pipeline completo verde
- [ ] Site de documentação público (≥3 ADRs)
- [ ] Referência de API pública gerada do OpenAPI
- [ ] README permite rodar em <10 min + licença
- [ ] Vídeo 10 min
- [ ] Apresentação ao vivo
```
