package ru.foxesworld.foxxey.server

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import ru.foxesworld.foxxey.server.logging.wrappedRunNoResult
import ru.foxesworld.foxxey.server.plugins.Plugin
import ru.foxesworld.foxxey.server.plugins.PluginsLoader
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

private val log = KotlinLogging.logger { }

abstract class FoxxeyBaseServer(info: Info) : Server {

    override val version: String = info.version
    override val config: Server.Config by inject()
    private val pluginsLoader: PluginsLoader by inject()
    override val plugins: ArrayList<Plugin> = arrayListOf()
    override val coroutineContext: CoroutineContext by inject(named("server"))
    override var state: Server.State = Server.State.Stopped
    override val runtimeSignature: String = UUID.randomUUID().toString().replace("-", "fox")
    private val hookToStopThread: Thread = thread(start = false) {
        runBlocking(coroutineContext) {
            this@FoxxeyBaseServer.stop()
        }
    }

    override suspend fun start() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Starting.." }
            }
            onSuccess { millis, _ ->
                log.info { "Started in $millis millis." }
            }
            onFailure {
                log.info(it) { "Starting failed" }
            }
        }
    ) {
        if (state != Server.State.Stopped) {
            throw IllegalStateException("Server already started!")
        }
        state = Server.State.Starting
        addShutdownHookToStop()
        loadPluginsFromDir(config.pluginsDir)
        startPlugins()
        state = Server.State.Started
    }

    private suspend fun addShutdownHookToStop() = wrappedRunNoResult {
        Runtime.getRuntime().addShutdownHook(hookToStopThread)
    }

    override suspend fun stop() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Stopping.." }
            }
            onSuccess { millis, _ ->
                log.info { "Stopped in $millis millis." }
            }
            onFailure {
                log.info(it) { "Stopping failed" }
            }
        }
    ) {
        if (state != Server.State.Started) {
            throw IllegalStateException("Server already stopped!")
        }
        state = Server.State.Stopping
        remShutdownHookToStop()
        stopPlugins()
        unloadPlugins()
        state = Server.State.Stopped
    }

    private suspend fun remShutdownHookToStop() = wrappedRunNoResult {
        Runtime.getRuntime().removeShutdownHook(hookToStopThread)
    }

    override suspend fun restart() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Restarting.." }
            }
            onSuccess { millis, _ ->
                log.info { "Restarted in $millis millis." }
            }
            onFailure {
                log.info(it) { "Restarting failed" }
            }
        }
    ) {
        stop()
        start()
    }

    override suspend fun loadPluginsFromDir(dir: String) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Loading plugins from dir $dir.." }
            }
            onSuccess { millis, _ ->
                log.info { "${plugins.size} plugins loaded in $millis millis." }
            }
            onFailure {
                log.info(it) { "Loading plugins from dir $dir failed" }
            }
        }
    ) {
        val loadedPlugins = pluginsLoader.loadPluginsFromDir(File(dir))
        plugins.addAll(loadedPlugins)
    }

    override suspend fun loadPluginFromFile(file: File) = wrappedRunNoResult {
        pluginsLoader.loadPluginFromFile(file)
            .onSuccess {
                plugins.add(it)
            }
    }

    override suspend fun reloadPlugins() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Reloading ${plugins.size} plugins.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugins reloaded in $millis millis." }
            }
            onFailure {
                log.info { "Reloading plugins failed by unexpected exception" }
                log.debug(it) { "Reloading plugins failed" }
            }
        }
    ) {
        unloadPlugins()
        loadPlugins()
    }

    override suspend fun loadPlugins() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Loading ${plugins.size} plugins.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugins loaded in $millis millis." }
            }
            onFailure {
                log.info { "Loading plugins failed by unexpected exception" }
                log.debug(it) { "Loading plugins failed" }
            }
        }
    ) {
        plugins.forEach {
            loadPlugin(it)
        }
    }

    override suspend fun loadPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Unloading plugin $plugin.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin unloaded in $millis millis." }
            }
            onFailure {
                log.info(it) { "Unloading plugin $plugin failed by unexpected exception" }
                log.debug(it) { "Unloading plugin $plugin failed" }
            }
        }
    ) {
        if (plugin.state == Plugin.State.Unloaded) {
            plugin.load()
            plugins.add(plugin)
        }
    }

    override suspend fun unloadPlugins() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Unloading ${plugins.size} plugins.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugins unloaded in $millis millis." }
            }
            onFailure {
                log.info { "Unloading plugins failed by unexpected exception" }
                log.debug(it) { "Unloading plugins failed" }
            }
        }
    ) {
        plugins.reversed().forEach {
            unloadPlugin(it)
        }
    }

    override suspend fun unloadPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Unloading plugin $plugin.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin unloaded in $millis millis." }
            }
            onFailure {
                log.info { "Unloading plugin $plugin failed by unexpected exception" }
                log.debug(it) { "Unloading plugin $plugin failed" }
            }
        }
    ) {
        if (plugin.state == Plugin.State.Started) {
            stopPlugin(plugin)
        }
        if (plugin.state == Plugin.State.Unloaded) {
            return@wrappedRunNoResult
        }
        plugin.unload()
        plugins.remove(plugin)
    }

    override suspend fun reloadPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Reloading plugin $plugin.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin reloaded in $millis millis." }
            }
            onFailure {
                log.info { "Realoding plugin $plugin failed by unexpected exception" }
                log.debug(it) { "Realoding plugin $plugin failed" }
            }
        }
    ) {
        unloadPlugin(plugin)
        loadPlugin(plugin)
    }

    override suspend fun startPlugins() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Starting ${plugins.size} plugins.." }
            }
            onSuccess { millis, _ ->
                log.info { "${plugins.size} plugins started in $millis millis." }
            }
            onFailure {
                log.info { "Starting ${plugins.size} failed by unexpected exception" }
                log.debug(it) { "Starting ${plugins.size} failed" }
            }
        }
    ) {
        plugins.forEach {
            startPlugin(it)
        }
    }

    override suspend fun startPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Starting plugin $plugin.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin started in $millis millis." }
            }
            onFailure {
                log.info { "Starting plugin $plugin failed by unexpected exception" }
                log.debug(it) { "Starting plugin $plugin failed" }
            }
        }
    ) {
        if (plugin.state == Plugin.State.Loaded) {
            plugin.start()
        }
    }

    override suspend fun stopPlugins() = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Stopping ${plugins.size} plugins.." }
            }
            onSuccess { millis, _ ->
                log.info { "${plugins.size} plugins stopped in $millis millis." }
            }
            onFailure {
                log.info { "Stopping ${plugins.size} failed by unexpected exception" }
                log.debug(it) { "Stopping ${plugins.size} failed" }
            }
        }
    ) {
        plugins.reversed().forEach {
            stopPlugin(it)
        }
    }

    override suspend fun stopPlugin(plugin: Plugin) = wrappedRunNoResult(
        logging = {
            beforeRun {
                log.info { "Stopping plugin $plugin.." }
            }
            onSuccess { millis, _ ->
                log.info { "Plugin $plugin stopped in $millis millis." }
            }
            onFailure {
                log.info { "Stopping plugin $plugin failed by unexpected exception" }
                log.debug(it) { "Stopping plugin $plugin failed" }
            }
        }
    ) {
        if (plugin.state == Plugin.State.Started) {
            plugin.stop()
        }
    }

    data class Info(
        val version: String
    )
}
