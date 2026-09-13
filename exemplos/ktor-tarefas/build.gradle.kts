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

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.5.2")
}

// Java 25, como no MUSI. O foojay (settings.gradle.kts) baixa o JDK se faltar.
kotlin { jvmToolchain(25) }

application { mainClass.set("br.ufrn.exemplo.tarefas.AplicacaoKt") }
