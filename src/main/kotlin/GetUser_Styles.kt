@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

import java.util.*
import java.util.concurrent.Executors

// *******************************************************
//
// Common types
//
// *******************************************************

inline class USid(val value: String)
inline class Token(val value: String)
inline class User(val value: String)


// *******************************************************
//
// Using Kotlin Coroutines
//
// *******************************************************

suspend fun getUser(usid: USid): User {
    val token = apiToken()
    val user = findUser(usid, token)
    return user
}

suspend fun apiToken() = Token("foo")

suspend fun findUser(usid: USid, token: Token) = User("babble")


// *******************************************************
//
// Using Kotlin Coroutines
//
// *******************************************************


@Suppress("MagicNumber", "FunctionName")
fun syncGetUser_callbacks(): User {
    var user: User? = null
    getUser(USid("234")) { u -> user = u }
    while (user == null) {
        Thread.sleep(100)
    }
    return user!!
}

fun <T> getUser(usid: USid, callback: (User) -> T) {
    Thread {
        apiToken { token ->
            findUser(usid, token) { user ->
                callback(user)
            }
        }
    }.start()
}

fun <T> apiToken(callback: (Token) -> T) {
    Thread {
        val token = Token("zoo")
        callback(token)
    }.start()
}

fun <T> findUser(usid: USid, token: Token, callback: (User) -> T) {
    Thread {
        Thread.sleep(200)
        val user = User("zabble")
        callback(user)
    }.start()
}


// *******************************************************
//
// Using Functors/Monads
//
// *******************************************************


interface Retriever<T> {
    fun blockAndGet(): T
    fun <R> map(callback: (T) -> R): Retriever<R>
}


fun <T, R> Retriever<T>.then(next: (T) -> R): Retriever<R> = map { item -> next(item) }

class SimpleRetriever<T>(private val doStuff: () -> T) : Retriever<T> {
    override fun blockAndGet(): T = doStuff()
    override fun <R> map(callback: (T) -> R): Retriever<R> = SimpleRetriever { callback(blockAndGet()) }
}

@Suppress("MagicNumber")
val executor = Executors.newFixedThreadPool(10)

class ThreadRetriever<T>(doStuff: () -> T) : Retriever<T> {
    private val future = executor.submit(doStuff)

    override fun blockAndGet(): T = future.get()
    override fun <R> map(callback: (T) -> R): Retriever<R> = ThreadRetriever { callback(blockAndGet()) }
}

fun <T> getUserAsync(usid: USid): Retriever<User> {
    return SimpleRetriever {
        val apiTokenAsync: Retriever<Token> = apiTokenAsync()
        apiTokenAsync.then { token ->
            findUserAsync(usid, token).blockAndGet()
        }.blockAndGet()
    }
}

fun apiTokenAsync(): Retriever<Token> {
    return SimpleRetriever {
        val token = Token("zoo")
        token
    }
}

fun findUserAsync(usid: USid, token: Token): Retriever<User> {
    return SimpleRetriever {
        val user = User("zabble")
        user
    }
}


// *******************************************************
//
// Main
//
// *******************************************************


fun main() {
    val user = syncGetUser_callbacks()
    println(user.toString())

    val opt = Optional.of("something").map { it + " else" }.ifPresent { println(it) }
}
