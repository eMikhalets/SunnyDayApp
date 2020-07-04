package com.emikhalets.sunnydayapp.utils

import androidx.lifecycle.MutableLiveData

/**
 * Constants used throughout the app
 */

const val API_KEY = "e1a0feec25754f7aa615945766b156b6"

const val QUERY_LANG = "ru"
const val QUERY_UNITS = "m"

var CURRENT_QUERY = MutableLiveData<String>()

fun buildIconUrl(icon: String) = "https://www.weatherbit.io/static/img/icons/$icon.png"