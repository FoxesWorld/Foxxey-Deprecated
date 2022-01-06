package ru.foxesworld.foxxey.server.config

import com.sksamuel.hoplite.ConfigAlias

/**
 * Information configuration model
 * @param version Version of Foxxey
 */
data class InfoConfig(
    @ConfigAlias("version")
    val version: String
)
