package ru.foxesworld.foxxey.server.commands

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.commands.server.RestartCommand
import ru.foxesworld.foxxey.server.commands.server.StartCommand
import ru.foxesworld.foxxey.server.commands.server.StopCommand
import ru.foxesworld.foxxey.server.commands.server.VersionCommand

private val logger = KotlinLogging.logger {  }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "",
    description = [
        "My description!",
        "It's really wonderful!"
    ],
    header = [
        "Header of this command"
    ],
    footer = [
        "Footer of this command"
    ],
    subcommands = [
        StartCommand::class, StopCommand::class, RestartCommand::class, VersionCommand::class
    ]
)
class CLICommands(
    val server: Server
) : Runnable {

    override fun run() {
        logger.info { CommandLine(this).usageMessage }
    }
}
