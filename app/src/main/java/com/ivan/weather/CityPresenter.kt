package com.ivan.weather

import com.ivan.weather.data.City
import com.ivan.weather.data.MeetupApiService
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

const val NUM_CITY_FROM = 0
const val NUM_CITY_TO = 1

@Singleton
class CityPresenter @Inject constructor(private val apiService: MeetupApiService,
                                        private val citySubject: PublishSubject<City>,
                                        private val numSubject: PublishSubject<Int>) {

    //init { Log.i("tag", "new") }
    fun getCityListSingle(countryCode: String? = null): Single<List<City>> =
            apiService.getCities(countryCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { it.printStackTrace() }
                    .map { it.cityList }

    fun getCityObservable(): Observable<City> = citySubject

    fun getCityObserver(): Observer<City> = citySubject

    fun getCityNumObserver(): Observer<Int> = numSubject

    fun getCityFromObservable(): Observable<City> =
            numSubject.flatMap { num ->
                citySubject.filter { num == NUM_CITY_FROM }
                        .take(1)
            }/*.startWith(getCityListSingle().map { it[0] }
                    .toObservable())*/

    fun getCityToObservable(): Observable<City> =
            numSubject.flatMap { num ->
                citySubject.filter { num == NUM_CITY_TO }
                        .take(1)
            }/*.startWith(getCityListSingle().map { it[1] }
                    .toObservable())*/
}