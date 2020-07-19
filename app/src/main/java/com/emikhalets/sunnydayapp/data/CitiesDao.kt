package com.emikhalets.sunnydayapp.data

import androidx.room.*

@Dao
interface CitiesDao {

    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<City>

    @Query("SELECT * FROM cities WHERE is_city_added = 1")
    suspend fun getAddedCities(): List<City>

    @Query("SELECT * FROM cities WHERE city_name LIKE '%' || :name || '%'")
    suspend fun getCitiesByName(name: String): List<City>

    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun getCityById(id: Int): City

    @Query("SELECT * FROM cities WHERE city_name = :name AND country_full = :country")
    suspend fun getCityByName(name: String, country: String): City

    @Query("DELETE FROM cities")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<City>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: City)

    @Update
    suspend fun update(city: City)

    @Delete
    suspend fun delete(city: City)
}