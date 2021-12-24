package com.ewadus.newsapi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ewadus.newsapi.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class NewsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)


        val btnNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val hostFragment = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
        btnNavView.setupWithNavController(hostFragment.findNavController())




    }
}