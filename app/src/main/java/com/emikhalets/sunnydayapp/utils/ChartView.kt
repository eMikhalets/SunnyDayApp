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
    private var chartHeight = 0f
    private var viewHeight = 0f

    private var density = 0f
    private var stretch = 0f
    private var chartPaddingTop = 0f
    private var chartPaddingBottom = 0f
    private val tempH = mutableListOf<Float>()

    // Paints
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Data
    var hourlyForecast = mutableListOf<Hourly>()

    var maxTemp = 0.0
    var minTemp = 0.0
    var prevTemp = 0.0
    var currentTemp = 0.0
    var nextTemp = 0.0
    var tempText = ""

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
            stretch = chartHeight / (maxTemp - minTemp).toFloat()
            viewHeight = chartHeight + chartPaddingTop + chartPaddingBottom + textSize
            val h = min(MeasureSpec.getSize(heightMeasureSpec).toFloat(), viewHeight)
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h.toInt())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hourlyForecast.isNotEmpty()) {
            computeYHeight()
            drawChart(canvas)
        }
    }

    private fun computeYHeight() {
        hourlyForecast.forEach {
            val temp = it.temp * density
            val h = (chartPaddingTop + textSize + (temp - minTemp) * stretch).toFloat()
            tempH.add(h)
        }
    }

    private fun drawChart(canvas: Canvas) {
        val currentX = (width / 2).toFloat()
        val currentH = (chartPaddingTop + textSize + (currentTemp - minTemp) * stretch)

        when {
            prevTemp == 0.0 -> {
                val endTemp =
                    if (currentTemp > nextTemp) currentTemp - nextTemp else nextTemp - currentTemp
                val endX = width.toFloat()
                val endH = (chartPaddingTop + textSize + (endTemp - minTemp) * stretch)

                canvas.drawLine(currentX, currentH.toFloat(), endX, endH.toFloat(), linePaint)
            }
            nextTemp == 0.0 -> {
                val startTemp =
                    if (currentTemp > prevTemp) currentTemp - prevTemp else prevTemp - currentTemp
                val startX = 0f
                val startH = (chartPaddingTop + textSize + (startTemp - minTemp) * stretch)

                canvas.drawLine(startX, startH.toFloat(), currentX, currentH.toFloat(), linePaint)
            }
            else -> {
                val startTemp =
                    if (currentTemp > prevTemp) currentTemp - prevTemp else prevTemp - currentTemp
                val startX = 0f
                val startH = (chartPaddingTop + textSize + (startTemp - minTemp) * stretch)

                val endTemp =
                    if (currentTemp > nextTemp) currentTemp - nextTemp else nextTemp - currentTemp
                val endX = width.toFloat()
                val endH = (chartPaddingTop + textSize + (endTemp - minTemp) * stretch)

                canvas.drawLine(startX, startH.toFloat(), currentX, currentH.toFloat(), linePaint)
                canvas.drawLine(currentX, currentH.toFloat(), endX, endH.toFloat(), linePaint)
            }
        }

        canvas.drawCircle(currentX, currentH.toFloat(), pointRadius, pointPaint)
        canvas.drawText(tempText, currentX, (currentH - 5 * density).toFloat(), textPaint)
    }
}