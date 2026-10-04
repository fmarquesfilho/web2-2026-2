package br.ufrn.exemplo.tarefas.adaptadores.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

// Erro no formato da RFC 9457: `application/problem+json`. `type` identifica a CLASSE do
// erro; `detail`, esta ocorrência. `violacoes` é um membro de extensão (a RFC permite).
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Problema(String type, String title, int status, String detail, List<String> violacoes) {

    public static final String MEDIA_TYPE = "application/problem+json";
}
