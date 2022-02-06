package ru.foxesworld.foxxey.server.commands

import ch.qos.logback.classic.LoggerContext
import kotlinx.coroutines.*
import org.jline.console.SystemRegistry
import org.jline.console.impl.SystemRegistryImpl
import org.jline.keymap.KeyMap
import org.jline.reader.*
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.TerminalBuilder
import org.jline.widget.TailTipWidgets
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.shell.jline3.PicocliCommands
import ru.foxesworld.foxxey.server.Server
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Supplier
import kotlin.coroutines.coroutineContext

@DelicateCoroutinesApi
class PicoCLICommandHandler : CommandHandler {
    private lateinit var job: Job
    private val server: Server by inject()

    private suspend fun setUp() {
        val workDir: Supplier<Path> = Supplier {
            Paths.get(System.getProperty("user.dir"))
        }
        val cliCommands = CLICommands(server)
        val picocliCommandsFactory = PicocliCommands.PicocliCommandsFactory()
        val commandLine = CommandLine(cliCommands, picocliCommandsFactory)
        val picocliCommands = PicocliCommands(commandLine)
        val parser: Parser = DefaultParser()
        TerminalBuilder.terminal().use {
            val systemRegistry: SystemRegistry = SystemRegistryImpl(parser, it, workDir, null)
            systemRegistry.setCommandRegistries(picocliCommands)
            systemRegistry.register("help", picocliCommands)

            val lineReader: LineReader = LineReaderBuilder.builder()
                .terminal(it)
                .completer(systemRegistry.completer())
                .parser(parser)
                .variable(LineReader.LIST_MAX, 50)
                .build()
            setUpLoggerToReader(lineReader)
            picocliCommandsFactory.setTerminal(it)
            val widgets =
                TailTipWidgets(lineReader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER)
            widgets.enable()
            val keyMap: KeyMap<Binding> = lineReader.keyMaps["main"]!!
            keyMap.bind(Reference("tailtip-toggle"), KeyMap.alt("s"))
            val prompt = "> "
            var line: String
            while (coroutineContext.isActive) {
                kotlin.runCatching {
                    systemRegistry.cleanUp()
                    line = lineReader.readLine(prompt, null, null as MaskingCallback?, null)
                    systemRegistry.execute(line)
                }.onFailure { e ->
                    if (e is UserInterruptException || e is EndOfFileException) {
                        stop()
                        return
                    }
                    systemRegistry.trace(e as Exception)
                }
            }
        }
    }

    private fun setUpLoggerToReader(lineReader: LineReader) {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        context.loggerList.forEach { logger ->
            logger.iteratorForAppenders().forEach { appender ->
                if (appender is JLineAppender) {
                    appender.lineReader = lineReader
                }
            }
        }
    }

    override fun start() {
        runBlocking {
            job = launch {
                setUp()
            }
        }
    }

    override fun stop() {
        job.cancel()
    }
}
