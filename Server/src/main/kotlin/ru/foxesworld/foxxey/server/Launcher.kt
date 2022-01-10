@file:Suppress("EXPERIMENTAL_API_USAGE")

package ru.foxesworld.foxxey.server

import com.sksamuel.hoplite.ConfigAlias
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import ru.foxesworld.foxxey.server.di.Modules

fun main() {
    startKoin {
        modules(
            Modules.foxxeyLauncher,
            Modules.foxxeyServer
        )
    }.apply {
        val launcher: Launcher = koin.get()
        launcher.launch()
    }
}

interface Launcher : KoinComponent {

    val config: Config
    val server: Server

    fun launch()

    data class Config(
        @ConfigAlias("threadsCount")
        val threadsCount: Int
    )
}
