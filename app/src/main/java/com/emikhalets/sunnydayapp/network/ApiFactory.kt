package com.emikhalets.sunnydayapp.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url().newBuilder()
            .addQueryParameter("key", "e1a0feec25754f7aa615945766b156b6")
            .addQueryParameter("lang", "ru")
            .build()

        val newRequest = chain.request().newBuilder().url(newUrl).build()

        chain.proceed(newRequest)
    }

    private val weatherClient = OkHttpClient().newBuilder().addInterceptor(authInterceptor).build()

    fun retrofit() = Retrofit.Builder()
        .client(weatherClient)
        .baseUrl("https://api.weatherbit.io/v2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val weatherApi = retrofit().create(ApiService::class.java)
}