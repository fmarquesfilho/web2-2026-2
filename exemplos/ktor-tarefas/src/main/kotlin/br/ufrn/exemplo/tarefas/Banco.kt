package br.ufrn.exemplo.tarefas

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import javax.sql.DataSource

// Onde está o banco. Em produção vem de variáveis de ambiente (Neon, na Sprint 3).
data class ConfigBanco(val url: String, val usuario: String, val senha: String) {
    companion object {
        fun doAmbiente() = ConfigBanco(
            url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/tarefas",
            usuario = System.getenv("DB_USER") ?: "tarefas",
            senha = System.getenv("DB_PASSWORD") ?: "tarefas",
        )
    }
}

// Pool de conexões: abrir conexão é caro; o pool reaproveita.
fun criarDataSource(config: ConfigBanco): HikariDataSource = HikariDataSource(
    HikariConfig().apply {
        jdbcUrl = config.url
        username = config.usuario
        password = config.senha
        maximumPoolSize = 5
    },
)

// Aplica as migrações pendentes de src/main/resources/db/migration.
fun migrar(dataSource: DataSource) {
    Flyway.configure().dataSource(dataSource).load().migrate()
}
