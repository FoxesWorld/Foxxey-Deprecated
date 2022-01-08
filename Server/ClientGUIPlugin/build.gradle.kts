plugins {
    kotlin("jvm")
}

group = "ru.foxesworld.foxxey"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":Server"))
}
