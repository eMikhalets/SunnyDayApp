package com.emikhalets.sunnydayapp.data

import androidx.room.*

@Dao
interface CitiesDao {

    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<City>

    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun getCityById(id: Int): City

    @Query("DELETE FROM cities")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(city: City)

    @Update
    suspend fun update(city: City)

    @Delete
    suspend fun delete(city: City)
}