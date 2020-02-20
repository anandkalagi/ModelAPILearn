package com.anand.modelprojectforapi.weather

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anand.modelprojectforapi.domain.model.Weather

@Database(
    entities = [Weather::class],
    version = 1
)
abstract class WeatherDb : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    
    companion object {
        const val NAME = "weather.db"
    }

}
