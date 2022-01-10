package ru.foxesworld.foxxey.server.clientgui.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import org.koin.core.module.Module
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.clientgui.ClientGUIPlugin
import java.io.File

object Modules {

    fun Module.configuration() {
        single<ClientGUIPlugin.Config> {
            ConfigLoader.Builder()
                .addFileSource(File(Server.configsFolder, "clientgui.json"))
                .build()
                .loadConfigOrThrow()
        }
    }
}
