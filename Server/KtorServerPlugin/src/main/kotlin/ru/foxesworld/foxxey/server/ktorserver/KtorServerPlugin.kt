package ru.foxesworld.foxxey.server.ktorserver

import io.ktor.server.engine.*
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.ktorserver.di.Modules.ktorServer
import ru.foxesworld.foxxey.server.plugins.Plugin

class KtorServerPlugin(info: Info) : Plugin(
    info = info,
    module = module {
        ktorServer(info)
    }
) {

    private val ktorServer: ApplicationEngine by inject()

    override suspend fun onStart() {
        ktorServer.start(wait = false)
    }

    override suspend fun onStop() {
        ktorServer.stop(0, 2000)
    }
}
