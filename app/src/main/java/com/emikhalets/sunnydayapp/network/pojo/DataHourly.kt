package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataHourly(
    @SerializedName("wind_cdir") @Expose val windDirAbbr: String,
    @SerializedName("rh") @Expose val humidity: Int,
    @SerializedName("timestamp_utc") @Expose val timeUtc: String,
    @SerializedName("pod") @Expose val partOfTheDay: String,
    @SerializedName("pres") @Expose val pressure: Double,
    @SerializedName("solar_rad") @Expose val solarRad: Double,
    @SerializedName("ozone") @Expose val ozone: Double,
    @SerializedName("weather") @Expose val weather: WeatherForecast,
    @SerializedName("wind_gust_spd") @Expose val windGustSpeed: Double,
    @SerializedName("timestamp_local") @Expose val timeLocal: String,
    @SerializedName("snow_depth") @Expose val snowDepth: Int,
    @SerializedName("clouds") @Expose val clouds: Int,
    @SerializedName("ts") @Expose val timestamp: Long,
    @SerializedName("wind_spd") @Expose val windSpeed: Double,
    @SerializedName("pop") @Expose val precipProb: Int,
    @SerializedName("wind_cdir_full") @Expose val windDirFull: String,
    @SerializedName("slp") @Expose val pressureSeaLevel: Double,
    @SerializedName("dni") @Expose val dni: Double,
    @SerializedName("dewpt") @Expose val dewPoint: Double,
    @SerializedName("snow") @Expose val snowfall: Int,
    @SerializedName("uv") @Expose val uvIndex: Double,
    @SerializedName("wind_dir") @Expose val windDir: Int,
    @SerializedName("clouds_hi") @Expose val cloudsHigh: Int,
    @SerializedName("precip") @Expose val precip: Double,
    @SerializedName("vis") @Expose val visibility: Double,
    @SerializedName("dhi") @Expose val dhi: Double,
    @SerializedName("app_temp") @Expose val tempFeelsLike: Double,
    @SerializedName("datetime") @Expose val datetime: String,
    @SerializedName("temp") @Expose val temperature: Double,
    @SerializedName("ghi") @Expose val ghi: Double,
    @SerializedName("clouds_mid") @Expose val cloudsMid: Int,
    @SerializedName("clouds_low") @Expose val cloudsLow: Int
)