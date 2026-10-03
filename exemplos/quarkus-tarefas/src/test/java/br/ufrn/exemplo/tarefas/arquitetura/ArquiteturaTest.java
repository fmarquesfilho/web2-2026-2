package br.ufrn.exemplo.tarefas.arquitetura;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

// A regra de dependência, como teste. O ArchUnit lê o bytecode compilado:
//
//     adaptadores (http, banco, memoria)  ──►  dominio
//
// A seta só aponta para dentro. No Quarkus não há raiz de composição escrita à mão:
// quem liga as peças é o CDI.
class ArquiteturaTest {

    static final ArchRule REGRA_DO_DOMINIO = noClasses().that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta..", "io.quarkus..", "org.hibernate..", "com.fasterxml..", "java.sql..",
                    "..adaptadores..")
            .because("o domínio é Java puro: não sabe de HTTP, banco nem injeção de dependência");

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("br.ufrn.exemplo.tarefas");

    @Test
    void oDominioNaoConheceFrameworkBancoNemAdaptadores() {
        REGRA_DO_DOMINIO.check(classes);
    }

    @Test
    void oRecursoNaoFalaComOBanco() {
        noClasses().that().resideInAPackage("..adaptadores.http..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..adaptadores.banco..", "jakarta.persistence..", "io.quarkus.hibernate..")
                .check(classes);
    }

    // A regra falha quando deve? `violacao.dominio.Contaminado` (só nos testes) importa
    // Jakarta REST de propósito. Se um dia a regra passar com ela, deixou de proteger.
    @Test
    void aRegraDoDominioPegaUmaViolacao() {
        var fixture = new ClassFileImporter().importPackages("br.ufrn.exemplo.tarefas.arquitetura.violacao");
        assertTrue(REGRA_DO_DOMINIO.evaluate(fixture).hasViolation());
    }
}
