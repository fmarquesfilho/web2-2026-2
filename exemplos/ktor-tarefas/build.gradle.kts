// Exemplo mínimo de API em Ktor — a MESMA stack do MUSI, isolada para a aula.
// As versões acompanham o MUSI (Kotlin 2.4.10, Ktor 3.5.2) para não divergir.

plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
    application
}

repositories { mavenCentral() }

dependencies {
    // Servidor + engine CIO (corrotinas puras), o mesmo do MUSI.
    implementation("io.ktor:ktor-server-core:3.5.2")
    implementation("io.ktor:ktor-server-cio:3.5.2")

    // JSON: negociação de conteúdo + kotlinx.serialization.
    implementation("io.ktor:ktor-server-content-negotiation:3.5.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.2")

    // Injeção de dependência (Passo 5).
    implementation("io.insert-koin:koin-ktor:4.2.2")

    implementation("ch.qos.logback:logback-classic:1.6.3")

    // Persistência (21/09): Exposed (SQL em Kotlin), driver JDBC, pool e migrações.
    implementation("org.jetbrains.exposed:exposed-core:1.5.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.5.0")
    implementation("org.postgresql:postgresql:42.7.13")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("org.flywaydb:flyway-core:13.7.0")
    implementation("org.flywaydb:flyway-database-postgresql:13.7.0")

    // OpenAPI gerado das rotas + Swagger UI.
    implementation("io.ktor:ktor-server-routing-openapi:3.5.2")
    implementation("io.ktor:ktor-server-swagger:3.5.2")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.5.2")
    testImplementation("io.ktor:ktor-client-content-negotiation:3.5.2")
    // PostgreSQL descartável, num container, para os testes de integração.
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.5")
}

// Java 25, como no MUSI. O foojay (settings.gradle.kts) baixa o JDK se faltar.
kotlin { jvmToolchain(25) }

tasks.test { useJUnitPlatform() }

application { mainClass.set("br.ufrn.exemplo.tarefas.AplicacaoKt") }
