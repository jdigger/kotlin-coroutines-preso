@file:Suppress("EXPERIMENTAL_API_USAGE", "FunctionName")

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val mainContext = coroutineContext + CoroutineName("main-fun")

    val baseFlow = flow {
        for (i in 1..9) {
            emit(i)
            delay(200)
        }
    }

    baseFlow
        .sample(500)
        .map { item -> "$item - [${Thread.currentThread().name}]" }
        .flowOn(Dispatchers.Default + CoroutineName("proc-int-flow"))
        .collect {
            withContext(mainContext) {
                println("printing on [${Thread.currentThread().name}] -> $it")
            }
        }
}
