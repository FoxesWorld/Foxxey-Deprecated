package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import picocli.CommandLine

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "version",
    description = [
        "Prints the server version"
    ]
)
class VersionCommand : BaseCommand() {

    override fun execute() {
        log.info {
            "Foxxey Server v. ${parent.server.version} © FoxesWorld 2022"
        }
    }
}
