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

class StartRegisterFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_register, container, false)

        val fieldName = view.findViewById<TextView>(R.id.input_username)
        val fieldSurname = view.findViewById<TextView>(R.id.input_username)
        val fieldEMail = view.findViewById<TextView>(R.id.input_email)
        val fieldPassword = view.findViewById<TextView>(R.id.input_password)
        val fieldRepPassword = view.findViewById<TextView>(R.id.input_repeat_password)

        val btnLogin = view.findViewById<TextView>(R.id.btnLogin)
        val btnRegister = view.findViewById<ImageButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            if(fieldPassword.text.toString() == fieldRepPassword.text.toString()){
                AuthManager.register(fieldEMail.text.toString(), fieldPassword.text.toString(),
                    fieldName.text.toString(), fieldSurname.text.toString()) { success, message ->
                    if (success){
                        Toast.makeText(context, "Uspješno ste registrirani!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(activity, MainActivity::class.java))
                        activity?.finish()
                    }
                    else{
                        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(context, "Lozinke se ne podudaraju!", Toast.LENGTH_SHORT).show()
            }
        }
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_login)
        }

        return view
    }

}