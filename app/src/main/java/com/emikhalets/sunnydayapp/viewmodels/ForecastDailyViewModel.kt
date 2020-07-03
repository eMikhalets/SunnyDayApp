package com.emikhalets.sunnydayapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.network.pojo.ResponseDaily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForecastDailyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val forecastDaily = MutableLiveData<ResponseDaily>()

    fun requestForecastDaily(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            forecastDaily.postValue(repository.requestForecastDaily(cityName))
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}