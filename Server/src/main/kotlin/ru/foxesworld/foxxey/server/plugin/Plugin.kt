package ru.foxesworld.foxxey.server.plugin

import com.sksamuel.hoplite.ConfigAlias
import mu.KotlinLogging
import ru.foxesworld.foxxey.server.Launcher
import ru.foxesworld.foxxey.server.logging.runCatchingWithMeasuring

private val logger = KotlinLogging.logger { }

/**
 * Plugin class for implementations
 * @param launcher Launcher that was loaded the plugin
 * @param config Plugin configuration from jar
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Plugin(
    val launcher: Launcher,
    val config: Config
) {

    /**
     * Invokes [start] with logging and catching
     */
    fun wrappedStart() {
        logger.info { "Starting plugin $this.." }
        runCatchingWithMeasuring(
            {
                start()
            }
        ) { millis ->
            logger.info { "Plugin $this started in $millis millis." }
        }.onFailure { e ->
            logger.info { "Starting plugin $this failed by unexpected exception" }
            logger.debug(e) { "Starting plugin $this failed" }
        }
    }

    /**
     * Starts the plugin
     */
    open fun start() {

    }

    /**
     * Invokes [stop] with logging and catching
     */
    fun wrappedStop() {
        logger.info { "Stopping plugin $this.." }
        runCatchingWithMeasuring(
            {
                stop()
            }
        ) { millis ->
            logger.info { "Plugin $this stopped in $millis millis." }
        }.onFailure {
            logger.info { "Stopping plugin $this failed by unexpected exception" }
            logger.debug { "Stopping plugin $this failed" }
        }
    }

    /**
     * Stops the plugin
     */
    open fun stop() {
    }

    override fun toString(): String {
        return config.toString()
    }

    /**
     * Plugin data for instantiating
     * @param clazz The plugin class
     * @param config A plugin config
     */
    data class Data(
        val clazz: Class<Plugin>,
        val config: Config
    ) {

        /**
         * Checks if the data has [data] in dependencies
         * @param data
         * @return True if the data is dependent on [data]
         */
        fun hasDependency(data: Data): Boolean {
            val dataId: String = data.config.id
            val dataVersionCode: Int = data.config.versionCode
            config.dependencies.forEach {
                if (it.id == dataId && it.versionCode.from <= dataVersionCode && it.versionCode.to >= dataVersionCode) {
                    return true
                }
            }
            return false
        }

        override fun toString(): String {
            return config.toString()
        }
    }

    /**
     * Plugin configuration model
     * @param id The plugin id
     * @param name The plugin name. Returns [id] if isn't declared in config
     * @param versionCode The plugin version code
     * @param version The plugin version. Returns [versionCode] if isn't declared in config
     * @param dependencies The plugin dependencies
     * @param pluginClass The plugin [Plugin] implementation class
     */
    @Suppress("MemberVisibilityCanBePrivate")
    data class Config(
        @ConfigAlias("id")
        val id: String,
        @ConfigAlias("name")
        var name: String = id,
        @ConfigAlias("versionCode")
        val versionCode: Int,
        @ConfigAlias("version")
        var version: String = versionCode.toString(),
        @ConfigAlias("dependencies")
        val dependencies: Set<Dependency>,
        @ConfigAlias("pluginClass")
        val pluginClass: String
    ) {

        /**
         * Returns name with version in format "name v. version"
         */
        val nameWithVersion: String = "$name v. $version"

        override fun toString(): String {
            return nameWithVersion
        }

        /**
         * Dependency configuration model
         * @param id The dependency id
         * @param versionCode The dependency required version code
         */
        data class Dependency(
            @ConfigAlias("id")
            val id: String,
            @ConfigAlias("versionCode")
            val versionCode: VersionCode,
        ) {

            /**
             * Version code configuration model
             * @param from Minimal supported version code
             * @param to Maximum supported version code
             */
            data class VersionCode(
                @ConfigAlias("from")
                val from: Int,
                @ConfigAlias("to")
                val to: Int
            )
        }
    }

}
