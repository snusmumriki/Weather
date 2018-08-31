package com.ivan.weather

import com.google.gson.GsonBuilder
import com.ivan.weather.data.City
import com.ivan.weather.data.WeatherApiService
import io.reactivex.subjects.BehaviorSubject
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import org.itishka.gsonflatten.FlattenTypeAdapterFactory
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    /*@Test
    fun meetupApiTest() {
        val cityPresenter = CityPresenter(
                Retrofit.Builder()
                        .baseUrl(MEETUP_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(MeetupApiService::class.java), PublishSubject.create(), PublishSubject.create())
                .getCityListObservable()
                .subscribe { list -> print(list) }
    }*/

    @Test
    fun weatherApiTest() {
        val cityPresenter = WeatherPresenter(
                Retrofit.Builder()
                        .baseUrl("http://api.openweathermap.org")
                        .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                                .registerTypeAdapterFactory(FlattenTypeAdapterFactory())
                                .create()))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(OkHttpClient.Builder()
                                //.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(8888)))
                                .addNetworkInterceptor {
                                    it.proceed(it.request()
                                            .newBuilder()
                                            .header("Cache-Control", CacheControl.Builder()
                                                    .maxAge(1, TimeUnit.HOURS).build().toString())
                                            .build())
                                }.build())
                        .build()
                        .create(WeatherApiService::class.java), BehaviorSubject.create())
                .getForecastObservable(City("London", "uk"))
                .subscribe (::println)
    }
}
