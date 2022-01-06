package ru.foxesworld.foxxey.server.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
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
import ru.foxesworld.foxxey.server.config.ServerConfig
import java.io.File
import java.security.KeyStore
import kotlin.io.path.Path

@Suppress("MemberVisibilityCanBePrivate")
object Modules {

    val release = DI.Module("release") {
        import(configuration)
        import(ktor)
        import(foxxey)
    }

    val foxxey = DI.Module("foxxey") {
        bindSingleton<Server> {
            FoxxeyServer(instance())
        }
    }

    val configuration = DI.Module("configuration") {
        bindSingleton<InfoConfig> {
            ConfigLoader.Builder()
                .addSource(PropertySource.resource("/info.json"))
                .build()
                .loadConfigOrThrow()
        }
        bindSingleton<ServerConfig> {
            ConfigLoader.Builder()
                .addSource(PropertySource.resource("/server.json"))
                .addSource(PropertySource.path(Path("server.json"), optional = true))
                .build()
                .loadConfigOrThrow()
        }
        bindSingleton<KtorServerConfig> {
            ConfigLoader.Builder()
                .addSource(PropertySource.resource("/ktor.json"))
                .addSource(PropertySource.path(Path("ktor.json"), optional = true))
                .build()
                .loadConfigOrThrow()
        }
    }

    val ktor = DI.Module("ktor") {
        bindSingleton {
            val config: KtorServerConfig = instance()
            applicationEngineEnvironment {
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
                if (config.callLogging.enabled) {
                    application.install(CallLogging) {
                        level = config.callLogging.level
                    }
                }
            }
        }
    }
}
