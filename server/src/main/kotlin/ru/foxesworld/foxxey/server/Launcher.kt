@file:Suppress("EXPERIMENTAL_API_USAGE")

package ru.foxesworld.foxxey.server

import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import ru.foxesworld.foxxey.server.di.Modules

fun main() {
    startKoin {
        modules(
            Modules.foxxeyLauncher,
            Modules.foxxeyServer,
            Modules.commands
        )
    }.apply {
        val launcher: Launcher = koin.get()
        launcher.launch()
    }
}

interface Launcher : KoinComponent {

    fun launch()
}
