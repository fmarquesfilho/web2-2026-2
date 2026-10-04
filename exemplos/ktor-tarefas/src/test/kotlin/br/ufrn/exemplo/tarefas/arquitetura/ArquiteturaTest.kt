package br.ufrn.exemplo.tarefas.arquitetura

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import kotlin.test.Test
import kotlin.test.assertTrue

// A regra de dependência, como teste. O ArchUnit lê o bytecode compilado:
//
//     adaptadores (http, banco, memoria)  ──►  dominio
//
// A seta só aponta para dentro. Aplicacao.kt, na raiz, é quem liga tudo.
class ArquiteturaTest {

    private val classes = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("br.ufrn.exemplo.tarefas")

    @Test
    fun `o dominio nao conhece framework, banco nem adaptadores`() = regraDoDominio.check(classes)

    @Test
    fun `as rotas nao falam com o banco`() =
        noClasses().that().resideInAPackage("..adaptadores.http..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..adaptadores.banco..", "org.jetbrains.exposed..", "java.sql..")
            .check(classes)

    // A regra falha quando deve? `violacao.dominio.Contaminado` (só nos testes) importa Ktor
    // de propósito. Se um dia a regra passar com ela, a regra deixou de proteger.
    @Test
    fun `a regra do dominio pega uma violacao`() {
        val fixture = ClassFileImporter().importPackages("br.ufrn.exemplo.tarefas.arquitetura.violacao")
        assertTrue(regraDoDominio.evaluate(fixture).hasViolation())
    }

    companion object {
        val regraDoDominio: ArchRule = noClasses().that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "io.ktor..", "org.koin..", "org.jetbrains.exposed..", "java.sql..", "javax.sql..",
                "com.zaxxer..", "org.flywaydb..", "..adaptadores..",
            )
            .because("o domínio é Kotlin puro: não sabe de HTTP, banco nem injeção de dependência")
    }
}
