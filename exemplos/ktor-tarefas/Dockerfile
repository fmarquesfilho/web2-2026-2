# Imagem da API Ktor (Passo 17). O contexto do build é a pasta exemplos/, porque o
# projeto precisa do contrato em ../protos:
#   docker build -f ktor-tarefas/Dockerfile -t tarefas-ktor .
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY protos protos
COPY ktor-tarefas ktor-tarefas
WORKDIR /src/ktor-tarefas
RUN ./gradlew --no-daemon -q installDist -x test

FROM eclipse-temurin:25-jre
# curl só para o healthcheck do docker compose.
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /src/ktor-tarefas/build/install/ktor-tarefas /app
EXPOSE 8080
CMD ["/app/bin/ktor-tarefas"]
