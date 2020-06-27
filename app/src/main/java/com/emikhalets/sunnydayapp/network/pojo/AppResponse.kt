package com.emikhalets.sunnydayapp.network.pojo

import com.emikhalets.sunnydayapp.data.WeatherData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppResponse(
    @SerializedName("data") @Expose val data: List<WeatherData>,
    @SerializedName("error") @Expose val longitude: String
)