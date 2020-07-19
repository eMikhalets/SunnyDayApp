package com.emikhalets.sunnydayapp.ui.citylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CityListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val addedCities = MutableLiveData<List<City>>()

    fun getAddedCities() {
        viewModelScope.launch(Dispatchers.IO) {
            addedCities.postValue(repository.getAddedCities())
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val cityDB = repository.getCity(city.id!!)
            cityDB.isAdded = false
            repository.updateCity(cityDB)
            getAddedCities()
            Timber.d("City deleted. Added cities updated")
        }
    }
}