import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    "java"
    kotlin("jvm")
}

repositories {
    jcenter()
}

operator fun String.unaryPlus() =
    project.property(this)

plugins.withType<KotlinPluginWrapper> {
    dependencies {
        "api"(kotlin("stdlib-jdk8"))
        "api"(kotlin("reflect"))
        "api"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${+"kotlinx_coroutines_version"}")
        "api"("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${+"kotlinx_coroutines_version"}")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${+"kotlinx_coroutines_version"}")
        "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:${+"kotlinx_coroutines_version"}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}
