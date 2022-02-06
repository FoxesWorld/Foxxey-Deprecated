package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine
import ru.foxesworld.foxxey.server.os.OSHelper

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "memory",
    description = [
        "Prints memory information"
    ],
)
class MemoryCommand : BaseCommand() {

    override fun execute() {
        val availableMemory = OSHelper.availableMemoryInMB
        val usedMemory = availableMemory - OSHelper.freeMemoryInMB
        log.info {
            """
                    |
                    |┌───────────────────────────
                    |│ Total memory available: $availableMemory megabytes
                    |│ Used memory: $usedMemory megabytes
                    |└───────────────────────────
                """.trimMargin()
        }
    }
}
