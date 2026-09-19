package br.ufrn.exemplo.tarefas

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
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

// O mapeamento da tabela para o Exposed. O esquema em si é da migração, não daqui.
object Tarefas : Table("tarefas") {
    val id = integer("id").autoIncrement()
    val titulo = varchar("titulo", 200)
    val feita = bool("feita").default(false)
    override val primaryKey = PrimaryKey(id)
}

class RepositorioPostgres(private val db: Database) : RepositorioDeTarefas {

    override suspend fun listar(): List<Tarefa> = suspendTransaction(db) {
        Tarefas.selectAll().orderBy(Tarefas.id to SortOrder.ASC).map { it.paraTarefa() }
    }

    override suspend fun buscar(id: Int): Tarefa? = suspendTransaction(db) {
        Tarefas.selectAll().where { Tarefas.id eq id }.singleOrNull()?.paraTarefa()
    }

    override suspend fun adicionar(nova: NovaTarefa): Tarefa = suspendTransaction(db) {
        val id = Tarefas.insert { it[titulo] = nova.titulo } get Tarefas.id
        Tarefa(id, nova.titulo)
    }

    private fun ResultRow.paraTarefa() = Tarefa(this[Tarefas.id], this[Tarefas.titulo], this[Tarefas.feita])
}
