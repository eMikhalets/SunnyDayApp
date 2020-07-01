package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseHourly(
    @SerializedName("data") @Expose val data: List<DataHourly>,
    @SerializedName("city_name") @Expose val cityName: String,
    @SerializedName("lon") @Expose val longitude: String,
    @SerializedName("timezone") @Expose val timezone: String,
    @SerializedName("lat") @Expose val latitude: String,
    @SerializedName("country_code") @Expose val countryCode: String,
    @SerializedName("state_code") @Expose val stateCode: String,
    @SerializedName("error") @Expose val error: String
)