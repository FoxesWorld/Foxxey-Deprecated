package ru.foxesworld.foxxey.server.plugins

import com.sksamuel.hoplite.ConfigAlias
import org.koin.core.component.KoinComponent
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.plugins.Plugin.State.*
import java.io.File

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class Plugin(
    val info: Info
) : KoinComponent {

    var state: State = Unloaded

    open suspend fun start() {
        state = Started
    }

    open suspend fun stop() {
        state = Stopped
    }

    open suspend fun load() {
        state = Loaded
    }

    open suspend fun unload() {
        state = Unloaded
    }

    override fun toString(): String = info.toString()

    enum class State {

        Loaded,
        Unloaded,
        Started,
        Stopped
    }

    data class Info(
        @ConfigAlias("id")
        val id: String,
        @ConfigAlias("name")
        val name: String = id,
        @ConfigAlias("versionCode")
        val versionCode: Int,
        @ConfigAlias("version")
        val version: String = versionCode.toString(),
        @ConfigAlias("dependencies")
        val dependencies: Set<Dependency>,
        @ConfigAlias("pluginClass")
        val pluginClass: String
    ) {

        fun hasDependency(info: Info): Boolean {
            dependencies.forEach {
                if (it.id == info.id) {
                    return true
                }
            }

            return false
        }

        data class Dependency(
            @ConfigAlias("id")
            val id: String,
            @ConfigAlias("versionCode")
            val versionCode: VersionCode
        ) {

            data class VersionCode(
                @ConfigAlias("from")
                val from: Int,
                @ConfigAlias("to")
                val to: Int
            ) {

                override fun toString(): String = "from $from to $to"
            }

            override fun toString(): String = "$id with version code $versionCode"
        }

        override fun toString(): String = "$name v. $version"
    }

    protected fun localConfigFile(configName: String): File {
        val configsFolder = Server.configFolder
        if (!configsFolder.exists()) {
            configsFolder.mkdir()
        }
        return File(configsFolder, configName)
    }

    protected fun File.createDefaultFromResourcesIfNotExists(clazz: Class<*>) {
        if (exists()) {
            return
        }
        writeBytes(
            clazz.getResourceAsStream("/$name")!!.readAllBytes()
        )
    }
}
