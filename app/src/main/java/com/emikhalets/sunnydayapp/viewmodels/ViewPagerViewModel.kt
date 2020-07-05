package com.emikhalets.sunnydayapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.utils.ADDED_CITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class ViewPagerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val citiesToDB = mutableListOf<City>()
    val searchingCities = MutableLiveData<Array<String>>()

    fun getCitiesByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getCitiesByName(name)
            val searchList = Array(list.size) { i -> "${list[i].cityName}, ${list[i].countryFull}" }
            searchingCities.postValue(searchList)
        }
    }

    fun insertCity(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            val city = repository.getCityByName(array[0], array[1])
            city.isAdded = true
            repository.updateCity(city)
            ADDED_CITY.postValue(query)
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
}