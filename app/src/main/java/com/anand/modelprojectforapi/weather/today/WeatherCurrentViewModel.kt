package com.anand.modelprojectforapi.weather.today

import com.anand.modelprojectforapi.R
import com.anand.modelprojectforapi.domain.SchedulerProviderType
import com.anand.modelprojectforapi.viewmodel.StatefulViewModel
import com.anand.modelprojectforapi.viewmodel.StatefulViewModelInputs
import com.anand.modelprojectforapi.viewmodel.StatefulViewModelOutputs
import com.anand.modelprojectforapi.weather.today.model.WeatherViewItem
import com.anand.modelprojectforapi.weather.today.model.mapper.WeatherViewItemMapper
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

interface WeatherTodayViewModelInputs : StatefulViewModelInputs {
    fun onSearchQuery(query: String)
}

interface WeatherTodayViewModelOutputs : StatefulViewModelOutputs {
    fun weather(): Observable<WeatherViewItem>
}

class WeatherTodayViewModel(
    private val schedulerProvider: SchedulerProviderType,
    private val repository: WeatherTodayRepositoryType,
    private val viewItemMapper: WeatherViewItemMapper
) : StatefulViewModel(schedulerProvider), WeatherTodayViewModelInputs, WeatherTodayViewModelOutputs {

    val inputs: WeatherTodayViewModelInputs
        get() = this

    val outputs: WeatherTodayViewModelOutputs
        get() = this

    private val weather = PublishSubject.create<WeatherViewItem>()


    internal fun search(location: String) {
        subscriptions += repository.weather(location)
            .doOnSubscribe { loading.onNext(true) }
            .doFinally { loading.onNext(false) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { viewItemMapper.map(it) }
            .subscribe(
                { weather.onNext(it) },
                {
                    Timber.e(it, "failed fetching weather")
                    error.onNext(R.string.error_generic)
                }
            )
    }

    override fun weather(): Observable<WeatherViewItem> =
        weather.observeOn(schedulerProvider.ui()).hide()

    override fun onSearchQuery(query: String) {
        search(query)
    }

}
