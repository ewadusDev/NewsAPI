package com.ewadus.newsapi.repo

import com.ewadus.newsapi.network.api.WeatherAPI

class WeatherRepository (private val retrofit: WeatherAPI) {
    suspend fun getCurrentWeather(lat:String,lon:String) = retrofit.getWeatherCurrent(lat, lon)
}