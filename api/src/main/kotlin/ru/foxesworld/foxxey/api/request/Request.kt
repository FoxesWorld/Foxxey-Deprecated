package ru.foxesworld.foxxey.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
abstract class Request(
    @SerialName("api:timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
