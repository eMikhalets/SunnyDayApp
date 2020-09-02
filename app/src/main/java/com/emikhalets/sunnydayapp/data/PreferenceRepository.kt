package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory
import com.emikhalets.sunnydayapp.network.AppResponse

class PreferenceRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestApiUsage() = callHandler.safeApiCall(apiCall = {
        val result = api.apiUsage()
        return@safeApiCall AppResponse.Success(result)
    })
}