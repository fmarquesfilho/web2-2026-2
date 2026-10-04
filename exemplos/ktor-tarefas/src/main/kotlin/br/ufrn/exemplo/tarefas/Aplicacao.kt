package br.ufrn.exemplo.tarefas

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") { modulo() }.start(wait = true)
}

// Abre o pool e aplica as migrações na subida. A API ainda responde da memória:
// fazer o banco existir e usá-lo são dois passos separados (o Passo 7 troca o repositório).
fun Application.modulo(config: ConfigBanco = ConfigBanco.doAmbiente()) {
    val dataSource = criarDataSource(config)
    migrar(dataSource)
    monitor.subscribe(ApplicationStopped) { dataSource.close() }
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
