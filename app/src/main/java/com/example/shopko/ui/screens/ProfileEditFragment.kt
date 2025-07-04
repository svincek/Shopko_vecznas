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
import com.example.shopko.data.model.objects.AuthManager

class ProfileEditFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        val nameField = view.findViewById<EditText>(R.id.editText)
        val surnameField = view.findViewById<EditText>(R.id.editText2)
        AuthManager.fetchUserProfile { name, surname ->
            nameField.setText(name)
            surnameField.setText(surname)
        }

        val btnSave = view.findViewById<ImageButton>(R.id.btnSave)
        val btnBack = view.findViewById<ImageButton>(R.id.backButton)

        btnSave.setOnClickListener {
            AuthManager.updateUserNameAndSurname(nameField.text.toString(), surnameField.text.toString())
            Toast.makeText(context, "Podaci uspješno promijenjeni!", Toast.LENGTH_SHORT).show()
            //TODO() vrati na screen
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

}