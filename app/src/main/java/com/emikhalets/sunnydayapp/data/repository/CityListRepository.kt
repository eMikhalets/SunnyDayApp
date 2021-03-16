package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.database.CitiesDao
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.database.DbResult
import com.emikhalets.sunnydayapp.utils.safeDatabaseCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CityListRepository @Inject constructor(
    private val db: CitiesDao
) {

    suspend fun getSavedCities(): DbResult<List<City>> {
        return withContext(Dispatchers.IO) { safeDatabaseCall { db.getSearchedCities() } }
    }

    suspend fun updateCity(city: City): DbResult<Unit> {
        return withContext(Dispatchers.IO) { safeDatabaseCall { db.update(city) } }
    }
}