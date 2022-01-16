package ru.foxesworld.foxxey.server.clientrestapi.restapi

import com.sksamuel.hoplite.ConfigAlias

data class RestApiConfig(
    @ConfigAlias("rootPath")
    val rootPath: String,
    @ConfigAlias("jarPath")
    val jrePath: JrePath
) {

    data class JrePath(
        @ConfigAlias("windows")
        val windows: String,
        @ConfigAlias("linux")
        val linux: String,
        @ConfigAlias("darwin")
        val darwin: String,
        @ConfigAlias("default")
        val default: String
    )
}
