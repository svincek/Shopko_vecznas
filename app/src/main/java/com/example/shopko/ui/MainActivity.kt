package com.example.shopko.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shopko.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val insetsController = window.insetsController
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = bottomInset)
            insets
        }

        val homeNavHost = supportFragmentManager.findFragmentById(R.id.nav_host_home)
        val profileNavHost = supportFragmentManager.findFragmentById(R.id.nav_host_profile)


        homeNavHost?.view?.visibility = View.VISIBLE
        profileNavHost?.view?.visibility = View.GONE
        bottomNav.selectedItemId = R.id.menu_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    homeNavHost?.view?.visibility = View.VISIBLE
                    profileNavHost?.view?.visibility = View.GONE
                    true
                }

                R.id.menu_profile -> {
                    homeNavHost?.view?.visibility = View.GONE
                    profileNavHost?.view?.visibility = View.VISIBLE
                    true
                }

                else -> false
            }
        }
    }
}
