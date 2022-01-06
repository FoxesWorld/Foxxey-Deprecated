package ru.foxesworld.foxxey.server

import io.ktor.server.engine.*
import mu.KotlinLogging
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.newInstance
import ru.foxesworld.foxxey.server.di.Modules.release
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() {
    val di = DI {
        import(release)
    }
    val launcher by di.newInstance {
        Launcher(instance())
    }
    launcher.wrappedStart()
}

val logger = KotlinLogging.logger { }

class Launcher(
    private val ktorServer: ApplicationEngine
) {

    fun wrappedStart() {
        logger.info { "Starting.." }
        val measuredTimeInMillis = measureTimeMillis {
            kotlin.runCatching { start() }
                .onFailure {
                    logger.error(it) { "Starting failed by unexpected exception" }
                    return
                }
        }
        logger.info { "Started in $measuredTimeInMillis millis." }
    }

    private fun start() {
        addShutdownHookToStop()
        ktorServer.start()
    }

    private fun addShutdownHookToStop() {
        logger.debug { "Adding shutdown hook to stop.." }
        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                wrappedStop()
            }
        )
        logger.debug { "Shutdown hook added" }
    }

    private fun wrappedStop() {
        logger.info { "Stopping.." }
        val measuredTimeInMillis = measureTimeMillis {
            kotlin.runCatching { stop() }
                .onFailure {
                    logger.error(it) { "Stopped by unexpected exception" }
                    return
                }
        }
        logger.info { "Stopped in $measuredTimeInMillis millis." }
    }

    private fun stop() {
        ktorServer.stop(0, 1000)
    }
}
