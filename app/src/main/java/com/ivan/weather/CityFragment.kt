package com.ivan.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivan.weather.di.CityScope
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_city_list.*
import javax.inject.Inject

@CityScope
class CityFragment : DaggerFragment() {
    @Inject
    lateinit var adapter: CityRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_city_list, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        city_recycler_view.adapter = adapter
    }
}
