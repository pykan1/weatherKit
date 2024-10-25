package com.example.weather.data.api

import android.util.Log
import retrofit2.Response

suspend fun <T> launchOperation(
    call: suspend () -> Response<T>,
    onSuccess: (body: T) -> Unit
) {
    try {
        val response = call()
        if (response.isSuccessful) {
            response.body()?.let(onSuccess) ?: logError("Response body is null")
        } else {
            logError("Response error: ${response.code()}")
        }
    } catch (e: Exception) {
        logError("Exception occurred: ${e.message}", e)
    }
}

private fun logError(message: String, throwable: Throwable? = null) {
    Log.e("ViewModel", message, throwable)
}