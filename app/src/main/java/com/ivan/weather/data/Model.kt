package com.ivan.weather.data

import android.arch.persistence.room.Embedded
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.itishka.gsonflatten.Flatten


data class CityListWrapper(@SerializedName("results") val cityList: List<City>)

@Parcelize
//@Entity
data class City(//@PrimaryKey(autoGenerate = true) val id: Long,
                @SerializedName("city") val name: String,
                @SerializedName("country") val countryCode: String,
                val lat: Double, val lon: Double) : Parcelable

data class ForecastListWrapper(@SerializedName("list") val weatherList: List<Weather>)


data class Weather(@Flatten("weather::0::main") val text: String,
                   @Flatten("weather::0::icon") val iconCode: String,
                   @Flatten("main::temp") val temp: Float,
                   @Flatten("wind::speed") val windSpeed: Float,
                   @SerializedName("dt") val timeSeconds: Int)

//@Entity
data class Forecast(/*@PrimaryKey(autoGenerate = true) val id: Long,*/
                    @Embedded val weatherList: List<Weather>, val indexOffset: Int, val dayOfWeek: String,
                    val nextDayOfWeek: String, val previousDayOfWeek: String)

data class TicketCounter(var adult: Int, var child: Int, var baby: Int) {
    fun sum() = adult + child + baby

    fun setCounter(counter: TicketCounter) {
        this.adult += counter.adult
        this.child += counter.child
        this.baby += counter.baby
    }

    fun hasNegative() = (adult == -1) or (child == -1) or (baby == -1)
}


