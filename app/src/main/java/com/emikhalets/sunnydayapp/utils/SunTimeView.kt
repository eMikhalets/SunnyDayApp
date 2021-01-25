package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class SunTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

//    private var sunCanvas: Canvas? = null

    private var density = 0f
    private var sunrise: String = ""
    private var sundown: String = ""

    init {
        density = resources.displayMetrics.density

        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 2 * density
        linePaint.style = Paint.Style.STROKE

//        sunPaint.color = Color.BLACK

        textPaint.color = Color.BLACK
        textPaint.textSize = 18 * density
        textPaint.textAlign = Paint.Align.CENTER

//        val sunBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_sun)
//        sunCanvas = Canvas(sunBitmap.copy(Bitmap.Config.ARGB_8888, true))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = (150 * density).toInt()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawView(canvas)
    }

    private fun drawView(canvas: Canvas) {
        val offset = 20 * density

        val oval = RectF(
            offset * 2,
            offset,
            width - offset * 2,
            (height - offset) * 2
        )
        canvas.drawArc(oval, 180f, 180f, false, linePaint)

        canvas.drawLine(
            offset,
            height - offset,
            width - offset,
            height - offset,
            linePaint
        )

        canvas.drawText(sunrise, offset, height - offset, textPaint)
        canvas.drawText(sundown, width - offset, height - offset, textPaint)
    }

    fun setTime(sunrise: String, sundown: String) {
        this.sunrise = sunrise
        this.sundown = sundown
        invalidate()
    }
}