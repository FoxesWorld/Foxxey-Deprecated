package ru.foxesworld.foxxey.server.ktorserver

import mu.KotlinLogging
import org.koin.core.component.inject
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.plugins.Plugin

private val log = KotlinLogging.logger {  }

class KtorServerPlugin(info: Info) : Plugin(info) {

    private val server: Server by inject()

    override suspend fun start() {
        log.info { "Hello from ktor server plugin! I can to get $server" }
    }
}
