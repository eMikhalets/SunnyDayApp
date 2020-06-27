package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ForecastWeather(
    @SerializedName("valid_date") @Expose val date: String,
    @SerializedName("ts") @Expose val timestamp: Long,
    @SerializedName("wind_gust_spd") @Expose val windGustSpeed: Double,
    @SerializedName("wind_spd") @Expose val windSpeed: Double,
    @SerializedName("wind_dir") @Expose val windDir : Int,
    @SerializedName("wind_cdir") @Expose val windDirAbbr: String,
    @SerializedName("wind_cdir_full") @Expose val windDirFull: String,
    @SerializedName("temp") @Expose val temperature : Double,
    @SerializedName("max_temp") @Expose val temperatureMax: Double,
    @SerializedName("min_temp") @Expose val temperatureMin: Double,
    @SerializedName("high_temp") @Expose val temperatureHigh: Double,
    @SerializedName("low_temp") @Expose val temperatureLow: Double,
    @SerializedName("app_max_temp") @Expose val tempFeelsLikeMax: Double,
    @SerializedName("app_min_temp") @Expose val tempFeelsLikeMin: Double,
    @SerializedName("pop") @Expose val precipProb: Int,
    @SerializedName("precip") @Expose val precip: Double,
    @SerializedName("snow") @Expose val snowfall: Int,
    @SerializedName("snow_depth") @Expose val snowDepth: Int,
    @SerializedName("pres") @Expose val pressure: Double,
    @SerializedName("slp") @Expose val pressureSeaLevel: Double,
    @SerializedName("dewpt") @Expose val dewPoint: Double,
    @SerializedName("rh") @Expose val humidity: Int,
    @SerializedName("weather") @Expose val weather: WeatherDesc,
    @SerializedName("clouds_low") @Expose val cloudsLow: Int,
    @SerializedName("clouds_mid") @Expose val cloudsMid: Int,
    @SerializedName("clouds_hi") @Expose val cloudsHigh: Int,
    @SerializedName("clouds") @Expose val clouds: Int,
    @SerializedName("vis") @Expose val visibility: Int,
    @SerializedName("uv") @Expose val uvIndex: Double,
    @SerializedName("ozone") @Expose val ozone: Double,
    @SerializedName("moon_phase") @Expose val moonPhase: Double,
    @SerializedName("moon_phase_lunation") @Expose val moonPhaseLunation: Double,
    @SerializedName("moonrise_ts") @Expose val moonriseTime: Long,
    @SerializedName("moonset_ts") @Expose val moonsetTime: Long,
    @SerializedName("sunset_ts") @Expose val sunsetTime: Long,
    @SerializedName("sunrise_ts") @Expose val sunriseTime: Long
)