# Branch `aula` — o exemplo de Tarefas, passo a passo

Este branch guarda o exemplo de Tarefas (Ktor e Quarkus, e na Sprint 2 o serviço Go) em cada etapa da construção. O branch começa no **Passo 12**, onde a Sprint 1 terminou, e cada passo está numa tag (`passo-05` a `passo-18`). O estado final é o mesmo da `main`.

## Como usar

Crie o Codespace a partir deste branch (**Code → Codespaces → New with options → Branch:
`aula`**) ou, no seu computador:

```bash
git fetch --tags origin
git switch aula
```

Escreva o código de cada passo a partir do anterior. Para conferir ou alcançar a turma:

```bash
./passo.sh        # lista os passos e mostra em qual o código está
./passo.sh 6      # leva o código para o estado final do Passo 6
```

O `./passo.sh` guarda o que você tinha digitado (`git stash list`) antes de trocar.

## Comandos

| | Ktor (porta 8080) | Quarkus (porta 8081) |
|---|---|---|
| Pasta | `exemplos/ktor-tarefas` | `exemplos/quarkus-tarefas` |
| Rodar | `./gradlew run` | `mvn quarkus:dev` |
| Testar | `./gradlew test` | `mvn test` |
| Banco (Passo 6 em diante) | `docker compose up -d` | sobe sozinho (Dev Services) |

Ao voltar para um passo anterior no Quarkus, rode `mvn clean` antes de subir de novo.

## Roteiro da quarta, 21/10: introdução a Go e o Passo 13

1. **Os exemplos do Curso básico de Go**, em `/workspaces/aprenda-go-com-testes` (no seu computador: `git clone https://github.com/classrooms-fmarquesfilho/aprenda-go-com-testes`).

   ```bash
   cd /workspaces/aprenda-go-com-testes/2-hello-world
   go test -v                          # quebre um caso de propósito e leia a falha
   go test -run 'TestHello/região' -v  # só os casos com "região" no nome
   cd ../3-metodos-interfaces && go test -v
   cd ../4-ponteiros-erros && go test -v   # tire o * de Depositar e rode de novo: saldo 0
   cd ../5-injecao-de-dependencia && go test -v
   ```

2. **Passo 13, o serviço de avisos**, de volta ao repositório da disciplina:

   ```bash
   cd /workspaces/web2-2026-2
   ./passo.sh 13
   cd exemplos/avisos-go
   go test ./...        # 3 testes: ao mesmo tempo, prazo do context, aviso inválido
   go run ./cmd/demo    # o tempo é o do canal mais lento, não a soma
   ```

## Demonstração de Processos (21/10): o sistema inteiro em containers

O branch `aula` está no Passo 12, e o `compose.yaml` só existe a partir do Passo 17. Por isso a demonstração usa uma cópia do Passo 18 numa pasta ao lado, sem mexer no código da aula:

```bash
cd /workspaces/web2-2026-2
git worktree add /workspaces/compose-demo passo-18   # só na primeira vez
cd /workspaces/compose-demo/exemplos
docker compose up --build    # banco, serviço Go e API Ktor
docker compose ps            # em outro terminal: os três healthy
curl localhost:8080/tarefas
docker compose down          # no fim
```

## Roteiro da segunda, 26/10: passos 14 a 18

Cada linha começa na raiz do repositório (`/workspaces/web2-2026-2`), onde fica o `passo.sh`.

| Passo | Comandos |
|---|---|
| 14 | `./passo.sh 14` · `cd exemplos/protos && buf lint && buf generate` |
| 15 | `./passo.sh 15` · `cd exemplos/avisos-go && go run ./cmd/avisos` · em outro terminal, os `grpcurl` do Passo 15 em `exemplos/avisos-go/PASSOS.md` (list, Avisar, `-max-time 0.3`, título em branco, Health) |
| 16 | `./passo.sh 16` · terminal 1: `cd exemplos/avisos-go && go run ./cmd/avisos` · terminal 2: `cd exemplos/ktor-tarefas && docker compose up -d && ./gradlew run` · terminal 3: o `curl -X POST` do Passo 16; depois, pare o serviço Go e repita |
| 17 | `./passo.sh 17` · pare o que estiver rodando · `cd exemplos && docker compose up --build` · em outro terminal, `docker compose ps` |
| 18 | `./passo.sh 18` · os modelos em `exemplos/implantacao/`; o resto está na leitura da sprint, capítulo 8 |

## O que muda em cada passo

| Passo | Assunto | Diferença em relação ao passo anterior |
|---|---|---|
| 5 | Injeção de dependência (Koin × CDI) | [passo-04...passo-05](../../compare/passo-04...passo-05) |
| 6 | Banco, pool e migração (Flyway) | [passo-05...passo-06](../../compare/passo-05...passo-06) |
| 7 | Repositório com banco (Exposed × Panache) | [passo-06...passo-07](../../compare/passo-06...passo-07) |
| 8 | Testes: sem banco e com PostgreSQL | [passo-07...passo-08](../../compare/passo-07...passo-08) |
| 9 | OpenAPI e Swagger UI | [passo-08...passo-09](../../compare/passo-08...passo-09) |
| 10 | O ambiente em tasks do mise | [passo-09...passo-10](../../compare/passo-09...passo-10) |
| 11 | Validação e erros em *problem details* | [passo-10...passo-11](../../compare/passo-10...passo-11) |
| 12 | Camadas em pacotes e teste de arquitetura | [passo-11...passo-12](../../compare/passo-11...passo-12) |
| 13 | O serviço Go: pacotes, interfaces, erros e `context` | [passo-12...passo-13](../../compare/passo-12...passo-13) |
| 14 | O contrato: Protocol Buffers e Buf | [passo-13...passo-14](../../compare/passo-13...passo-14) |
| 15 | O servidor gRPC em Go | [passo-14...passo-15](../../compare/passo-14...passo-15) |
| 16 | A API chama o Go por gRPC (Ktor e Quarkus) | [passo-15...passo-16](../../compare/passo-15...passo-16) |
| 17 | Tudo em containers: Dockerfiles e `docker compose` | [passo-16...passo-17](../../compare/passo-16...passo-17) |
| 18 | Implantação: GHCR, migração por comando, Render | [passo-17...passo-18](../../compare/passo-17...passo-18) |

A explicação de cada passo está em `exemplos/ktor-tarefas/PASSOS.md`,
`exemplos/quarkus-tarefas/PASSOS.md` e, a partir do Passo 13, `exemplos/avisos-go/PASSOS.md`.
