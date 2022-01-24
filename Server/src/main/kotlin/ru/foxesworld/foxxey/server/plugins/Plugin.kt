package ru.foxesworld.foxxey.server.plugins

import com.sksamuel.hoplite.ConfigAlias
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.plugins.Plugin.State.*
import java.io.File

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class Plugin(
    val info: Info,
    val module: Module? = null
) : KoinComponent {

    var state: State = Unloaded

    suspend fun start() {
        onStart()
        state = Started
    }

    open suspend fun onStart() {}

    suspend fun stop() {
        onStop()
        state = Stopped
    }

    open suspend fun onStop() {}

    fun load() {
        module?.run {
            getKoin().loadModules(listOf(this))
        }
        state = Loaded
    }

    fun unload() {
        module?.run {
            getKoin().unloadModules(listOf(this))
        }
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
        val pluginClass: String,
        @ConfigAlias("configNames")
        val configNames: List<String>,
        val configFolder: File = File(Server.configFolder, id)
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
}
