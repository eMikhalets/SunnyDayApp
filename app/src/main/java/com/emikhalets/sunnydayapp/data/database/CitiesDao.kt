package com.emikhalets.sunnydayapp.data.database

import androidx.room.*

@Dao
interface CitiesDao {

    /**
     * Get cities from database that appear in the cities list, that user looking for
     */
    @Query("SELECT * FROM cities WHERE isSearched = 1")
    suspend fun getSearchedCities(): List<City>

    /**
     * Get cities that contains string from search
     * @param name part of name or full name of the city
     */
    @Query("SELECT * FROM cities WHERE name LIKE '%' || :name || '%'")
    suspend fun getCitiesByName(name: String): List<City>

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
    @Insert
    suspend fun insertAll(cities: List<City>)

    @Update
    suspend fun update(city: City)
}