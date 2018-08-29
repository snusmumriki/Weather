package com.ivan.weather.di

import com.ivan.weather.CityFragment
import com.ivan.weather.ForecastFragment
import com.ivan.weather.MainActivity
import com.ivan.weather.WeatherActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module()
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = [CityModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [CityModule::class])
    abstract fun bindCityFragment(): CityFragment

    @ContributesAndroidInjector(modules = [WeatherModule::class])
    abstract fun bindWeatherActivity(): WeatherActivity

    @ContributesAndroidInjector(modules = [WeatherModule::class])
    abstract fun bindForecastFragment(): ForecastFragment
}