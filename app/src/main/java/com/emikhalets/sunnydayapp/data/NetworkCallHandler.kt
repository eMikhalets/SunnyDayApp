package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.network.pojo.ResponseError
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class NetworkCallHandler {

    suspend fun <T> safeApiCall(apiCall: suspend () -> AppResponse<T>): AppResponse<T> {
        try {
            val result = apiCall.invoke()
            Timber.d("Successful request to the server")
            return result
        } catch (t: Throwable) {
            Timber.d(t)
            when (t) {
                is IOException -> {
                    val error = AppResponse.NetworkError
                    Timber.d("Caught an IOException: $error")
                    return error
                }
                is HttpException -> {
                    val code = t.code()
                    val errorResponse = convertError(t)
                    val result = AppResponse.Error(code, errorResponse)
                    Timber.d("Caught an HttpException: $result")
                    return result
                }
                else -> {
                    val result = AppResponse.Error(null, null)
                    Timber.d("Caught an unexpected error")
                    return result
                }
            }
        }
    }

    private fun convertError(t: HttpException): ResponseError? {
        return try {
            t.response()?.errorBody()?.charStream()?.let {
                val builder = GsonBuilder().create()
                val response = builder.fromJson(it, ResponseError::class.java)
                Timber.d("Response parsed into ResponseError object: (${response.error})")
                return response
            }
        } catch (ex: Exception) {
            Timber.d("Caught an Exception")
            return null
        }
    }
}