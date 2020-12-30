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

fun setTempUnit(context: Context, view: TextView, value: Double, unit: String) {
    when (unit) {
        "F" -> view.text =
            context.getString(R.string.variable_text_temp_f, ((value * 9 / 5) + 32).toInt())
        "K" -> view.text =
            context.getString(R.string.variable_text_temp_k, (value + 273).toInt())
        else ->
            view.text = context.getString(R.string.variable_text_temp, value.toInt())
    }
}

fun setPressureUnit(context: Context, view: TextView, value: Double, unit: String) {
    when (unit) {
        "atm" -> view.text =
            context.getString(R.string.variable_text_pressure_atm, value / 1013)
        "pa" -> view.text =
            context.getString(R.string.variable_text_pressure_pa, value * 100)
        else -> view.text =
            context.getString(R.string.variable_text_pressure, value)
    }
}

fun setSpeedUnit(context: Context, view: TextView, value: Double, unit: String) {
    when (unit) {
        "kmh" -> view.text =
            context.getString(R.string.variable_text_wind_speed, value * 3.6)
        else -> view.text =
            context.getString(R.string.variable_text_wind_speed, value)
    }
}