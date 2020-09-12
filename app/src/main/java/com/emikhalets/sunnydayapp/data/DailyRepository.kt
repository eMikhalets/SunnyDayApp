package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory
import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.utils.Keys

class DailyRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestForecastDaily(name: String, country: String, lang: String, units: String) =
        callHandler.safeApiCall(apiCall = {
            val result = api.forecastDaily(Keys.getApiKey(), name, country, lang, units)
            return@safeApiCall AppResponse.Success(result)
        })
}