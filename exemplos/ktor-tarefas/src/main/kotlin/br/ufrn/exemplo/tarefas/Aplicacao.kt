package br.ufrn.exemplo.tarefas

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

// Ponto de entrada. Engine CIO (corrotinas puras), o mesmo do MUSI.
fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") { modulo() }
        .start(wait = true)
}

// A configuração da aplicação, em plugins explícitos e ordenados.
fun Application.modulo() {
    // Grafo de dependências num lugar só (Koin).
    install(Koin) {
        modules(
            module {
                single<RepositorioDeTarefas> { RepositorioEmMemoria() }
            },
        )
    }

    // Liga o JSON (kotlinx.serialization) à negociação de conteúdo.
    install(ContentNegotiation) { json() }

    rotas()
}
