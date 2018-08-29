package com.ivan.weather

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.ivan.weather.data.Forecast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

data class GridParams(val minX: Int, val minY: Int, val maxY: Int, val period: Float)

data class LineParams(val strokeWidth: Float, val pointRadius: Float,
                      val lineColor: Int, val backgroundColor: Int)

class ForecastView(context: Context, attrs: AttributeSet) :
        SurfaceView(context, attrs), SurfaceHolder.Callback {

    var mLineParams: LineParams = LineParams(4.0f, 8.0f, Color.BLUE, Color.GRAY)
    var mGridParams: GridParams = GridParams(-1, -40, 40, 1.0f)
    var forecast: Forecast = Forecast(emptyList(), 0, 0)
    private var currentLine = 0
    var drawing = true

    init {
        holder.addCallback(this)
    }

    fun showForecast() {
        val canvas = holder.lockCanvas()
        canvas.drawColor(mLineParams.backgroundColor)
        if (forecast.lines.isNotEmpty()) {
            val forecastLine = forecast.lines[currentLine]
            val offset = if (currentLine == 0) forecast.lineLength - forecastLine.size else 0
            val hourStep = forecast.hourStep
            val tempRange = mGridParams.maxY - mGridParams.minY
            val cellHeight = canvas.height.toFloat() / tempRange.toFloat()
            val cellWidth = cellHeight * mGridParams.period

            for (i in 0 until forecastLine.size) {
                val f0 = forecastLine[i]
                val x0 = (i + offset) * cellWidth
                val y0 = (tempRange - f0.temp) * cellHeight

                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.color = mLineParams.lineColor
                canvas.drawCircle(x0, y0, mLineParams.pointRadius, paint)

                if (i < forecastLine.size - 1) {
                    val f1 = forecastLine[i + 1]
                    val x1 = (i + 1 + offset) * cellWidth
                    val y1 = (tempRange - f1.temp) * cellHeight
                    canvas.drawLine(x0, y0, x1, y1, paint)
                }

                /*val time = (i + offset) * hourStep
                    canvas.drawText("%2d°C".format(f0.temp),
                            x - 40.0f, y - 20.0f, tempPaint)
                    canvas.drawText("%02d:00".format(time),
                            x - 50.0f, canvas.height - 50.0f, timePaint)*/
            }
        }
        holder.unlockCanvasAndPost(canvas)
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
}