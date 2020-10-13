package com.emikhalets.sunnydayapp.data.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataCurrent(
    @SerializedName("rh") @Expose val humidity: Double,
    @SerializedName("pod") @Expose val partOfTheDay: String,
    @SerializedName("lon") @Expose val longitude: Double,
    @SerializedName("pres") @Expose val pressure: Double,
    @SerializedName("timezone") @Expose val timezone: String,
    @SerializedName("ob_time") @Expose val observationTime: String,
    @SerializedName("country_code") @Expose val countryCode: String,
    @SerializedName("clouds") @Expose val clouds: Double,
    @SerializedName("ts") @Expose val timestamp: Long,
    @SerializedName("solar_rad") @Expose val solarRad: Double,
    @SerializedName("state_code") @Expose val stateCode: String,
    @SerializedName("city_name") @Expose val cityName: String,
    @SerializedName("wind_spd") @Expose val windSpeed: Double,
    @SerializedName("wind_cdir_full") @Expose val windDirFull: String,
    @SerializedName("wind_cdir") @Expose val windDirAbbr: String,
    @SerializedName("slp") @Expose val seaLevelPres: Double,
    @SerializedName("vis") @Expose val visibility: Double,
    @SerializedName("h_angle") @Expose val hourAngle: Double,
    @SerializedName("sunset") @Expose val sunset: String,
    @SerializedName("dni") @Expose val dni: Double,
    @SerializedName("dewpt") @Expose val dewPoint: Double,
    @SerializedName("snow") @Expose val snowfall: Double,
    @SerializedName("uv") @Expose val uvIndex: Double,
    @SerializedName("precip") @Expose val precipitation: Double,
    @SerializedName("wind_dir") @Expose val windDir: Double,
    @SerializedName("sunrise") @Expose val sunrise: String,
    @SerializedName("ghi") @Expose val ghi: Double,
    @SerializedName("dhi") @Expose val dhi: Double,
    @SerializedName("aqi") @Expose val aqi: Double,
    @SerializedName("lat") @Expose val latitude: Double,
    @SerializedName("weather") @Expose val weather: WeatherCurrent,
    @SerializedName("datetime") @Expose val datetime: String,
    @SerializedName("temp") @Expose val temperature: Double,
    @SerializedName("station") @Expose val station: String,
    @SerializedName("elev_angle") @Expose val elevationAngle: Double,
    @SerializedName("app_temp") @Expose val tempFeelsLike: Double
) : Serializable