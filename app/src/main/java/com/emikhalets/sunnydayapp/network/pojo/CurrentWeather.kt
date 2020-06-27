package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("lat") @Expose val latitude: Double,
    @SerializedName("lon") @Expose val longitude: Double,
    @SerializedName("sunrise") @Expose val sunrise: String,
    @SerializedName("sunset") @Expose val sunset: String,
    @SerializedName("timezone") @Expose val timezone: String,
    @SerializedName("station") @Expose val station: String,
    @SerializedName("ob_time") @Expose val observationTime: String,
    @SerializedName("datetime") @Expose val datetime: String,
    @SerializedName("ts") @Expose val unixTime: Long,
    @SerializedName("city_name") @Expose val cityName: String,
    @SerializedName("country_code") @Expose val countryCode: String,
    @SerializedName("state_code") @Expose val stateCode: String,
    @SerializedName("pres") @Expose val pressure: Double,
    @SerializedName("slp") @Expose val seaLevelPres: Double,
    @SerializedName("wind_spd") @Expose val windSpeed: Double,
    @SerializedName("wind_dir") @Expose val windDir: Int,
    @SerializedName("wind_cdir") @Expose val windDirAbbr: String,
    @SerializedName("wind_cdir_full") @Expose val windDirFull: String,
    @SerializedName("temp") @Expose val temperature: Double,
    @SerializedName("app_temp") @Expose val tempFeelsLike: Double,
    @SerializedName("rh") @Expose val humidity: Int,
    @SerializedName("dewpt") @Expose val dewPoint: Double,
    @SerializedName("clouds") @Expose val clouds: Int,
    @SerializedName("pod") @Expose val partOfTheDay: String,
    @SerializedName("weather") @Expose val weather: WeatherDesc,
    @SerializedName("vis") @Expose val visibility : Double,
    @SerializedName("precip") @Expose val precipitation: Int,
    @SerializedName("snow") @Expose val snowfall: Int,
    @SerializedName("uv") @Expose val uvIndex: Double,
    @SerializedName("aqi") @Expose val aqi: Int,
    @SerializedName("dhi") @Expose val dhi: Double,
    @SerializedName("dni") @Expose val dni: Double,
    @SerializedName("ghi") @Expose val ghi: Double,
    @SerializedName("solar_rad") @Expose val solarRad: Double,
    @SerializedName("elev_angle") @Expose val elevationAngle: Int,
    @SerializedName("h_angle") @Expose val hourAngle: Int
)