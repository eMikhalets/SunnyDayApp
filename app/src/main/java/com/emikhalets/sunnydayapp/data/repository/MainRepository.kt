package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiResult
import com.emikhalets.sunnydayapp.data.api.ApiService
import com.emikhalets.sunnydayapp.data.database.CitiesDao
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.database.DbResult
import com.emikhalets.sunnydayapp.data.model.WeatherResponse
import com.emikhalets.sunnydayapp.utils.safeDatabaseCall
import com.emikhalets.sunnydayapp.utils.safeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val db: CitiesDao,
    private val api: ApiService
) {

    suspend fun weatherRequest(lat: Double, lon: Double): ApiResult<WeatherResponse> {
        return withContext(Dispatchers.IO) { safeNetworkCall { api.weather(lat, lon) } }
    }

    suspend fun getCitiesByName(name: String): DbResult<List<City>> {
        return withContext(Dispatchers.IO) { safeDatabaseCall { db.getCitiesByName(name) } }
    }

    suspend fun insertAllCities(cities: List<City>): DbResult<Unit> {
        return withContext(Dispatchers.IO) { safeDatabaseCall { db.insertAll(cities) } }
    }
}