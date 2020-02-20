package com.anand.modelprojectforapi.weather.today

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.anand.modelprojectforapi.R
import com.anand.modelprojectforapi.currentlocation.CurrentLocationClass
import com.anand.modelprojectforapi.currentlocation.LocationCallBack
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.anand.modelprojectforapi.view.BaseActivity
import com.anand.modelprojectforapi.view.hideSoftKeyboard
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_weather_today.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherCurrentActivity : BaseActivity(), LocationCallBack {

    private val weatherViewModel: WeatherTodayViewModel by viewModel()

    val currentalocation = CurrentLocationClass(this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_today)

        subscriptions += weatherViewModel.outputs.weather()
            .subscribe {
                weather_location.text = "Location: ${it.locationName}"
                weather_coordinates.text = "Coordinates: ${it.locationCoordinates}"
                weather_temp.text = "Temperature: ${it.temperature}"
                weather_temp_range.text = "Range: ${it.temperatureRange}"
                Glide.with(weather_icon.context).load(it.iconUrl).into(weather_icon)
            }

        subscriptions += weatherViewModel.outputs.error()
            .subscribe { Snackbar.make(weather_container, getString(it), Snackbar.LENGTH_SHORT).show() }

        subscriptions += weatherViewModel.outputs.loading()
            .subscribe { weather_progressbar.isVisible = it }

    }

    override fun onStart() {
        super.onStart()
        currentalocation.enableMyLocation(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        currentalocation.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun callbackFromLocation(address: String) {
        weatherViewModel.inputs.onSearchQuery(address)
    }

}
