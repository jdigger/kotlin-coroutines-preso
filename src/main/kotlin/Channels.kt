@file:Suppress("EXPERIMENTAL_API_USAGE", "FunctionName")

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.take
import kotlinx.coroutines.runBlocking
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

fun main(): Unit = runBlocking<Unit> {
    // the "classic FP" way of doing sequences
    for (i in fibonacci_infinite().take(10)) {
        println("fib: $i")
    }

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
}
