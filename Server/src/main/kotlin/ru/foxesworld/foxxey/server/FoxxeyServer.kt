package ru.foxesworld.foxxey.server

import ru.foxesworld.foxxey.server.config.InfoConfig

class FoxxeyServer(
    infoConfig: InfoConfig
) : Server {

    override val version: String = infoConfig.version
}
