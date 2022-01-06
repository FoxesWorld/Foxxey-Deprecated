package ru.foxesworld.foxxey.server

import org.kodein.di.DI
import ru.foxesworld.foxxey.server.di.Modules.release

fun main() {
    val di = DI {
        import(release)
    }
}
