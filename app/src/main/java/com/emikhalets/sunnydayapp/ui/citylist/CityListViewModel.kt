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
            addedCities.postValue(repository.getAddedCities())
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val receivedCity = repository.getCity(city.id!!)
            receivedCity.isAdded = false
            repository.updateCity(receivedCity)
            getAddedCities()
            Timber.d("City deleted from searched list. Added cities updated")
        }
    }
}