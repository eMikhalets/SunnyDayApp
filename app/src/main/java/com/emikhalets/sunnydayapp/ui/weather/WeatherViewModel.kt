package com.emikhalets.sunnydayapp.ui.weather

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.api.AppResponse
import com.emikhalets.sunnydayapp.data.model.ResponseCurrent
import com.emikhalets.sunnydayapp.data.model.ResponseDaily
import com.emikhalets.sunnydayapp.data.repository.WeatherRepository
import com.emikhalets.sunnydayapp.utils.status.WeatherState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WeatherViewModel @ViewModelInject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private val _currentWeather = MutableLiveData<WeatherState<ResponseCurrent>>()
    val currentWeather: LiveData<WeatherState<ResponseCurrent>> get() = _currentWeather

    private val _forecastDaily = MutableLiveData<WeatherState<ResponseDaily>>()
    val forecastDaily: LiveData<WeatherState<ResponseDaily>> get() = _forecastDaily

    fun requestCurrent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending current weather request by city name: ($query)")
            val array = query.split(", ")
            when (val data = repository.requestCurrent(array[0], array[1], "ru", "M")) {
                is AppResponse.Success -> _currentWeather.postValue(WeatherState.weather(data.response))
                is AppResponse.Error -> _currentWeather.postValue(WeatherState.error("Code: ${data.code}, Data: ${data.error?.error}"))
                is AppResponse.NetworkError -> _currentWeather.postValue(WeatherState.error(data.toString()))
            }
        }
    }

    fun requestCurrent(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending current weather request by location: (lat=$lat, lon=$lon)")
            when (val data = repository.requestCurrent(lat, lon, "ru", "M")) {
                is AppResponse.Success -> _currentWeather.postValue(WeatherState.weather(data.response))
                is AppResponse.Error -> _currentWeather.postValue(WeatherState.error("Code: ${data.code}, Data: ${data.error?.error}"))
                is AppResponse.NetworkError -> _currentWeather.postValue(WeatherState.error(data.toString()))
            }
        }
    }

    fun requestForecastDaily(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending daily forecast request by city name: ($query)")
            val array = query.split(", ")
            when (val data = repository.requestForecastDaily(array[0], array[1], "ru", "M")) {
                is AppResponse.Success -> _forecastDaily.postValue(WeatherState.weather(data.response))
                is AppResponse.Error -> _forecastDaily.postValue(WeatherState.error("Code: ${data.code}, Data: ${data.error?.error}"))
                is AppResponse.NetworkError -> _forecastDaily.postValue(WeatherState.error(data.toString()))
            }
        }
    }

    fun requestForecastDaily(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Sending daily forecast request by location: (lat=$lat, lon=$lon)")
            when (val data = repository.requestForecastDaily(lat, lon, "ru", "M")) {
                is AppResponse.Success -> _forecastDaily.postValue(WeatherState.weather(data.response))
                is AppResponse.Error -> _forecastDaily.postValue(WeatherState.error("Code: ${data.code}, Data: ${data.error?.error}"))
                is AppResponse.NetworkError -> _forecastDaily.postValue(WeatherState.error(data.toString()))
            }
        }
    }
}