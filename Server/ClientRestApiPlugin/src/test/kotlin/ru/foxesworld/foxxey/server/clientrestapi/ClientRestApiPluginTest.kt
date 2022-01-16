package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import ru.foxesworld.foxxey.server.plugins.Plugin

internal class ClientRestApiPluginTest {

    @Test
    fun testStart() {
        startKoin {
            modules(
                module {
                    single {
                        embeddedServer(Netty, port = 9020) {

                        }
                    } bind ApplicationEngine::class
                    single {
                        RestApiConfig(
                            "/",
                            RestApiConfig.JrePath(windows = "", linux = "", darwin = "", default = "")
                        )
                    }
                }
            )
        }
        val clientRestApiPlugin = ClientRestApiPlugin(
            info = Plugin.Info(
                id = "",
                name = "",
                versionCode = 0,
                version = "",
                dependencies = setOf(),
                pluginClass = ""
            )
        )
        runBlocking {
            clientRestApiPlugin.start()
        }
    }
}
