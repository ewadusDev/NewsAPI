package com.ewadus.newsapi.network.model.weatheronecall

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)