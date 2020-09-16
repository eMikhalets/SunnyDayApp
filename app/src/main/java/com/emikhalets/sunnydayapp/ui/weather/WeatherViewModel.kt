package com.emikhalets.sunnydayapp.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.WeatherRepository
import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.network.pojo.ResponseCurrent
import com.emikhalets.sunnydayapp.network.pojo.ResponseDaily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _currentWeather = MutableLiveData<ResponseCurrent>()
    val currentWeather: LiveData<ResponseCurrent> get() = _currentWeather

    private val _forecastDaily = MutableLiveData<ResponseDaily>()
    val forecastDaily: LiveData<ResponseDaily> get() = _forecastDaily

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun requestCurrent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Current Weather Query %s", query)
            val array = query.split(", ")
            when (val data = repository.requestCurrent(array[0], array[1], "ru", "M")) {
                is AppResponse.Success ->
                    _currentWeather.postValue(data.response)
                is AppResponse.Error ->
                    _errorMessage.postValue("Code: ${data.code}, Data: ${data.error?.error}")
                is AppResponse.NetworkError ->
                    _errorMessage.postValue(data.toString())
            }
        }
    }

    fun requestForecastDaily(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Forecast Daily Query %s", query)
            val array = query.split(", ")
            when (val data = repository.requestForecastDaily(array[0], array[1], "ru", "M")) {
                is AppResponse.Success ->
                    _forecastDaily.postValue(data.response)
                is AppResponse.Error ->
                    _errorMessage.postValue("Code: ${data.code}, Data: ${data.error?.error}")
                is AppResponse.NetworkError ->
                    _errorMessage.postValue(data.toString())
            }
        }
    }
}