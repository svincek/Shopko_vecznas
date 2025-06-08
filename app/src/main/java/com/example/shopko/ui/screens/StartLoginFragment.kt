package com.example.shopko.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shopko.R
import com.example.shopko.data.model.AuthManager
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

        val fieldEMail = view.findViewById<TextView>(R.id.input_username)
        val fieldPassword = view.findViewById<TextView>(R.id.input_password)

        val btnLogin = view.findViewById<ImageButton>(R.id.btnLogin)
        val btnRegister = view.findViewById<TextView>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            AuthManager.login(fieldEMail.text.toString(), fieldPassword.text.toString()) { success, message ->
                Toast.makeText(context, "Uspješno ste ulogirani!", Toast.LENGTH_SHORT).show()
                if (success){
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
                else{
                    Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }

        return view
    }

}