package com.example.shopko

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.shopko.ui.screens.StartActivity
import kotlinx.coroutines.launch
import syncDataFromApiToRoom

class SplashActivity : AppCompatActivity() {
    
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            !isReady
        }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            syncDataFromApiToRoom(this@SplashActivity)

            isReady = true

            startActivity(Intent(this@SplashActivity, StartActivity::class.java))
            finish()
        }
    }
}
