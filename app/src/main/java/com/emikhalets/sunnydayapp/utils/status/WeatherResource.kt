package com.emikhalets.sunnydayapp.utils.status

data class WeatherResource<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        WEATHER,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> weather(data: T): WeatherResource<T> =
            WeatherResource(Status.WEATHER, data, null)

        fun <T> error(message: String): WeatherResource<T> =
            WeatherResource(Status.ERROR, null, message)
    }
}