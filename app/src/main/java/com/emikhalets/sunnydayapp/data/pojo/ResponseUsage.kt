package com.emikhalets.sunnydayapp.data.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseUsage(
    @SerializedName("calls_count") @Expose val callsCount: Double,
    @SerializedName("calls_remaining") @Expose val callsRemaining: Double,
    @SerializedName("calls_reset_ts") @Expose val callsResetTs: Long,
    @SerializedName("historical_calls_count") @Expose val historicalCallsCount: Double,
    @SerializedName("historical_calls_remaining") @Expose val historicalCallsRemaining: Double,
    @SerializedName("historical_calls_reset_ts") @Expose val historicalCallsResetTs: Long
)