package com.emikhalets.sunnydayapp.utils

import android.view.MotionEvent
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView

open class CustomItemTouchListener : RecyclerView.OnItemTouchListener {

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return onInterceptTouchEvent(rv, e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}

open class CustomSearchQueryListener : SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return onQueryTextChange(newText)
    }
}

interface OnLocationSettingsClick {
    fun onLocationSettingsClick()
}

interface OnThemeListener {
    fun onThemeChange(isNight: Boolean)
}