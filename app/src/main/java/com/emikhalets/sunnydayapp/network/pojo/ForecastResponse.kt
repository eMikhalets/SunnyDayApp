package com.emikhalets.sunnydayapp.network.pojo

import com.emikhalets.sunnydayapp.data.WeatherData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("data") @Expose val data: List<WeatherData>,
    @SerializedName("lon") @Expose val longitude: String,
    @SerializedName("lat") @Expose val latitude: String,
    @SerializedName("timezone") @Expose val timezone: String,
    @SerializedName("country_code") @Expose val countryCode: String,
    @SerializedName("state_code") @Expose val stateCode: String,
    @SerializedName("error") @Expose val error: String
)