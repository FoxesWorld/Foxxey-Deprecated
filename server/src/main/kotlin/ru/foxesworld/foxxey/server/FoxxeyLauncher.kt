package ru.foxesworld.foxxey.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import org.koin.core.component.inject
import ru.foxesworld.foxxey.server.commands.CommandHandler
import ru.foxesworld.foxxey.server.os.OSHelper

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
class FoxxeyLauncher : Launcher {

    private val commandHandler: CommandHandler by inject()

    override fun launch() {
        printBanner()
        commandHandler.start()
    }

    private fun printBanner() {
        log.info {
            """
                    |
                    |┌───────────────────────────
                    |│ FoxxeyLauncher
                    |│
                    |│ You're trying to launch Foxxey at ${OSHelper.machineName}
                    |│ Operation system: ${OSHelper.osName}
                    |│ Architecture: ${OSHelper.osArchitecture}
                    |│ Memory: ${OSHelper.usedMemoryInMB} / ${OSHelper.availableMemoryInMB} megabytes
                    |└───────────────────────────
                """.trimMargin()
        }
    }
}
