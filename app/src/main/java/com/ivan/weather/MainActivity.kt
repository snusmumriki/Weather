package com.ivan.weather

import android.app.DatePickerDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.ivan.weather.data.City
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import com.jakewharton.rxbinding2.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {


    @Inject
    lateinit var presenter: CityPresenter
    private val cityFragment = CityFragment()
    lateinit var searchItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCityPresenter()
        initDatePick()
    }

    private fun initCityPresenter() {
        lateinit var cityFrom: City
        lateinit var cityTo: City

        presenter.getCityObservable()
                .filter { isCityFragmentAdded() }
                .doOnNext { searchItem.collapseActionView() }
                .subscribe { removeCityFragment() }

        presenter.getCityFromObservable()
                .doOnNext { cityFrom = it }
                .subscribe { city_from_text_view.text = it.name }
        presenter.getCityToObservable()
                .doOnNext { cityTo = it }
                .subscribe { city_to_text_view.text = it.name }

        Observable.merge(
                city_from_text_view.clicks().map { NUM_CITY_FROM },
                city_to_text_view.clicks().map { NUM_CITY_TO })
                .filter { !isCityFragmentAdded() }
                .doOnNext { addCityFragment() }
                .subscribe(presenter.getCityNumObserver())

        swap_button.clicks()
                .map { Observable.just(cityTo, cityFrom) }
                .subscribe(presenter.getSwapObserver())

        find_tickets_button.setOnClickListener {
            startActivity(intentFor<WeatherActivity>(
                    "cityFrom" to cityFrom, "cityTo" to cityTo))
        }
    }

    private fun initDatePick() {
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
                if (item is SearchView)
                    item.isIconified = false
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
}
