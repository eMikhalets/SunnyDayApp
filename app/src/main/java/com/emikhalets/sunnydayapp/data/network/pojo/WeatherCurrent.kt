package com.emikhalets.sunnydayapp.data.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherCurrent(
    @SerializedName("icon") @Expose val icon: String,
    @SerializedName("code") @Expose val code: String,
    @SerializedName("description") @Expose val description: String
)