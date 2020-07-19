package com.emikhalets.sunnydayapp.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseHourly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val currentWeather = MutableLiveData<ResponseCurrent>()
    val forecastHourly = MutableLiveData<ResponseHourly>()

    fun requestCurrent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            currentWeather.postValue(repository.requestCurrent(array[0], array[1]))
        }
    }

    fun requestForecastHourly(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            forecastHourly.postValue(repository.requestForecastHourly(array[0], array[1]))
        }
    }
}