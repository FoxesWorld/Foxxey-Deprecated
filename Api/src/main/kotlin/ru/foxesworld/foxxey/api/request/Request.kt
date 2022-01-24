package ru.foxesworld.foxxey.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
abstract class Request(
    @SerialName(API_TIMESTAMP_KEY)
    val timestamp: Long = System.currentTimeMillis()
) {

    companion object {
        const val API_TIMESTAMP_KEY = "api:timestamp"
    }
}
