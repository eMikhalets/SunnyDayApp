package com.emikhalets.sunnydayapp.data.model

import com.google.gson.annotations.SerializedName

data class FellsLike(
    @SerializedName("day")
    val day: Double,
    @SerializedName("night")
    val night: Double,
    // evening
    @SerializedName("eve")
    val eve: Double,
    // morning
    @SerializedName("morn")
    val morn: Double
) {
    val averageFeelsLike: Int
        get() = (morn + day + eve + night).toInt() / 4
}
