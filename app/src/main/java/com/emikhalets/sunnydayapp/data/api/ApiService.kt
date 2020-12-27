package com.emikhalets.sunnydayapp.data.api

import com.emikhalets.sunnydayapp.data.model.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    suspend fun weather(
        @Query("lat") lat: Int,
        @Query("lon") lon: Int,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Response

    companion object {

        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun create(): ApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}