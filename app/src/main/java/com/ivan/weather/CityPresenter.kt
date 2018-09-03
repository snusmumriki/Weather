package com.ivan.weather

import android.util.Log
import com.ivan.weather.data.City
import com.ivan.weather.data.MeetupApiService
import com.ivan.weather.data.TicketCounter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

const val NUM_CITY_FROM = 0
const val NUM_CITY_TO = 1
const val TICKETS_MAX_NUM = 9

@Singleton
class CityPresenter
@Inject
constructor(private val apiService: MeetupApiService,
            private val numSubject: PublishSubject<Int>,
            private val citySubject: PublishSubject<City>,
            private val swapSubject: PublishSubject<Observable<City>>,
            private val searchSubject: BehaviorSubject<CharSequence>,
            private val counterSubject: PublishSubject<TicketCounter>) {

    init {
        Log.i("tag", "created")
    }

    private val initCityObservable = apiService.getCities(null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { it.printStackTrace() }.retry()
            .map { it.cityList }
            .flatMap { it.toObservable() }
            .take(2)
            .cacheWithInitialCapacity(1)

    fun getCityListObservable(): Observable<List<City>> =
            apiService.getCities(null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { it.printStackTrace() }.retry()
                    .map { it.cityList }
                    .flatMap { list ->
                        searchSubject.flatMap { str ->
                            list.toObservable()
                                    .filter { it.name.contains(str, true) or str.isEmpty() }
                                    .toList().toObservable()
                        }
                    }.cacheWithInitialCapacity(1)

    fun getCityObservable(): Observable<City> = citySubject

    fun getCityFromObservable(): Observable<City> =
            numSubject.flatMap { num ->
                citySubject.filter { num == NUM_CITY_FROM }
                        .take(1)
            }.mergeWith(swapSubject.flatMap { it.take(1) })
                    .startWith(initCityObservable.take(1))

    fun getCityToObservable(): Observable<City> =
            numSubject.flatMap { num ->
                citySubject.filter { num == NUM_CITY_TO }
                        .take(1)
            }.mergeWith(swapSubject.flatMap { it.takeLast(1) })
                    .startWith(initCityObservable.takeLast(1))

    fun getAdultTicketCounterObservable(): Observable<Int> =
            counterSubject.map { it.adult }

    fun getChildTicketCounterObservable(): Observable<Int> =
            counterSubject.map { it.child }

    fun getBabyTicketCounterObservable(): Observable<Int> =
            counterSubject.map { it.baby }

    fun getCityNumObserver(): Observer<Int> = numSubject

    fun getCityObserver(): Observer<City> = citySubject

    fun getSearchObserver(): Observer<CharSequence> = searchSubject

    fun getCitySwapObserver(): Observer<Observable<City>> = swapSubject

    fun getTicketCounterObserver(): Observer<TicketCounter> = counterSubject
}