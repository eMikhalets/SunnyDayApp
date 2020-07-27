package com.emikhalets.sunnydayapp.data

import com.emikhalets.sunnydayapp.data.network.ApiFactory
import com.emikhalets.sunnydayapp.data.network.AppResponse
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseError
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.lang.Exception

class AppRepository {

    private val api = ApiFactory.getService()
    private val db = AppDatabase.get().citiesDao()

    private suspend fun <T> safeApiCall(apiCall: suspend () -> AppResponse<T>): AppResponse<T> =
        try {
            apiCall.invoke()
        } catch (t: Throwable) {
            Timber.d(t)
            when (t) {
                is IOException -> AppResponse.NetworkError
                is HttpException -> {
                    val code = t.code()
                    val errorResponse = convertError(t)
                    AppResponse.Error(code, errorResponse)
                }
                else -> AppResponse.Error(null, null)
            }
        }

    private fun convertError(t: HttpException): ResponseError? {
        return try {
            t.response()?.errorBody()?.charStream()?.let {
                val builder = GsonBuilder().create()
                builder.fromJson(it, ResponseError::class.java)
            }
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun requestCurrent(name: String, country: String) = safeApiCall(
        apiCall = {
            val result = api.currentWeather(name, country)
            return@safeApiCall AppResponse.Success(result)
        }
    )

    suspend fun requestForecastDaily(name: String, country: String) = safeApiCall(
        apiCall = {
            val result = api.forecastDaily(name, country)
            return@safeApiCall AppResponse.Success(result)
        }
    )

    suspend fun requestForecastHourly(name: String, country: String) = safeApiCall(
        apiCall = {
            val result = api.forecastHourly(name, country)
            return@safeApiCall AppResponse.Success(result)
        }
    )

    suspend fun getAddedCities() = db.getAddedCities()
    suspend fun getCitiesByName(name: String) = db.getCitiesByName(name)
    suspend fun getCityByName(name: String, country: String) = db.getCityByName(name, country)
    suspend fun getCity(id: Int) = db.getCityById(id)

    suspend fun insertAllCities(cities: List<City>) = db.insertAll(cities)
    suspend fun updateCity(city: City) = db.update(city)
}