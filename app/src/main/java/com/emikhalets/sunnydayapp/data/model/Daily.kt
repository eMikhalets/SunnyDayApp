package com.emikhalets.sunnydayapp.data.model

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val feels_like: FellsLike,
    val pressure: Double,
    val humidity: Double,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Double,
    val weather: List<Weather>,
    val clouds: Double,
    val pop: Double,
    val uvi: Double
)
