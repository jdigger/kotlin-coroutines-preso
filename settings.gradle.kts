rootProject.name = "kotlin-coroutines-preso"

val kotlin_version: String by settings

pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlin_version
    }
}
