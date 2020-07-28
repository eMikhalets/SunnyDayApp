package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.data.network.ApiFactory
import com.emikhalets.sunnydayapp.data.network.AppResponse

class CurrentRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestCurrent(name: String, country: String) = callHandler.safeApiCall(
        apiCall = {
            val result = api.currentWeather(name, country)
            return@safeApiCall AppResponse.Success(result)
        }
    )
}