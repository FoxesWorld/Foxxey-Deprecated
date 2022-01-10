package ru.foxesworld.foxxey.server.ktorserver

import io.ktor.server.engine.*
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.ktorserver.di.Modules.ktorServer
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

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
        File("ktor.json").createDefaultFromResourcesIfNotExists()
        getKoin().loadModules(listOf(module))
        super.load()
    }

    override suspend fun unload() {
        getKoin().unloadModules(listOf(module))
        super.unload()
    }

    private fun File.createDefaultFromResourcesIfNotExists() {
        if (exists()) {
            return
        }
        writeBytes(
            KtorServerPlugin::class.java.getResourceAsStream("/$name")!!.readAllBytes()
        )
    }
}
