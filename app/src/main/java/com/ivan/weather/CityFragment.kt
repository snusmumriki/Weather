package com.ivan.weather

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivan.weather.di.CityScope
import dagger.android.support.DaggerFragment
import javax.inject.Inject

@CityScope
class CityFragment: DaggerFragment() {
    @Inject
    lateinit var adapter: CityRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_city_list, container, false)
        if (view is RecyclerView)
            view.adapter = adapter
        return view
    }
}
