package com.ivan.weather

import android.util.Log
import com.ivan.weather.data.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

const val FORECAST_HOURS_STEP = 3
const val WEATHER_NUMBER = 8

private fun weatherWeekDayNum(weather: Weather): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = weather.timeSeconds * 1000L
    return calendar.get(Calendar.DAY_OF_WEEK)
}

private fun createForecast(i: Int, forecastList: List<List<Weather>>): Forecast {
    var offset = 0
    if (i == 0) offset += WEATHER_NUMBER - forecastList.size
    val list = forecastList[i]
    val date = Date(list[0].timeSeconds * 1000L)
    val format = SimpleDateFormat("EEEE", Locale.US)
    return Forecast(list, offset, format.format(date))
}

@Singleton
class WeatherPresenter @Inject constructor(private val apiService: WeatherApiService,
                                           private val indexSubject: BehaviorSubject<Int>) {

    fun getForecastObservable(city: City): Observable<Forecast> =
            apiService.getForecast(cityWithCountryCode(city))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { it.printStackTrace() }.retry()
                    .map { it.weatherList }
                    .flatMap { it.toObservable() }
                    .groupBy(::weatherWeekDayNum)
                    .flatMap { it.toList().toObservable() }
                    .toSortedList { l0, l1 -> l0[0].timeSeconds - l1[0].timeSeconds }
                    .flatMapObservable { list ->
                        indexSubject.scan(0) { sum, num ->
                            sum + if ((0 <= sum + num)
                                    and (sum + num < list.size))
                                num else 0
                        }//.startWith(0)
                                .doOnNext { Log.i("tag", it.toString()) }
                                .map { createForecast(it, list) }
                    }
    //.map { createForecast(1, it) }.toObservable()

    fun getIndexObserver(): Observer<Int> = indexSubject
}