package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.data.network.ApiFactory
import com.emikhalets.sunnydayapp.data.network.AppResponse

class DailyRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestForecastDaily(name: String, country: String) = callHandler.safeApiCall(
        apiCall = {
            val result = api.forecastDaily(name, country)
            return@safeApiCall AppResponse.Success(result)
        }
    )
}