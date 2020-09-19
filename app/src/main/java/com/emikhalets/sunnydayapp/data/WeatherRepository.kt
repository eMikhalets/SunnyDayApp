package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory
import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.utils.Keys
import timber.log.Timber

class WeatherRepository {

    private val api = ApiFactory.getService()
    private val callHandler = NetworkCallHandler()

    suspend fun requestCurrent(name: String, country: String, lang: String, units: String) =
        callHandler.safeApiCall(apiCall = {
            Timber.d("Sending a request for the current weather by name to the server")
            val result = api.currentWeather(Keys.getApiKey(), name, country, lang, units)
            return@safeApiCall AppResponse.Success(result)
        })

    suspend fun requestCurrent(lat: Double, lon: Double, lang: String, units: String) =
        callHandler.safeApiCall(apiCall = {
            Timber.d("Sending a request for the current weather by location to the server")
            val result = api.currentWeather(Keys.getApiKey(), lat, lon, lang, units)
            return@safeApiCall AppResponse.Success(result)
        })

    suspend fun requestForecastDaily(name: String, country: String, lang: String, units: String) =
        callHandler.safeApiCall(apiCall = {
            Timber.d("Sending a request for the forecast by name to the server")
            val result = api.forecastDaily(Keys.getApiKey(), name, country, lang, units)
            return@safeApiCall AppResponse.Success(result)
        })

    suspend fun requestForecastDaily(lat: Double, lon: Double, lang: String, units: String) =
        callHandler.safeApiCall(apiCall = {
            Timber.d("Sending a request for the forecast by location to the server")
            val result = api.forecastDaily(Keys.getApiKey(), lat, lon, lang, units)
            return@safeApiCall AppResponse.Success(result)
        })
}