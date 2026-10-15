package br.ufrn.exemplo.tarefas

import br.ufrn.exemplo.tarefas.adaptadores.avisos.AvisadorGrpc
import br.ufrn.exemplo.tarefas.adaptadores.banco.ConfigBanco
import br.ufrn.exemplo.tarefas.adaptadores.banco.RepositorioPostgres
import br.ufrn.exemplo.tarefas.adaptadores.banco.criarDataSource
import br.ufrn.exemplo.tarefas.adaptadores.banco.migrar
import br.ufrn.exemplo.tarefas.adaptadores.http.rotas
import br.ufrn.exemplo.tarefas.adaptadores.http.tratarErros
import br.ufrn.exemplo.tarefas.adaptadores.memoria.AvisadorEmMemoria
import br.ufrn.exemplo.tarefas.dominio.Avisador
import br.ufrn.exemplo.tarefas.dominio.RepositorioDeTarefas
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

// A raiz de composição: o único lugar que conhece todas as camadas e as liga.
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

    // Serviço de avisos em Go (Passo 16). O endereço vem do ambiente.
    val avisador = AvisadorGrpc.para(System.getenv("AVISOS_ENDERECO") ?: "localhost:50051")
    monitor.subscribe(ApplicationStopped) { avisador.fechar() }

    configurar(RepositorioPostgres(Database.connect(dataSource)), avisador)
}

// O resto da aplicação só conhece a porta: os testes podem passar outro repositório.
fun Application.configurar(
    repositorio: RepositorioDeTarefas,
    avisador: Avisador = AvisadorEmMemoria(),
) {
    install(Koin) {
        modules(
            module {
                single<RepositorioDeTarefas> { repositorio }
                single<Avisador> { avisador }
            },
        )
    }
    install(ContentNegotiation) { json() }
    tratarErros()
    rotas()
}
