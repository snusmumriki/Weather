package com.ivan.weather.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ivan.weather.App
import com.ivan.weather.data.WEATHER_BASE_URL
import com.ivan.weather.data.WeatherApiService
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import org.itishka.gsonflatten.FlattenTypeAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(subcomponents = [WeatherActivityComponent::class, ForecastFragmentComponent::class])
class WeatherModule {
    @Provides
    @Singleton
    @Named("weatherCacheControl")
    fun provideCacheControl(): CacheControl =
            CacheControl.Builder().maxAge(3, TimeUnit.HOURS).build()

    @Provides
    @Singleton
    @Named("weatherCache")
    fun provideCache(context: App): Cache =
            Cache(File(context.cacheDir, "weather_cache"), 0x80_000)

    @Provides
    @Singleton
    @Named("weatherClient")
    fun provideClient(@Named("weatherCacheControl") cacheControl: CacheControl,
                      @Named("weatherCache") cache: Cache): OkHttpClient =
            OkHttpClient.Builder()
                    .addNetworkInterceptor {
                        it.proceed(it.request().newBuilder()
                                .header("Cache-Control", cacheControl.toString()).build())
                    }.cache(cache)
                    .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
            GsonBuilder()
                    .registerTypeAdapterFactory(FlattenTypeAdapterFactory())
                    .create()

    @Provides
    @Singleton
    fun provideApiService(@Named("weatherClient") client: OkHttpClient,
                          gson: Gson): WeatherApiService =
            Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build()
                    .create(WeatherApiService::class.java)

    @Provides
    @Singleton
    fun provideIndexSubject() = BehaviorSubject.create<Int>()

    /*@Provides
    @Singleton
    fun provideDao(app: App) =
            Room.databaseBuilder(app,  WeatherDatabase::class.java, "weather_db")
                    .build().getDao()*/
}