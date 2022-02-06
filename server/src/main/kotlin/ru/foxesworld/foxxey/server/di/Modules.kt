package ru.foxesworld.foxxey.server.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.*
import ru.foxesworld.foxxey.server.commands.CommandHandler
import ru.foxesworld.foxxey.server.commands.PicoCLICommandHandler
import ru.foxesworld.foxxey.server.plugins.JarPluginsLoader
import ru.foxesworld.foxxey.server.plugins.PluginsLoader
import java.io.File
import kotlin.coroutines.CoroutineContext

@DelicateCoroutinesApi
object Modules {

    val foxxeyLauncher = module {
        single {
            FoxxeyLauncher()
        } bind Launcher::class
    }

    val commands = module {
        single {
            PicoCLICommandHandler()
        } bind CommandHandler::class
    }

    val foxxeyServer = module {
        single<FoxxeyBaseServer.Info> {
            ConfigLoader.Builder()
                .addResourceSource("/info.json")
                .build()
                .loadConfigOrThrow()
        }
        single {
            FoxxeyServer(get())
        } bind Server::class
        single {
            createConfigFileIfNotExistsAndLoad<Server.Config>("server.json")
        }
        single {
            JarPluginsLoader()
        } bind PluginsLoader::class
        single(named("server")) {
            val serverConfig: Server.Config = get()
            newFixedThreadPoolContext(serverConfig.threadsCount, "Server")
        } bind CoroutineContext::class
    }

    private inline fun <reified T> createConfigFileIfNotExistsAndLoad(fileName: String): T {
        val configsFolder = Server.configFolder
        if (!configsFolder.exists()) {
            configsFolder.mkdir()
        }
        val configFile = File(Server.configFolder, fileName)
        configFile.writeBytes(
            this::class.java.getResourceAsStream("/$fileName")!!.readAllBytes()
        )
        return ConfigLoader.Builder()
            .addFileSource(configFile)
            .build()
            .loadConfigOrThrow()
    }
}
