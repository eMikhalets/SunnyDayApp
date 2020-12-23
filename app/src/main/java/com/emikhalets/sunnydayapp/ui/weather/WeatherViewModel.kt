package com.emikhalets.sunnydayapp.ui.weather

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.model.ResponseCurrent
import com.emikhalets.sunnydayapp.data.model.ResponseDaily
import com.emikhalets.sunnydayapp.data.repository.WeatherRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class WeatherViewModel @ViewModelInject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable -> Timber.e(throwable) }
    private val coroutineContext = Dispatchers.IO + SupervisorJob()

    private val _currentWeather = MutableLiveData<WeatherState<ResponseCurrent>>()
    val currentWeather: LiveData<WeatherState<ResponseCurrent>> get() = _currentWeather

    private val _forecastDaily = MutableLiveData<WeatherState<ResponseDaily>>()
    val forecastDaily: LiveData<WeatherState<ResponseDaily>> get() = _forecastDaily

    fun requestCurrent(query: String) {
        viewModelScope.launch(coroutineContext) {
            try {
                Timber.d("Sending current weather request by city name: ($query)")
                _currentWeather.postValue(WeatherState.loading())
                val array = query.split(", ")
                val data = repository.requestCurrent(array[0], array[1], "ru", "M")
                _currentWeather.postValue(WeatherState.weather(data))
            } catch (ex: Exception) {
                Timber.e(ex)
                _currentWeather.postValue(WeatherState.error(ex.message))
            }
        }
    }

    fun requestCurrent(lat: Double, lon: Double) {
        viewModelScope.launch(coroutineContext) {
            try {
                Timber.d("Sending current weather request by location: (lat=$lat, lon=$lon)")
                _currentWeather.postValue(WeatherState.loading())
                val data = repository.requestCurrent(lat, lon, "ru", "M")
                _currentWeather.postValue(WeatherState.weather(data))
            } catch (ex: Exception) {
                Timber.e(ex)
                _currentWeather.postValue(WeatherState.error(ex.message))
            }
        }
    }

    fun requestForecastDaily(query: String) {
        viewModelScope.launch(coroutineContext) {
            try {
                Timber.d("Sending daily forecast request by city name: ($query)")
                _forecastDaily.postValue(WeatherState.loading())
                val array = query.split(", ")
                val data = repository.requestForecastDaily(array[0], array[1], "ru", "M")
                _forecastDaily.postValue(WeatherState.weather(data))
            } catch (ex: Exception) {
                Timber.e(ex)
                _forecastDaily.postValue(WeatherState.error(ex.message))
            }
        }
    }

    fun requestForecastDaily(lat: Double, lon: Double) {
        viewModelScope.launch(coroutineContext) {
            try {
                Timber.d("Sending daily forecast request by location: (lat=$lat, lon=$lon)")
                _forecastDaily.postValue(WeatherState.loading())
                val data = repository.requestForecastDaily(lat, lon, "ru", "M")
                _forecastDaily.postValue(WeatherState.weather(data))
            } catch (ex: Exception) {
                Timber.e(ex)
                _forecastDaily.postValue(WeatherState.error(ex.message))
            }
        }
    }
}