package com.emikhalets.sunnydayapp.ui

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.*
import com.emikhalets.sunnydayapp.data.api.ApiResult
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.database.DbResult
import com.emikhalets.sunnydayapp.data.model.WeatherResponse
import com.emikhalets.sunnydayapp.data.repository.MainRepository
import com.emikhalets.sunnydayapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {

    private var searchingJob: Job? = null
    private var weatherJob: Job? = null

    private val _database = MutableLiveData<State>()
    val database: LiveData<State> get() = _database

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> get() = _weather

    private val _searching = MutableLiveData<List<City>>()
    val searching: LiveData<List<City>> get() = _searching

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _searchingState = MutableLiveData<State>()
    val searchingState: LiveData<State> get() = _searchingState

    val prefs = MutableLiveData<Map<String, String>>()
    val selecting = MutableLiveData<City?>()
    val location = MutableLiveData<Location>()
    val scrollCallback = MutableLiveData<Boolean>()

    var currentCity = ""
    var currentLang = ""
    var currentUnits = ""
    var currentLat = 0.0
    var currentLong = 0.0

    // =================== Parsing cities

    fun parseCitiesJson(stream: InputStream) {
        viewModelScope.launch {
            _database.postValue(State.LOADING)
            stream.bufferedReader().use { reader ->
                val json = reader.readText()
                createCitiesDatabase(json)
            }
        }
    }

    private fun createCitiesDatabase(json: String) {
            viewModelScope.launch {
                val cities = mutableListOf<City>()
                val jsonCities = JSONArray(json)
                for (i in 0 until jsonCities.length()) {
                    val city = parseCityItem(jsonCities.getJSONObject(i))
                    cities.add(city)
                }
                when (val result = repository.insertAllCities(cities)) {
                    is DbResult.Success -> {
                        _database.postValue(State.LOADED)
                    }
                    is DbResult.Error -> {
                        _error.postValue(result.msg)
                        _database.postValue(State.ERROR)
                    }
                }
        }
    }

    private fun parseCityItem(json: JSONObject): City {
        return City(
            id = json.getInt("id"),
            name = json.getString("name"),
            state = json.getString("state"),
            country = json.getString("country"),
            lon = json.getJSONObject("coord").getDouble("lon"),
            lat = json.getJSONObject("coord").getDouble("lat")
        )
    }

    fun setDatabaseStateLoaded() {
        _database.postValue(State.LOADED)
    }

    // =================== Network requests

    @SuppressLint("NullSafeMutableLiveData")
    fun sendWeatherRequest(lat: Double, lon: Double) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            if (currentLat != 0.0 && currentLong != 0.0) {
                _searchingState.postValue(State.LOADING)
                when (val response = repository.weatherRequest(lat, lon)) {
                    is ApiResult.Success -> {
                        _weather.postValue(response.result)
                        currentLat = response.result.lat
                        currentLong = response.result.lon
                        _searchingState.postValue(State.LOADED)
                    }
                    is ApiResult.Error -> {
                        _error.postValue(response.msg)
                        _searchingState.postValue(State.ERROR)
                    }
                }
            }
        }
    }

    // =================== Searching id local database

    fun searchCitiesInDb(query: String) {
        searchingJob?.cancel()
        searchingJob = viewModelScope.launch {
            when (val result = repository.getCitiesByName(query)) {
                is DbResult.Success -> {
                    val cities = mutableListOf<City>()
                    if (query.length >= 2) cities.addAll(result.result)
                    _searching.postValue(cities)
                }
                is DbResult.Error -> _error.postValue(result.msg)
            }
        }
    }

    fun cancelSearchingCities() {
        _searching.postValue(mutableListOf())
    }
}