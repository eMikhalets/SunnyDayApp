package com.emikhalets.sunnydayapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private var retrofit: Retrofit? = null

    private fun getRetrofit() = retrofit ?: Retrofit.Builder()
        .baseUrl("https://api.weatherbit.io/v2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getService(): ApiService = getRetrofit().create(ApiService::class.java)
}