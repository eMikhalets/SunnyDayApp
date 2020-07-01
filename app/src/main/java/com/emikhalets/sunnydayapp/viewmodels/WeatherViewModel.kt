package com.emikhalets.sunnydayapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.network.pojo.ResponseHourly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = AppRepository()
    val currentWeather = MutableLiveData<ResponseCurrent>()
    val forecastDaily = MutableLiveData<ResponseDaily>()
    val forecastHourly = MutableLiveData<ResponseHourly>()

    fun requestCurrent(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentWeather.postValue(repository.requestCurrent(cityName))
        }
    }

    fun requestForecastDaily(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            forecastDaily.postValue(repository.requestForecastDaily(cityName))
        }
    }

    fun requestForecastHourly(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            forecastHourly.postValue(repository.requestForecastHourly(cityName))
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}