package com.emikhalets.sunnydayapp.ui.weather

data class WeatherState<out T>(val status: Status, val data: T?, val error: String?) {

    enum class Status {
        WEATHER,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> loading(): WeatherState<T> = WeatherState(Status.LOADING, null, null)
        fun <T> weather(data: T): WeatherState<T> = WeatherState(Status.WEATHER, data, null)
        fun <T> error(error: String?): WeatherState<T> = WeatherState(Status.ERROR, null, error)
    }
}