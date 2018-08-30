package com.ivan.weather

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_weather.*
import javax.inject.Inject

class WeatherActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var presenter: WeatherPresenter
    lateinit var pagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        setSupportActionBar(toolbar)
        pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = pagerAdapter
        //supportActionBar!!.setIcon(R.drawable.vector_drawable_flight)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.menu_weather, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment =
                ForecastFragment.newInstance(position)

        override fun getCount(): Int = 2
    }
}
