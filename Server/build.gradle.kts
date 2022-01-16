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

plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("ru.foxesworld.foxxey.server.LauncherKt")
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

tasks.create<Copy>("collectPluginsToDevData") {
    subprojects {
        dependsOn("${name}:shadowJar")
    }
    destinationDir = File(projectDir, "devdata/plugins")
    from(
        subprojects.map {
            File(it.buildDir, "libs/")
        }
    )
}

tasks.create<Copy>("prepareDistribution") {
    subprojects {
        dependsOn("${name}:shadowJar")
    }
    destinationDir = layout.buildDirectory.dir("tmp/distribution/plugins").get().asFile
    from(
        subprojects.map {
            File(it.buildDir, "libs/")
        }
    )
}

tasks.create<Zip>("packageDistribution") {
    dependsOn("shadowJar", "prepareDistribution")
    archiveFileName.set("server.zip")
    destinationDirectory.set(
        layout.buildDirectory.dir("dist")
    )
    from(
        layout.buildDirectory.dir("tmp/distribution"),
        layout.buildDirectory.dir("libs")
    )
}

tasks.processResources {
    dependsOn("createServerInfoConfiguration")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

/*

    The code is only for beautify previous.

 */

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun ktorm(part: String) = "org.ktorm:ktorm-$part:$ktorm"
