package com.example.shopko.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shopko.R
import com.example.shopko.data.model.AuthManager
import com.google.firebase.auth.EmailAuthProvider

class ProfilePasswordFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_password, container, false)

        val btnBack = view.findViewById<ImageButton>(R.id.backButton)
        val btnSave = view.findViewById<ImageButton>(R.id.btnSave)

        val currentPass = view.findViewById<EditText>(R.id.password)
        val newPass = view.findViewById<EditText>(R.id.new_password)

        btnSave.setOnClickListener {
            val user = AuthManager.currentUser()
            val email = user?.email
            val oldPassword = currentPass.text.toString()
            val updatedPassword = newPass.text.toString()

            if (email != null && oldPassword.isNotEmpty() && updatedPassword.isNotEmpty()) {
                val credential = EmailAuthProvider.getCredential(email, oldPassword)

                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user.updatePassword(updatedPassword)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Lozinka je uspješno promijenjena!", Toast.LENGTH_SHORT).show()
                                //TODO() vrati na screen
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Nešto je pošlo po krivu: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Netočna lozinka!: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(context, "Potrebno je popuniti sva polja", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

}