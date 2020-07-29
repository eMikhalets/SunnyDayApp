package com.emikhalets.sunnydayapp.utils

import androidx.lifecycle.MutableLiveData

/**
 * Constants used throughout the app
 */

// TODO: hide api key
const val API_KEY = "e1a0feec25754f7aa615945766b156b6"

var CURRENT_QUERY = MutableLiveData<String>()
var ADDED_CITY = MutableLiveData<String>()

fun buildIconUrl(icon: String) = "https://www.weatherbit.io/static/img/icons/$icon.png"