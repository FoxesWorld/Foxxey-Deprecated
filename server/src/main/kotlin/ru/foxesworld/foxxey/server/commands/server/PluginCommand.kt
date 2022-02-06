package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "plugin",
    description = [
        "Prints the plugin information"
    ]
)
class PluginCommand : BaseCommand() {

    @CommandLine.Option(
        names = ["id"],
        required = true,
        description = [
            "Plugin id"
        ]
    )
    lateinit var id: String

    override fun execute() {
        server.findPluginById(id).onFailure {
            log.info { "Plugin with $id not found" }
        }.onSuccess { plugin ->
            val info = plugin.info
            log.info {
                """
                    |
                    |┌───────────────────────────
                    |│ Name: ${info.name}
                    |│ Id: ${info.id}
                    |│ Version: ${info.version}
                    |│ Version code: ${info.versionCode}
                    |│ Plugin class: ${info.pluginClass}
                    |│ Dependencies: ${info.dependencies.joinToString(" & ")}
                    |│ Configs: ${info.configNames.joinToString(" & ")}
                    |└───────────────────────────
                """.trimMargin()
            }
        }
    }
}
