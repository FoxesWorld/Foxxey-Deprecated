package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.commands.CLICommands

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "info",
    description = [
        "Prints the server information"
    ],
)
class InfoCommand : Runnable {

    @CommandLine.ParentCommand
    lateinit var parent: CLICommands

    override fun run() {
        log.info {
            """
                    |
                    |┌───────────────────────────
                    |│ Name: Foxxey
                    |│ State: ${parent.server.state.name}
                    |│ Version: ${parent.server.version}
                    |│ Plugins loaded: ${parent.server.plugins.size}
                    |│ Runtime signature: ${parent.server.runtimeSignature}
                    |│ Development by FoxesWorld.ru
                    |└───────────────────────────
                """.trimMargin()
        }
    }
}
