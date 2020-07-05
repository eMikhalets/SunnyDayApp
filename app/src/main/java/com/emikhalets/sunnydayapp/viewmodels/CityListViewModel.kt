package com.emikhalets.sunnydayapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.utils.ADDED_CITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val cities = MutableLiveData<List<City>>()

    fun getAddedCities() {
        viewModelScope.launch(Dispatchers.IO) {
            cities.postValue(repository.getAddedCities())
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val cityDB = repository.getCity(city.id!!)
            cityDB.isAdded = false
            repository.updateCity(cityDB)
            getAddedCities()
        }
    }
}