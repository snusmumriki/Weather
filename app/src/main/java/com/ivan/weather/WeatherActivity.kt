package com.ivan.weather

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import com.ivan.weather.data.City
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_weather.*
import org.jetbrains.anko.intentFor
import javax.inject.Inject
import kotlin.math.sign


class WeatherActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var presenter: WeatherPresenter
    lateinit var pagerAdapter: SectionsPagerAdapter
    lateinit var detector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cityFrom = intent.getParcelableExtra<City>("cityFrom")
        val cityTo = intent.getParcelableExtra<City>("cityTo")
        from_view.text = cityFrom.name
        to_view.text = cityTo.name

        pagerAdapter = SectionsPagerAdapter(supportFragmentManager, arrayOf(cityFrom, cityTo))
        container.adapter = pagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        detector = GestureDetectorCompat(this, SwipeListener())
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(intentFor<MainActivity>())
        return true
    }

    inner class SectionsPagerAdapter(fm: FragmentManager, private val cityFromTo: Array<City>) :
            FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val fragment = ForecastFragment()
            val bundle = Bundle()
            bundle.putParcelable("city", cityFromTo[position])
            fragment.arguments = bundle
            return fragment
        }

        override fun getCount(): Int = 2
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onFling(event1: MotionEvent, event2: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {
            presenter.getIndexObserver().onNext(-velocityX.sign.toInt())
            return true
        }
    }

}
