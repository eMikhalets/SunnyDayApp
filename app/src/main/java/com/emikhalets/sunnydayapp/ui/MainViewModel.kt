package com.emikhalets.sunnydayapp.ui

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var searchingJob: Job? = null
    private var weatherJob: Job? = null

    private val _database = MutableLiveData<State>()
    val database: LiveData<State> get() = _database

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> get() = _weather

    private val _searchingCity = MutableLiveData<List<City>>()
    val searchingCity: LiveData<List<City>> get() = _searchingCity

    private val _weatherState = MutableLiveData<State>()
    val weatherState: LiveData<State> get() = _weatherState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    val location = MutableLiveData<Location>()
    val selectingCityCallback = MutableLiveData<City?>()
    val hourlyScrollCallback = MutableLiveData<Boolean>()

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    var currentCity = ""
    var isNightTheme = false
    var isWeatherLoaded = false
    var isPreferencesChanged = false

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

    private suspend fun createCitiesDatabase(json: String) {
        val cities = mutableListOf<City>()
        val jsonCities = JSONArray(json)
        for (i in 0 until jsonCities.length()) {
            val jsonObject = jsonCities.getJSONObject(i)
            val city = parseCityItem(jsonObject)
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

    fun sendWeatherRequest() {
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            sendWeatherRequest(currentLatitude, currentLongitude)
        }
    }

    fun sendWeatherRequest(lat: Double, lon: Double) {
        if (!isWeatherLoaded || isPreferencesChanged) {
            Timber.d("Отмена текущего запроса погоды. Отправка нового")
            weatherJob?.cancel()
            weatherJob = viewModelScope.launch {
                _weatherState.postValue(State.LOADING)
                when (val response = repository.weatherRequest(lat, lon)) {
                    is ApiResult.Success -> {
                        currentLatitude = lat
                        currentLongitude = lon
                        isWeatherLoaded = true
                        isPreferencesChanged = false
                        _weather.postValue(response.result)
                        _weatherState.postValue(State.LOADED)
                    }
                    is ApiResult.Error -> {
                        isWeatherLoaded = false
                        isPreferencesChanged = true
                        _error.postValue(response.msg)
                        _weatherState.postValue(State.ERROR)
                    }
                }
            }
        }
    }

    // =================== Searching in local database

    fun searchCitiesInDb(query: String) {
        searchingJob?.cancel()
        searchingJob = viewModelScope.launch {
            when (val result = repository.getCitiesByName(query)) {
                is DbResult.Success -> {
                    val cities = mutableListOf<City>()
                    if (query.length >= 2) cities.addAll(result.result)
                    _searchingCity.postValue(cities)
                }
                is DbResult.Error -> _error.postValue(result.msg)
            }
        }
    }

    fun cancelSearchingCities() {
        _searchingCity.postValue(mutableListOf())
    }
}