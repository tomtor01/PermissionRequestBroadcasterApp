package com.example.noaaweatherapp

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class NavigationController : AppCompatActivity() {

    fun setupNavigationView() {
        val navigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        navigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_weather -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_weather)
                    true
                }
                R.id.nav_receiver -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_receiver)
                    true
                }
                else -> false
            }
        }
    }
}