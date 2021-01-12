package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Hourly

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var textColor: Float = Color.BLACK.toFloat()
    private var textSize: Float = 55f
    private var lineColor: Float = Color.BLACK.toFloat()
    private var lineWidth: Float = 20f
    private var pointColor: Float = Color.BLACK.toFloat()
    private var pointRadius: Float = 2f

    private var density = 0f
    private var chartHeight = 0
    private var chartYOffset = 0
    private val tempH = mutableListOf<Float>()

    private val linePaint = Paint()
    private val pointPaint = Paint()
    private val textPaint = Paint()

    var hourlyForecast = mutableListOf<Hourly>()

//    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.FILL
//        textAlign = Paint.Align.CENTER
//        textSize = 55.0f
//        typeface = Typeface.create("", Typeface.BOLD)
//    }

    init {
        context.withStyledAttributes(attrs, R.styleable.ChartView) {
            density = resources.displayMetrics.scaledDensity
            textColor = getDimension(R.styleable.ChartView_textColor, Color.BLACK.toFloat())
            textSize = getDimension(R.styleable.ChartView_textSize, 18f * density)
            lineColor = getDimension(R.styleable.ChartView_lineColor, Color.BLACK.toFloat())
            lineWidth = getDimension(R.styleable.ChartView_lineWidth, 2f * density)
            pointColor = getDimension(R.styleable.ChartView_pointColor, Color.BLACK.toFloat())
            pointRadius = getDimension(R.styleable.ChartView_pointRadius, 2f * density)
        }

        linePaint.isAntiAlias = true
        linePaint.color = lineColor.toInt()
        linePaint.strokeWidth = lineWidth
        linePaint.style = Paint.Style.STROKE

        pointPaint.isAntiAlias = true
        pointPaint.color = pointColor.toInt()

        textPaint.isAntiAlias = true
        textPaint.color = textColor.toInt()
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hourlyForecast.isNotEmpty()) {
            if (chartHeight == 0) setChartHeight()
            computeYHeight()
            drawChart(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    private fun setChartHeight() {
        val localMinH = hourlyForecast.minOf { it.temp }.toInt()
        val localMaxH = hourlyForecast.maxOf { it.temp }.toInt()
        chartYOffset = (localMinH * density - textSize - pointRadius).toInt()
        chartHeight = ((localMaxH - localMinH) + 10) + textSize.toInt()
    }

    private fun computeYHeight() {
        for (i in hourlyForecast.indices) {
            tempH.add(((hourlyForecast[i].temp * density - chartYOffset)).toFloat())
        }
    }

    private fun drawChart(canvas: Canvas) {
        var xOffset = 20 * density

        for (i in hourlyForecast.indices) {
            val x = xOffset
            val y = tempH[i]
            val temp = hourlyForecast[i].temp.toInt()

            canvas.drawCircle(x, y, pointRadius, pointPaint)
            canvas.drawText("$temp°C", x, y - pointRadius - 10, textPaint)

            if (i <= hourlyForecast.size - 2) {
                val nextX = xOffset + 50 * density
                val nextY = tempH[i + 1]
                canvas.drawLine(x, y, nextX, nextY, linePaint)
            }

            xOffset += 50 * density
        }
    }
}