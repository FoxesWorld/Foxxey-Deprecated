package ru.foxesworld.foxxey.api.response

@Suppress("unused")
abstract class OkResponse : Response(status = STATUS) {
    companion object {
        const val STATUS = 0
    }
}
