package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.widget.TextView
import android.widget.Toast
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.api.ApiResult
import com.emikhalets.sunnydayapp.data.database.DbResult
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Conf {
    var lang = "en"
    var units = "metric"
}

object Keys {
    init {
        System.loadLibrary("keys")
    }

    external fun getApiKey(): String
}

fun buildIconUrl(icon: String) = "http://openweathermap.org/img/wn/$icon@2x.png"

fun getCityFromLocation(context: Context, location: Location): String {
    val address = Geocoder(context, Locale.getDefault()).getFromLocation(
        location.latitude,
        location.longitude,
        1
    ).first()
    return "${address.locality}, ${address.countryCode}"
}

suspend fun <T> safeNetworkCall(call: suspend () -> T): ApiResult<T> =
    try {
        val result = call.invoke()
        ApiResult.Success(result)
    } catch (ex: Exception) {
        ex.printStackTrace()
        ApiResult.Error(ex.message ?: "", ex)
    }

suspend fun <T> safeDatabaseCall(call: suspend () -> T): DbResult<T> =
    try {
        val result = call.invoke()
        DbResult.Success(result)
    } catch (ex: Exception) {
        ex.printStackTrace()
        DbResult.Error(ex.message ?: "", ex)
    }

fun setTemperature(context: Context, view: TextView, value: Int) {
    when (Conf.units) {
        "imperial" -> view.text = context.getString(R.string.units_temp_i, value)
        else -> view.text = context.getString(R.string.units_temp_m, value)
    }
}

fun setFeelsLike(context: Context, view: TextView, value: Int) {
    when (Conf.units) {
        "imperial" -> view.text = context.getString(R.string.units_feels_like_i, value)
        else -> view.text = context.getString(R.string.units_feels_like_m, value)
    }
}

fun setTemperatureUnit(context: Context, view: TextView) {
    when (Conf.units) {
        "imperial" -> view.text = context.getString(R.string.units_temp_unit_i)
        else -> view.text = context.getString(R.string.units_temp_unit_m)
    }
}

fun setWindSpeed(context: Context, view: TextView, value: Int) {
    when (Conf.units) {
        "imperial" -> view.text = context.getString(R.string.units_speed_i, value)
        else -> view.text = context.getString(R.string.units_speed_m, value)
    }
}

fun formatDate(timestamp: Long, timezone: String): String {
    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp * 1000),
        ZoneId.of(timezone)
    )
    return date.format(DateTimeFormatter.ofPattern("E, d MMM"))
}

fun formatDateWithWeek(timestamp: Long, timezone: String): String {
    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp * 1000),
        ZoneId.of(timezone)
    )
    return date.format(DateTimeFormatter.ofPattern("E, d MMM y"))
}

fun formatTime(timestamp: Long, timezone: String): String {
    val time = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp * 1000),
        ZoneId.of(timezone)
    )
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
}