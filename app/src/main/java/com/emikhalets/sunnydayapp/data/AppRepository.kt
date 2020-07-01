package com.emikhalets.sunnydayapp.data

import android.content.Context
import com.emikhalets.sunnydayapp.network.ApiFactory

class AppRepository(context: Context) {

    private val api = ApiFactory.getService()
    private val citiesDao = AppDatabase.get(context)!!.citiesDao()

    suspend fun requestCurrent(cityName: String) = api.currentWeather(cityName)
    suspend fun requestForecastDaily(cityName: String) = api.forecastDaily(cityName)
    suspend fun requestForecastHourly(cityName: String) = api.forecastHourly(cityName)

    suspend fun getAllCities() = citiesDao.getAllCities()
    suspend fun getCity(id: Int) = citiesDao.getCityById(id)
    suspend fun deleteAll() = citiesDao.deleteAll()
    suspend fun insertCity(city: City) = citiesDao.insert(city)
    suspend fun updateCity(city: City) = citiesDao.update(city)
    suspend fun deleteCity(city: City) = citiesDao.delete(city)
}