package ru.foxesworld.foxxey.server.clientrestapi.ktor

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

object Routes {

    fun Route.jre(jrePaths: Map<String, String>) {
        val osToJreFileMap: Map<String, File> = jrePaths.mapValues {
            File(it.value)
        }
        get("jre") {
            val os = call.parameters["os"]
            if (os != null && osToJreFileMap.containsKey(os)) {
                call.respondFile(osToJreFileMap[os]!!)
                return@get
            }
            call.respondFile(osToJreFileMap.firstNotNullOf { it.value })
        }
    }
}
