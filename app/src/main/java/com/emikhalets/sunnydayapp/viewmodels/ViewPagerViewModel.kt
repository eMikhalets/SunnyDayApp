package com.emikhalets.sunnydayapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class ViewPagerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val citiesToDB = mutableListOf<City>()
    val cities = MutableLiveData<List<City>>()
    val searchingCities = MutableLiveData<Array<String>>()

    fun getAllCities() {
        viewModelScope.launch(Dispatchers.IO) {
            cities.postValue(repository.getAllCities())
        }
    }

    fun getCitiesByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getCitiesByName(name)
            val searchList = Array(list.size) { i -> "${list[i].cityName}, ${list[i].countryFull}" }
            searchingCities.postValue(searchList)
        }
    }

//    fun insertCity(cityName: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertCity(city)
//            getAllCities()
//        }
//    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
            getAllCities()
        }
    }

    fun parseAndInsertToDB(string: String) {
        val array = JSONArray(string)

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

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAllCities(citiesToDB)
        }
    }

//    fun parseAndInsertToDB(string: String) {
//        val array = string.split("\n")
//
//        for (i in 1 until array.size) {
//            val values = array[i].split(",")
//            val city = City(
//                cityId = values[0].toInt(),
//                cityName = values[1],
//                stateCode = values[2],
//                countryCode = values[3],
//                countryFull = values[4],
//                lat = values[5].toDouble(),
//                lon = values[6].toDouble(),
//                isAdded = false
//            )
//            citiesToDB.add(city)
//        }
//
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertAllCities(citiesToDB)
//        }
//    }

    override fun onCleared() {
        super.onCleared()
    }
}