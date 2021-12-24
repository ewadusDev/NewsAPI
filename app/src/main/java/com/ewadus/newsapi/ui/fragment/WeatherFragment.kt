package com.ewadus.newsapi.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ewadus.newsapi.R
import com.ewadus.newsapi.databinding.FragmentWeatherBinding
import com.ewadus.newsapi.network.api.RetrofitWeather
import com.ewadus.newsapi.repo.WeatherRepository
import com.ewadus.newsapi.ui.viewmodel.weather.WeatherViewModel
import com.ewadus.newsapi.ui.viewmodel.weather.WeatherViewModelProviderFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        val retrofitService = RetrofitWeather.weatherAPI
        val weatherRepository = WeatherRepository(retrofitService)
        viewModel = ViewModelProvider(
            this,
            WeatherViewModelProviderFactory(weatherRepository)
        ).get(WeatherViewModel::class.java)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            viewModel.getResponseCurrentWeather(it.latitude.toString(),it.longitude.toString())
        }


        viewModel.weatherResponse.observe(viewLifecycleOwner, Observer {
            binding.tvTemp.text = "${it.main.temp}°c"
            binding.tvCityname.text = it.name
            binding.tvCloud.text = it.clouds.all.toString()
            binding.tvWeatherDes.text = it.weather[0].description
            binding.tvWindSpeed.text = it.wind.speed.toString() + "%"
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        return binding.root

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}