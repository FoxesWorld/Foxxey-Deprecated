package ru.foxesworld.foxxey.server.plugins

import java.io.File
import java.net.URLClassLoader

class PluginsClassLoader(parent: ClassLoader?) : URLClassLoader(arrayOf(), parent) {

    fun addPluginJar(jarFile: File) {
        addURL(jarFile.toURI().toURL())
    }
}
