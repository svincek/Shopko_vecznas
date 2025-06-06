package com.example.shopko.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.shopko.R
import com.example.shopko.SplashActivity
import com.example.shopko.ui.MainActivity

class StartLoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_login, container, false)
        val btnLogin = view.findViewById<ImageButton>(R.id.btnLogin)
        val btnRegister = view.findViewById<TextView>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }

        return view
    }

}