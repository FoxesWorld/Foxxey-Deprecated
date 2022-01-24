package ru.foxesworld.foxxey.server.commands

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.commands.server.*

private val logger = KotlinLogging.logger {  }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "",
    subcommands = [
        StartCommand::class, StopCommand::class, RestartCommand::class, VersionCommand::class, PluginsCommand::class,
        PluginCommand::class, InfoCommand::class, ClearCommand::class, LoadCommand::class, MemoryCommand::class,
        GarbageCollectCommand::class, UnloadCommand::class
    ]
)
class CLICommands(
    val server: Server
) : Runnable {

    override fun run() {
        logger.info { CommandLine(this).usageMessage }
    }
}
