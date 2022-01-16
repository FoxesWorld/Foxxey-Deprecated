package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.foxesworld.foxxey.server.clientrestapi.restapi.RestApiConfig
import java.io.File

object Routes {

    private lateinit var windowsJreFile: File
    private lateinit var linuxJreFile: File
    private lateinit var darwinJreFile: File
    private lateinit var defaultJreFile: File

    fun Route.jre(jrePath: RestApiConfig.JrePath) {
        jrePath.initJreVariables()
        get("jre") {
            when (call.parameters["os"]) {
                "windows" -> {
                    call.respondFile(windowsJreFile)
                }
                "linux" -> {
                    call.respondFile(linuxJreFile)
                }
                "darwin" -> {
                    call.respondFile(darwinJreFile)
                }
                else -> {
                    call.respondFile(defaultJreFile)
                }
            }
        }
    }

    private fun RestApiConfig.JrePath.initJreVariables() {
        windowsJreFile = File(windows)
        linuxJreFile = File(linux)
        darwinJreFile = File(darwin)
        defaultJreFile = File(default)
    }
}
