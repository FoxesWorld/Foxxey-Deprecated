package ru.foxesworld.foxxey.server.clientrestapi.restapi

import com.sksamuel.hoplite.ConfigAlias

data class RestApiConfig(
    @ConfigAlias("rootPath")
    val rootPath: String,
    @ConfigAlias("jarPaths")
    val jrePaths: Map<String, String>
)
