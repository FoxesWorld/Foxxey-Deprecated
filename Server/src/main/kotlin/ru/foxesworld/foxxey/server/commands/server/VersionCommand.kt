package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.commands.CLICommands

private val logger = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "version",
    description = [
        "Prints the server version"
    ]
)
class VersionCommand : Runnable {

    @CommandLine.ParentCommand
    lateinit var parent: CLICommands

    override fun run() {
        logger.info {
            "Foxxey Server v. ${parent.server.version} © FoxesWorld 2022"
        }
    }
}
