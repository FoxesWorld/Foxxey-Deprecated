package ru.foxesworld.foxxey.server.logging

import kotlin.system.measureTimeMillis

suspend fun <T> wrappedRunNoResult(
    logging: (ActionLogging<T>.() -> Unit)? = null,
    action: suspend () -> T
) {
    val actionLogging = ActionLogging(
        action, {}, { _, _ -> }, {}
    )
    logging?.let {
        actionLogging.apply(it)
    }
    actionLogging.run()
}

suspend fun <T> wrappedRun(
    logging: (ActionLogging<T>.() -> Unit)? = null,
    action: suspend () -> T
): Result<T> {
    val actionLogging = ActionLogging(
        action, {}, { _, _ -> }, {}
    )
    logging?.let {
        actionLogging.apply(it)
    }
    return actionLogging.run()
}

@Suppress("MemberVisibilityCanBePrivate")
class ActionLogging<T>(
    private val action: suspend () -> T,
    var beforeRun: () -> Unit,
    var onSuccess: (millis: Long, result: T) -> Unit,
    var onFailure: (e: Throwable) -> Unit
) {

    fun beforeRun(action: () -> Unit) {
        beforeRun = action
    }

    fun onSuccess(action: (millis: Long, result: T) -> Unit) {
        onSuccess = action
    }

    fun onFailure(action: (e: Throwable) -> Unit) {
        onFailure = action
    }

    suspend fun run(): Result<T> {
        beforeRun.invoke()
        val result: Result<T>
        val measuredTimeInMillis = measureTimeMillis {
            result = runCatching {
                action.invoke()
            }
        }
        result.onSuccess {
            onSuccess.invoke(measuredTimeInMillis, it)
        }.onFailure {
            onFailure.invoke(it)
        }
        return result
    }
}
