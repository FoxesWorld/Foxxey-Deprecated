package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "info",
    description = [
        "Prints the server information"
    ],
)
class InfoCommand : BaseCommand() {

    override fun execute() {
        log.info {
            """
                    |
                    |┌───────────────────────────
                    |│ Name: Foxxey
                    |│ State: ${server.state.name}
                    |│ Version: ${server.version}
                    |│ Plugins loaded: ${server.plugins.size}
                    |│ Runtime signature: ${server.runtimeSignature}
                    |│ Development by FoxesWorld.ru
                    |└───────────────────────────
                """.trimMargin()
        }
    }
}
