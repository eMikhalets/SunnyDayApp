package com.emikhalets.sunnydayapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.network.pojo.ResponseHourly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val currentWeather = MutableLiveData<ResponseCurrent>()
    val forecastHourly = MutableLiveData<ResponseHourly>()

    fun requestCurrent(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentWeather.postValue(repository.requestCurrent(cityName))
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