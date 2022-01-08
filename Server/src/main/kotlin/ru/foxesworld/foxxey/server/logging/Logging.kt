package ru.foxesworld.foxxey.server.logging

import kotlin.system.measureTimeMillis

/**
 * Runs the [action] with catching and measuring time in millis and on success invokes [onSuccess] with the time
 * @param action Some action that returns [T]
 * @param onSuccess Some action on success result with the time as parameter
 * @return The action result
 */
fun <T> runCatchingWithMeasuring(action: () -> T, onSuccess: (millis: Long) -> Unit): Result<T> {
    return runCatchingWithMeasuring(action) { millis, _ ->
        onSuccess.invoke(millis)
    }
}

/**
 * Runs the [action] with catching and measuring time in millis and on success invokes [onSuccess] with the time and result
 * @param action Some action that returns [T]
 * @param onSuccess Some action on success result with the time and result as parameters
 * @return The action result
 */
fun <T> runCatchingWithMeasuring(action: () -> T, onSuccess: (millis: Long, result: T) -> Unit): Result<T> {
    val result: Result<T>
    val measuredTimeInMillis = measureTimeMillis {
        result = runCatching(action)
    }
    result.onSuccess {
        onSuccess.invoke(measuredTimeInMillis, it)
    }
    return result
}
