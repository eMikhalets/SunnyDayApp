package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory
import com.emikhalets.sunnydayapp.network.AppResponse

class CurrentRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestCurrent(name: String, country: String, lang: String, units: String) =
        callHandler.safeApiCall(apiCall = {
            val result = api.currentWeather(name, country, lang, units)
            return@safeApiCall AppResponse.Success(result)
        })
}