package com.emikhalets.sunnydayapp.data.api

import com.emikhalets.sunnydayapp.data.pojo.ResponseError

sealed class AppResponse<out T> {
    data class Success<out T>(val response: T) : AppResponse<T>()
    data class Error(val code: Int? = null, val error: ResponseError? = null) : AppResponse<Nothing>()
    object NetworkError : AppResponse<Nothing>()
}