package com.emikhalets.sunnydayapp.utils

import android.widget.Toast
import com.emikhalets.sunnydayapp.SunnyDayApp

class ToastBuilder {

    companion object {

        private val context = SunnyDayApp().applicationContext

        fun build(text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}