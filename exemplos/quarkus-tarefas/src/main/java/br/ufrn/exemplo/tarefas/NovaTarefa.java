package br.ufrn.exemplo.tarefas;

import java.util.List;

// O que chega no corpo do POST — sem id, que é o servidor quem atribui.
public record NovaTarefa(String titulo) {

    // A regra do domínio, em Java puro: devolve TUDO o que está errado, não só o primeiro.
    // `null` também conta: com Jackson, `{}` chega aqui como título nulo.
    public List<String> violacoes() {
        if (titulo == null || titulo.isBlank()) {
            return List.of("titulo: não pode ficar em branco");
        }
        if (titulo.length() > 200) {
            return List.of("titulo: no máximo 200 caracteres");
        }
        return List.of();
    }
}
