@file:Suppress("FunctionName", "DuplicatedCode", "EXPERIMENTAL_API_USAGE")

import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private fun executor_2_threads() {
    println("starting main")

    val e = Executors.newFixedThreadPool(2)

    e.execute {
        println("starting thread1")
        Thread.sleep(500)
        println("ending thread1")
    }
    e.execute {
        println("starting thread2")
        Thread.sleep(500)
        println("ending thread2")
    }

    e.shutdown()
    e.awaitTermination(2, TimeUnit.SECONDS)

    println("ending main")
}


fun thread_scaling(max: Int) {
    val threads = mutableListOf<Thread>()
    for (i in 0..max) {
        val thread = Thread {
            Thread.sleep(10_000L) //simulate waiting 10s for a remote call
            if (i % 1_000 == 0) {
                println("[${Thread.currentThread().name}] - ${Thread.activeCount()} active")
            }
        }
        threads.add(thread)
        thread.start()
    }
    threads.forEach { thread -> thread.join() } // make sure every thread has finished before returning
}


fun coroutine_main_scaling(max: Int) {
    runBlocking {
        for (i in 0..max) {
            launch {
                delay(10_000L) //simulate waiting 10s for a remote call
                if (i % 1_000_000 == 0) {
                    println("[${Thread.currentThread().name}] - ${Thread.activeCount()} active")
                }
            }
        }
    }
}


fun coroutine_IO_scaling(max: Int) {
    runBlocking {
        for (i in 0..max) {
            launch(Dispatchers.IO) {
                delay(10_000L) //simulate waiting 10s for a remote call
//                sleep_on_IO(10_000L) //simulate waiting 10s for a remote call
                if (i % 1_000 == 0) {
                    println("[${Thread.currentThread().name}] - ${Thread.activeCount()} active")
                }
            }
        }
    }
}


suspend fun spreadSum(): Int = coroutineScope {
    val deferredCalls: List<Deferred<Int>> = (1..100).map {
        async {
            delay(1000) // remote service call
            2
        }
    }
    deferredCalls
            .map { deferred -> deferred.await() }
            .sum()
}


suspend fun spreadSum_force_single_thread(): Int = coroutineScope {
    val dispatcher = newSingleThreadContext("Forced single thread")
    val deferredCalls: List<Deferred<Int>> = (1..100).map {
        async(dispatcher) {
            delay(1000) // remote service call
            println("spreadSum [${Thread.currentThread().name}]")
            2
        }
    }
    deferredCalls
            .map { deferred -> deferred.await() }
            .sum()
}


suspend fun sleep_on_IO(time: Long) = coroutineScope {
    withContext(Dispatchers.IO) {
        Thread.sleep(time)
        println("Finished sleeping ${Thread.currentThread().name} for ${time}ms")
    }
}

fun processLauncher(max: Int, launcher: (Int) -> Unit) {
    val start = Instant.now()
    println("START [${Thread.currentThread().name}] - ${Thread.activeCount()} active")

    launcher(max)

    val end = Instant.now()
    println("END Duration: " + Duration.between(start, end).toMillis())
}


fun main() {
//    executor_2_threads()
//    processLauncher(5_000) { thread_scaling(it) }
//    processLauncher(6_000) { thread_scaling(it) }
//    processLauncher(5_000) { coroutine_main_scaling(it) }
//    processLauncher(5_000) { coroutine_IO_scaling(it) }
//    processLauncher(5_000_000) { coroutine_main_scaling(it) }
//    processLauncher(5_000_000) { coroutine_IO_scaling(it) }

    processLauncher(1) { runBlocking { spreadSum() } }
}
