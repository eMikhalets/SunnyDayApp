package com.emikhalets.sunnydayapp.data.model

data class Hourly(
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Double,
    val humidity: Double,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Double,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Double,
    val weather: List<Weather>,
    val pop: Double
)
