package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiService
import com.emikhalets.sunnydayapp.data.api.AppResponse
import com.emikhalets.sunnydayapp.data.api.NetworkCallHandler
import com.emikhalets.sunnydayapp.utils.Keys
import timber.log.Timber
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: ApiService,
    private val call: NetworkCallHandler
) {

    suspend fun requestCurrent(name: String, country: String, lang: String, units: String) =
        api.currentWeather(Keys.getApiKey(), name, country, lang, units)

    suspend fun requestCurrent(lat: Double, lon: Double, lang: String, units: String) =
        api.currentWeather(Keys.getApiKey(), lat, lon, lang, units)

    suspend fun requestForecastDaily(name: String, country: String, lang: String, units: String) =
        api.forecastDaily(Keys.getApiKey(), name, country, lang, units)

    suspend fun requestForecastDaily(lat: Double, lon: Double, lang: String, units: String) =
        api.forecastDaily(Keys.getApiKey(), lat, lon, lang, units)

//    suspend fun requestCurrent(name: String, country: String, lang: String, units: String) =
//        call.safeApiCall(apiCall = {
//            Timber.d("Sending a request for the current weather by name to the server")
//            val result = api.currentWeather(Keys.getApiKey(), name, country, lang, units)
//            return@safeApiCall AppResponse.Success(result)
//        })
//
//    suspend fun requestCurrent(lat: Double, lon: Double, lang: String, units: String) =
//        call.safeApiCall(apiCall = {
//            Timber.d("Sending a request for the current weather by location to the server")
//            val result = api.currentWeather(Keys.getApiKey(), lat, lon, lang, units)
//            return@safeApiCall AppResponse.Success(result)
//        })
//
//    suspend fun requestForecastDaily(name: String, country: String, lang: String, units: String) =
//        call.safeApiCall(apiCall = {
//            Timber.d("Sending a request for the forecast by name to the server")
//            val result = api.forecastDaily(Keys.getApiKey(), name, country, lang, units)
//            return@safeApiCall AppResponse.Success(result)
//        })
//
//    suspend fun requestForecastDaily(lat: Double, lon: Double, lang: String, units: String) =
//        call.safeApiCall(apiCall = {
//            Timber.d("Sending a request for the forecast by location to the server")
//            val result = api.forecastDaily(Keys.getApiKey(), lat, lon, lang, units)
//            return@safeApiCall AppResponse.Success(result)
//        })
}