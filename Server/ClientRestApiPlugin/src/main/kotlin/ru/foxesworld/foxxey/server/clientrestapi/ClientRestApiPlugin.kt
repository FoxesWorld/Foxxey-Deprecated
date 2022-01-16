package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.routing.*
import io.ktor.server.engine.*
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.clientrestapi.Routes.jre
import ru.foxesworld.foxxey.server.clientrestapi.di.Modules.configuration
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

@Suppress("unused")
class ClientRestApiPlugin(info: Info) : Plugin(info) {

    private val module = module {
        configuration()
    }
    private val applicationEngine: ApplicationEngine by inject()

    override suspend fun start() {
        val restApiConfig: RestApiConfig = get()
        applicationEngine.application.routing {
            route(restApiConfig.rootPath) {
                jre(restApiConfig.jrePath)
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
