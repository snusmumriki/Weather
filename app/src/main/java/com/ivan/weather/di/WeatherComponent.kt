package com.ivan.weather.di

import com.ivan.weather.WeatherActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
interface WeatherActivityComponent: AndroidInjector<WeatherActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<WeatherActivity>()
}

@Subcomponent()
interface ForecastFragmentComponent: AndroidInjector<WeatherActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<WeatherActivity>()
}