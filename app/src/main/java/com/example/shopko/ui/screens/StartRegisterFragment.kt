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
import com.example.shopko.ui.MainActivity

class StartRegisterFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_register, container, false)

        val btnLogin = view.findViewById<TextView>(R.id.btnLogin)
        val btnRegister = view.findViewById<ImageButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_login)
        }

        return view
    }

}