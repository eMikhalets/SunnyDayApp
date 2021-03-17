package com.emikhalets.sunnydayapp.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.widget.TextView
import androidx.core.content.ContextCompat
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

fun setTemperature(context: Context, view: TextView, value: Int) {
    when (RequestConfig.units) {
        "imperial" -> view.text = context.getString(R.string.units_temp_i, value)
        else -> view.text = context.getString(R.string.units_temp_m, value)
    }
}

fun setFeelsLike(context: Context, view: TextView, value: Int) {
    when (RequestConfig.units) {
        "imperial" -> view.text = context.getString(R.string.units_feels_like_i, value)
        else -> view.text = context.getString(R.string.units_feels_like_m, value)
    }
}

fun setTemperatureUnit(context: Context, view: TextView) {
    when (RequestConfig.units) {
        "imperial" -> view.text = context.getString(R.string.units_temp_unit_i)
        else -> view.text = context.getString(R.string.units_temp_unit_m)
    }
}

fun setWindSpeed(context: Context, view: TextView, value: Int) {
    when (RequestConfig.units) {
        "imperial" -> view.text = context.getString(R.string.units_speed_i, value)
        else -> view.text = context.getString(R.string.units_speed_m, value)
    }
}

fun getBackgroundColor(context: Context, weather: String): Int {
    return when (weather) {
        "01d" -> ContextCompat.getColor(context, R.color.colorPrimaryClear)
        "01n" -> ContextCompat.getColor(context, R.color.colorPrimaryClear)
        "02d" -> ContextCompat.getColor(context, R.color.colorPrimaryClouds)
        "02n" -> ContextCompat.getColor(context, R.color.colorPrimaryClouds)
        "03d" -> ContextCompat.getColor(context, R.color.colorPrimaryClouds)
        "03n" -> ContextCompat.getColor(context, R.color.colorPrimaryClouds)
        "04d" -> ContextCompat.getColor(context, R.color.colorPrimaryClouds)
        "04n" -> ContextCompat.getColor(context, R.color.colorPrimaryClouds)
        "09d" -> ContextCompat.getColor(context, R.color.colorPrimaryRain)
        "09n" -> ContextCompat.getColor(context, R.color.colorPrimaryRain)
        "10d" -> ContextCompat.getColor(context, R.color.colorPrimaryRain)
        "10n" -> ContextCompat.getColor(context, R.color.colorPrimaryRain)
        "11d" -> ContextCompat.getColor(context, R.color.colorPrimaryStorm)
        "11n" -> ContextCompat.getColor(context, R.color.colorPrimaryStorm)
        "13d" -> ContextCompat.getColor(context, R.color.colorPrimarySnow)
        "13n" -> ContextCompat.getColor(context, R.color.colorPrimarySnow)
        "50d" -> ContextCompat.getColor(context, R.color.colorPrimaryMist)
        "50n" -> ContextCompat.getColor(context, R.color.colorPrimaryMist)
        else -> -1
    }
}

fun setLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = activity.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}