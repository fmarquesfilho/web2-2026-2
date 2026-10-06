// Demonstração 9 — delegação de propriedades com `by`
// `by` entrega a outro objeto o trabalho de ler e gravar uma propriedade.
// É o que há por trás de `var texto by remember { mutableStateOf("") }` (Compose)
// e de `val repositorio by inject<RepositorioDeTarefas>()` (Koin, no Ktor).

import kotlin.reflect.KProperty

// Um delegado é qualquer objeto com os operadores getValue e (para var) setValue.
// Este avisa a cada mudança: é a ideia, bem simplificada, do estado observável do Compose.
class Observavel<T>(private var valor: T, private val aoMudar: (String, T, T) -> Unit) {
    operator fun getValue(dono: Any?, propriedade: KProperty<*>): T = valor

    operator fun setValue(dono: Any?, propriedade: KProperty<*>, novo: T) {
        val antigo = valor
        valor = novo
        aoMudar(propriedade.name, antigo, novo)
    }
}

class Repositorio {
    init { println("  (repositório criado agora)") }
    fun listar() = listOf("Estudar Kotlin", "Entender lambdas")
}

fun main() {
    // Sem `by`: a variável guarda o objeto observável, e é preciso passar por ele.
    // Com `by`: a variável se comporta como um String comum; ler e gravar passam pelo delegado.
    var texto: String by Observavel("") { nome, antigo, novo ->
        println("  $nome mudou de \"$antigo\" para \"$novo\"")
    }

    texto = "Estudar"            // chama setValue
    texto = "Estudar Kotlin"
    println(texto.length)        // chama getValue; o tipo é String, não Observavel<String>

    // `by lazy`: o valor só é calculado no primeiro uso, e uma vez só.
    // O `by inject` do Koin segue a mesma ideia: busca a dependência no primeiro uso.
    println("antes de usar o repositório")
    val repositorio: Repositorio by lazy { Repositorio() }
    println("primeiro uso:")
    println(repositorio.listar())
    println("segundo uso:")
    println(repositorio.listar())

    // Experimente:
    // 1. Troque `var texto: String by Observavel("")...` por `val`. O que deixa de compilar?
    // 2. Comente as duas chamadas a repositorio.listar(). A mensagem "(repositório criado agora)"
    //    ainda aparece?
    // 3. Faça o delegado recusar texto em branco: em setValue, só troque o valor se novo.isNotBlank()
    //    (dica: restrinja a classe a String para poder chamar isNotBlank).
}
