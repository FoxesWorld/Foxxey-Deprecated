package ru.foxesworld.foxxey.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
abstract class Response(
    @SerialName(API_STATUS_KEY)
    val status: Int,
    @SerialName(API_TIMESTAMP_KEY)
    val timestamp: Long = System.currentTimeMillis()
) {

    companion object {

        const val API_STATUS_KEY = "api:status"
        const val API_TIMESTAMP_KEY = "api:timestamp"
    }
}
