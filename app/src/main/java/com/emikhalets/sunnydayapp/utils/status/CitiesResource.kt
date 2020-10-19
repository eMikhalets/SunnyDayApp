package com.emikhalets.sunnydayapp.utils.status

data class CitiesResource<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        CITIES,
        EMPTY,
        LOADING
    }

    companion object {
        fun <T> cities(data: T?): CitiesResource<T> =
            CitiesResource(Status.CITIES, data, null)

        fun <T> empty(): CitiesResource<T> =
            CitiesResource(Status.EMPTY, null, null)
    }
}