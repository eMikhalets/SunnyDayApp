package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherDesc(
    @SerializedName("icon") @Expose val icon: String,
    @SerializedName("code") @Expose val code: Int,
    @SerializedName("description") @Expose val description: String
)