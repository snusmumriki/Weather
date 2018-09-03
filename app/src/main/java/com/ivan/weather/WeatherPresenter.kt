package com.ivan.weather

import com.ivan.weather.data.City
import com.ivan.weather.data.Forecast
import com.ivan.weather.data.Weather
import com.ivan.weather.data.WeatherApiService
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
    val list = forecastList[i]
    val offset = if (i == 0) WEATHER_NUMBER - list.size else 0
    val date = Date(list[0].timeSeconds * 1000L)
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(date)
    var nextDayOfWeek = ""
    var previousDayOfWeek = ""
    if (i > 0) {
        val dt = Date(forecastList[i - 1][0].timeSeconds * 1000L)
        previousDayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(dt)
    }
    if (i < forecastList.size - 1) {
        val dt = Date(forecastList[i + 1][0].timeSeconds * 1000L)
        nextDayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(dt)
    }
    return Forecast(list, offset, dayOfWeek, nextDayOfWeek, previousDayOfWeek)
}

//загружает пргноз погоды на один день во фрагмент, реагирует на свайп или на нажатия верхней и нижней tetxtview
@Singleton
class WeatherPresenter @Inject constructor(private val apiService: WeatherApiService,
                                           private val indexSubject: BehaviorSubject<Int>
                                           /*,private  val weatherDao: WeatherDao*/) {

    fun getForecastObservable(city: City): Observable<Forecast> =
            apiService.getForecast(city.lat, city.lon)
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
                        }.map { createForecast(it, list) }
                    }//.doOnNext{weatherDao.insertForecast(it)}

    fun getIndexObserver(): Observer<Int> = indexSubject
}