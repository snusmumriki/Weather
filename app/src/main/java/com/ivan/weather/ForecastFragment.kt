package com.ivan.weather

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_weather.*
import javax.inject.Inject


class ForecastFragment : DaggerFragment() {
    @Inject
    lateinit var presenter: WeatherPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_weather, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val graphSeries = LineGraphSeries<DataPoint>(arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)))
        with(graphSeries) {
            color = Color.CYAN
            isDrawDataPoints = true
            dataPointsRadius = 8.0f
            thickness = 4
        }

        val titleSeries = PointsGraphSeries(arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)))

        titleSeries.setCustomShape { canvas, paint, x, y, dataPoint ->
            paint.color = Color.BLACK
            paint.textSize = 40.0f
            paint.flags = Paint.ANTI_ALIAS_FLAG
            val temp = dataPoint.y.toInt()
            val time = dataPoint.x.toInt() * 3
            canvas.drawText("%2d°C".format(temp), x - 40.0f, y - 20.0f, paint)
            canvas.drawText("%02d:00".format(time), x - 50.0f, canvas.height - 50.0f, paint)
            //
        }

        val boundSeries = PointsGraphSeries(arrayOf(
                DataPoint(-1.0, -1.0),
                DataPoint(5.0, -1.0)))
        boundSeries.size = 0.0f

        with(forecast_view) {
            viewport.isXAxisBoundsManual = true
            viewport.isYAxisBoundsManual = true
            viewport.setMaxX(3.0)
            viewport.setMinX(-1.0)
            viewport.setMaxY(9.0)
            viewport.setMinY(-1.0)
            viewport.isScrollable = true
            gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
            gridLabelRenderer.isVerticalLabelsVisible = false
            gridLabelRenderer.isHorizontalLabelsVisible = false
            addSeries(graphSeries)
            addSeries(titleSeries)
            addSeries(boundSeries)
        }
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