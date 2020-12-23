package com.emikhalets.sunnydayapp.ui.citylist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.repository.CityListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class CityListViewModel @ViewModelInject constructor(
    private val repository: CityListRepository
) : ViewModel() {

    private val coroutineContext = Dispatchers.IO + SupervisorJob()

    private val _addedCities = MutableLiveData<CitiesState<List<City>>>()
    val addedCities: LiveData<CitiesState<List<City>>> get() = _addedCities

    fun getAddedCities() {
        viewModelScope.launch(coroutineContext) {
            try {
                _addedCities.postValue(CitiesState.loading())
                val result = repository.getAddedCities()
                Timber.d("The list of the added cities from the database is loaded")
                if (result.isNotEmpty()) _addedCities.postValue(CitiesState.cities(result))
                else _addedCities.postValue(CitiesState.empty())
            } catch (ex: Exception) {
                Timber.e(ex)
                _addedCities.postValue(CitiesState.error(ex.message))
            }
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(coroutineContext) {
            try {
                val receivedCity = repository.getCity(city.id!!)
                receivedCity.isAdded = false
                repository.updateCity(receivedCity)
                Timber.d("City removed from search history : (${city.getQuery()})")
                getAddedCities()
            } catch (ex: Exception) {
                Timber.e(ex)
                _addedCities.postValue(CitiesState.error(ex.message))
            }
        }
    }
}