package com.emikhalets.sunnydayapp.ui.citylist

data class CitiesState<out T>(val status: Status, val data: T?, val error: String?) {

    enum class Status {
        LOADING,
        CITIES,
        EMPTY,
        ERROR
    }

    companion object {
        fun <T> loading(): ForecastState<T> = ForecastState(Status.LOADING, null, null)
        fun <T> empty(): ForecastState<T> = ForecastState(Status.EMPTY, null, null)
        fun <T> cities(data: T?): ForecastState<T> = ForecastState(Status.CITIES, data, null)
        fun <T> error(error: String?): ForecastState<T> = ForecastState(Status.ERROR, null, error)
    }
}