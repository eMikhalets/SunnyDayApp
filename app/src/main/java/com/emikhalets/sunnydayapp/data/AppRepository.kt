package com.emikhalets.sunnydayapp.data

import android.content.Context
import com.emikhalets.sunnydayapp.data.network.ApiFactory

class AppRepository(context: Context) {

    private val api = ApiFactory.getService()
    private val citiesDao = AppDatabase.get(context)!!.citiesDao()

    suspend fun requestCurrent(name: String, country: String) = api.currentWeather(name, country)
    suspend fun requestForecastDaily(name: String, country: String) =
        api.forecastDaily(name, country)
    suspend fun requestForecastHourly(name: String, country: String) =
        api.forecastHourly(name, country)

    suspend fun getAllCities() = citiesDao.getAllCities()
    suspend fun getAddedCities() = citiesDao.getAddedCities()
    suspend fun getCitiesByName(name: String) = citiesDao.getCitiesByName(name)

    suspend fun getCity(id: Int) = citiesDao.getCityById(id)
    suspend fun getCityByName(name: String, country: String) =
        citiesDao.getCityByName(name, country)

    suspend fun deleteAll() = citiesDao.deleteAll()
    suspend fun insertAllCities(cities: List<City>) = citiesDao.insertAll(cities)
    suspend fun insertCity(city: City) = citiesDao.insert(city)
    suspend fun updateCity(city: City) = citiesDao.update(city)
    suspend fun deleteCity(city: City) = citiesDao.delete(city)
}