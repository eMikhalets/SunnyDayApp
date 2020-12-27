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

    private val _userLocation = MutableLiveData<Location>()
    val userLocation: LiveData<Location> get() = _userLocation

    private val _searchingCities = MutableLiveData<List<City>>()
    val searchingCities: LiveData<List<City>> get() = _searchingCities

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

//    private val _location = MutableLiveData<List<Double>>()
//    val location: LiveData<List<Double>> get() = _location
//
//    private val _currentLocation = MutableLiveData<Location>()
//    val currentLocation: LiveData<Location> get() = _currentLocation

//    private var isLocation = false

    var isWeatherLoaded = false
    lateinit var currentCity: City

    fun sendWeatherRequest(city: City) {
        viewModelScope.launch(coroutineContext) {
            try {
                Timber.d("Query has been updated: (${city.name})")
                val response = repository.weatherRequest(city.lat, city.lon, "metric", "en")
                _weather.postValue(response)
                currentCity = city
                isWeatherLoaded = true
            } catch (ex: Exception) {
                Timber.e(ex)
                _error.postValue(ex.message)
            }
        }
    }

    fun searchCitiesInDb(name: String) {
        viewModelScope.launch(coroutineContext) {
            val cities = repository.getCitiesByName(name)
            _searchingCities.postValue(cities)
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

//    fun getCityAndCountry(lat: Double, lon: Double): String {
//        val address = Geocoder(getApplication(), Locale.getDefault())
//            .getFromLocation(lat, lon, 1).first()
//        return "${address.locality}, ${address.countryName}"
//    }
//
//    fun checkLocationPermissions(): Boolean =
//        ContextCompat.checkSelfPermission(
//            getApplication(),
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
//            getApplication(),
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//
//    @SuppressLint("MissingPermission")
//    fun requestLocation(fusedLocationClient: FusedLocationProviderClient) {
//        if (!isLocation) {
//            isLocation = true
//            val locationRequest = LocationRequest.create()?.apply {
//                interval = 1000 * 60 * 10
//                fastestInterval = interval / 2
//                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//                smallestDisplacement = 1000f
//            }
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback(),
//                Looper.getMainLooper()
//            )
//        }
//    }
//
//    private fun locationCallback() = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult?) {
//            super.onLocationResult(locationResult)
//            locationResult?.let { _currentLocation.value = locationResult.locations.first() }
//        }
//    }
}