package ru.foxesworld.foxxey.server.config

import com.sksamuel.hoplite.ConfigAlias

/**
 * Configuration model for Server
 * @param whitelist Whitelist configuration
 * @see ru.foxesworld.foxxey.server.Server
 */
data class ServerConfig(
    @ConfigAlias("whitelist")
    val whitelist: Whitelist
) {

    /**
     * Whitelist configuration model
     * @param enabled While true only given users can interact with server
     * @param users Ids of users that will be able to interact with server while whitelist enabled
     * @see ru.foxesworld.foxxey.server.config.ServerConfig
     */
    data class Whitelist(
        @ConfigAlias("enabled")
        val enabled: Boolean,
        @ConfigAlias("users")
        val users: Set<String>
    )
}
