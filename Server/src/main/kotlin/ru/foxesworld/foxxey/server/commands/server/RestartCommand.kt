package ru.foxesworld.foxxey.server.commands.server

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import picocli.CommandLine
import ru.foxesworld.foxxey.server.commands.CLICommands

@DelicateCoroutinesApi
@CommandLine.Command(
    name = "restart",
    description = [
        "Restarts the server"
    ]
)
class RestartCommand : Runnable {

    @CommandLine.ParentCommand
    lateinit var parent: CLICommands

    override fun run() {
        GlobalScope.launch(parent.server.coroutineContext) {
            parent.server.restart()
        }
    }
}
