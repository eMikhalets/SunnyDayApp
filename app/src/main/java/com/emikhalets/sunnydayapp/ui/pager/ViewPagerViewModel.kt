package com.emikhalets.sunnydayapp.ui.pager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.PagerRepository
import com.emikhalets.sunnydayapp.data.database.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber

class ViewPagerViewModel : ViewModel() {

    private val repository = PagerRepository()
    private val citiesToDB = mutableListOf<City>()

    private var _searchingCities = MutableLiveData<Array<String>>()
    val searchingCities get() = _searchingCities

    private var _currentQuery = MutableLiveData<String>()
    val currentQuery get() = _currentQuery

    private var _addedCity = MutableLiveData<String>()
    val addedCity get() = _addedCity

    fun updateCurrentQuery(query: String) {
        _currentQuery.postValue(query)
        Timber.d("Query updated: $query")
    }

    fun getCitiesByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getCitiesByName(name)
            val searchList = Array(list.size) { i -> "${list[i].cityName}, ${list[i].countryFull}" }
            _searchingCities.postValue(searchList)
        }
    }

    fun changeIsAddedCity(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val array = query.split(", ")
            val city = repository.getCityByName(array[0], array[1])

            if (!city.isAdded) {
                city.isAdded = true
                repository.updateCity(city)
                Timber.d("City isAdded status updated: $city")
                _addedCity.postValue(query)
            }
        }
    }

    // TODO: make async
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

        Timber.d(citiesToDB.toString())

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAllCities(citiesToDB)
        }
    }
}