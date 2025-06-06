package com.example.shopko.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.shopko.R
import com.example.shopko.ui.screens.PocetnaFragment
import com.example.shopko.utils.location.LocationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import syncDataFromApiToRoom

class MainActivity : AppCompatActivity() {

    private lateinit var locationHelper: LocationHelper

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            syncDataFromApiToRoom(this@MainActivity)
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = window.insetsController
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        val mainLayout = findViewById<View>(R.id.main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = 0,
                left = systemBars.left,
                right = systemBars.right
            )
            insets
        }

        locationHelper = LocationHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = bottomInset)
            insets
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PocetnaFragment())
            .commit()

        bottomNav.selectedItemId = R.id.menu_home

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.menu_home-> PocetnaFragment()
                else -> null
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
                true
            } == true
        }
    }
}