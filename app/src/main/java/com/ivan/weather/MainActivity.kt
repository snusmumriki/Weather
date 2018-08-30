package com.ivan.weather

import android.os.Bundle
import com.ivan.weather.data.City
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var presenter: CityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cityFragment = CityFragment()
        lateinit var cityFrom: City
        lateinit var cityTo: City
        presenter.getCityObservable()
                .filter { supportFragmentManager.findFragmentByTag("cityFrag") != null }
                .subscribe {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in,
                                    android.R.animator.fade_out)
                            .remove(cityFragment)
                            .commit()
                }

        presenter.getCityFromObservable()
                .doOnNext { cityFrom = it }
                .subscribe { city_from_text_view.text = it.name }
        presenter.getCityToObservable()
                .doOnNext { cityTo = it }
                .subscribe { city_to_text_view.text = it.name }

        Observable.merge(
                RxView.clicks(city_from_text_view).map { NUM_CITY_FROM },
                RxView.clicks(city_to_text_view).map { NUM_CITY_TO })
                .filter { supportFragmentManager.findFragmentByTag("cityFrag") == null }
                .doOnNext {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in,
                                    android.R.animator.fade_out)
                            .add(R.id.city_fragment_container, cityFragment, "cityFrag")
                            .commit()
                }.subscribe(presenter.getCityNumObserver())

        RxView.clicks(swap_button)
                .map { Observable.just(cityTo, cityFrom) }
                .subscribe(presenter.getSwapObserver())

        find_tickets_button.setOnClickListener {
            startActivity(intentFor<WeatherActivity>(
                    "cityFrom" to cityFrom, "cityTo" to cityTo))
        }
    }
}
