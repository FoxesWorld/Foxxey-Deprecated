package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "gc",
    description = [
        "Call System.gc() method",
        "DO NOT USE IT IF YOU DON'T KNOW WHAT IT DO"
    ],
)
class GarbageCollectCommand : BaseCommand() {

    override fun execute() {
        System.gc()
        log.info {
            """Called.""".trimMargin()
        }
    }
}
