package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "plugins",
    description = [
        "Prints the server loaded plugins"
    ]
)
class PluginsCommand : BaseCommand() {

    override fun execute() {
        val pluginsInfoBuilder = StringBuilder()
        server.plugins.forEach { plugin ->
            val info = plugin.info
            pluginsInfoBuilder.append("\n").append("— [${info.id}] $info")
        }
        log.info {
            "${server.plugins.size} plugins: $pluginsInfoBuilder"
        }
    }
}
