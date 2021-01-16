package com.emikhalets.sunnydayapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Hourly
import timber.log.Timber
import kotlin.math.min

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    // Attributes
    private var textColor: Float = 0f
    private var textSize: Float = 0f
    private var lineColor: Float = 0f
    private var lineWidth: Float = 0f
    private var pointColor: Float = 0f
    private var pointRadius: Float = 0f

    // Local sizes
    private var density = 0f
    private var minTemp = 0f
    private var maxTemp = 0f
    private var chartHeight = 0f
    private var chartYOffset = 0f
    private var stretch = 0f
    private var viewHeight = 0f
    private var chartPaddingTop = 0f
    private var chartPaddingBottom = 0f
    private val tempH = mutableListOf<Float>()

    // Paints
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Data
    var hourlyForecast = mutableListOf<Hourly>()

    init {
        context.withStyledAttributes(attrs, R.styleable.ChartView) {
            density = resources.displayMetrics.density
            Timber.d("$density")
            textColor = getDimension(R.styleable.ChartView_textColor, Color.BLACK.toFloat())
            textSize = getDimension(R.styleable.ChartView_textSize, 18f * density)
            lineColor = getDimension(R.styleable.ChartView_lineColor, Color.BLACK.toFloat())
            lineWidth = getDimension(R.styleable.ChartView_lineWidth, 2f * density)
            pointColor = getDimension(R.styleable.ChartView_pointColor, Color.BLACK.toFloat())
            pointRadius = getDimension(R.styleable.ChartView_pointRadius, 2f * density)
        }

        chartHeight = 100f * density
        chartPaddingTop = 10 * density
        chartPaddingBottom = 10 * density

        linePaint.color = lineColor.toInt()
        linePaint.strokeWidth = lineWidth
        linePaint.style = Paint.Style.STROKE
        pointPaint.color = pointColor.toInt()
        textPaint.color = textColor.toInt()
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (hourlyForecast.isNotEmpty()) {
            minTemp = (hourlyForecast.minOf { it.temp } * density).toFloat()
            maxTemp = (hourlyForecast.maxOf { it.temp } * density).toFloat()
            stretch = chartHeight / (maxTemp - minTemp)
            viewHeight = chartHeight + chartPaddingTop + chartPaddingBottom
            val w = 20f * density + 50f * density * (hourlyForecast.size - 1)
            val h = min(MeasureSpec.getSize(heightMeasureSpec).toFloat(), viewHeight)
            setMeasuredDimension(w.toInt(), h.toInt())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hourlyForecast.isNotEmpty()) {
            chartYOffset = minTemp - textSize - pointRadius
            computeYHeight()
            drawChart(canvas)
        }
    }

    private fun computeYHeight() {
        hourlyForecast.forEach {
            val temp = it.temp * density
            val h = (chartPaddingTop + (temp - minTemp) * stretch).toFloat()
            tempH.add(h)
        }
    }

    private fun drawChart(canvas: Canvas) {
        var xOffset = 20f * density

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