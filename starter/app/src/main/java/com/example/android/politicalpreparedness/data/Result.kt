package com.example.android.politicalpreparedness.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val message: String?, val statusCode: Int? = null) :
        Result<Nothing>()
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null