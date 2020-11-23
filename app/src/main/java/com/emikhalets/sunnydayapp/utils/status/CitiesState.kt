package com.emikhalets.sunnydayapp.utils.status

data class CitiesState<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        CITIES,
        EMPTY,
        LOADING
    }

    companion object {
        fun <T> cities(data: T?): CitiesState<T> =
            CitiesState(Status.CITIES, data, null)

        fun <T> empty(): CitiesState<T> =
            CitiesState(Status.EMPTY, null, null)
    }
}