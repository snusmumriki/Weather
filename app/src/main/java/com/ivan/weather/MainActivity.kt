package com.ivan.weather

import android.app.DatePickerDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.ivan.weather.data.City
import com.ivan.weather.data.TicketCounter
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import com.jakewharton.rxbinding2.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {


    @Inject
    lateinit var presenter: CityPresenter
    lateinit var searchItem: MenuItem

    private val cityFragment = CityFragment()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.title = "Choose the flight details"
        initCityPresenter()
        initDatePick()
        initTicketCounters()
    }

    private fun initCityPresenter() {
        lateinit var cityFrom: City
        lateinit var cityTo: City

        presenter.getCityObservable()
                .filter { isCityFragmentAdded() }
                .doOnNext { searchItem.collapseActionView() }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { removeCityFragment() }

        presenter.getCityFromObservable()
                .doOnNext { cityFrom = it }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { city_from_text_view.text = it.name }
        presenter.getCityToObservable()
                .doOnNext { cityTo = it }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { city_to_text_view.text = it.name }

        Observable.merge(
                city_from_text_view.clicks().map { NUM_CITY_FROM },
                city_to_text_view.clicks().map { NUM_CITY_TO })
                .filter { !isCityFragmentAdded() }
                .doOnNext { addCityFragment() }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe(presenter.getCityNumObserver())

        swap_button.clicks()
                .doOnSubscribe{compositeDisposable.add(it)}
                .map { Observable.just(cityTo, cityFrom) }
                .subscribe(presenter.getCitySwapObserver())

        find_tickets_button.clicks()
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe {
                    startActivity(intentFor<WeatherActivity>(
                            "cityFrom" to cityFrom, "cityTo" to cityTo))
                }
    }

    //дэйтпикеры не связаны с презентером, т. к. их данные не используются
    private fun initDatePick() {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd MMM, E", Locale.US)
        forward_date_view.text = format.format(calendar.time)

        forward_date_layout.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, date ->
                val format = SimpleDateFormat("dd MMM, E", Locale.US)
                calendar.set(year, month, date)
                forward_date_view.text = format.format(calendar.time)
            }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }

    private fun initTicketCounters() {
        Observable.mergeArray(
                adult_add.clicks().map { +1 }
                        .map { TicketCounter(it, 0, 0) },
                adult_remove.clicks().map { -1 }
                        .map { TicketCounter(it, 0, 0) },
                child_add.clicks().map { +1 }
                        .map { TicketCounter(0, it, 0) },
                child_remove.clicks().map { -1 }
                        .map { TicketCounter(0, it, 0) },
                baby_add.clicks().map { +1 }
                        .map { TicketCounter(0, 0, it) },
                baby_remove.clicks().map { -1 }
                        .map { TicketCounter(0, 0, it) })
                .scan(TicketCounter(0, 0, 0)) { accumulator, value ->
                    value.setCounter(accumulator)
                    //value
                    when {
                        value.sum() > TICKETS_MAX_NUM -> {
                            toast("max tickets amount is 9")
                            accumulator
                        }
                        value.baby > value.adult -> {
                            toast("baby must not more adult")
                            accumulator
                        }
                        value.hasNegative() -> accumulator
                        else -> value
                    }
                }.cacheWithInitialCapacity(1)
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe(presenter.getTicketCounterObserver())

        presenter.getAdultTicketCounterObservable()
                .map { it.toString() }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { adult_counter.text = it }

        presenter.getChildTicketCounterObservable()
                .map { it.toString() }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { child_counter.text = it }

        presenter.getBabyTicketCounterObservable()
                .map { it.toString() }
                .doOnSubscribe{compositeDisposable.add(it)}
                .subscribe { baby_counter.text = it }
    }

    private fun isCityFragmentAdded() =
            supportFragmentManager.findFragmentByTag("cityFrag") != null

    private fun addCityFragment() {
        searchItem.expandActionView()
        supportFragmentManager.beginTransaction()
                .add(R.id.city_fragment_container, cityFragment, "cityFrag")
                .commit()
    }

    private fun removeCityFragment() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportFragmentManager.beginTransaction()
                .remove(cityFragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_city, menu)

        searchItem = menu.findItem(R.id.city_search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean = true
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                removeCityFragment()
                return true
            }
        })
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = searchItem.actionView as SearchView
        searchView.queryTextChanges()
                .subscribe(presenter.getSearchObserver())
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        //if (id == R.id.action_settings) {
        //    return true
        //}
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}
