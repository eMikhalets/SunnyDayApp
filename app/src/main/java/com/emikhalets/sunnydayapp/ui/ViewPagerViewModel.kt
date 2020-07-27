package com.emikhalets.sunnydayapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.AppRepository
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.utils.ADDED_CITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber

class ViewPagerViewModel : ViewModel() {

    private val repository = AppRepository()
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
            Timber.d("City before isAdded = ${city.isAdded}")

            if (!city.isAdded) {
                city.isAdded = true
                Timber.d("City after isAdded = ${city.isAdded}")
                repository.updateCity(city)
                Timber.d("City isAdded status updated: $city")
                ADDED_CITY.postValue(query)
            }
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