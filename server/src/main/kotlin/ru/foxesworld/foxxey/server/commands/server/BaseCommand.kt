package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.commands.CLICommands
import ru.foxesworld.foxxey.server.logging.wrappedRunNoResult

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
abstract class BaseCommand : Runnable {

    @CommandLine.ParentCommand
    lateinit var parent: CLICommands
    protected val server: Server by lazy(LazyThreadSafetyMode.NONE) {
        parent.server
    }
    protected val name: String by lazy(LazyThreadSafetyMode.NONE) {
        this::class.annotations.forEach { annotation ->
            if (annotation is CommandLine.Command) {
                return@lazy annotation.name
            }
        }
        this::class.java.simpleName
    }

    override fun run() = runBlocking {
        wrappedRunNoResult(
            logging = {
                beforeRun {
                    log.debug {
                        "Executing command $name.."
                    }
                }
                onSuccess { millis, _ ->
                    log.debug {
                        "Command $name executed in $millis millis."
                    }
                }
                onFailure {
                    log.info { "Command $name execution failed" }
                    log.debug(it) { "Command $name execution failed" }
                }
            }
        ) {
            execute()
        }
    }

    abstract fun execute()
}
