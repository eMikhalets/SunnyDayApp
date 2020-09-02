package com.emikhalets.sunnydayapp.network.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseUsage(
    @SerializedName("calls_remaining") @Expose val callsRemaining: Double,
    @SerializedName("historical_calls_count") @Expose val historicalCallsCount: Double,
    @SerializedName("calls_count") @Expose val callsCount: Double,
    @SerializedName("calls_reset_ts") @Expose val callsResetTs: Double,
    @SerializedName("historical_calls_reset_ts") @Expose val historicalCallsResetTs: Double,
    @SerializedName("historical_calls_remaining") @Expose val historicalCallsRemaining: Double
)