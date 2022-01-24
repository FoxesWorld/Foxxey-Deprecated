package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.server.engine.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import ru.foxesworld.foxxey.server.plugins.Plugin

internal class ClientRestApiPluginTest {

    @Test
    fun `WHEN plugins IS starting THEN no exception HAS thrown`() {
        startKoin {
            modules(
                module {
                    single {
                        embeddedServer(TestEngine) {}.start().application
                    }
                    single {
                        RestApiConfig(
                            "/",
                            mapOf()
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
