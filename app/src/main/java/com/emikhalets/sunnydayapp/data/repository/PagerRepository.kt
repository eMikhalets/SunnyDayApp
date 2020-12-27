package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.database.CitiesDao
import com.emikhalets.sunnydayapp.data.database.City
import javax.inject.Inject

class PagerRepository @Inject constructor(
    private val db: CitiesDao
    ) {

    suspend fun deleteAllCities() = db.deleteAllCities()
    suspend fun getCitiesByName(name: String) = db.getCitiesByName(name)
    suspend fun getCityByName(name: String, country: String) = db.getCityByName(name, country)
    suspend fun insertAllCities(cities: List<City>) = db.insertAll(cities)
    suspend fun updateCity(city: City) = db.update(city)
}