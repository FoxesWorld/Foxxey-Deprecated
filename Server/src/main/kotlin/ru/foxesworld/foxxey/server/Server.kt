package ru.foxesworld.foxxey.server

import com.sksamuel.hoplite.ConfigAlias
import org.koin.core.component.KoinComponent
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

@Suppress("unused")
interface Server : KoinComponent {

    val version: String
    val config: Config
    val plugins: List<Plugin>

    suspend fun restart()

    suspend fun start()

    suspend fun stop()

    suspend fun loadPluginsFromDir(dir: String)

    suspend fun loadPluginFromFile(file: File)

    suspend fun reloadPlugins()

    suspend fun reloadPlugin(plugin: Plugin)

    suspend fun unloadPlugins()

    suspend fun unloadPlugin(plugin: Plugin)

    suspend fun loadPlugins()

    suspend fun loadPlugin(plugin: Plugin)

    suspend fun startPlugins()

    suspend fun startPlugin(plugin: Plugin)

    suspend fun stopPlugins()

    suspend fun stopPlugin(plugin: Plugin)

    fun findPluginById(id: String): Result<Plugin> {
        plugins.forEach {
            if (it.info.id == id) {
                return Result.success(it)
            }
        }
        return Result.failure(IllegalArgumentException("Plugin with id $id not exists"))
    }

    data class Config(
        @ConfigAlias("pluginsDir")
        val pluginsDir: String,
        @ConfigAlias("whitelist")
        val whitelist: Whitelist
    ) {

        data class Whitelist(
            @ConfigAlias("enabled")
            val enabled: Boolean,
            @ConfigAlias("userNicknames")
            val userNicknames: Set<String>
        )
    }
}
