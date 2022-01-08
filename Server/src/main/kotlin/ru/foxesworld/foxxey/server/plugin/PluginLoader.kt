package ru.foxesworld.foxxey.server.plugin

/**
 * Loads plugins
 * @see Plugin
 */
interface PluginLoader {

    /**
     * Loads all plugins
     * @return Set of loaded [Plugin.Data]
     */
    fun loadAll(): Set<Plugin.Data>
}
