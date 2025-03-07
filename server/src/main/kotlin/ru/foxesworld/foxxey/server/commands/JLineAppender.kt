package ru.foxesworld.foxxey.server.commands

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import org.jline.reader.LineReader
import picocli.CommandLine

@Suppress("MemberVisibilityCanBePrivate")
class JLineAppender : AppenderBase<ILoggingEvent>() {
    lateinit var encoder: PatternLayoutEncoder
    var lineReader: LineReader? = null

    override fun append(eventObject: ILoggingEvent?) {
        val encoded = CommandLine.Help.Ansi.AUTO.string(
            String(encoder.encode(eventObject))
        )
        lineReader?.printAbove(encoded) ?: println(encoded)
    }
}
