package com.emikhalets.sunnydayapp.ui.pager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.PagerRepository
import com.emikhalets.sunnydayapp.data.database.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber

class ViewPagerViewModel : ViewModel() {

    private val dbDeleted = "DELETED"
    private val dbCreated = "CREATED"

    private val repository = PagerRepository()
    private val citiesToDB = mutableListOf<City>()

    private var _searchingCities = MutableLiveData<Array<String>>()
    val searchingCities: LiveData<Array<String>> get() = _searchingCities

    private var _currentQuery = MutableLiveData<String>()
    val currentQuery: LiveData<String> get() = _currentQuery

    private var _addedCity = MutableLiveData<String>()
    val addedCity: LiveData<String> get() = _addedCity

    private var _location = MutableLiveData<List<Double>>()
    val location: LiveData<List<Double>> get() = _location

    private var _locationQuery = MutableLiveData<String>()
    val locationQuery: LiveData<String> get() = _locationQuery

    private var _dbStatus = MutableLiveData<String>()
    val dbStatus: LiveData<String> get() = _dbStatus

    private var _citiesList = MutableLiveData<List<City>>()
    val citiesList: LiveData<List<City>> get() = _citiesList

    private var _timezone = MutableLiveData<String>()
    val timezone: LiveData<String> get() = _timezone

    var isWeatherLoaded = false
    var isLocation = false

    fun updateCurrentQuery(query: String) {
        Timber.d("Query has been updated: ($query)")
        _currentQuery.postValue(query)
    }

    fun updateTimezone(timezone: String) {
        _timezone.postValue(timezone)
    }

    fun updateLocation(lat: Double, lon: Double, query: String) {
        Timber.d("Location query has been updated: (lat=$lat, lon=$lon)")
        _location.value = listOf(lat, lon)
        Timber.d("Location has been updated: ($query)")
        _locationQuery.value = query
    }

    fun getCitiesByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getCitiesByName(name)
            Timber.d("The list of cities by name has been updated")
            list.forEach { Timber.d(it.toString()) }
            val searchList = Array(list.size) { i -> "${list[i].cityName}, ${list[i].countryFull}" }
            Timber.d("Search query has been updated")
            _searchingCities.postValue(searchList)
        }
    }

    fun changeIsAddedCity(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            val city = repository.getCityByName(array[0], array[1])
            Timber.d("Loaded city by name")
            if (!city.isAdded) {
                city.isAdded = true
                repository.updateCity(city)
                Timber.d("The isAdded field updated: $city")
                _addedCity.postValue(query)
            } else {
                Timber.d("The isAdded field is already true")
            }
        }
    }

    fun deleteCitiesTable() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Deleting cities database")
            repository.deleteAllCities()
            _dbStatus.postValue(dbDeleted)
        }
    }

    fun parseAndInsertToDB(string: String) {
        val array = JSONArray(string)
        Timber.d("Data parsed into JSON array")
        for (i in 0 until array.length()) {
            val json = array.getJSONObject(i)
            val city = City(
                cityId = json.getInt("city_id"),
                cityName = json.getString("city_name"),
                stateCode = json.getString("state_code"),
                countryCode = json.getString("country_code"),
                countryFull = json.getString("country_full"),
                lat = json.getDouble("lat"),
                lon = json.getDouble("lon"),
                isAdded = false
            )
            citiesToDB.add(city)
        }
        Timber.d("Number of cities in the list: (${citiesToDB.size})")

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAllCities(citiesToDB)
            Timber.d("Parsed list of cities added to the database")
            _dbStatus.postValue(dbCreated)
        }
    }
}