package com.emikhalets.sunnydayapp.utils

data class FragmentState<out T>(val status: Status, val data: T?, val error: String?) {

    enum class Status {
        LOADING,
        LOADED,
        ERROR
    }

    companion object {
        fun <T> loading(): FragmentState<T> = FragmentState(Status.LOADING, null, null)
        fun <T> loaded(data: T): FragmentState<T> = FragmentState(Status.LOADED, data, null)
        fun <T> error(error: String?): FragmentState<T> = FragmentState(Status.ERROR, null, error)
    }
}