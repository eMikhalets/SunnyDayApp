package com.emikhalets.sunnydayapp.ui.citylist

data class CitiesState<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        CITIES,
        EMPTY
    }

    companion object {
        fun <T> cities(data: T?): CitiesState<T> =
            CitiesState(Status.CITIES, data, null)

        fun <T> empty(): CitiesState<T> =
            CitiesState(Status.EMPTY, null, null)
    }
}