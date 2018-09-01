package com.ivan.weather

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ivan.weather.data.iconCodeToUrl
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_weather.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.*
import org.jetbrains.anko.toast
import javax.inject.Inject
import kotlin.math.roundToInt


class ForecastFragment : DaggerFragment() {
    @Inject
    lateinit var presenter: WeatherPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_weather, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graph_view.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)
        graph_view.isZoomEnabled = true
        graph_view.zoomType = ZoomType.HORIZONTAL

        presenter.getForecastObservable(arguments.getParcelable("city")!!)
                .doOnNext { day_view.text = it.dayOfWeek }
                .subscribe({
                    val list = it.weatherList
                    val offset = it.indexOffset
                    val pointValues = list.mapIndexed { i, w ->
                        val time = (offset + i) * FORECAST_HOURS_STEP
                        PointValue(time.toFloat(), w.temp)
                                .setLabel("${w.temp.roundToInt()}°C")
                    }
                    val axisValuesTop = list.mapIndexed { i, w ->
                        val time = (offset + i) * FORECAST_HOURS_STEP
                        AxisValue(time.toFloat())
                                .setLabel("${w.windSpeed}m/s")
                    }
                    val axisValuesBottom = list.mapIndexed { i, _ ->
                        val time = (offset + i) * FORECAST_HOURS_STEP
                        AxisValue(time.toFloat())
                                .setLabel("%02d:00".format(time))
                    }

                    val data = LineChartData()
                    data.lines = listOf(Line(pointValues)
                            .setColor(Color.BLUE)
                            .setCubic(true)
                            .setHasLabels(true))
                    data.axisXTop = Axis(axisValuesTop)
                    data.axisXTop.textColor = Color.GRAY
                    data.axisXBottom = Axis(axisValuesBottom)
                    data.axisXBottom.textColor = Color.DKGRAY

                    graph_view.onValueTouchListener = object : LineChartOnValueSelectListener {
                        override fun onValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue) {
                            val weather = list[pointIndex]
                            weather_text.text = weather.text
                            Glide.with(context)
                                    .load(iconCodeToUrl(weather.iconCode))
                                    .into(weather_image)
                        }

                        override fun onValueDeselected() {

                        }
                    }

                    graph_view.lineChartData = data
                    val y = graph_view.currentViewport.centerY()
                    val zoomLevel = 2f * (list.size.toFloat() / WEATHER_NUMBER.toFloat())
                    graph_view.setZoomLevel(0f, y, zoomLevel)
                    //graph_view.scrollX = 0
                }, { context.toast("Internet connection failed") })
    }
}