package com.ivan.weather

import com.ivan.weather.data.City
import com.ivan.weather.data.Weather
import com.ivan.weather.data.WeatherApiService
import com.ivan.weather.data.cityWithCountryCode
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherPresenter @Inject constructor(private val apiService: WeatherApiService) {
    fun getForecastListSingle(city: City): Single<List<List<Weather>>> =
            apiService.getForecast(cityWithCountryCode(city))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.weatherList }
                    .flatMapObservable { it.toObservable() }
                    .groupBy {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it.timeSeconds * 1000L
                        calendar.get(Calendar.DAY_OF_WEEK)
                    }
                    .flatMap { it.toList().toObservable() }
                    .toSortedList{l0, l1 -> l0[0].timeSeconds - l1[0].timeSeconds}
}