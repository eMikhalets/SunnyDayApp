package com.emikhalets.sunnydayapp.data.network

import com.emikhalets.sunnydayapp.data.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseHourly
import com.emikhalets.sunnydayapp.utils.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current?key=$API_KEY")
    suspend fun currentWeather(
        @Query("city") cityName: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseCurrent

    @GET("forecast/daily?key=$API_KEY")
    suspend fun forecastDaily(
        @Query("city") cityName: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseDaily

    @GET("forecast/hourly?key=$API_KEY")
    suspend fun forecastHourly(
        @Query("city") cityName: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseHourly
}