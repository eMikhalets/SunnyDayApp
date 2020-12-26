package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.widget.TextView
import com.emikhalets.sunnydayapp.R

fun buildIconUrl(icon: String) = "https://www.weatherbit.io/static/img/icons/$icon.png"

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