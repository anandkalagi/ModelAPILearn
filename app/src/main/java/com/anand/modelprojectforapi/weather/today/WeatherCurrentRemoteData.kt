package com.anand.modelprojectforapi.weather.today

import com.anand.modelprojectforapi.domain.model.WeatherResponseDto
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber

interface WeatherTodayRemoteSourceType {
    @GET("/data/2.5/weather")
    fun getWeather(
        @Query("q") location: String,
        @Query("appid") appId: String,
        @Query("id") id: String,
        @Query("units") units: String
    ): Single<WeatherResponseDto>
}

class WeatherTodayRemoteSource(retrofit: Retrofit) : WeatherTodayRemoteSourceType {

    private val service = retrofit.create(WeatherTodayRemoteSourceType::class.java)

    override fun getWeather(location: String, appId: String, id:String, units: String) =
        service.getWeather(location, appId, id, units)
            .doOnSuccess { Timber.v("loaded from network: $it") }

}
