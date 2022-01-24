package ru.foxesworld.foxxey.server.clientgui.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import org.koin.core.module.Module
import ru.foxesworld.foxxey.server.clientgui.ClientGUIPlugin
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

object Modules {

    fun Module.configuration(info: Plugin.Info) {
        single<ClientGUIPlugin.Config> {
            ConfigLoader.Builder()
                .addFileSource(File(info.configFolder, "clientgui.json"))
                .build()
                .loadConfigOrThrow()
        }
    }
}
