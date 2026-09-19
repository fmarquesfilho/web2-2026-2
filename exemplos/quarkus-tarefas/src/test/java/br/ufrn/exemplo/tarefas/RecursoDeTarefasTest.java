package br.ufrn.exemplo.tarefas;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

// @QuarkusTest sobe a aplicação inteira; sem URL de banco no perfil de teste,
// o Dev Services sobe um PostgreSQL no Docker e o Flyway aplica as migrações.
@QuarkusTest
class RecursoDeTarefasTest {

    @Test
    void listaAsTarefasCriadasPelaMigracao() {
        given().when().get("/tarefas")
                .then().statusCode(200)
                .body("titulo", hasItem("Estudar Quarkus"));
    }

    @Test
    void criaEDepoisBuscaPeloId() {
        int id = given().contentType(ContentType.JSON).body("{\"titulo\":\"Escrever testes\"}")
                .when().post("/tarefas")
                .then().statusCode(201)
                .extract().path("id");

        given().when().get("/tarefas/" + id)
                .then().statusCode(200)
                .body("titulo", equalTo("Escrever testes"));
    }

    @Test
    void idInexistenteDevolve404() {
        given().when().get("/tarefas/9999").then().statusCode(404);
    }
}
