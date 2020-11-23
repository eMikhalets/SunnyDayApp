package com.emikhalets.sunnydayapp.utils.status

data class WeatherState<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        WEATHER,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> weather(data: T): WeatherState<T> =
            WeatherState(Status.WEATHER, data, null)

        fun <T> error(message: String): WeatherState<T> =
            WeatherState(Status.ERROR, null, message)
    }
}