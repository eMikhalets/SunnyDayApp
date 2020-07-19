package com.emikhalets.sunnydayapp.ui.forecast

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseDaily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForecastDailyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val forecastDaily = MutableLiveData<ResponseDaily>()

    fun requestForecastDaily(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            forecastDaily.postValue(repository.requestForecastDaily(array[0], array[1]))
        }
    }
}