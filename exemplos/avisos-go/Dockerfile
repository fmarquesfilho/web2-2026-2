# Imagem do serviço de avisos (Passo 17). Dois estágios: compila com a imagem do Go e
# copia só o binário para uma imagem mínima, sem shell (distroless).
FROM golang:1.26 AS build
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY . .
# CGO_ENABLED=0: binário estático, que roda sem as bibliotecas do sistema.
RUN CGO_ENABLED=0 go build -o /avisos ./cmd/avisos

FROM gcr.io/distroless/static-debian12
COPY --from=build /avisos /avisos
USER nonroot
EXPOSE 50051
ENTRYPOINT ["/avisos"]
