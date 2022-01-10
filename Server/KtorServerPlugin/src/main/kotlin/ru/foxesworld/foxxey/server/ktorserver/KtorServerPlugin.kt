package ru.foxesworld.foxxey.server.ktorserver

import io.ktor.server.engine.*
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.ktorserver.di.Modules.ktorServer
import ru.foxesworld.foxxey.server.plugins.Plugin

class KtorServerPlugin(info: Info) : Plugin(info) {

    private val module = module {
        ktorServer()
    }
    private val ktorServer: ApplicationEngine by inject()

    override suspend fun start() {
        ktorServer.start()
    }

    override suspend fun stop() {
        ktorServer.stop(0, 2000)
    }

    override suspend fun load() {
        getKoin().loadModules(listOf(module))
        super.load()
    }

    override suspend fun unload() {
        getKoin().unloadModules(listOf(module))
        super.unload()
    }
}
