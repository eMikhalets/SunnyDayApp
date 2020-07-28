package com.emikhalets.sunnydayapp.data.database

import androidx.room.*
import com.emikhalets.sunnydayapp.data.database.City

@Dao
interface CitiesDao {

    /**
     * Get cities from database that appear in the cities list, that user looking for
     */
    @Query("SELECT * FROM cities WHERE is_city_added = 1")
    suspend fun getAddedCities(): List<City>

    /**
     * Get cities that contains string from search
     * @param name part of name or full name of the city
     */
    @Query("SELECT * FROM cities WHERE city_name LIKE '%' || :name || '%'")
    suspend fun getCitiesByName(name: String): List<City>

    /**
     * Get city by name and country. Used for update isAdded state of the city
     * @param name city name
     * @param country city country
     */
    @Query("SELECT * FROM cities WHERE city_name = :name AND country_full = :country")
    suspend fun getCityByName(name: String, country: String): City

    /**
     * Get city by database id
     * @param id database id of the city
     */
    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun getCityById(id: Int): City

    /**
     * Insert all cities in database. Used for create database from json file with cities.
     * @param cities created cities list from json asset
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<City>)

    /**
     * Update city in database. Used for update isAdded column
     * @param city city for update
     */
    @Update
    suspend fun update(city: City)
}