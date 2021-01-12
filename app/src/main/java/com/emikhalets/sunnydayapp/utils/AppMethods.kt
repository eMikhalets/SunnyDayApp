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

fun setTempUnit(context: Context, view: TextView, value: Int, units: String) {
    when (units) {
        "metric" -> view.text = context.getString(R.string.variable_text_temp_m, value)
        "imperial" -> view.text = context.getString(R.string.variable_text_temp_i, value)
        else -> view.text = context.getString(R.string.variable_text_temp_m, value)
    }
}

fun setWindUnit(context: Context, view: TextView, value: Int, units: String) {
    when (units) {
        "metric" -> view.text = context.getString(R.string.variable_text_wind_speed_m, value)
        "imperial" -> view.text = context.getString(R.string.variable_text_wind_speed_i, value)
        else -> view.text = context.getString(R.string.variable_text_wind_speed_m, value)
    }
}