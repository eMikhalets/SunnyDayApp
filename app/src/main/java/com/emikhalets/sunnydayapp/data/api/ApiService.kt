package com.emikhalets.sunnydayapp.data.api

import com.emikhalets.sunnydayapp.data.model.WeatherResponse
import com.emikhalets.sunnydayapp.utils.Conf
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    suspend fun weather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = Conf.units,
        @Query("lang") lang: String = Conf.lang
    ): WeatherResponse
}