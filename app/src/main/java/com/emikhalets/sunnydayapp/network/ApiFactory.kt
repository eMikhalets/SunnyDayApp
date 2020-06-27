package com.emikhalets.sunnydayapp.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private fun retrofit(): Retrofit = Retrofit.Builder()
        .client(OkHttpClient().newBuilder().build())
        .baseUrl("https://api.weatherbit.io/v2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val service: ApiService = retrofit().create(ApiService::class.java)
}