package com.emikhalets.sunnydayapp.data.api

sealed class ApiResult<out T> {
    data class Success<out T>(val result: T) : ApiResult<T>()
    data class Error(val msg: String, val exception: Exception? = null) : ApiResult<Nothing>()
}