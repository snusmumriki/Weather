package com.ivan.weather

import com.ivan.weather.data.City
import com.ivan.weather.data.WeatherApiService
import com.ivan.weather.data.cityWithCountryCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherPresenter @Inject constructor(private val apiService: WeatherApiService) {
    fun getForecastListSingle(city: City) =
            apiService.getForecast(cityWithCountryCode(city))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.weatherList }
                    .flatMapObservable { Observable.fromIterable(it) }
                    .groupBy {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it.timeSeconds * 1000L
                        calendar.get(Calendar.DAY_OF_WEEK)
                    }
                    .flatMap { it.toList().toObservable() }
                    .toList()
}