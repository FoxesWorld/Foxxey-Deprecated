package ru.foxesworld.foxxey.api.response

import kotlinx.serialization.SerialName

data class LatestNativeClientResponse(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("versionCode")
    val versionCode: Int,
    @SerialName("downloadLink")
    val downloadLink: String,
    @SerialName("hasRequiredUpdates")
    val hasRequiredUpdates: Boolean
) : OkResponse()
