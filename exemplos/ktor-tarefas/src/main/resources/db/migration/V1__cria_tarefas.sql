CREATE TABLE tarefas (
    id     SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    feita  BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO tarefas (titulo) VALUES ('Estudar Ktor'), ('Estudar Quarkus');
