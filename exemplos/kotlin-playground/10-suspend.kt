// Demonstração 10 — funções suspend e corrotinas
// `suspend` marca uma função que pode esperar sem prender a thread.
// No exemplo de Tarefas, o repositório é suspend porque consulta o banco.

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTime

data class Tarefa(val id: Int, val titulo: String)

// delay() suspende a corrotina por um tempo, sem bloquear a thread (diferente de Thread.sleep).
// Aqui ele faz o papel da consulta ao banco.
suspend fun buscar(id: Int): Tarefa {
    delay(500)
    return Tarefa(id, "Tarefa $id")
}

// Uma função suspend só pode ser chamada de outra função suspend ou de dentro de uma corrotina.
suspend fun buscarEmSequencia(): List<Tarefa> = listOf(buscar(1), buscar(2), buscar(3))

// async inicia cada busca sem esperar a anterior; await() pega o resultado.
// coroutineScope só termina quando todas as corrotinas criadas dentro dele terminam.
suspend fun buscarAoMesmoTempo(): List<Tarefa> = coroutineScope {
    val pedidos = listOf(1, 2, 3).map { id -> async { buscar(id) } }
    pedidos.map { it.await() }
}

// runBlocking cria uma corrotina e espera por ela: é a ponte entre o main comum e o mundo suspend.
// No Ktor, cada requisição já roda numa corrotina: não se escreve runBlocking nas rotas.
fun main() = runBlocking {
    val tempo1 = measureTime { println(buscarEmSequencia()) }
    println("em sequência: ${tempo1.inWholeMilliseconds} ms")

    val tempo2 = measureTime { println(buscarAoMesmoTempo()) }
    println("ao mesmo tempo: ${tempo2.inWholeMilliseconds} ms")

    // Corrotinas são leves: dez mil esperando ao mesmo tempo, em poucas threads.
    val tempo3 = measureTime {
        coroutineScope {
            repeat(10_000) { launch { delay(500) } }
        }
    }
    println("10.000 corrotinas: ${tempo3.inWholeMilliseconds} ms")

    // Experimente:
    // 1. Tire a palavra suspend de buscar() e leia o erro em delay().
    // 2. Chame buscar(1) dentro de uma função comum (sem suspend) e leia o erro.
    // 3. Troque delay(500) por Thread.sleep(500) dentro do launch e compare o tempo das 10.000.
}
