///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val ktor = "1.6.7"

///////////////////////////////////////////////////////////////////////////
// Project settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm")
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
    api(ktor("server-core"))
}

///////////////////////////////////////////////////////////////////////////
// Tasks
///////////////////////////////////////////////////////////////////////////

tasks.test {
    useJUnitPlatform()
}

///////////////////////////////////////////////////////////////////////////
// Helpers for beautify previous code
///////////////////////////////////////////////////////////////////////////

fun DependencyHandlerScope.api(any: Any) {
    compileOnly(any)
    testImplementation(any)
}

fun ktor(name: String) = "io.ktor:ktor-$name:$ktor"
