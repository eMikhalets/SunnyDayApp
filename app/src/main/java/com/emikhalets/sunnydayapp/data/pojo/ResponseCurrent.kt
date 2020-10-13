package com.emikhalets.sunnydayapp.data.pojo

import com.emikhalets.sunnydayapp.data.pojo.DataCurrent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseCurrent(
    @SerializedName("data") @Expose val data: List<DataCurrent>,
    @SerializedName("count") @Expose val count: Int,
    @SerializedName("error") @Expose val error: String
)