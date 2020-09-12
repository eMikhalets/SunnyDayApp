package com.emikhalets.sunnydayapp.network

import com.emikhalets.sunnydayapp.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.network.pojo.ResponseHourly
import com.emikhalets.sunnydayapp.network.pojo.ResponseUsage
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current")
    suspend fun currentWeather(
        @Query("key") apiKey: String,
        @Query("city") cityName: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseCurrent

    @GET("forecast/daily")
    suspend fun forecastDaily(
        @Query("key") apiKey: String,
        @Query("city") cityName: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseDaily

    @GET("forecast/hourly")
    suspend fun forecastHourly(
        @Query("key") apiKey: String,
        @Query("city") cityName: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseHourly

    @GET("subscription/usage")
    suspend fun apiUsage(
        @Query("key") apiKey: String
    ): ResponseUsage
}