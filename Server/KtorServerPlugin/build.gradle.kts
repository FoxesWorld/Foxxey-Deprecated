val ktor = "1.6.7"
val kotlinCoroutines = "1.6.0"
val junitBom = "5.8.2"
val mockito = "4.2.0"
val hoplite = "1.4.16"
val koin = "3.1.5"
val ktorm = "3.4.1"
val log4j = "2.17.1"
val logback = "1.2.9"
val kotlinLogging = "2.1.21"

plugins {
    kotlin("jvm")
}

group = "ru.foxesworld.foxxey"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Server
    compileOnly(project(":Server"))
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlinCoroutines("core"))
    compileOnly(hoplite("core"))
    compileOnly(hoplite("json"))
    compileOnly(ktorm("core"))
    compileOnly("io.insert-koin", "koin-core", koin)
    compileOnly(log4j("core"))
    compileOnly(log4j("api"))
    compileOnly("ch.qos.logback", "logback-classic", logback)
    compileOnly("io.github.microutils", "kotlin-logging", kotlinLogging)
}

/*

    The code is only for beautify previous.

 */

fun ktor(name: String) = "io.ktor:ktor-$name:$ktor"

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun ktorm(part: String) = "org.ktorm:ktorm-$part:$ktorm"

fun log4j(part: String) = "org.apache.logging.log4j:log4j-$part:$log4j"
