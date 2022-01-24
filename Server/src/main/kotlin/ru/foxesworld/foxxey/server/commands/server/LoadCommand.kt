package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.logging.wrappedRunNoResult
import ru.foxesworld.foxxey.server.plugins.Plugin

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "load",
    description = [
        "Loads the plugin"
    ],
)
class LoadCommand : BaseCommand() {

    @CommandLine.Option(
        names = ["id"],
        required = true,
        description = [
            "Plugin id"
        ]
    )
    lateinit var id: String

    override fun execute() {
        val plugin = server.findPluginById(id).getOrElse {
            log.info { "Plugin with id $id not found" }
            return
        }
        if (plugin.state != Plugin.State.Unloaded) {
            log.info { "Plugin is already loaded" }
            return
        }
        runBlocking {
            loadPlugin(plugin)
        }
    }

    private suspend fun loadPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Loading plugin $plugin.." }
            }
            onFailure {
                log.info(it) { "Loading plugin $plugin failed" }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin loaded in $millis millis." }
            }
        }
    ) {
        plugin.load()
    }
}
