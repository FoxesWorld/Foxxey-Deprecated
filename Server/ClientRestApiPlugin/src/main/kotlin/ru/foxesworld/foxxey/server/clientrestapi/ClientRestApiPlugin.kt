package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.application.*
import io.ktor.routing.*
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.clientrestapi.di.Modules.configuration
import ru.foxesworld.foxxey.server.clientrestapi.ktor.Routes.jre
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import ru.foxesworld.foxxey.server.plugins.Plugin

@Suppress("unused")
class ClientRestApiPlugin(info: Info) : Plugin(
    info = info,
    module = module {
        configuration(info)
    }
) {

    private val ktorServer: Application by inject()
    private val restApiConfig: RestApiConfig by inject()

    override suspend fun onStart() {
        ktorServer.routing {
            route(restApiConfig.rootPath) {
                jre(restApiConfig.jrePaths)
            }
        }
    }
}
