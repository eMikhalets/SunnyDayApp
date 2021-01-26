package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.emikhalets.sunnydayapp.R


class SunTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var image: Int = 0
    private var textSize: Float = 0f

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var sunDrawable: Drawable? = null

    private var offset = 0f
    private var density = 0f
    private var now: String = ""
    private var sunrise: String = ""
    private var sundown: String = ""

    init {
        context.withStyledAttributes(attrs, R.styleable.SunTimeView) {
            density = resources.displayMetrics.density
            image = getResourceId(R.styleable.SunTimeView_android_src, 0)
            textSize = getDimension(R.styleable.SunTimeView_android_textSize, 14 * density)
        }

        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 2 * density
        linePaint.style = Paint.Style.STROKE

        textPaint.color = Color.BLACK
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = (150 * density).toInt()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        offset = 20 * density
        drawLines(canvas)
        drawText(canvas)
        drawImage(canvas)
    }

    private fun drawLines(canvas: Canvas) {
        val lineHeight = height - offset
        val oval = RectF(
            offset * 4,
            offset * 2,
            width - offset * 4,
            (height - offset * 2) * 2
        )

        canvas.drawArc(oval, 180f, 180f, false, linePaint)
        canvas.drawLine(offset, lineHeight, width - offset, lineHeight, linePaint)
    }

    private fun drawText(canvas: Canvas) {
        if (now == "") now = "14:00"
        if (sunrise == "") sunrise = "06:00"
        if (sundown == "") sundown = "22:00"

        val textOffset = 5 * density

        canvas.drawText(now, (width / 2).toFloat(), offset, textPaint)
        canvas.drawText(sunrise, offset * 2, height - offset - textOffset, textPaint)
        canvas.drawText(sundown, width - offset * 2, height - offset - textOffset, textPaint)
    }

    private fun drawImage(canvas: Canvas) {
        if (image != 0) {
            val halfImage = (16 * density).toInt()
            val ix = width / 2
            val iy = (offset * 2).toInt()
            sunDrawable = ResourcesCompat.getDrawable(resources, image, null)

            sunDrawable?.setBounds(
                ix - halfImage,
                iy - halfImage,
                ix + halfImage,
                iy + halfImage
            )
            sunDrawable?.draw(canvas)
        } else {
            canvas.drawCircle((width / 2).toFloat(), offset * 2, 10 * density, linePaint)
        }
    }

    fun setTime(now: String = "", sunrise: String = "", sundown: String = "") {
        this.now = now
        this.sunrise = sunrise
        this.sundown = sundown
        invalidate()
    }
}