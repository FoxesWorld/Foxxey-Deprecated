package ru.foxesworld.foxxey.server.commands

import org.koin.core.component.KoinComponent

interface CommandHandler : KoinComponent {

    fun start()

    fun stop()
}
