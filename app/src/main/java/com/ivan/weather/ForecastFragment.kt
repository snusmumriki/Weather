package com.ivan.weather

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivan.weather.data.City
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_weather.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
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

        val city = arguments.getParcelable<City>("city")

        presenter.getForecastObservable(City("London", "gb"))
                .subscribe({
                    val list = it.weatherList
                    val offset = it.indexOffset
                    val pointValues = list.mapIndexed { i, w ->
                        val time = offset + i * FORECAST_HOURS_STEP
                        PointValue(time.toFloat(), w.temp)
                                .setLabel("${w.temp.roundToInt()}°C")
                    }
                    val axisValuesTop = list.mapIndexed { i, w ->
                        val time = offset + i * FORECAST_HOURS_STEP
                        AxisValue(time.toFloat())
                                .setLabel("${w.windSpeed}m/s")
                    }
                    val axisValuesBottom = list.mapIndexed { i, _ ->
                        val time = offset + i * FORECAST_HOURS_STEP
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

                    graph_view.lineChartData = data

                    val v = graph_view.currentViewport
                    val x = v.centerX()
                    val y = v.centerY()
                    graph_view.setZoomLevel(x, y, 2f)
                }, { context.toast("ERROR") })
    }
}