package ru.foxesworld.foxxey.server.ktorserver

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import ru.foxesworld.foxxey.server.plugins.Plugin

internal class KtorServerPluginTest : KoinTest {

    @Test
    fun `WHEN plugin IS starting THEN no exception HAS thrown`() {
        startKoin {
        }
        val plugin = KtorServerPlugin(
            Plugin.Info(
                id = "",
                name = "",
                versionCode = 0,
                version = "",
                dependencies = setOf(),
                pluginClass = "",
                configNames = emptyList()
            )
        )
        runBlocking {
            plugin.load()
            plugin.start()
        }
    }
}
