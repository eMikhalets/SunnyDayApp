package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiResult
import com.emikhalets.sunnydayapp.data.database.DbResult

object DataCallWrapper {
    suspend fun <T : Any> safeNetworkCall(call: suspend () -> T): ApiResult<T> =
        try {
            val result = call.invoke()
            ApiResult.Success(result)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ApiResult.Error(ex.message ?: "", ex)
        }

    suspend fun <T : Any> safeDatabaseCall(call: suspend () -> T): DbResult<T> =
        try {
            val result = call.invoke()
            DbResult.Success(result)
        } catch (ex: Exception) {
            ex.printStackTrace()
            DbResult.Error(ex.message ?: "", ex)
        }
}