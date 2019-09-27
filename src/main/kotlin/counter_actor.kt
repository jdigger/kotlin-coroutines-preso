@file:Suppress("EXPERIMENTAL_API_USAGE", "FunctionName")

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking


sealed class CounterMsg
object IncCounter : CounterMsg() // one-way message to increment counter
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply

fun main() = runBlocking<Unit> {
    val counterChannel: SendChannel<CounterMsg> = actor {
        var counter = 0 // private actor state
        for (msg in channel) { // iterate over incoming messages
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
