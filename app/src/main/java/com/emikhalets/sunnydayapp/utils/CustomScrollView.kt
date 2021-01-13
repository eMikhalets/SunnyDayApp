package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import androidx.annotation.Nullable
import androidx.viewpager2.widget.ViewPager2


class CustomScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr, defStyleRes) {

    var viewPager2: ViewPager2? = null

    var startY = 0f
    var dy = 0f

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        viewPager2?.isUserInputEnabled = scrollY <= 15
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            startY = ev.rawY
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> startY = ev.rawY
            MotionEvent.ACTION_MOVE -> {
                dy = ev.rawY - startY
                if (dy > -5 && dy < 70) { // range tested on 5 devices but can be modified
                    return false
                } else {
                    viewPager2?.isUserInputEnabled = false
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    fun attachViewPager(vp: ViewPager2) {
        viewPager2 = vp
    }

    fun detachViewPager() {
        viewPager2 = null
    }
}