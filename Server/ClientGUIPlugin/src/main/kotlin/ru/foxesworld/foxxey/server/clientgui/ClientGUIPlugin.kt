package ru.foxesworld.foxxey.server.clientgui

import com.sksamuel.hoplite.ConfigAlias
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import org.koin.core.component.inject
import org.koin.dsl.module
import ru.foxesworld.foxxey.server.clientgui.di.Modules.configuration
import ru.foxesworld.foxxey.server.plugins.Plugin
import java.io.File

class ClientGUIPlugin(info: Info) : Plugin(info) {

    private val module = module {
        configuration()
    }
    private val ktorServer: ApplicationEngine by inject()
    private val config: Config by inject()

    override suspend fun start() {
        ktorServer.application.configureRoutes()
        super.start()
    }

    private fun Application.configureRoutes() {
        val webKitFolder = File(config.webKitFolder)
        val redirectBaseUrl =
            if (config.redirectBaseUrl.endsWith("/")) config.redirectBaseUrl else "${config.redirectBaseUrl}/"
        val redirectWebPath =
            if (config.redirectWebPath.endsWith("/")) config.redirectWebPath else "${config.redirectWebPath}/"
        routing {
            route(config.redirectWebPath) {
                get("*") {
                    call.respondRedirect(
                        "$redirectBaseUrl${call.request.path().substringAfter(redirectWebPath)}",
                        permanent = true
                    )
                }
            }
            route(config.webPath) {
                install(ContentNegotiation) {
                    json()
                }
                get("*") {
                    val contextPath = call.request.path().substringAfter(config.webPath)
                    val contextFile = File(webKitFolder, contextPath)
                    if (contextFile.isDirectory) {
                        val directoryFilesArray = buildJsonArray {
                            contextFile.listFiles()?.forEach {
                                add(it.name)
                            }
                        }
                        call.respond(directoryFilesArray)
                    }
                }
                static {
                    files(webKitFolder)
                    default(File(webKitFolder, config.webKitRootFile))
                }
            }
        }
    }

    override suspend fun load() {
        localConfigFile("clientgui.json").createDefaultFromResourcesIfNotExists(ClientGUIPlugin::class.java)
        getKoin().loadModules(listOf(module))
        super.load()
    }

    override suspend fun unload() {
        getKoin().unloadModules(listOf(module))
        super.unload()
    }

    data class Config(
        @ConfigAlias("webPath")
        val webPath: String,
        @ConfigAlias("webKitFolder")
        val webKitFolder: String,
        @ConfigAlias("webKitRootFile")
        val webKitRootFile: String,
        @ConfigAlias("redirectWebPath")
        val redirectWebPath: String,
        @ConfigAlias("redirectBaseUrl")
        val redirectBaseUrl: String
    )
}
