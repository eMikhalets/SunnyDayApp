package com.emikhalets.sunnydayapp.ui.pager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.data.repository.PagerRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class ViewPagerViewModel @ViewModelInject constructor(
    private val repository: PagerRepository,
    application: Application
) : AndroidViewModel(application) {

    private val coroutineContext = Dispatchers.IO + SupervisorJob()

    private val _dbCreating = MutableLiveData<Boolean>()
    val dbCreating: LiveData<Boolean> get() = _dbCreating

    private val _weather = MutableLiveData<Response>()
    val weather: LiveData<Response> get() = _weather


    private val _timezone = MutableLiveData<String>()
    val timezone: LiveData<String> get() = _timezone

    private val _addedCity = MutableLiveData<String>()
    val addedCity: LiveData<String> get() = _addedCity

    private val _currentQuery = MutableLiveData<String>()
    val currentQuery: LiveData<String> get() = _currentQuery

    private val _locationQuery = MutableLiveData<String>()
    val locationQuery: LiveData<String> get() = _locationQuery

    private val _location = MutableLiveData<List<Double>>()
    val location: LiveData<List<Double>> get() = _location

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> get() = _currentLocation

    private val _searchingCities = MutableLiveData<Array<String>>()
    val searchingCities: LiveData<Array<String>> get() = _searchingCities

    private var isLocation = false
    var isWeatherLoaded = false

    fun updateCurrentQuery(query: String) {
        Timber.d("Query has been updated: ($query)")
        _currentQuery.value = query
        changeIsAddedCity(query)
    }

    fun updateTimezone(timezone: String) {
        this._timezone.value = timezone
    }

    fun updateLocation(lat: Double, lon: Double, query: String) {
        Timber.d("Location query has been updated: (lat=$lat, lon=$lon)")
        _location.value = listOf(lat, lon)
        Timber.d("Location has been updated: ($query)")
        _locationQuery.value = query
    }

    fun getCitiesByName(name: String) {
        viewModelScope.launch(coroutineContext) {
            val list = repository.getCitiesByName(name)
            Timber.d("The list of cities by name has been updated")
            list.forEach { Timber.d(it.toString()) }
            val searchList = Array(list.size) { i -> "${list[i].cityName}, ${list[i].countryFull}" }
            Timber.d("Search query has been updated")
            _searchingCities.postValue(searchList)
        }
    }

    private fun changeIsAddedCity(query: String) {
        viewModelScope.launch(coroutineContext) {
            val array = query.split(", ")
            val city = repository.getCityByName(array[0], array[1])
            Timber.d("Loaded city by name")
            if (!city.isSearched) {
                city.isSearched = true
                repository.updateCity(city)
                Timber.d("The isSearched status updated: $city")
                _addedCity.postValue(query)
            } else {
                Timber.d("The isSearched status is already true")
            }
        }
    }

    fun deleteCitiesTable() {
        viewModelScope.launch(coroutineContext) {
            Timber.d("Deleting cities database")
            repository.deleteAllCities()
            _dbCreating.postValue(false)
        }
    }

    // Parsing cities

    fun parseAndInsertToDB(string: String) {
        viewModelScope.launch(coroutineContext) {
            val array = JSONArray(string)
            val cities = mutableListOf<City>()
            Timber.d("Data parsed into JSON array")

            for (i in 0 until array.length()) {
                cities.add(parseItem(array.getJSONObject(i)))
            }

            Timber.d("Number of cities in the list: (${cities.size})")
            repository.insertAllCities(cities)
            Timber.d("Parsed list of cities added to the database")
            _dbCreating.postValue(true)
        }
    }

    private fun parseItem(json: JSONObject): City {
        return City(
            id = json.getInt("city_id"),
            name = json.getString("city_name"),
            state = json.getString("state_code"),
            country = json.getString("country_code"),
            lon = json.getDouble("lon"),
            lat = json.getDouble("lat")
        )
    }

    // Location

    fun getCityAndCountry(lat: Double, lon: Double): String {
        val address = Geocoder(getApplication(), Locale.getDefault())
            .getFromLocation(lat, lon, 1).first()
        return "${address.locality}, ${address.countryName}"
    }

    fun checkLocationPermissions(): Boolean =
        ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun requestLocation(fusedLocationClient: FusedLocationProviderClient) {
        if (!isLocation) {
            isLocation = true
            val locationRequest = LocationRequest.create()?.apply {
                interval = 1000 * 60 * 10
                fastestInterval = interval / 2
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 1000f
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback(),
                Looper.getMainLooper()
            )
        }
    }

    private fun locationCallback() = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.let { _currentLocation.value = locationResult.locations.first() }
        }
    }
}