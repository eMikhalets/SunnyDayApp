package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory

class AppRepository {

    private val api = ApiFactory.getService()

    suspend fun requestCurrent(cityName: String) = api.currentWeather(cityName)

    suspend fun requestForecastDaily(cityName: String) = api.forecastDaily(cityName)

    suspend fun requestForecastHourly(cityName: String) = api.forecastHourly(cityName)
}