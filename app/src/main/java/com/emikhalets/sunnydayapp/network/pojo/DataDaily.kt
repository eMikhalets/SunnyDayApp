package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataDaily(
    @SerializedName("moonrise_ts") @Expose val moonriseTime: Long,
    @SerializedName("wind_cdir") @Expose val windDirAbbr: String,
    @SerializedName("rh") @Expose val humidity: Int,
    @SerializedName("pres") @Expose val pressure: Double,
    @SerializedName("high_temp") @Expose val temperatureHigh: Double,
    @SerializedName("sunset_ts") @Expose val sunsetTime: Long,
    @SerializedName("ozone") @Expose val ozone: Double,
    @SerializedName("moon_phase") @Expose val moonPhase: Double,
    @SerializedName("wind_gust_spd") @Expose val windGustSpeed: Double,
    @SerializedName("snow_depth") @Expose val snowDepth: Int,
    @SerializedName("clouds") @Expose val clouds: Int,
    @SerializedName("ts") @Expose val timestamp: Long,
    @SerializedName("sunrise_ts") @Expose val sunriseTime: Long,
    @SerializedName("app_min_temp") @Expose val tempFeelsLikeMin: Double,
    @SerializedName("wind_spd") @Expose val windSpeed: Double,
    @SerializedName("pop") @Expose val precipProb: Int,
    @SerializedName("wind_cdir_full") @Expose val windDirFull: String,
    @SerializedName("slp") @Expose val pressureSeaLevel: Double,
    @SerializedName("moon_phase_lunation") @Expose val moonPhaseLunation: Double,
    @SerializedName("valid_date") @Expose val date: String,
    @SerializedName("app_max_temp") @Expose val tempFeelsLikeMax: Double,
    @SerializedName("vis") @Expose val visibility: Double,
    @SerializedName("dewpt") @Expose val dewPoint: Double,
    @SerializedName("snow") @Expose val snowfall: Int,
    @SerializedName("uv") @Expose val uvIndex: Double,
    @SerializedName("weather") @Expose val weather: WeatherForecast,
    @SerializedName("wind_dir") @Expose val windDir : Int,
    @SerializedName("clouds_hi") @Expose val cloudsHigh: Int,
    @SerializedName("precip") @Expose val precip: Double,
    @SerializedName("low_temp") @Expose val temperatureLow: Double,
    @SerializedName("max_temp") @Expose val temperatureMax: Double,
    @SerializedName("moonset_ts") @Expose val moonsetTime: Long,
    @SerializedName("temp") @Expose val temperature : Double,
    @SerializedName("min_temp") @Expose val temperatureMin: Double,
    @SerializedName("clouds_mid") @Expose val cloudsMid: Int,
    @SerializedName("clouds_low") @Expose val cloudsLow: Int
)