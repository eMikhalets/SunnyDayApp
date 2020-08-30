package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.data.database.AppDatabase
import com.emikhalets.sunnydayapp.data.database.City

class PagerRepository {

    private val db = AppDatabase.get().citiesDao()

    suspend fun getCitiesByName(name: String) = db.getCitiesByName(name)
    
    suspend fun getCityByName(name: String, country: String) = db.getCityByName(name, country)
    
    suspend fun insertAllCities(cities: List<City>) = db.insertAll(cities)
    
    suspend fun updateCity(city: City) = db.update(city)
}