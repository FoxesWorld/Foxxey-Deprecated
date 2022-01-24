///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

val mainClass = "ru.foxesworld.foxxey.server.LauncherKt"
val distributionDir = "dist"
val runDir = "run"

///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val ktor = "1.6.7"
val kotlinCoroutines = "1.6.0"
val junitBom = "5.8.2"
val mockito = "4.2.0"
val hoplite = "1.4.16"
val koin = "3.1.5"
val ktorm = "3.4.1"
val slf4j = "1.7.29"
val logback = "1.2.9"
val kotlinLogging = "2.1.21"
val picocli = "4.6.2"

///////////////////////////////////////////////////////////////////////////
// Project settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.foxesworld.foxxey"
version = "1.0"

repositories {
    mavenCentral()
}

///////////////////////////////////////////////////////////////////////////
// Dependencies
///////////////////////////////////////////////////////////////////////////

dependencies {
    // CLI
    implementation("info.picocli", "picocli-shell-jline3", picocli)

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
    implementation("ch.qos.logback", "logback-classic", logback)
    implementation("io.github.microutils", "kotlin-logging", kotlinLogging)

    // Testing
    testImplementation(kotlinCoroutines("test"))
    testImplementation(platform("org.junit:junit-bom:$junitBom"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("io.insert-koin", "koin-test-junit5", koin)
    testImplementation("org.mockito", "mockito-core", mockito)
}

///////////////////////////////////////////////////////////////////////////
// Tasks
///////////////////////////////////////////////////////////////////////////

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

tasks.create<Copy>("collectPlugins") {
    subprojects {
        dependsOn("${name}:shadowJar")
    }
    destinationDir = layout.buildDirectory.dir("libs/plugins").get().asFile
    from(
        subprojects.map {
            File(it.buildDir, "libs/")
        }
    )
}

tasks.create<Zip>("packageDistribution") {
    dependsOn("shadowJar", "collectPlugins")
    destinationDirectory.set(
        buildDir.distribution
    )
    from(
        buildDir.libs
    )
}

tasks.create<Copy>("prepareRun") {
    dependsOn("collectPlugins")
    destinationDir = buildDir.run
    from(
        buildDir.libs
    )
}

tasks.processResources {
    dependsOn("createServerInfoConfiguration")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClass
    }
}

///////////////////////////////////////////////////////////////////////////
// Helpers for beautify previous code
///////////////////////////////////////////////////////////////////////////

val buildDir: DirectoryProperty
    get() = layout.buildDirectory

val DirectoryProperty.libs: File
    get() = dir("libs").get().asFile

val DirectoryProperty.run: File
    get() = dir(runDir).get().asFile

val DirectoryProperty.distribution: File
    get() = dir(distributionDir).get().asFile

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun ktorm(part: String) = "org.ktorm:ktorm-$part:$ktorm"
