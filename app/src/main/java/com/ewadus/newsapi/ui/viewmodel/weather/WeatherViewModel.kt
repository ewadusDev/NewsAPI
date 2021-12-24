package com.ewadus.newsapi.ui.viewmodel.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ewadus.newsapi.network.model.weathercurrent.WeatherModel
import com.ewadus.newsapi.repo.WeatherRepository
import kotlinx.coroutines.*

class WeatherViewModel (val weatherRepository: WeatherRepository): ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherModel>()
    val weatherResponse: LiveData<WeatherModel> get() = _weatherResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    private var job: Job? = null

    fun getResponseCurrentWeather(lat:String,lon:String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = weatherRepository.getCurrentWeather(lat, lon)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _weatherResponse.postValue(response.body())
                }else{
                    setErrorMessage(response.message())
                }
            }
        }
    }
//
//    fun getLatLonLocationWeather(lat:String,lon:String) {
//
//    }




    private fun setErrorMessage(message:String) {
        _errorMessage.value = message
    }







}

