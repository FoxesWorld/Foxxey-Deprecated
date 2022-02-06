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
    name = "unload",
    description = [
        "Unloads the plugin"
    ],
)
class UnloadCommand : BaseCommand() {

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
        if (plugin.state == Plugin.State.Unloaded) {
            log.info { "Plugin is already unloaded" }
            return
        }
        if (plugin.state == Plugin.State.Started) {
            runBlocking { plugin.stop() }
        }
        runBlocking {
            unloadPlugin(plugin)
        }
    }

    private suspend fun unloadPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Unloading plugin $plugin.." }
            }
            onFailure {
                log.info(it) { "Unloading plugin $plugin failed" }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin unloaded in $millis millis." }
            }
        }
    ) {
        plugin.unload()
    }
}
