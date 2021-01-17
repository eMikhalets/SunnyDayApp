package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.widget.TextView
import com.emikhalets.sunnydayapp.R
import java.util.*

fun buildIconUrl(icon: String) = "http://openweathermap.org/img/wn/$icon@2x.png"

fun getCityFromLocation(context: Context, location: Location): String {
    val address = Geocoder(context, Locale.getDefault()).getFromLocation(
        location.latitude,
        location.longitude,
        1
    ).first()
    return "${address.locality}, ${address.countryCode}"
}

fun setTemperature(context: Context, view: TextView, value: Int, units: String) {
    when (units) {
        "imperial" -> view.text = context.getString(R.string.units_temp_i, value)
        else -> view.text = context.getString(R.string.units_temp_m, value)
    }
}

fun setFeelsLike(context: Context, view: TextView, value: Int, units: String) {
    when (units) {
        "imperial" -> view.text = context.getString(R.string.units_feels_like_i, value)
        else -> view.text = context.getString(R.string.units_feels_like_m, value)
    }
}

fun setTemperatureUnit(context: Context, view: TextView, units: String) {
    when (units) {
        "imperial" -> view.text = context.getString(R.string.units_temp_unit_i)
        else -> view.text = context.getString(R.string.units_temp_unit_m)
    }
}

fun setWindSpeed(context: Context, view: TextView, value: Int, units: String) {
    when (units) {
        "imperial" -> view.text = context.getString(R.string.units_speed_i, value)
        else -> view.text = context.getString(R.string.units_speed_m, value)
    }
}