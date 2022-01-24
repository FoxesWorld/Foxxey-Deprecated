package ru.foxesworld.foxxey.server.clientrestapi.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import org.koin.core.module.Module
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

object Modules {

    fun Module.configuration(info: Plugin.Info) {
        single<RestApiConfig> {
            ConfigLoader.Builder()
                .addFileSource(File(info.configFolder, "restapi.json"))
                .build()
                .loadConfigOrThrow()
        }
    }
}
