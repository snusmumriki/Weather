package com.ivan.weather.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.itishka.gsonflatten.Flatten


data class CityListWrapper(@SerializedName("results") val cityList: List<City>)

@Parcelize
data class City(@SerializedName("city") val name: String,
                @SerializedName("country") val countryCode: String) : Parcelable

data class ForecastListWrapper(@SerializedName("list") val weatherList: List<Weather>)

data class Weather(@Flatten("weather::0::main") val text: String,
                   @Flatten("weather::0::icon") val iconCode: String,
                   @Flatten("main::temp") val temp: Float,
                   @Flatten("wind::speed") val windSpeed: Float,
                   @SerializedName("dt") val timeSeconds: Int)

data class Forecast(val weatherList: List<Weather>, val indexOffset: Int, val dayWeek: String)


