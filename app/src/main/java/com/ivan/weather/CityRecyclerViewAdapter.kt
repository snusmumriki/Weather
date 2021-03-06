package com.ivan.weather

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ivan.weather.data.City
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_city.view.*
import javax.inject.Inject
import javax.inject.Singleton


//выводит список городов
@Singleton
class CityRecyclerViewAdapter @Inject constructor(private val presenter: CityPresenter) :
        RecyclerView.Adapter<CityRecyclerViewAdapter.ViewHolder>() {

    private var items: List<City> = emptyList()

    init {
        presenter.getCityListObservable()
                .subscribe({
                    items = it
                    notifyDataSetChanged()
                }, { it.printStackTrace() })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_city, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            // отправляем выбранный объект города в главную активность
            Observable.just(items[position])
                    .doOnNext {
                        holder.nameView.text = it.name
                        holder.countryCodeView.text = it.countryCode
                    }.flatMap { city ->
                        holder.itemView.clicks().map { city }
                    }.subscribe(presenter.getCityObserver())

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View,
                           var nameView: TextView = itemView.item_name,
                           var countryCodeView: TextView = itemView.item_country_code) :
            RecyclerView.ViewHolder(itemView)
}
