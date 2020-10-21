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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.repository.PagerRepository
import com.emikhalets.sunnydayapp.utils.status.PagerStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import java.util.*

class ViewPagerViewModel @ViewModelInject constructor(
    private val repository: PagerRepository,
    application: Application
) : AndroidViewModel(application) {

    private val citiesToDB = mutableListOf<City>()

    val timezone = MutableLiveData<String>()
    val addedCity = MutableLiveData<String>()
    val currentQuery = MutableLiveData<String>()
    val locationQuery = MutableLiveData<String>()
    val dbStatus = MutableLiveData<PagerStatus>()
    val location = MutableLiveData<List<Double>>()
    val currentLocation = MutableLiveData<Location>()

    //    val citiesList = MutableLiveData<List<City>>()
    val searchingCities = MutableLiveData<Array<String>>()

    private var isLocation = false
    var isWeatherLoaded = false

    fun updateCurrentQuery(query: String) {
        Timber.d("Query has been updated: ($query)")
        currentQuery.value = query
        changeIsAddedCity(query)
    }

    fun updateTimezone(timezone: String) {
        this.timezone.value = timezone
    }

    fun updateLocation(lat: Double, lon: Double, query: String) {
        Timber.d("Location query has been updated: (lat=$lat, lon=$lon)")
        location.value = listOf(lat, lon)
        Timber.d("Location has been updated: ($query)")
        locationQuery.value = query
    }

    fun getCitiesByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getCitiesByName(name)
            Timber.d("The list of cities by name has been updated")
            list.forEach { Timber.d(it.toString()) }
            val searchList = Array(list.size) { i -> "${list[i].cityName}, ${list[i].countryFull}" }
            Timber.d("Search query has been updated")
            searchingCities.postValue(searchList)
        }
    }

    private fun changeIsAddedCity(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            val city = repository.getCityByName(array[0], array[1])
            Timber.d("Loaded city by name")
            if (!city.isAdded) {
                city.isAdded = true
                repository.updateCity(city)
                Timber.d("The isAdded field updated: $city")
                addedCity.postValue(query)
            } else {
                Timber.d("The isAdded field is already true")
            }
        }
    }

//    fun deleteCitiesTable() {
//        viewModelScope.launch(Dispatchers.IO) {
//            Timber.d("Deleting cities database")
//            repository.deleteAllCities()
//            dbStatus.postValue(PagerStatus.DB_DELETED)
//        }
//    }

    fun parseAndInsertToDB(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
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

            repository.insertAllCities(citiesToDB)
            Timber.d("Parsed list of cities added to the database")
            dbStatus.postValue(PagerStatus.DB_CREATED)
        }
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
            locationResult?.let { currentLocation.value = locationResult.locations.first() }
        }
    }
}