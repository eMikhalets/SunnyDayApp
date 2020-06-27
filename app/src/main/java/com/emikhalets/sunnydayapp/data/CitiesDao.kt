package com.emikhalets.sunnydayapp.data

import androidx.room.*

@Dao
interface CitiesDao {

    @Query("SELECT * from cities")
    fun getAllCities(): List<City>

    @Query("SELECT * from cities WHERE id = :id")
    fun getCityById(id: Int): City

    @Insert
    suspend fun insert(city: City)

    @Update
    suspend fun update(city: City)

    @Delete
    suspend fun delete(city: City)
}