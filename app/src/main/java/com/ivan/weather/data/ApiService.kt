package com.ivan.weather.data

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

const val MEETUP_BASE_URL ="https://api.meetup.com"

interface MeetupApiService {
    @GET("/2/cities")
    fun getCities(@Query("country") countryCode: String?,
                  @Query("sign") sign: Boolean = true,
                  @Query("photo-host") photoHost: String = "public"): Observable<CityListWrapper>
}

const val WEATHER_BASE_URL = "https://api.openweathermap.org"
const val APP_ID = "86b284d3a5564b77e1ee5847d21b6a73"
fun iconCodeToUrl(iconCode: String): String = "http://openweathermap.org/img/w/$iconCode.png"

interface WeatherApiService {
    @GET("/data/2.5/forecast")
    fun getForecast(@Query("lat") lat: Double, @Query("lon") lon: Double,
                    @Query("units") units: String = "metric", //в метрических единица
                    @Query("appid") appId: String = APP_ID): Observable<ForecastListWrapper>
}