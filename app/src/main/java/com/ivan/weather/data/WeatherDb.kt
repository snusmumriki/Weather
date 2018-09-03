package com.ivan.weather.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.RoomDatabase
import io.reactivex.Observable


@Dao
interface WeatherDao {
    //@Query("SELECT * FROM Forecast")
    fun getForecastListObservable(): Observable<List<Forecast>>

    //@Insert
    fun insertForecast(forecast: Forecast)
}

//@Database(entities = [Forecast::class, Weather::class/*, City::class*/], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getDao(): WeatherDao
}