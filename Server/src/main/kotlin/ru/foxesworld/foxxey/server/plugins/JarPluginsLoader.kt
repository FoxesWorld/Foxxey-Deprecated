package ru.foxesworld.foxxey.server.plugins

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addStreamSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import ru.foxesworld.foxxey.server.logging.wrappedRun
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.jar.JarFile

private val log = KotlinLogging.logger { }

class JarPluginsLoader : PluginsLoader {

    private val pluginsClassLoader = PluginsClassLoader(ClassLoader.getSystemClassLoader())

    override suspend fun loadPluginsFromDir(dir: File): List<Plugin> {
        if (!dir.isDirectory) {
            if (!dir.exists()) {
                dir.mkdir()
            }
            return emptyList()
        }
        val listFiles = dir.listFiles(FileFilter {
            it.extension == "jar"
        }) ?: return emptyList()

        val pluginsData = asyncLoadPluginsDataFromFiles(listFiles).apply {
            sortByDependencyTree()
        }
        return pluginsData.mapNotNull {
            val notInstalledDependencies = it.findNotInstalledDependencies(pluginsData)
            if (notInstalledDependencies.isEmpty()) {
                it.newInstance().getOrNull()
            } else {
                log.info { "Plugin $it have not installed dependencies, install their to load it\n${notInstalledDependencies.beautifulString}" }
                null
            }
        }
    }

    private val List<Plugin.Info.Dependency>.beautifulString: String
        get() {
            val stringBuilder = StringBuilder("\n")
            forEach {
                stringBuilder.append("— $it\n")
            }
            return stringBuilder.toString()
        }

    private fun PluginData.findNotInstalledDependencies(installedPluginsData: List<PluginData>): List<Plugin.Info.Dependency> {
        val notInstalledDependencies = arrayListOf<Plugin.Info.Dependency>()
        pluginInfo.dependencies.forEach { dependency ->
            for (installedPluginDataIndex in installedPluginsData.indices) {
                val installedPluginData = installedPluginsData[installedPluginDataIndex]
                if (installedPluginData.passesDependency(dependency)) break
                if (installedPluginDataIndex == installedPluginsData.lastIndex) {
                    notInstalledDependencies.add(dependency)
                }
            }
        }
        return notInstalledDependencies
    }

    private suspend fun PluginData.newInstance(): Result<Plugin> = wrappedRun(
        logging = {
            beforeRun {
                log.info { "Loading plugin ${this@newInstance} from file ${this@newInstance.pluginFile.name}.." }
            }
            onSuccess { millis, result ->
                log.info { "Plugin $result loaded in $millis millis." }
            }
            onFailure {
                log.info { "Loading plugin ${this@newInstance} failed by unexpected exception" }
                log.debug(it) { "Instantiating plugin class of ${this@newInstance} failed" }
            }
        }
    ) {
        pluginClass.getDeclaredConstructor(pluginInfo::class.java).newInstance(pluginInfo).apply {
            load()
        }
    }

    private fun LinkedList<PluginData>.sortByDependencyTree() {
        if (size < 2) {
            return
        }
        if (size == 2) {
            first.hasDependency(last)
            moveToStart(last)
            return
        }
        var currentItemIndex = lastIndex
        while (true) {
            val currentItem = this[currentItemIndex]
            for (otherItemIndex in 0 until currentItemIndex - 1) {
                val otherItem = this[otherItemIndex]
                if (currentItem.hasDependency(otherItem)) {
                    moveToStart(currentItem)
                    break
                }
                if (otherItemIndex == currentItemIndex - 1) {
                    currentItemIndex++
                    break
                }
            }
        }
    }

    private fun <T> LinkedList<T>.moveToStart(item: T) {
        remove(item)
        add(0, item)
    }

    private fun asyncLoadPluginsDataFromFiles(listFiles: Array<File>): LinkedList<PluginData> {
        val pluginsData: AbstractList<PluginData> = Vector(listFiles.size)
        runBlocking {
            for (file in listFiles) {
                async {
                    file.toJarFile().loadPluginData(file)
                        .onSuccess {
                            pluginsData.add(it)
                        }
                }.start()
            }
        }
        return LinkedList(pluginsData)
    }

    private fun File.toJarFile(): JarFile = JarFile(this)

    private suspend fun JarFile.loadPluginData(file: File): Result<PluginData> = wrappedRun(
        logging = {
            onSuccess { millis, _ ->
                log.debug { "Plugin data loaded from jar $name in $millis millis." }
            }
            onFailure {
                log.debug(it) { "Loading plugin data from jar $name failed" }
            }
        }
    ) {
        val pluginInfo = withContext(Dispatchers.IO) {
            ConfigLoader.Builder()
                .addStreamSource(getInputStream(getJarEntry("plugin.json")), "json")
                .build()
                .loadConfigOrThrow<Plugin.Info>()
        }
        pluginsClassLoader.addPluginJar(file)
        val pluginClass = withContext(Dispatchers.IO) {
            val loadedClass = Class.forName(pluginInfo.pluginClass, true, pluginsClassLoader)
            loadedClass.asSubclass(Plugin::class.java)
        }

        PluginData(pluginClass, pluginInfo, file)
    }

    override suspend fun loadPluginFromFile(file: File): Result<Plugin> = wrappedRun {
        file.toJarFile().loadPluginData(file).getOrThrow().newInstance().getOrThrow()
    }

    class PluginData(
        val pluginClass: Class<out Plugin>,
        val pluginInfo: Plugin.Info,
        val pluginFile: File
    ) {

        fun passesDependency(dependency: Plugin.Info.Dependency): Boolean =
            pluginInfo.id == dependency.id && pluginInfo.versionCode >= dependency.versionCode.from
                    && pluginInfo.versionCode <= dependency.versionCode.to

        fun hasDependency(pluginData: PluginData) = pluginInfo.hasDependency(pluginData.pluginInfo)

        override fun toString(): String = pluginInfo.toString()
    }
}
