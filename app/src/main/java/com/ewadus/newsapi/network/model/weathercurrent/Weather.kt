package com.ewadus.newsapi.network.model.weathercurrent

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)