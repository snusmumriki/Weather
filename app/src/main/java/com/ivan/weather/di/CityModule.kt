package com.ivan.weather.di

import com.ivan.weather.App
import com.ivan.weather.data.City
import com.ivan.weather.data.MEETUP_BASE_URL
import com.ivan.weather.data.MeetupApiService
import com.ivan.weather.data.TicketCounter
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(subcomponents = [MainActivityComponent::class, CityFragmentComponent::class])
class CityModule {
    @Provides
    @Singleton
    @Named("cityCacheControl")
    fun provideCacheControl(): CacheControl =
            CacheControl.Builder().maxAge(1, TimeUnit.DAYS).build()

    @Provides
    @Singleton
    @Named("cityCache")
    fun provideCache(context: App): Cache =
            Cache(File(context.cacheDir, "city_cache"), 0x80_000)

    @Provides
    @Singleton
    @Named("cityClient")
    fun provideClient(@Named("cityCacheControl") cacheControl: CacheControl,
                      @Named("cityCache") cache: Cache): OkHttpClient =
            OkHttpClient.Builder()
                    .addNetworkInterceptor {
                        it.proceed(it.request()
                                .newBuilder()
                                .header("Cache-Control", cacheControl.toString())
                                .build())
                    }
                    .cache(cache)
                    .build()

    @Provides
    @Singleton
    fun provideApiService(@Named("cityClient") client: OkHttpClient): MeetupApiService =
            Retrofit.Builder()
                    .baseUrl(MEETUP_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build()
                    .create(MeetupApiService::class.java)

    @Provides
    @Singleton
    fun provideNumSubject() = PublishSubject.create<Int>()

    @Provides
    @Singleton
    fun provideCitySubject() = PublishSubject.create<City>()

    @Provides
    @Singleton
    fun provideSwapSubject() = PublishSubject.create<Observable<City>>()

    @Provides
    @Singleton
    fun provideSearchSubject() = BehaviorSubject.create<CharSequence>()

    @Provides
    @Singleton
    fun provideTicketCounterSubject() = PublishSubject.create<TicketCounter>()
}