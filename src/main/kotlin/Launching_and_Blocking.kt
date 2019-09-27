@file:Suppress("FunctionName")

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    runBlocking {
        val sleepWaiter = CoroutineScope::sleepWaiter
        val delayWaiter = CoroutineScope::delayWaiter

        callBlockingLaunchWaiter("sync launchMain2Main_sync_sleep", EmptyCoroutineContext, sleepWaiter)
        callBlockingLaunchWaiter("sync launchMain2Default_sync_sleep", Dispatchers.Default, sleepWaiter)
        callBlockingLaunchWaiter("sync launchMain2Main_sync_delay", EmptyCoroutineContext, delayWaiter)
        callBlockingLaunchWaiter("sync launchMain2Default_sync_delay", Dispatchers.Default, delayWaiter)

        callLaunchWaiter("async launchMain2Main_sync_sleep", EmptyCoroutineContext, sleepWaiter)
        callLaunchWaiter("async launchMain2Default_sync_sleep", Dispatchers.Default, sleepWaiter)
        callLaunchWaiter("async launchMain2Main_sync_delay", EmptyCoroutineContext, delayWaiter)
        callLaunchWaiter("async launchMain2Default_sync_delay", Dispatchers.Default, delayWaiter)
    }
}

@Suppress("unused")
suspend fun CoroutineScope.delayWaiter(funName: String) {
    println("$funName delaying for 500ms - ${Thread.currentThread().name}")
    delay(500)
}


@Suppress("RedundantSuspendModifier", "unused")
suspend fun CoroutineScope.sleepWaiter(funName: String) {
    println("$funName sleeping for 500ms - ${Thread.currentThread().name}")
    @Suppress("BlockingMethodInNonBlockingContext")
    Thread.sleep(500)
}

fun blockingLaunchWaiter(funName: String, lastTime: AtomicLong, launchContext: CoroutineContext, waiter: suspend CoroutineScope.(String) -> Unit) {
    runBlocking {
        launchWaiter(funName, launchContext, lastTime) { waiter(funName) }
    }
}


private fun callBlockingLaunchWaiter(functionName: String, launchContext: CoroutineContext, waiter: suspend CoroutineScope.(String) -> Unit) {
    callFunc(functionName) { name, lastTime ->
        blockingLaunchWaiter(name, lastTime, launchContext, waiter)
    }
}


private suspend fun callLaunchWaiter(functionName: String, launchContext: CoroutineContext, waiter: suspend CoroutineScope.(String) -> Unit) {
    suspendingCallFunc(functionName) { name: String, lastTime: AtomicLong ->
        launchWaiter(name, launchContext, lastTime) { waiter(name) }
    }
}


fun callFunc(funName: String, func: (String, AtomicLong) -> Unit) {
    runBlocking {
        suspendingCallFunc(funName) { funName: String, lastTime: AtomicLong -> func(funName, lastTime) }
    }
}


suspend fun suspendingCallFunc(funName: String, func: suspend (String, AtomicLong) -> Unit) {
    val startTime = System.currentTimeMillis()
    val lastTime = AtomicLong(startTime)
    println("start calling $funName")
    coroutineScope {
        func("sync1", lastTime)
        repeat(5) { i ->
            launch { func("async$i", lastTime) }
        }
        func("sync2", lastTime)
        func("sync3", lastTime)
    }
    println("end calling $funName - ${System.currentTimeMillis() - startTime}\n\n")
}


suspend fun launchWaiter(funName: String, launchContext: CoroutineContext, lastTime: AtomicLong, waiter: suspend CoroutineScope.() -> Unit) {
    coroutineScope { launch(context = launchContext, block = waiter) }
    val newVal = System.currentTimeMillis()
    val oldVal = lastTime.getAndSet(newVal)
    println("${newVal - oldVal} <<<< $funName - ${Thread.currentThread().name}")
}
//
//fun CoroutineScope.launchWaiter(funName: String, launchContext: CoroutineContext, lastTime: AtomicLong, waiter: suspend CoroutineScope.() -> Unit) {
//    launch(context = launchContext, block = waiter)
//    val newVal = System.currentTimeMillis()
//    val oldVal = lastTime.getAndSet(newVal)
//    println("${newVal - oldVal} <<<< $funName - ${Thread.currentThread().name}")
//}
