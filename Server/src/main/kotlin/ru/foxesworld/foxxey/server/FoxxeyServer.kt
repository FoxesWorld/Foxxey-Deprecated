package ru.foxesworld.foxxey.server

import ru.foxesworld.foxxey.server.config.InfoConfig
import ru.foxesworld.foxxey.server.config.ServerConfig

/**
 * Foxxey [Server] implementation
 * @param infoConfig The server information config
 * @param serverConfig The server configuration
 */
class FoxxeyServer(
    infoConfig: InfoConfig,
    private val serverConfig: ServerConfig
) : Server {

    override val version: String = infoConfig.version
}
