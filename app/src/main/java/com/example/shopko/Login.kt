package com.example.shopko

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.setPadding
import kotlin.jvm.java

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //val login_layout : LinearLayout = findViewById<LinearLayout>(R.id.main)
        //login_layout.setPadding(40,40,40,40)

        val goToSecondButton: Button = findViewById(R.id.login_button)
        goToSecondButton.setOnClickListener {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
    }
}
}