package ru.foxesworld.foxxey.server.plugins

import org.koin.core.component.KoinComponent
import java.io.File

interface PluginsLoader : KoinComponent {

    suspend fun loadPluginsFromDir(dir: File): List<Plugin>

    suspend fun loadPluginFromFile(file: File): Result<Plugin>
}
