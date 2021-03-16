package com.emikhalets.sunnydayapp.ui.citylist

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.database.DbResult
import com.emikhalets.sunnydayapp.data.repository.CityListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityListRepository
) : ViewModel() {

    private val _savedCities = MutableLiveData<List<City>>()
    val savedCities: LiveData<List<City>> get() = _savedCities

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun saveCity(city: City) {
        viewModelScope.launch {
            if (!city.isSearched) {
                city.isSearched = true
                when (val result = repository.updateCity(city)) {
                    is DbResult.Success -> getSearchedCities()
                    is DbResult.Error -> _error.postValue(result.msg)
                }
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun getSearchedCities() {
        viewModelScope.launch {
            when (val result = repository.getSavedCities()) {
                is DbResult.Success -> _savedCities.postValue(result.result)
                is DbResult.Error -> _error.postValue(result.msg)
            }
        }
    }

    fun removeCityFromSaved(city: City) {
        viewModelScope.launch {
            city.isSearched = false
            when (val result = repository.updateCity(city)) {
                is DbResult.Success -> getSearchedCities()
                is DbResult.Error -> _error.postValue(result.msg)
            }
        }
    }
}