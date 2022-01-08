package ru.foxesworld.foxxey.server.config

import com.sksamuel.hoplite.ConfigAlias

/**
 * Plugins configuration model
 * @param dir Path to dir with plugins
 */
data class PluginsConfig(
    @ConfigAlias("dir")
    val dir: String
)
