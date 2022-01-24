///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

val mainClass = "ru.foxesworld.foxxey.server.LauncherKt"
val distributionDir = "dist"
val runDir = "run"
val infoConfigFile = "info.json"

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

val testImplementation: DependencyHandlerScopeRunnable = {
    // Coroutines
    testImplementation(kotlinCoroutines("test"))
    // Junit
    testImplementation(platform("org.junit:junit-bom:$junitBom"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    // Koin
    testImplementation("io.insert-koin", "koin-test-junit5", koin)
    // Mockito
    testImplementation("org.mockito", "mockito-core", mockito)
}

val dependenciesProvider: DependenciesProvider = { add ->
    // CLI
    add("info.picocli:picocli-shell-jline3:$picocli")

    // Kotlin
    add(kotlinCoroutines("core"))

    // Configurations
    add(hoplite("core"))
    add(hoplite("json"))

    // Database communication
    add(ktorm("core"))

    // Dependency injection
    add("io.insert-koin:koin-core:$koin")

    // Logging
    add("ch.qos.logback:logback-classic:$logback")
    add("io.github.microutils:kotlin-logging:$kotlinLogging")
}

dependencies {
    dependenciesProvider {
        implementation(it)
    }
    testImplementation.invoke(this)
}

subprojects {
    plugins.apply("com.github.johnrengelman.shadow")
    plugins.withType(JavaPlugin::class.java) {
        dependencies {
            compileOnly(parent!!)
            testImplementation(parent!!)
            dependenciesProvider {
                compileOnly(it)
                testImplementation(it)
            }
            testImplementation()
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// Tasks
///////////////////////////////////////////////////////////////////////////

tasks.create("createServerInfoConfiguration") {
    buildDir.resources.main.infoConfig.apply {
        parentFile.mkdirs()
        createNewFile()
    }.writeText(
        "{\"version\":\"$version\"}"
    )
}

tasks.create<Copy>("collectPlugins") {
    subprojects {
        dependsOn("${name}:shadowJar")
    }
    destinationDir = buildDir.libs.plugins
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

typealias DependencyHandlerScopeRunnable = DependencyHandlerScope.() -> Unit
typealias DependenciesProvider = ((Any) -> Unit) -> Unit

val buildDir: DirectoryProperty
    get() = layout.buildDirectory

val DirectoryProperty.libs: File
    get() = dir("libs").get().asFile

val DirectoryProperty.resources: File
    get() = dir("resources").get().asFile

val DirectoryProperty.run: File
    get() = dir(runDir).get().asFile

val DirectoryProperty.distribution: File
    get() = dir(distributionDir).get().asFile

val File.infoConfig: File
    get() = File(this, infoConfigFile)

val File.main: File
    get() = File(this, "main")

val File.plugins: File
    get() = File(this, "plugins")

fun kotlinCoroutines(part: String) = "org.jetbrains.kotlinx:kotlinx-coroutines-$part:$kotlinCoroutines"

fun hoplite(part: String) = "com.sksamuel.hoplite:hoplite-$part:$hoplite"

fun ktorm(part: String) = "org.ktorm:ktorm-$part:$ktorm"
