package com.example.noaaweatherapp

import android.os.Bundle

class MainActivity : NavigationController() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigationView()
    }

}