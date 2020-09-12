package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory
import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.utils.Keys

class PreferenceRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestApiUsage() = callHandler.safeApiCall(apiCall = {
        val result = api.apiUsage(Keys.getApiKey())
        return@safeApiCall AppResponse.Success(result)
    })
}