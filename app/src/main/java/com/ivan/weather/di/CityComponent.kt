package com.ivan.weather.di

import com.ivan.weather.CityFragment
import com.ivan.weather.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
interface MainActivityComponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}

@Subcomponent()
interface CityFragmentComponent : AndroidInjector<CityFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CityFragment>()
}