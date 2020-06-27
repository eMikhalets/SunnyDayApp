package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.network.ApiFactory
import com.emikhalets.sunnydayapp.utils.API_KEY
import com.emikhalets.sunnydayapp.utils.QUERY_LANG
import com.emikhalets.sunnydayapp.utils.QUERY_UNITS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppRepository {

    private val service = ApiFactory.service

    fun getCurrentWeather(cityName: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.getCurrentWeather(API_KEY, QUERY_UNITS, QUERY_LANG, cityName)
            val response = request.await()
            val data = response.body()
        }
    }
}