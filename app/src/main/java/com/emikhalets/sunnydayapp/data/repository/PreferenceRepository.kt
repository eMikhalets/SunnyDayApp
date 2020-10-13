package com.emikhalets.sunnydayapp.data.repository

import com.emikhalets.sunnydayapp.data.api.ApiService
import com.emikhalets.sunnydayapp.data.api.AppResponse
import com.emikhalets.sunnydayapp.data.api.NetworkCallHandler
import com.emikhalets.sunnydayapp.utils.Keys
import javax.inject.Inject

class PreferenceRepository @Inject constructor(
    private val api: ApiService,
    private val call: NetworkCallHandler
) {

    suspend fun requestApiUsage() = call.safeApiCall(apiCall = {
        val result = api.apiUsage(Keys.getApiKey())
        return@safeApiCall AppResponse.Success(result)
    })
}