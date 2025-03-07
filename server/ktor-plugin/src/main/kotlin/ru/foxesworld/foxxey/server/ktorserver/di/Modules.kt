package ru.foxesworld.foxxey.server.ktorserver.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging
import org.koin.core.module.Module
import org.koin.dsl.bind
import ru.foxesworld.foxxey.server.ktorserver.config.KtorServerConfig
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File
import java.security.KeyStore

object Modules {

    fun Module.ktorServer(info: Plugin.Info) {
        single {
            val applicationEngine: ApplicationEngine = get()
            applicationEngine.application
        } bind Application::class
        single {
            applicationEngineEnvironment {
                val config: KtorServerConfig = get()
                log = KotlinLogging.logger("ktor.application")
                rootPath = config.rootPath
                if (config.https.enabled) {
                    sslConnector(
                        keyStore = KeyStore.getInstance(
                            File(config.https.keyStore.path),
                            config.https.keyStore.password.toCharArray()
                        ),
                        keyStorePassword = { config.https.keyStore.password.toCharArray() },
                        keyAlias = config.https.key.alias,
                        privateKeyPassword = { config.https.key.password.toCharArray() }
                    ) {
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
        single<ApplicationEngine> {
            val config: KtorServerConfig = get()
            embeddedServer(Netty, environment = get()) {
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
        single<KtorServerConfig> {
            ConfigLoader.Builder()
                .addFileSource(File(info.configFolder, "ktor.json"))
                .build()
                .loadConfigOrThrow()
        }
    }

}
