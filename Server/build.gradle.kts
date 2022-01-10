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
    kotlin("jvm") version "1.6.10"
    application
}

group = "ru.foxesworld.foxxey"
version = "1.0"

application {
    mainClass.set("ru.foxesworld.foxxey.server.LauncherKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlinCoroutines("core"))

    // Configurations
    implementation(hoplite("core"))
    implementation(hoplite("json"))

    // Database communication
    implementation(ktorm("core"))

    // Dependency injection
    implementation("io.insert-koin", "koin-core", koin)

    // Logging
    implementation(log4j("core"))
    implementation(log4j("api"))
    implementation("ch.qos.logback", "logback-classic", logback)
    implementation("io.github.microutils", "kotlin-logging", kotlinLogging)

    // Testing
    testImplementation(kotlinCoroutines("test"))
    testImplementation(platform("org.junit:junit-bom:$junitBom"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("io.insert-koin", "koin-test-junit5", koin)
    testImplementation("org.mockito", "mockito-core", mockito)
}

tasks.create("createServerInfoConfiguration") {
    doLast {
        File("$buildDir/resources/main/info.json").apply {
            parentFile.mkdirs()
            createNewFile()
        }.writeText(
            "{\"version\":\"$version\"}"
        )
    }
}

tasks.processResources {
    dependsOn("createServerInfoConfiguration")
}

/*

    The code is only for beautify previous.

 */

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun ktorm(part: String) = "org.ktorm:ktorm-$part:$ktorm"

fun log4j(part: String) = "org.apache.logging.log4j:log4j-$part:$log4j"
