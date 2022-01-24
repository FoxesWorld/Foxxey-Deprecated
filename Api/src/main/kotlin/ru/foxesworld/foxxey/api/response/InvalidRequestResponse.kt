package ru.foxesworld.foxxey.api.response

@Suppress("unused")
abstract class InvalidRequestResponse : Response(status = STATUS) {

    companion object {
        const val STATUS = -1
    }
}
