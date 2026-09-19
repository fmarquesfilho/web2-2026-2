package br.ufrn.exemplo.tarefas

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

// Ponto de entrada. Engine CIO (corrotinas puras), o mesmo do MUSI.
fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") { modulo() }
        .start(wait = true)
}

// Produção: banco de verdade, migrado na subida.
fun Application.modulo(config: ConfigBanco = ConfigBanco.doAmbiente()) {
    val dataSource = criarDataSource(config)
    migrar(dataSource)
    monitor.subscribe(ApplicationStopped) { dataSource.close() }
    configurar(RepositorioPostgres(Database.connect(dataSource)))
}

// O resto da aplicação só conhece a porta: os testes podem passar outro repositório.
fun Application.configurar(repositorio: RepositorioDeTarefas) {
    install(Koin) {
        modules(module { single<RepositorioDeTarefas> { repositorio } })
    }
    install(ContentNegotiation) { json() }
    rotas()
}
