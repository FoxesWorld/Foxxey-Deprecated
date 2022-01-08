package ru.foxesworld.foxxey.server.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import ru.foxesworld.foxxey.server.FoxxeyServer
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.config.InfoConfig
import ru.foxesworld.foxxey.server.config.KtorServerConfig
import ru.foxesworld.foxxey.server.config.PluginsConfig
import ru.foxesworld.foxxey.server.plugin.JarPluginLoader
import ru.foxesworld.foxxey.server.plugin.PluginLoader
import java.io.File
import java.security.KeyStore

/**
 * Dependency injection logic
 */
@Suppress("MemberVisibilityCanBePrivate")
object Modules {

    /**
     * Release dependency injection logic
     */
    val release = DI.Module("release") {
        import(server)
        import(ktorServer)
        import(plugins)
    }

    /**
     * Server dependency injection logic
     */
    val server = DI.Module("server") {
        bindSingleton<Server> {
            FoxxeyServer(instance(), instance())
        }
        bindSingleton<InfoConfig> {
            ConfigLoader.Builder()
                .addResourceSource("/info.json")
                .build()
                .loadConfigOrThrow()
        }
    }

    /**
     * Ktor server dependency injection logic
     */
    val ktorServer = DI.Module("ktor") {
        bindSingleton {
            applicationEngineEnvironment {
                val config: KtorServerConfig = instance()
                log = KotlinLogging.logger("ktor.application")
                rootPath = config.rootPath
                if (config.https.enabled) {
                    sslConnector(
                        keyStore = KeyStore.getInstance(config.https.keyStore.type),
                        keyStorePassword = { config.https.keyStore.password.toCharArray() },
                        keyAlias = config.https.key.alias,
                        privateKeyPassword = { config.https.key.password.toCharArray() }
                    ) {
                        keyStorePath = File(config.https.keyStore.path)
                        host = config.https.host
                        port = config.https.port
                    }
                }
                if (config.http.enabled) {
                    connector {
                        host = config.http.host
                        port = config.http.port
                    }
                }
            }
        }
        bindSingleton<ApplicationEngine> {
            val config: KtorServerConfig = instance()
            embeddedServer(Netty, environment = instance()) {
                runningLimit = config.runningLimit
                requestQueueLimit = config.requestQueueLimit
            }.apply {
                application.apply {
                    if (config.callLogging.enabled) {
                        install(CallLogging) {
                            level = config.callLogging.level
                        }
                    }
                }
            }
        }
        bindSingleton<KtorServerConfig> {
            val fileName = "ktor.json"
            ConfigLoader.Builder()
                .addFileSource(File(fileName), optional = true)
                .addResourceSource("/$fileName")
                .build()
                .loadConfigOrThrow()
        }
    }

    /**
     * Dependency injection logic related to plugins
     */
    val plugins = DI.Module("plugins") {
        bindSingleton<PluginLoader> {
            JarPluginLoader(instance())
        }
        bindSingleton<PluginsConfig> {
            val fileName = "plugins.json"
            ConfigLoader.Builder()
                .addFileSource(File(fileName), optional = true)
                .addResourceSource("/$fileName")
                .build()
                .loadConfigOrThrow()
        }
    }
}
