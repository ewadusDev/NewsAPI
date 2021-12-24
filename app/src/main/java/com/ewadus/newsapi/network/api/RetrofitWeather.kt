package com.ewadus.newsapi.network.api

import com.ewadus.newsapi.util.Constants.Companion.BASE_URL_WEATHER
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitWeather {
   companion object{
       private val retrofit by lazy {
           val logging = HttpLoggingInterceptor()
           logging.setLevel(HttpLoggingInterceptor.Level.BODY)
           val client = OkHttpClient.Builder()
               .addInterceptor(logging)
               .build()

           Retrofit.Builder()
               .addConverterFactory(GsonConverterFactory.create())
               .client(client)
               .baseUrl(BASE_URL_WEATHER)
               .build()

       }
       val weatherAPI: WeatherAPI by lazy {
           retrofit.create(WeatherAPI::class.java)
       }
   }
}