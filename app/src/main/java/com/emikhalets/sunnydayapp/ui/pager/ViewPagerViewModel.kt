package com.emikhalets.sunnydayapp.ui.pager

import android.app.Application
import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.data.repository.PagerRepository
import com.emikhalets.sunnydayapp.utils.FragmentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class ViewPagerViewModel @ViewModelInject constructor(
    private val repository: PagerRepository,
    application: Application
) : AndroidViewModel(application) {

    private val coroutineContext = Dispatchers.IO
    private val searchingCoroutineContext = Dispatchers.IO
    private var searchingJob: Job? = null

    private val _dbCreating = MutableLiveData<Boolean>()
    val dbCreating: LiveData<Boolean> get() = _dbCreating

    private val _weather = MutableLiveData<FragmentState<Response>>()
    val weather: LiveData<FragmentState<Response>> get() = _weather

    private val _searchingCities = MutableLiveData<List<City>>()
    val searchingCities: LiveData<List<City>> get() = _searchingCities

    private val _selectSearching = MutableLiveData<City>()
    val selectSearching: LiveData<City> get() = _selectSearching

    val userLocation = MutableLiveData<Location>()
    val scrollCallback = MutableLiveData<Boolean>()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

//    private val _location = MutableLiveData<List<Double>>()
//    val location: LiveData<List<Double>> get() = _location
//
//    private val _currentLocation = MutableLiveData<Location>()
//    val currentLocation: LiveData<Location> get() = _currentLocation

//    private var isLocation = false

    var isWeatherLoaded = false
    var currentCity: String = ""

    fun sendWeatherRequest(lat: Double, lon: Double) {
        viewModelScope.launch(coroutineContext) {
            try {
                _weather.postValue(FragmentState.loading())
                val response = repository.weatherRequest(lat, lon, "metric", "en")
                _weather.postValue(FragmentState.loaded(response))
                isWeatherLoaded = true
            } catch (ex: Exception) {
                Timber.e(ex)
                _error.postValue(ex.message)
            }
        }
    }

    fun searchCitiesInDb(query: String) {
        searchingJob?.cancel()
        searchingJob = viewModelScope.launch(searchingCoroutineContext) {
            Timber.d("The searching query: '$query'")
            val cities = mutableListOf<City>()
            if (query.length >= 2) {
                cities.addAll(repository.getCitiesByName(query))
                Timber.d("Matching cities by search query: ${cities.size} items")
            }
            _searchingCities.postValue(cities)
        }
    }

    fun cancelSearchingCities() {
        Timber.d("Cancel searching cities")
        _searchingCities.postValue(mutableListOf())
    }

    fun selectSearchingCity(city: City) {
        _selectSearching.postValue(city)
    }

    // Parsing cities

    fun parseAndInsertToDB(json: String) {
        viewModelScope.launch(coroutineContext) {
            val cities = mutableListOf<City>()
            val jsonCities = JSONArray(json)
            for (i in 0 until jsonCities.length()) {
                val city = parseCityJson(jsonCities.getJSONObject(i))
                cities.add(city)
            }
            Timber.d("Number of cities in the list: (${cities.size})")
            repository.insertAllCities(cities)
            Timber.d("Parsed list of cities added to the database")
            _dbCreating.postValue(true)
        }
    }

    private fun parseCityJson(json: JSONObject): City {
        return City(
            id = json.getInt("id"),
            name = json.getString("name"),
            state = json.getString("state"),
            country = json.getString("country"),
            lon = json.getJSONObject("coord").getDouble("lon"),
            lat = json.getJSONObject("coord").getDouble("lat")
        )
    }
}