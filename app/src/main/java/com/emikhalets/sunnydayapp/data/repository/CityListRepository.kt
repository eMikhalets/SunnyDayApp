package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.database.CitiesDao
import com.emikhalets.sunnydayapp.data.database.City
import javax.inject.Inject

class CityListRepository @Inject constructor(private val db: CitiesDao) {

    suspend fun getAddedCities() = db.getAddedCities()
    suspend fun getCity(id: Int) = db.getCityById(id)
    suspend fun updateCity(city: City) = db.update(city)
}