package com.emikhalets.sunnydayapp.data.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("error") @Expose val error: String
)