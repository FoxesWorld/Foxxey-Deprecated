package ru.foxesworld.foxxey.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.koin.core.component.inject
import ru.foxesworld.foxxey.server.Launcher.Config
import kotlin.concurrent.thread

@DelicateCoroutinesApi
class FoxxeyLauncher : Launcher {

    override val config: Config by inject()
    override val server: Server by inject()

    override fun launch() {
        val coroutineContext = newFixedThreadPoolContext(config.threadsCount, "Server")
        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                runBlocking(coroutineContext) {
                    server.stop()
                }
            }
        )
        runBlocking(coroutineContext) {
            server.start()
        }
    }
}
