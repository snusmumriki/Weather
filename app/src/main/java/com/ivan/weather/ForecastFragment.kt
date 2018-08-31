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

        presenter.getForecastListSingle(City("London", "gb"))
                .map { it[1] }
                .toObservable()
                .subscribe({
                    val pointValues = it.mapIndexed { i, w ->
                        val time = i * FORECAST_HOURS_STEP
                        PointValue(time.toFloat(), w.temp)
                                .setLabel("${w.temp.roundToInt()}°C")
                    }
                    val axisValuesTop = it.mapIndexed { i, w ->
                        val time = i * FORECAST_HOURS_STEP
                        AxisValue(time.toFloat())
                                .setLabel("${w.windSpeed}m/s")
                    }
                    val axisValuesBottom = it.mapIndexed { i, w ->
                        val time = i * FORECAST_HOURS_STEP
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
                }, { it.printStackTrace() })
    }

    companion object {
        const val SECTION_NUMBER = "section_number"
        fun newInstance(sectionNumber: Int): ForecastFragment {
            val fragment = ForecastFragment()
            val args = Bundle()
            args.putInt(SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}