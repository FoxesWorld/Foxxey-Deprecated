package ru.foxesworld.foxxey.server

import kotlinx.coroutines.DelicateCoroutinesApi
import org.koin.core.component.inject
import ru.foxesworld.foxxey.server.commands.CommandHandler

@DelicateCoroutinesApi
class FoxxeyLauncher : Launcher {

    private val commandHandler: CommandHandler by inject()

    override fun launch() {
        commandHandler.start()
    }
}
