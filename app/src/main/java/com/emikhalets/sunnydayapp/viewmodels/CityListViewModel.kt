package com.emikhalets.sunnydayapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.network.pojo.ResponseCurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val currentWeather = MutableLiveData<ResponseCurrent>()
    val cities = MutableLiveData<List<City>>()

    fun requestCurrent(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentWeather.postValue(repository.requestCurrent(cityName))
        }
    }

    fun getAllCities() {
        viewModelScope.launch(Dispatchers.IO) {
            cities.postValue(repository.getAllCities())
        }
    }

    fun insertCity(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val city = City(name = cityName)
            repository.insertCity(city)
            getAllCities()
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
            getAllCities()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}