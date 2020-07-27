package com.emikhalets.sunnydayapp.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.network.AppResponse
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseCurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CurrentWeatherViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _currentWeather = MutableLiveData<ResponseCurrent>()
    val currentWeather: LiveData<ResponseCurrent> get() = _currentWeather

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun requestCurrent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Current Weather Query %s", query)
            val array = query.split(", ")
            when (val data = repository.requestCurrent(array[0], array[1])) {
                is AppResponse.Success ->
                    _currentWeather.postValue(data.response)
                is AppResponse.Error ->
                    _errorMessage.postValue("Code: ${data.code}, Data: ${data.error?.error}")
                is AppResponse.NetworkError ->
                    _errorMessage.postValue(data.toString())
            }
        }
    }
}