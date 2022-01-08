package ru.foxesworld.foxxey.server

import io.ktor.server.engine.*
import mu.KotlinLogging
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.newInstance
import ru.foxesworld.foxxey.server.di.Modules.release
import ru.foxesworld.foxxey.server.logging.runCatchingWithMeasuring
import ru.foxesworld.foxxey.server.plugin.Plugin
import ru.foxesworld.foxxey.server.plugin.PluginLoader
import kotlin.concurrent.thread

fun main() {
    val di = DI {
        import(release)
    }
    val launcher by di.newInstance {
        Launcher(instance(), instance(), instance())
    }
    launcher.wrappedStart()
}

private val logger = KotlinLogging.logger { }

/**
 * Loads plugins and launch the components
 * @param server The server for plugins
 * @param ktorServer Ktor server for launching
 * @param pluginLoader Plugin loading strategy
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Launcher(
    val server: Server,
    val ktorServer: ApplicationEngine,
    val pluginLoader: PluginLoader
) {

    /**
     * Instantiated plugins
     */
    private val plugins: ArrayList<Plugin> = arrayListOf()

    /**
     * Invokes [start] with logging and catching
     */
    fun wrappedStart() {
        logger.info { "Starting.." }
        runCatchingWithMeasuring(
            {
                start()
            }
        ) { millis ->
            logger.info { "Started in $millis millis." }
        }.onFailure { e ->
            logger.info(e) { "Starting failed" }
        }
    }

    /**
     * Starts the launcher components
     */
    private fun start() {
        addShutdownHookToStop()
        wrappedInstantiatePlugins(
            wrappedLoadPlugins()
        )
        ktorServer.start()
        wrappedStartPlugins()
    }

    /**
     * Adds shutdown hook to [stop]
     */
    private fun addShutdownHookToStop() {
        logger.debug { "Adding shutdown hook to stop.." }
        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                wrappedStop()
            }
        )
        logger.debug { "Shutdown hook added" }
    }

    /**
     * Invokes [startPlugins] with logging and catching
     */
    private fun wrappedStartPlugins() {
        logger.info { "Starting plugins.." }
        runCatchingWithMeasuring(
            {
                startPlugins()
            }
        ) { millis ->
            logger.info { "Plugins started in $millis millis." }
        }.onFailure {
            logger.info(it) { "Starting plugins failed" }
        }
    }

    /**
     * Starts the [plugins]
     */
    private fun startPlugins() {
        plugins.forEach {
            it.wrappedStart()
        }
    }

    /**
     * Invokes [loadPlugins] with logging and catching
     * @return Loaded plugin's data
     */
    private fun wrappedLoadPlugins(): Set<Plugin.Data> {
        logger.info { "Loading plugins.." }
        return runCatchingWithMeasuring(
            {
                loadPlugins()
            }
        ) { millis ->
            logger.info { "Plugins loaded in $millis millis." }
        }.onFailure { e ->
            logger.info(e) { "Loading plugins failed" }
        }.getOrElse {
            emptySet()
        }
    }

    /**
     * Loads plugins by the [pluginLoader]
     * @return Loaded plugin's data
     */
    private fun loadPlugins(): Set<Plugin.Data> {
        return pluginLoader.loadAll()
    }

    /**
     * Invokes [instantiatePlugin] with logging and catching
     * @param pluginsData Data for instantiate plugins
     */
    private fun wrappedInstantiatePlugins(pluginsData: Set<Plugin.Data>) {
        logger.info { "Instantiating plugins.." }
        runCatchingWithMeasuring(
            {
                instantiatePlugins(pluginsData)
            }
        ) { millis ->
            logger.info { "Plugins instantiated in $millis millis." }
        }.onFailure { e ->
            logger.info(e) { "Instantiating plugins failed" }
        }
    }

    /**
     * Instantiates plugins by [pluginsData] and adds it to [plugins]
     * @param pluginsData The data for instantiating plugins
     */
    private fun instantiatePlugins(pluginsData: Set<Plugin.Data>) {
        pluginsData.forEach {
            wrappedInstantiatePlugin(it)
        }
    }

    /**
     * Invokes [instantiatePlugin] with logging and catching
     * @param pluginData The plugin data for instantiate
     */
    private fun wrappedInstantiatePlugin(pluginData: Plugin.Data) {
        logger.debug { "Instantiating plugin $pluginData.." }
        runCatchingWithMeasuring(
            {
                instantiatePlugin(pluginData)
            }
        ) { millis ->
            logger.debug { "Plugin $pluginData instantiated in $millis millis." }
        }.onFailure { e ->
            logger.debug(e) { "Instantiating plugin $pluginData failed" }
            logger.info { "Instantiating plugin $pluginData failed by unexpected exception" }
        }
    }

    /**
     * Instantiates the plugin by its data and adds it to [plugins]
     * @param pluginData The plugin data
     */
    private fun instantiatePlugin(pluginData: Plugin.Data) {
        val pluginInstance = kotlin.runCatching {
            val constructor = pluginData.clazz.getDeclaredConstructor(Launcher::class.java, Plugin.Config::class.java)
            constructor.newInstance(this, pluginData.config)
        }.getOrElse {
            val constructor = pluginData.clazz.getDeclaredConstructor(Plugin.Config::class.java, Launcher::class.java)
            constructor.newInstance(pluginData.config, this)
        }
        plugins.add(pluginInstance)
    }

    /**
     * Invokes [stop] with logging and catching
     */
    private fun wrappedStop() {
        logger.info { "Stopping.." }
        runCatchingWithMeasuring(
            {
                stop()
            }
        ) { millis ->
            logger.info { "Stopped in $millis millis." }
        }.onFailure {
            logger.error(it) { "Stopped by unexpected exception" }
        }
    }

    /**
     * Stops the launcher components
     */
    private fun stop() {
        wrappedStopPlugins()
        ktorServer.stop(0, 1000)
    }

    /**
     * Invokes [stopPlugins] with logging and catching
     */
    private fun wrappedStopPlugins() {
        logger.info { "Stopping plugins.." }
        runCatchingWithMeasuring(
            {
                stopPlugins()
            }
        ) { millis ->
            logger.info { "Plugins stopped in $millis millis." }
        }.onFailure {
            logger.info(it) { "Stopping plugins failed" }
        }
    }

    /**
     * Stops the [plugins]
     */
    private fun stopPlugins() {
        plugins.forEach {
            it.wrappedStop()
        }
    }

    /**
     * Finds plugin with the [id]
     * @return Result with the plugin or [IllegalArgumentException] if not founded
     */
    fun findPluginById(id: String): Result<Plugin> {
        plugins.forEach {
            if (it.config.id == id) {
                return Result.success(it)
            }
        }

        return Result.failure(IllegalArgumentException("Plugin with id $id not found"))
    }
}
