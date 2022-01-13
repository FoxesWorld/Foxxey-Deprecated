package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.commands.CLICommands

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "plugins",
    description = [
        "Prints the server loaded plugins"
    ]
)
class PluginsCommand : Runnable {

    @CommandLine.ParentCommand
    lateinit var parent: CLICommands

    override fun run() {
        val pluginsInfoBuilder = StringBuilder()
        parent.server.plugins.forEach { plugin ->
            val info = plugin.info
            pluginsInfoBuilder.append("\n").append("— [${info.id}] $info")
        }
        log.info {
            "${parent.server.plugins.size} plugins: $pluginsInfoBuilder"
        }
    }
}
