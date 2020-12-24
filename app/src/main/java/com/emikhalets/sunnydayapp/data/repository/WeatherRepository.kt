package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiService
import com.emikhalets.sunnydayapp.utils.Keys
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun requestCurrent(name: String, country: String, lang: String, units: String) =
        api.currentWeather(Keys.getApiKey(), name, country, lang, units)

    suspend fun requestCurrent(lat: Double, lon: Double, lang: String, units: String) =
        api.currentWeather(Keys.getApiKey(), lat, lon, lang, units)

    suspend fun requestForecastDaily(name: String, country: String, lang: String, units: String) =
        api.forecastDaily(Keys.getApiKey(), name, country, lang, units)

    suspend fun requestForecastDaily(lat: Double, lon: Double, lang: String, units: String) =
        api.forecastDaily(Keys.getApiKey(), lat, lon, lang, units)
}