package com.ivan.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivan.weather.data.City
import com.ivan.weather.data.Forecast
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
        presenter.getForecastListSingle(City("London", "gb"))
                .map { Forecast(it, 3, 8) }
                .subscribe { forecast ->
                    forecast_view.forecast = forecast
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