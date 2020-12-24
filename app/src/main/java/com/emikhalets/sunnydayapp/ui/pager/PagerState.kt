package com.emikhalets.sunnydayapp.ui.pager

data class PagerState<out T>(val status: Status, val data: T?, val error: String?) {

    enum class Status {
        DB_CREATING,
        DB_CREATED,
        DB_DELETED
    }

//    companion object {
//        fun <T> creating(): PagerState<T> = PagerState(Status.DB_CREATING, null, null)
//        fun <T> created(data: T): PagerState<T> = PagerState(Status.DB_CREATED, data, null)
//        fun <T> deleted(error: String?): PagerState<T> = PagerState(Status.DB_DELETED, null, error)
//    }
}