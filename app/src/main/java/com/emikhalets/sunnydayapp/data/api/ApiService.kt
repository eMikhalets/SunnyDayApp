package com.emikhalets.sunnydayapp.data.api

import com.emikhalets.sunnydayapp.data.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.data.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.data.pojo.ResponseUsage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @GET("current")
    suspend fun currentWeather(
        @Query("key") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
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

    @GET("forecast/daily")
    suspend fun forecastDaily(
        @Query("key") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ResponseDaily

    @GET("subscription/usage")
    suspend fun apiUsage(
        @Query("key") apiKey: String
    ): ResponseUsage

    companion object {
        private const val BASE_URL = "https://api.weatherbit.io/v2.0/"

        fun create(): ApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}