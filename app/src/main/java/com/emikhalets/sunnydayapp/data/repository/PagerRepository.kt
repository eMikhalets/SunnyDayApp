package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiService
import com.emikhalets.sunnydayapp.data.database.CitiesDao
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.utils.Keys
import javax.inject.Inject

class PagerRepository @Inject constructor(
    private val db: CitiesDao,
    private val api: ApiService
) {

    suspend fun weatherRequest(lat: Double, lon: Double, units: String, lang: String) =
        api.weather(lat, lon, Keys.getApiKey(), units, lang)

    suspend fun deleteAllCities() = db.deleteAllCities()
    suspend fun getCitiesByName(name: String) = db.getCitiesByName(name)
    suspend fun insertAllCities(cities: List<City>) = db.insertAll(cities)
}