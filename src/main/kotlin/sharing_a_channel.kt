@file:Suppress("EXPERIMENTAL_API_USAGE", "FunctionName")

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() = runBlocking<Unit> {
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
