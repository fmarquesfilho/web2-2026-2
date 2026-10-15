// Exemplo mínimo de API em Ktor — a MESMA stack do MUSI, isolada para a aula.
// As versões acompanham o MUSI (Kotlin 2.4.10, Ktor 3.5.2) para não divergir.

plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
    application
    // Gera as classes do contrato .proto (Passo 16): mensagens, stub Java e stub Kotlin.
    id("com.google.protobuf") version "0.10.0"
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

    // Persistência (Passo 6): Exposed (SQL em Kotlin), driver JDBC, pool e migrações.
    implementation("org.jetbrains.exposed:exposed-core:1.5.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.5.0")
    implementation("org.postgresql:postgresql:42.7.13")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("org.flywaydb:flyway-core:13.7.0")
    implementation("org.flywaydb:flyway-database-postgresql:13.7.0")

    // OpenAPI gerado das rotas + Swagger UI (Passo 9).
    implementation("io.ktor:ktor-server-routing-openapi:3.5.2")
    implementation("io.ktor:ktor-server-swagger:3.5.2")

    // Erros num lugar só, em problem details (Passo 11).
    implementation("io.ktor:ktor-server-status-pages:3.5.2")

    // Cliente gRPC do serviço de avisos em Go (Passo 16): stubs com corrotinas.
    // O BOM alinha a versão de todos os módulos io.grpc, inclusive os que o grpc-kotlin traz.
    implementation(platform("io.grpc:grpc-bom:1.85.0"))
    implementation("io.grpc:grpc-netty-shaded")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-kotlin-stub:1.5.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.36.2")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.5.2")
    testImplementation("io.ktor:ktor-client-content-negotiation:3.5.2")
    // PostgreSQL descartável, num container, para os testes de integração (Passo 8).
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.5")
    // Teste de arquitetura: a regra de dependência verificada no bytecode (Passo 12).
    testImplementation("com.tngtech.archunit:archunit:1.5.0")
    // Servidor gRPC em memória, para testar o cliente sem o serviço Go (Passo 16).
    testImplementation("io.grpc:grpc-inprocess")
}

// O contrato fica fora do projeto, em ../protos, compartilhado com o Go e o Quarkus.
sourceSets { main { proto { srcDir("../protos") } } }

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:4.36.2" }
    plugins {
        create("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.85.0" }
        create("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:1.5.0:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins { create("grpc"); create("grpckt") }
            it.builtins { create("kotlin") }      // DSL Kotlin para montar as mensagens
        }
    }
}

// Java 25, como no MUSI. O foojay (settings.gradle.kts) baixa o JDK se faltar.
kotlin { jvmToolchain(25) }

tasks.test { useJUnitPlatform() }

application { mainClass.set("br.ufrn.exemplo.tarefas.AplicacaoKt") }
