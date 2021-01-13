package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class ObservableScrollView(context: Context, attrs: AttributeSet) :
    HorizontalScrollView(context, attrs) {

    private var mOnScrollChangedListener: OnScrollChangedListener? = null

    fun setOnScrollChangedListener(l: OnScrollChangedListener?) {
        mOnScrollChangedListener = l
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener!!.onScrollChanged(this, l, t)
        }
    }

    /**
     * Interface definition for a callback to be invoked with the scroll
     * position changes.
     */
    interface OnScrollChangedListener {
        /**
         * Called when the scroll position of `view` changes.
         *
         * @param view The view whose scroll position changed.
         * @param l Current horizontal scroll origin.
         * @param t Current vertical scroll origin.
         */
        fun onScrollChanged(view: ObservableScrollView, l: Int, t: Int)
    }
}