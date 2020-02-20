package com.anand.modelprojectforapi.weather.today

import com.anand.modelprojectforapi.BuildConfig
import com.anand.modelprojectforapi.domain.model.Weather
import com.anand.modelprojectforapi.domain.model.mapper.WeatherMapper
import io.reactivex.Single
import java.util.concurrent.TimeUnit

interface WeatherTodayRepositoryType {
    fun weather(location: String): Single<Weather>
    fun weatherLatestLocal(): Single<Weather>
}

class WeatherTodayRepository(
    private val localSource: WeatherTodayLocalSourceType,
    private val remoteSource: WeatherTodayRemoteSourceType,
    private val weatherMapper: WeatherMapper
) : WeatherTodayRepositoryType {

    override fun weather(location: String): Single<Weather> {
        return localSource.load()
            .filter { (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - it.lastUpdated) < VALIDITY_THRESHOLD_SECONDS) }
            .filter { location.equals(it.name, true) }
            .switchIfEmpty(remoteSource
                .getWeather(
                    location = location,
                    appId = BuildConfig.OPEN_WEATHER_MAPS_APP_ID,
                    id = "7778677",
                    units = "metric"
                )
                .map { weatherMapper.map(it) }
                .flatMap { localSource.store(it) }
            )
    }

    override fun weatherLatestLocal() =
        localSource.load().toSingle()

    companion object {
        const val VALIDITY_THRESHOLD_SECONDS = 120
    }
}
