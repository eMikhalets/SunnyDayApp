package com.emikhalets.sunnydayapp.network

import com.emikhalets.sunnydayapp.network.pojo.AppResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current")
    fun getCurrentWeather(
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("city") cityName: String
    ): Deferred<Response<AppResponse>>
}