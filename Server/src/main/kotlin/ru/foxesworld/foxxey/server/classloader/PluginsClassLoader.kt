package ru.foxesworld.foxxey.server.classloader

import java.io.File
import java.net.URLClassLoader

class PluginsClassLoader(parent: ClassLoader?) : URLClassLoader(arrayOf(), parent) {

    fun addPluginJar(jarFile: File) {
        addURL(jarFile.toURI().toURL())
    }
}
