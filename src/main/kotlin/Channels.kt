@file:Suppress("EXPERIMENTAL_API_USAGE", "FunctionName")

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.whileSelect


fun CoroutineScope.fibonacci_infinite(): ReceiveChannel<Int> = produce {
    var x = 0
    var y = 1
    while (true) {
        send(x)
        val next = x + y
        x = y
        y = next
    }
}


suspend fun fibonacci_send(n: Int, channel: SendChannel<Int>) {
    var x = 0
    var y = 1
    for (i in 0 until n) {
        channel.send(x)
        val next = x + y
        x = y
        y = next
    }
    channel.close()
}


suspend fun fibonacci_select(c: SendChannel<Int>, quit: ReceiveChannel<Int>) {
    var x = 0;
    var y = 1
    whileSelect {
        c.onSend(x) {
            val next = x + y
            x = y; y = next
            true // continue while loop
        }
        quit.onReceive {
            println("quit")
            false // break while loop
        }
    }
}

sealed class CounterMsg
object IncCounter : CounterMsg() // one-way message to increment counter
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply

suspend fun counter_actor() = coroutineScope {
    val counterChannel: SendChannel<CounterMsg> = actor {
        var counter = 0 // actor state
        for (msg in this.channel) { // iterate over incoming messages
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }

    for (i in 1..100) {
        counterChannel.send(IncCounter)
    }

    val response = CompletableDeferred<Int>()
    counterChannel.send(GetCounter(response))
    println("Counter = ${response.await()}")
    counterChannel.close() // shutdown the actor
}


suspend fun sharing_a_channel() = coroutineScope {
    val channel = Channel<String>()
    for (i in 1..9)
        launch(Dispatchers.Default + CoroutineName("launch$i")) {
            for (str in channel) {
                println("${Thread.currentThread().name} - $str")
            }
        }
    for (letter in 'a'..'z') {
        channel.send(letter.toString())
    }
    channel.close()
}


fun main(): Unit = runBlocking<Unit> {
    //    // the "classic FP" way of doing sequences
//    for (i in fibonacci_infinite().take(10)) {
//        println("fib: $i")
//    }
//
//    val fsc = Channel<Int>() // akin to a Go Channel
//    launch { fibonacci_send(10, fsc) } // akin to a Go Routine
//    for (i in fsc) {
//        println("fib: $i")
//    }
//
//
//    val sfsc = Channel<Int>()
//    val quitChannel = Channel<Int>()
//    launch {
//        for (i in 0 until 10) {
//            println("fib: ${sfsc.receive()}")
//        }
//        quitChannel.send(0)
//    }
//    fibonacci_select(sfsc, quitChannel)
//
//    counter_actor()
//
    sharing_a_channel()
}
