package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import picocli.CommandLine

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "start",
    description = [
        "Starts the server"
    ]
)
class StartCommand : BaseCommand() {

    override fun execute() {
        GlobalScope.launch(server.coroutineContext) {
            server.start()
        }
    }
}
