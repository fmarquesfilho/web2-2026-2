# Imagem da API Quarkus (Passo 17). O contexto do build é a pasta exemplos/, porque o
# projeto precisa do contrato em ../protos:
#   docker build -f quarkus-tarefas/Dockerfile -t tarefas-quarkus .
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY protos protos
COPY quarkus-tarefas quarkus-tarefas
WORKDIR /src/quarkus-tarefas
RUN mvn -B -ntp -q package -DskipTests

FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
# O Quarkus empacota em camadas (quarkus-app/): bibliotecas, aplicação e o quarkus-run.jar.
COPY --from=build /src/quarkus-tarefas/target/quarkus-app /app
EXPOSE 8081
CMD ["java", "-jar", "/app/quarkus-run.jar"]
