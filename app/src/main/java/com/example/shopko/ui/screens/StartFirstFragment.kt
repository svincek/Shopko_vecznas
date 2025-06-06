package com.example.shopko.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.shopko.R

class StartFirstFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_first, container, false)
        val btnRegister = view.findViewById<ImageView>(R.id.btnRegister)
        val btnLogin = view.findViewById<TextView>(R.id.btnLogin)

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }

        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_login)
        }


        return view
    }
}