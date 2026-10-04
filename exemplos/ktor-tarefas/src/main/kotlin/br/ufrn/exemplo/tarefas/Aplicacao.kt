package br.ufrn.exemplo.tarefas

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") { modulo() }.start(wait = true)
}

fun Application.modulo() {
    configurar(RepositorioEmMemoria())
}

// O resto da aplicação só conhece a porta: quem monta o grafo é esta função.
fun Application.configurar(repositorio: RepositorioDeTarefas) {
    install(Koin) {
        modules(module { single<RepositorioDeTarefas> { repositorio } })
    }
    install(ContentNegotiation) { json() }
    rotas()
}
