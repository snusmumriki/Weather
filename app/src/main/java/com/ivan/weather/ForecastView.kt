package com.ivan.weather

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.ivan.weather.data.Forecast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

data class GridOptions(val minX: Int, val minY: Int, val maxY: Int, val period: Float)

data class LineOptions(val strokeWidth: Float, val pointRadius: Float,
                       val lineColor: Int, val backgroundColor: Int
)

data class WeatherPoint(val x: Float, val y: Float,
                        val temp: Int, val time: Int,
                        val windSpeed: Float, val text: String)

class ForecastView(context: Context, attrs: AttributeSet) :
        SurfaceView(context, attrs), SurfaceHolder.Callback {

    var lineOptions: LineOptions = LineOptions(8.0f, 8.0f, Color.BLUE, Color.GRAY)
    var gridOptions: GridOptions = GridOptions(-1, -40, 40, 10.0f)
    var forecast: Forecast = Forecast(emptyList(), 0, 0)
    private var currentLine = 1
    private var drawing = true

    init {
        holder.addCallback(this)
    }

    fun showForecast() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawColor(lineOptions.backgroundColor)
            if (forecast.lines.isNotEmpty()) {
                val forecastLine = forecast.lines[currentLine]
                val posOffset = if (currentLine == 0)
                    forecast.lineLength - forecastLine.size else 0
                val tempRange = gridOptions.maxY - gridOptions.minY
                val cellHeight = canvas.height.toFloat() / tempRange.toFloat()
                val cellWidth = cellHeight * gridOptions.period

                val weatherPoints = forecastLine.mapIndexed { i, w ->
                    val pos = i + posOffset
                    WeatherPoint(pos * cellWidth,
                            (gridOptions.maxY - w.temp) * cellHeight,
                            w.temp.roundToInt(),
                            pos * forecast.hourStep,
                            w.windSpeed, w.text)
                }

                for (i in 0 until weatherPoints.size) {
                    val wp0 = weatherPoints[i]

                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    paint.style = Paint.Style.FILL_AND_STROKE
                    paint.color = lineOptions.lineColor
                    canvas.drawCircle(wp0.x, wp0.y, lineOptions.pointRadius, paint)

                    paint.textSize = 40.0f
                    canvas.drawText("%2d°C".format(wp0.temp),
                            wp0.x - 40.0f, wp0.y - 20.0f, paint)
                    canvas.drawText("%02d:00".format(wp0.time),
                            wp0.x - 50.0f, canvas.height - 200.0f, paint)

                    if (i < weatherPoints.size - 1) {
                        val wp1 = weatherPoints[i + 1]
                        canvas.drawLine(wp0.x, wp0.y, wp1.x, wp1.y, paint)
                    }
                }
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Observable.fromCallable {
            while (drawing)
                showForecast()
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        drawing = false
    }

    var x0 = 0.0f
    var y0 = 0.0f
    var t0 = 0L

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                x0 = event.x
                y0 = event.y
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - x0
                val dy = event.y - y0
                if (abs(dy) > abs(dx)) {
                    var i = currentLine - dy.sign.toInt()
                    if (i < 0) i = 0
                    if (i > forecast.lines.size) i = forecast.lines.size
                    currentLine = i
                }
            }

        }
        return super.onTouchEvent(event)
    }
}