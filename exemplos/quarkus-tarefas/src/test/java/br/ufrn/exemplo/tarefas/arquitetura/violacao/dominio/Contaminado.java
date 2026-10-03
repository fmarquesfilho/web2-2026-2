package br.ufrn.exemplo.tarefas.arquitetura.violacao.dominio;

import jakarta.ws.rs.core.Response;

// Fixture do ArquiteturaTest: um "domínio" que importa Jakarta REST. Não imite.
public class Contaminado {
    public Response status() {
        return Response.ok().build();
    }
}
