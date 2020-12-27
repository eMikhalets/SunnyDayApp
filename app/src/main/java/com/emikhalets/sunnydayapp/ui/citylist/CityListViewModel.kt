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

    private val _searchedCities = MutableLiveData<CitiesState<List<City>>>()
    val searchedCities: LiveData<CitiesState<List<City>>> get() = _searchedCities

    fun checkIsSearched(city: City) {
        viewModelScope.launch(coroutineContext) {
            try {
                if (!city.isSearched) {
                    city.isSearched = true
                    repository.updateCity(city)
                    getSearchedCities()
                } else {
                    Timber.d("The isSearched status is already true")
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    fun getSearchedCities() {
        viewModelScope.launch(coroutineContext) {
            try {
                _searchedCities.postValue(CitiesState.loading())
                val result = repository.getSearchedCities()
                Timber.d("The list of the added cities from the database is loaded")
                if (result.isNotEmpty()) _searchedCities.postValue(CitiesState.cities(result))
                else _searchedCities.postValue(CitiesState.empty())
            } catch (ex: Exception) {
                Timber.e(ex)
                _searchedCities.postValue(CitiesState.error(ex.message))
            }
        }
    }

    fun removeCityFromSearched(city: City) {
        viewModelScope.launch(coroutineContext) {
            try {
                val receivedCity = repository.getCity(city.id)
                receivedCity.isSearched = false
                repository.updateCity(receivedCity)
                Timber.d("City removed from search history : (${city.name})")
                getSearchedCities()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }
}