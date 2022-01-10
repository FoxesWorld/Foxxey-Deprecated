package ru.foxesworld.foxxey.server.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import kotlinx.coroutines.DelicateCoroutinesApi
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.*
import ru.foxesworld.foxxey.server.plugins.JarPluginsLoader
import ru.foxesworld.foxxey.server.plugins.PluginsLoader
import java.io.File

@DelicateCoroutinesApi
object Modules {

    val foxxeyLauncher = module {
        single {
            FoxxeyLauncher()
        } bind Launcher::class
        single {
            loadConfigFromFileOrResourceOrThrow<Launcher.Config>("configs/launcher.json")
        }
    }

    val foxxeyServer = module {
        single {
            loadConfigFromResource<FoxxeyBaseServer.Info>("configs/info.json")
        }
        single {
            FoxxeyServer(get())
        } bind Server::class
        single {
            loadConfigFromFileOrResourceOrThrow<Server.Config>("configs/server.json")
        }
        single {
            JarPluginsLoader()
        } bind PluginsLoader::class
    }

    private inline fun <reified T> loadConfigFromResource(fileName: String): T = ConfigLoader.Builder()
        .addResourceSource("/$fileName")
        .build()
        .loadConfigOrThrow()

    private inline fun <reified T> loadConfigFromFileOrResourceOrThrow(fileName: String): T = ConfigLoader.Builder()
        .addFileSource(File(fileName), optional = true)
        .addResourceSource("/$fileName")
        .build()
        .loadConfigOrThrow()
}
