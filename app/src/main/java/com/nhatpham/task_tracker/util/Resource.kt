package com.nhatpham.task_tracker.util

sealed class Resource<out T> {

    /**
     * Represents a successful operation with data
     * @param data The successful result data
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Represents an error state
     * @param exception The exception that occurred
     * @param message Optional custom error message
     * @param data Optional partial data that might be available despite the error
     */
    data class Error<out T>(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error occurred",
        val data: T? = null
    ) : Resource<T>()

    /**
     * Represents a loading state
     * @param data Optional cached/previous data to show while loading
     * @param progress Optional progress indicator (0.0 to 1.0)
     */
    data class Loading<out T>(
        val data: T? = null, val progress: Float? = null
    ) : Resource<T>()

    /**
     * Represents an idle/initial state before any operation
     */
    object Idle : Resource<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    val isIdle: Boolean get() = this is Idle

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> failure(exception: Throwable): Resource<T> = Error(exception)
        fun <T> loading(data: T? = null, progress: Float? = null): Resource<T> =
            Loading(data, progress)
    }
}