package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: ApiService
) {}