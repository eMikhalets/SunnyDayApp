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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    lateinit var currentCity: String

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

    // Parsing cities

    fun parseAndInsertToDB(json: String) {
        viewModelScope.launch(coroutineContext) {
            val cities = Gson().fromJson<List<City>>(json, object : TypeToken<List<City>>() {}.type)
            Timber.d("Number of cities in the list: (${cities.size})")
            repository.insertAllCities(cities)
            Timber.d("Parsed list of cities added to the database")
            _dbCreating.postValue(true)
        }
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