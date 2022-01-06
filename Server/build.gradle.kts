val ktor = "1.6.7"
val kotlinCoroutines = "1.6.0"
val junitBom = "5.8.2"
val mockito = "4.2.0"
val hoplite = "1.4.16"
val kodein = "7.10.0"
val ktorm = "3.4.1"
val log4j = "2.17.1"
val logback = "1.2.9"

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "ru.foxesworld.foxxey"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlinCoroutines("core"))

    // Ktor
    implementation(ktor("server-core"))
    implementation(ktor("server-netty"))
    implementation(ktor("network"))
    implementation(ktor("network-tls"))

    // Configurations
    implementation(hoplite("core"))
    implementation(hoplite("json"))

    // Database communication
    implementation(ktorm("core"))

    // Dependency injection
    implementation("org.kodein.di", "kodein-di", kodein)

    // Logging
    implementation(log4j("core"))
    implementation(log4j("api"))
    implementation("ch.qos.logback", "logback-classic", logback)

    // Testing
    testImplementation(kotlinCoroutines("test"))
    testImplementation(platform("org.junit:junit-bom:$junitBom"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("org.mockito", "mockito-core", mockito)
}

/*

    The code is only for beautify previous.

 */

fun ktor(name: String) = "io.ktor:ktor-$name:$ktor"

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:hoplite"

fun ktorm(part: String) = "org.ktorm:ktorm-$part:$ktorm"

fun log4j(part: String) = "org.apache.logging.log4j:log4j-$part:$log4j"