package ru.foxesworld.foxxey.server.clientrestapi.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import org.koin.core.module.Module
import ru.foxesworld.foxxey.server.Server
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import java.io.File

object Modules {

    fun Module.configuration() {
        single<RestApiConfig> {
            ConfigLoader.Builder()
                .addFileSource(File(Server.configFolder, "restapi.json"))
                .build()
                .loadConfigOrThrow()
        }
    }
}
