package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.data.network.AppResponse
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseError
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.lang.Exception

class NetworkCallHandler {

    suspend fun <T> safeApiCall(apiCall: suspend () -> AppResponse<T>): AppResponse<T> =
        try {
            apiCall.invoke()
        } catch (t: Throwable) {
            Timber.d(t)
            when (t) {
                is IOException -> AppResponse.NetworkError
                is HttpException -> {
                    val code = t.code()
                    val errorResponse = convertError(t)
                    AppResponse.Error(code, errorResponse)
                }
                else -> AppResponse.Error(null, null)
            }
        }

    private fun convertError(t: HttpException): ResponseError? {
        return try {
            t.response()?.errorBody()?.charStream()?.let {
                val builder = GsonBuilder().create()
                builder.fromJson(it, ResponseError::class.java)
            }
        } catch (ex: Exception) {
            null
        }
    }
}