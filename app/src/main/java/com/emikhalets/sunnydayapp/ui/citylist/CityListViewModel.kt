package com.emikhalets.sunnydayapp.ui.citylist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.repository.CityListRepository
import com.emikhalets.sunnydayapp.utils.status.CitiesResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CityListViewModel @ViewModelInject constructor(private val repository: CityListRepository) :
    ViewModel() {

    val addedCities = MutableLiveData<CitiesResource<List<City>>>()

    fun getAddedCities() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAddedCities()
            Timber.d("The list of the added cities from the database is loaded")
            if (result.isNotEmpty()) addedCities.postValue(CitiesResource.cities(result))
            else addedCities.postValue(CitiesResource.empty())
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