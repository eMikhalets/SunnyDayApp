package com.emikhalets.sunnydayapp.ui.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.DailyRepository
import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.network.pojo.ResponseDaily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ForecastDailyViewModel : ViewModel() {

    private val repository = DailyRepository()

    private val _forecastDaily = MutableLiveData<ResponseDaily>()
    val forecastDaily: LiveData<ResponseDaily> get() = _forecastDaily

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

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