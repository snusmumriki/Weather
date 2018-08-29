package com.ivan.weather.data

import com.google.gson.annotations.SerializedName
import org.itishka.gsonflatten.Flatten


data class CityListWrapper(@SerializedName("results") val cityList: List<City>)

data class City(@SerializedName("city") val name: String,
                @SerializedName("country") val countryCode: String)

data class ForecastListWrapper(@SerializedName("list") val weatherList: List<Forecast>)

data class Forecast(@Flatten("weather::0::main") val text: String,
                    @Flatten("weather::0::icon") val iconCode: String,
                    @Flatten("main::temp") val temp: Float,
                    @Flatten("wind::speed") val windSpeed: Float,
                    @SerializedName("dt") val timeSeconds: Int)


