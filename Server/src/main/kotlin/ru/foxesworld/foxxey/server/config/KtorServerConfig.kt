package ru.foxesworld.foxxey.server.config

import com.sksamuel.hoplite.ConfigAlias
import org.slf4j.event.Level

/**
 * Ktor server configuration model
 * @param callLogging Call logging configuration
 * @param https HTTPS configuration
 * @param http HTTP configuration
 * @param rootPath Root path to all routes
 * @param runningLimit Number of concurrently running requests from the same http pipeline
 * @param requestQueueLimit Size of the queue to store ApplicationCall instances that cannot be immediately processed
 */
data class KtorServerConfig(
    @ConfigAlias("callLogging")
    val callLogging: CallLogging,
    @ConfigAlias("https")
    val https: HTTPS,
    @ConfigAlias("http")
    val http: HTTP,
    @ConfigAlias("rootPath")
    val rootPath: String,
    @ConfigAlias("runningLimit")
    val runningLimit: Int,
    @ConfigAlias("requestQueueLimit")
    val requestQueueLimit: Int
) {

    /**
     * Call logging configuration model
     * @param enabled Logging ktor server calls
     * @param level Logging level
     */
    data class CallLogging(
        @ConfigAlias("enabled")
        val enabled: Boolean,
        @ConfigAlias("level")
        val level: Level
    )

    /**
     * HTTPS configuration model
     * @param enabled Listen https
     * @param keyStore Key store configuration
     * @param key Certificate key configuration
     * @param host Host to listen
     * @param port Port to listen
     */
    data class HTTPS(
        @ConfigAlias("enabled")
        val enabled: Boolean,
        @ConfigAlias("keyStore")
        val keyStore: KeyStore,
        @ConfigAlias("key")
        val key: Key,
        @ConfigAlias("host")
        val host: String,
        @ConfigAlias("port")
        val port: Int
    ) {

        /**
         * Key store configuration model
         * @param path Path to key store file
         * @param type Key store type
         * @param password Password to access key store
         */
        data class KeyStore(
            @ConfigAlias("path")
            val path: String,
            @ConfigAlias("type")
            val type: String,
            @ConfigAlias("password")
            val password: String
        )

        /**
         * Certificate key configuration model
         * @param alias Certificate key alias
         * @param password Certificate key password
         */
        data class Key(
            @ConfigAlias("alias")
            val alias: String,
            @ConfigAlias("password")
            val password: String
        )
    }

    /**
     * HTTP configuration model
     * @param enabled Listen http
     * @param host Host to listen
     * @param port Port to listen
     */
    data class HTTP(
        @ConfigAlias("enabled")
        val enabled: Boolean,
        @ConfigAlias("host")
        val host: String,
        @ConfigAlias("port")
        val port: Int
    )
}
