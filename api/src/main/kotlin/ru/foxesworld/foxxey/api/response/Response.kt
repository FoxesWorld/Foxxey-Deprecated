package ru.foxesworld.foxxey.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
abstract class Response(
    @SerialName("api:status")
    val status: Int,
    @SerialName("api:timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
