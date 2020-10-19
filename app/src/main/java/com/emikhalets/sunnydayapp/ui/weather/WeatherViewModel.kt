package com.emikhalets.sunnydayapp.ui.weather

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.api.AppResponse
import com.emikhalets.sunnydayapp.data.model.ResponseCurrent
import com.emikhalets.sunnydayapp.data.model.ResponseDaily
import com.emikhalets.sunnydayapp.data.repository.WeatherRepository
import com.emikhalets.sunnydayapp.utils.status.WeatherResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WeatherViewModel @ViewModelInject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    val forecastDaily = MutableLiveData<WeatherResource<ResponseDaily>>()
    val currentWeather = MutableLiveData<WeatherResource<ResponseCurrent>>()

    fun requestCurrent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending current weather request by city name: ($query)")
            val array = query.split(", ")
            when (val data = repository.requestCurrent(array[0], array[1], "ru", "M")) {
                is AppResponse.Success ->
                    currentWeather.postValue(WeatherResource.weather(data.response))
                is AppResponse.Error ->
                    currentWeather.postValue(
                        WeatherResource.error(
                            "Code: ${data.code}, Data: ${data.error?.error}"
                        )
                    )
                is AppResponse.NetworkError ->
                    currentWeather.postValue(WeatherResource.error(data.toString()))
            }
        }
    }

    fun requestCurrent(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending current weather request by location: (lat=$lat, lon=$lon)")
            when (val data = repository.requestCurrent(lat, lon, "ru", "M")) {
                is AppResponse.Success ->
                    currentWeather.postValue(WeatherResource.weather(data.response))
                is AppResponse.Error ->
                    currentWeather.postValue(
                        WeatherResource.error(
                            "Code: ${data.code}, Data: ${data.error?.error}"
                        )
                    )
                is AppResponse.NetworkError ->
                    currentWeather.postValue(WeatherResource.error(data.toString()))
            }
        }
    }

    fun requestForecastDaily(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending daily forecast request by city name: ($query)")
            val array = query.split(", ")
            when (val data = repository.requestForecastDaily(array[0], array[1], "ru", "M")) {
                is AppResponse.Success ->
                    forecastDaily.postValue(WeatherResource.weather(data.response))
                is AppResponse.Error ->
                    forecastDaily.postValue(
                        WeatherResource.error(
                            "Code: ${data.code}, Data: ${data.error?.error}"
                        )
                    )
                is AppResponse.NetworkError ->
                    forecastDaily.postValue(WeatherResource.error(data.toString()))
            }
        }
    }

    fun requestForecastDaily(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending daily forecast request by location: (lat=$lat, lon=$lon)")
            when (val data = repository.requestForecastDaily(lat, lon, "ru", "M")) {
                is AppResponse.Success ->
                    forecastDaily.postValue(WeatherResource.weather(data.response))
                is AppResponse.Error ->
                    forecastDaily.postValue(
                        WeatherResource.error(
                            "Code: ${data.code}, Data: ${data.error?.error}"
                        )
                    )
                is AppResponse.NetworkError ->
                    forecastDaily.postValue(WeatherResource.error(data.toString()))
            }
        }
    }
}