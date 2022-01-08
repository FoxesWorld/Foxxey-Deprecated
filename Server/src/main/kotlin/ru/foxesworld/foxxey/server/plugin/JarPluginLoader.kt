package ru.foxesworld.foxxey.server.plugin

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import mu.KotlinLogging
import ru.foxesworld.foxxey.server.config.PluginsConfig
import ru.foxesworld.foxxey.server.logging.runCatchingWithMeasuring
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

private val logger = KotlinLogging.logger { }

/**
 * Jar plugins loader
 * @param pluginsConfig Configuration of plugins loading
 */
class JarPluginLoader(
    private val pluginsConfig: PluginsConfig
) : PluginLoader {

    override fun loadAll(): Set<Plugin.Data> = ArrayList(
        jarFilesFromPluginsDir().mapNotNull {
            it.wrappedLoadPlugin().getOrNull()
        }
    ).apply {
        sortByDependenciesTree()
    }.toSet()

    /**
     * Sorts the list by dependencies tree
     */
    private fun ArrayList<Plugin.Data>.sortByDependenciesTree() {
        var currentElementIndex = lastIndex
        repeat(lastIndex) {
            val currentElement = get(currentElementIndex)
            for (elementForCompareIndex in 0..currentElementIndex) {
                val elementForCompare = get(elementForCompareIndex)
                if (elementForCompare.hasDependency(currentElement)) {
                    remove(currentElement)
                    add(0, currentElement)
                    break
                }
                if (elementForCompareIndex == currentElementIndex) {
                    currentElementIndex--
                    break
                }
            }
        }
    }

    /**
     * Invokes [loadPlugin] with logging and catching
     * @return Result with loaded [Plugin.Data]
     */
    private fun JarFile.wrappedLoadPlugin(): Result<Plugin.Data> {
        logger.info { "Loading plugin from jar $name.." }
        return runCatchingWithMeasuring(
            {
                loadPlugin()
            }
        ) { millis, data ->
            logger.info { "Plugin $data loaded from jar $name in $millis millis." }
        }.onFailure { e ->
            logger.info { "Loading plugin from jar $name failed by unexpected exception" }
            logger.debug(e) { "Loading plugin from jar $name failed" }
        }
    }

    /**
     * Loads [Plugin.Data] from the jar file
     * @return Loaded [Plugin.Data]
     * @throws NullPointerException If no configuration founded in the jar
     * @throws ClassNotFoundException If declared in configuration plugin class not founded
     */
    private fun JarFile.loadPlugin(): Plugin.Data {
        val pluginConfig = wrappedLoadPluginConfig().getOrThrow()
        val pluginClass = wrappedLoadPluginClass(pluginConfig.pluginClass).getOrThrow()
        return Plugin.Data(pluginClass, pluginConfig)
    }


    /**
     * Invokes [loadPluginConfig] with logging and catching
     * @return Result with loaded [Plugin.Config]
     */
    private fun JarFile.wrappedLoadPluginConfig(): Result<Plugin.Config> {
        logger.debug { "Loading plugin config from jar $name.." }
        return runCatchingWithMeasuring(
            {
                loadPluginConfig()
            }
        ) { millis, pluginConfig ->
            logger.debug { "Plugin config $pluginConfig loaded from jar $name in $millis millis." }
        }.onFailure { e ->
            logger.debug(e) { "Loading plugin config from jar $name failed" }
        }
    }

    /**
     * Loads [Plugin.Config] from the jar
     * @return Loaded [Plugin.Config]
     */
    private fun JarFile.loadPluginConfig(): Plugin.Config {
        val jarEntry = getJarEntry("plugin.json")
        val configContent = getInputStream(jarEntry).bufferedReader().readText()
        return ConfigLoader.Builder()
            .addSource(PropertySource.string(configContent, "json"))
            .build()
            .loadConfigOrThrow()
    }

    /**
     * Invokes [loadPluginClass] with logging and catching
     * @param pluginClassName Class name of [Plugin] implementation in the jar
     * @return Result with the class
     */
    private fun JarFile.wrappedLoadPluginClass(pluginClassName: String): Result<Class<Plugin>> {
        logger.debug { "Loading plugin class $pluginClassName from jar $name.." }
        return runCatchingWithMeasuring(
            {
                loadPluginClass(pluginClassName)
            }
        ) { millis ->
            logger.debug { "Plugin class $pluginClassName loaded in $millis millis." }
        }.onFailure { e ->
            logger.debug(e) { "Loading plugins class $pluginClassName from jar $name failed" }
        }
    }

    /**
     * Loads [Plugin] implementation class from the jar
     * @param pluginClassName The class name
     * @return The class
     */
    @Suppress("UNCHECKED_CAST")
    private fun JarFile.loadPluginClass(pluginClassName: String): Class<Plugin> {
        val pluginFilePath = File(pluginsConfig.dir, name.substringAfterLast("/")).absolutePath
        val classLoader = URLClassLoader.newInstance(
            arrayOf(
                URL("jar:file:$pluginFilePath!/")
            )
        )
        val pluginClass = classLoader.loadClass(pluginClassName)
        if (Plugin::class.java.isAssignableFrom(pluginClass)) {
            return pluginClass as Class<Plugin>
        }
        throw IllegalStateException("Class $pluginClass isn't assignable from ${Plugin::class.java.name}")
    }

    /**
     * Indexes the plugin's dir from [PluginsConfig.dir] to list of files and filters it by .jar extension
     * @return The list with jar files
     */
    private fun jarFilesFromPluginsDir(): List<JarFile> = File(pluginsConfig.dir).listFiles()?.filter {
        it.extension == "jar"
    }?.map {
        JarFile(it)
    } ?: emptyList()
}
