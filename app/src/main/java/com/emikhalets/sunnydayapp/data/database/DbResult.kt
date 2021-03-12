package com.emikhalets.sunnydayapp.data.database

sealed class DbResult<out T> {
    data class Success<out T>(val result: T) : DbResult<T>()
    data class Error(val msg: String, val exception: Exception? = null) : DbResult<Nothing>()
}