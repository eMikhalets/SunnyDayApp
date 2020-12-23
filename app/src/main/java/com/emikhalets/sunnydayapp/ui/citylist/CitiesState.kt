package com.emikhalets.sunnydayapp.ui.citylist

data class CitiesState<out T>(val status: Status, val data: T?, val error: String?) {

    enum class Status {
        LOADING,
        CITIES,
        EMPTY,
        ERROR
    }

    companion object {
        fun <T> loading(): CitiesState<T> = CitiesState(Status.LOADING, null, null)
        fun <T> empty(): CitiesState<T> = CitiesState(Status.EMPTY, null, null)
        fun <T> cities(data: T?): CitiesState<T> = CitiesState(Status.CITIES, data, null)
        fun <T> error(error: String?): CitiesState<T> = CitiesState(Status.ERROR, null, error)
    }
}