package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.application.*
import io.ktor.routing.*
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.clientrestapi.ktor.Routes.jre
import ru.foxesworld.foxxey.server.clientrestapi.di.Modules.configuration
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

@Suppress("unused")
class ClientRestApiPlugin(info: Info) : Plugin(info) {

    private val module = module {
        configuration()
    }
    private val ktorServer: Application by inject()

    override suspend fun start() {
        val restApiConfig: RestApiConfig = get()
        ktorServer.routing {
            route(restApiConfig.rootPath) {
                jre(restApiConfig.jrePaths)
            }
        }
        super.start()
    }

    override suspend fun stop() {
        super.stop()
    }

    override suspend fun load() {
        File(Server.configFolder, "restapi.json").createDefaultFromResourcesIfNotExists(ClientRestApiPlugin::class.java)
        getKoin().loadModules(listOf(module))
        super.load()
    }

    override suspend fun unload() {
        getKoin().loadModules(listOf(module))
        super.unload()
    }
}
