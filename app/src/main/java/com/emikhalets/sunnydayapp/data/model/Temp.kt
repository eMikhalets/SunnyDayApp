package com.emikhalets.sunnydayapp.data.model

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    // evening
    val eve: Double,
    // morning
    val morn: Double
)
