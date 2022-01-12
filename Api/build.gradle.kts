val ktor = "1.6.7"

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "ru.foxesworld.foxxey"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(ktor("serialization"))
}

/*

    The code is only for beautify previous.

 */

fun ktor(name: String) = "io.ktor:ktor-$name:$ktor"
