package com.emikhalets.sunnydayapp.data

import androidx.room.*

@Dao
interface CitiesDao {

    @Query("SELECT * from cities")
    fun getAllCities(): List<City>

    @Query("SELECT * from cities WHERE id = :id")
    fun getCityById(id: Int): City

    @Insert
    suspend fun insert()

    @Update
    suspend fun update()

    @Delete
    suspend fun delete()
}