package ru.foxesworld.foxxey.server.clientrestapi

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

object Routes {

    private val jreMap: HashMap<String, File> = hashMapOf()

    fun Route.jre(jrePaths: Map<String, String>) {
        jrePaths.initJreVariables()
        get("jre") {
            val os = call.parameters["os"]
            if (os != null && jreMap.containsKey(os)) {
                call.respondFile(jreMap[os]!!)
                return@get
            }
            call.respondFile(jreMap.firstNotNullOf { it.value })
        }
    }

    private fun Map<String, String>.initJreVariables() {
        forEach { (path, filePath) ->
            jreMap[path] = File(filePath)
        }
    }
}
