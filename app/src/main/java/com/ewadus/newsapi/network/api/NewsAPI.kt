package com.ewadus.newsapi.network.api

import com.ewadus.newsapi.network.model.NewsResponse
import com.ewadus.newsapi.network.model.weathercurrent.WeatherModel
import com.ewadus.newsapi.util.Constants.Companion.API_KEY_NEWS
import com.ewadus.newsapi.util.Constants.Companion.API_KEY_WEATHER
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "th",
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey:String = API_KEY_NEWS

    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchEverything(
        @Query("q")
        searchQuery :String ,
        @Query("page")
        page: Int = 1,
        @Query("sortBy")
        sortBy: String = "publishedAt",
        @Query("apiKey")
        apiKey:String = API_KEY_NEWS

    ): Response<NewsResponse>

}

interface WeatherAPI {

    @GET("data/2.5/weather")
    suspend fun getWeatherCurrent(
        @Query("lat")
        lat:String,
        @Query("lon")
        lon:String,
        @Query("units")
        unit:String = "metric",
        @Query("appid")
        apiKey: String = API_KEY_WEATHER
    ): Response<WeatherModel>



}
