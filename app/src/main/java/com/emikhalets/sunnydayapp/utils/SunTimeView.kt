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
import kotlin.math.sin
import kotlin.math.sqrt

class SunTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var image: Int = 0
    private var textSize: Float = 0f

    private val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textLeftPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textRightPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var sunDrawable: Drawable? = null
    private var oval: RectF? = null

    // Parameters
    private var density = 0f
    private var offset = 0f
    private var radius = 0f
    private var imageSize = 0
    private var textOffset = 0f

    // Coordinates
    private var sunX = 0
    private var sunY = 0
    private var groundY = 0f
    private var groundLeftX = 0f
    private var groundRightX = 0f
    private var arcTop = 0f
    private var arcBottom = 0f
    private var arcLeft = 0f
    private var arcRight = 0f
    private var textRiseX = 0f
    private var textDownX = 0f
    private var textTimeY = 0f
    private var textNowX = 0f
    private var textNowY = 0f

    // Utils
    private var nowNum = 0.0
    private var startNum = 0.0
    private var endNum = 0.0
    private var angleStep = 0.0
    private var legA = 0.0
    private var legB = 0.0

    // Data
    private var now: String = ""
    private var sunrise: String = ""
    private var sundown: String = ""
    private var isDataEnabled = false

    init {
        context.withStyledAttributes(attrs, R.styleable.SunTimeView) {
            density = resources.displayMetrics.density
            image = getResourceId(R.styleable.SunTimeView_android_src, 0)
            textSize = getDimension(R.styleable.SunTimeView_android_textSize, 14 * density)
        }

        sunPaint.color = Color.YELLOW
        linePaint.style = Paint.Style.FILL_AND_STROKE

        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 2 * density
        linePaint.style = Paint.Style.STROKE

        textLeftPaint.color = Color.BLACK
        textLeftPaint.textSize = textSize
        textLeftPaint.textAlign = Paint.Align.RIGHT

        textCenterPaint.color = Color.BLACK
        textCenterPaint.textSize = textSize
        textCenterPaint.textAlign = Paint.Align.CENTER

        textRightPaint.color = Color.BLACK
        textRightPaint.textSize = textSize
        textRightPaint.textAlign = Paint.Align.LEFT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = (w / 2 - textSize).toInt()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        computeCoordinates()
        drawLines(canvas)
        drawText(canvas)
        drawImage(canvas)
    }

    private fun computeCoordinates() {
        offset = 20 * density
        textOffset = 10 * density
        imageSize = (32 * density).toInt()
        radius = width / 2 - offset - textSize * 3 - imageSize / 2

        groundY = height - offset
        groundLeftX = offset
        groundRightX = width - offset

        arcTop = height - offset - radius
        arcBottom = height + radius - offset
        arcLeft = width / 2 - radius
        arcRight = width / 2 + radius

        textRiseX = width / 2 - radius - imageSize / 2 - textOffset
        textDownX = width / 2 + radius + imageSize / 2 + textOffset
        textTimeY = groundY - textOffset

        if (isDataEnabled) {
            nowNum = convertTimeToDouble(now)
            startNum = convertTimeToDouble(sunrise)
            endNum = convertTimeToDouble(sundown)

            when {
                nowNum < startNum -> {
                    sunX = (width / 2 - radius).toInt()
                    sunY = groundY.toInt()
                }
                nowNum > endNum -> {
                    sunX = (width / 2 + radius).toInt()
                    sunY = groundY.toInt()
                }
                else -> {
                    angleStep = Math.toRadians(180.00 / (endNum - startNum))
                    legA = radius * sin(angleStep * (nowNum - startNum))
                    legB = sqrt(radius * radius - legA * legA)

                    sunX = (width / 2 - legB).toInt()
                    sunY = (groundY - legA).toInt()
                }
            }
        } else {
            sunX = width / 2
            sunY = arcTop.toInt()
        }

        textNowX = sunX.toFloat()
        textNowY = sunY - imageSize / 2 - textOffset
    }

    private fun drawLines(canvas: Canvas) {
        oval = RectF(arcLeft, arcTop, arcRight, arcBottom)
        canvas.drawArc(oval!!, 180f, 180f, false, linePaint)
        canvas.drawLine(groundLeftX, groundY, groundRightX, groundY, linePaint)
    }

    private fun drawText(canvas: Canvas) {
        if (isDataEnabled) {
            canvas.drawText(now, textNowX, textNowY, textCenterPaint)
            canvas.drawText(sunrise, textRiseX, textTimeY, textLeftPaint)
            canvas.drawText(sundown, textDownX, textTimeY, textRightPaint)
        } else {
            canvas.drawText("14:00", textNowX, textNowY, textCenterPaint)
            canvas.drawText("06:00", textRiseX, textTimeY, textLeftPaint)
            canvas.drawText("22:00", textDownX, textTimeY, textRightPaint)
        }
    }

    private fun drawImage(canvas: Canvas) {
        if (image != 0) {
            sunDrawable = ResourcesCompat.getDrawable(resources, image, null)
            sunDrawable?.apply {
                setBounds(
                    sunX - imageSize / 2,
                    sunY - imageSize / 2,
                    sunX + imageSize / 2,
                    sunY + imageSize / 2
                )
                draw(canvas)
            }
        } else {
            canvas.drawCircle((sunX).toFloat(), sunY.toFloat(), 10 * density, sunPaint)
            canvas.drawCircle((sunX).toFloat(), sunY.toFloat(), 10 * density, linePaint)
        }
    }

    private fun convertTimeToDouble(time: String): Double {
        val timeArr = time.split(":")
        val minutes = timeArr[1].toDouble() / 60
        return timeArr[0].toDouble() + minutes
    }

    fun setTime(nowTime: String, sunriseTime: String, sundownTime: String) {
        now = nowTime
        sunrise = sunriseTime
        sundown = sundownTime
        if (now.isNotEmpty() && sunriseTime.isNotEmpty() && sundownTime.isNotEmpty()) {
            isDataEnabled = true
        }
        invalidate()
    }
}