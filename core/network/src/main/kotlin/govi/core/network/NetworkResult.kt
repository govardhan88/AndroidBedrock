package govi.core.network

sealed interface NetworkResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : NetworkResult<T>

    data class Error(
        val code: Int,
        val message: String?,
        val errorBody: String? = null,
    ) : NetworkResult<Nothing>

    data class Exception(
        val throwable: Throwable,
    ) : NetworkResult<Nothing>
}

inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> =
    when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> this
        is NetworkResult.Exception -> this
    }

inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (code: Int, message: String?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(code, message)
    return this
}

inline fun <T> NetworkResult<T>.onException(action: (Throwable) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Exception) action(throwable)
    return this
}

fun <T> NetworkResult<T>.getOrNull(): T? =
    when (this) {
        is NetworkResult.Success -> data
        else -> null
    }
