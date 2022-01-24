package ru.foxesworld.foxxey.server.os

import java.net.InetAddress

object OSHelper {

    val machineName: String
        get() = runCatching { InetAddress.getLocalHost().hostName }.getOrElse { "unknown" }
    val osName: String
        get() = runCatching { System.getProperty("os.name") }.getOrElse { "unknown" }
    val osArchitecture: String
        get() = runCatching { System.getProperty("os.arch") }.getOrElse { "unknown" }
    val availableMemoryInMB: Long
        get() = kotlin.runCatching {
            Runtime.getRuntime().totalMemory() / 1000000
        }.getOrElse { 0 }
    val freeMemoryInMB: Long
        get() = kotlin.runCatching {
            Runtime.getRuntime().freeMemory() / 1000000
        }.getOrElse { 0 }
    val usedMemoryInMB: Long
        get() = availableMemoryInMB - freeMemoryInMB
}
