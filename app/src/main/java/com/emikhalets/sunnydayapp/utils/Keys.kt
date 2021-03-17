package com.emikhalets.sunnydayapp.utils

object Keys {
    init {
        System.loadLibrary("keys")
    }

    external fun getApiKey(): String
}