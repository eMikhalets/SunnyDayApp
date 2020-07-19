package com.emikhalets.sunnydayapp.data.network

import com.emikhalets.sunnydayapp.data.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseHourly
import com.emikhalets.sunnydayapp.utils.API_KEY
import com.emikhalets.sunnydayapp.utils.QUERY_LANG
import com.emikhalets.sunnydayapp.utils.QUERY_UNITS
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current?key=$API_KEY&lang=$QUERY_LANG&units=$QUERY_UNITS")
    suspend fun currentWeather(
        @Query("city") cityName: String,
        @Query("country") country: String
    ): ResponseCurrent

    @GET("forecast/daily?key=$API_KEY&lang=$QUERY_LANG&units=$QUERY_UNITS")
    suspend fun forecastDaily(
        @Query("city") cityName: String,
        @Query("country") country: String
    ): ResponseDaily

    @GET("forecast/hourly?key=$API_KEY&lang=$QUERY_LANG&units=$QUERY_UNITS")
    suspend fun forecastHourly(
        @Query("city") cityName: String,
        @Query("country") country: String
    ): ResponseHourly
}