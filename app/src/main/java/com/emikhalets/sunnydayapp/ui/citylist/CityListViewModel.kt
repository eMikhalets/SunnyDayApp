package com.emikhalets.sunnydayapp.ui.citylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.CityListRepository
import com.emikhalets.sunnydayapp.data.database.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CityListViewModel : ViewModel() {

    private val repository = CityListRepository()
    val addedCities = MutableLiveData<List<City>>()

    fun getAddedCities() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAddedCities()
            Timber.d("The list of the added cities from the database is loaded")
            addedCities.postValue(result)
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val receivedCity = repository.getCity(city.id!!)
            receivedCity.isAdded = false
            repository.updateCity(receivedCity)
            Timber.d("City removed from search history : (${city.getQuery()})")
            getAddedCities()
        }
    }
}