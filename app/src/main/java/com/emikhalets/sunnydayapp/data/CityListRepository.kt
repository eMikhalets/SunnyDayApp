package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.data.database.AppDatabase
import com.emikhalets.sunnydayapp.data.database.City

class CityListRepository {

    private val db = AppDatabase.get().citiesDao()

    suspend fun getAddedCities() = db.getAddedCities()
    suspend fun getCity(id: Int) = db.getCityById(id)
    suspend fun updateCity(city: City) = db.update(city)
}