package com.example.shopko.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shopko.R
import com.example.shopko.data.model.AuthManager


class ProfileFragment : Fragment() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameField = view.findViewById<TextView>(R.id.nameField)
        AuthManager.fetchUserProfile { name, surname ->
            nameField.text = StringBuilder().append(name).append(" ").append(surname)
        }
        val emailField = view.findViewById<TextView>(R.id.emailField)
        emailField.text = AuthManager.currentUser()?.email
        val btnMyPreferences: ConstraintLayout = view.findViewById(R.id.my_preferences)
        val btnEditProfile: ConstraintLayout = view.findViewById(R.id.edit_profile)
        val btnLogout: ConstraintLayout = view.findViewById(R.id.logout)
        val btnPasswordChange: ConstraintLayout = view.findViewById(R.id.change_password)
        val btnHistory: ConstraintLayout = view.findViewById(R.id.history)

        btnMyPreferences.setOnClickListener {
            findNavController().navigate(R.id.action_preferences)
        }
        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_edit)
        }
        btnPasswordChange.setOnClickListener {
            findNavController().navigate(R.id.action_password)
        }
        btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_history)
        }
        btnLogout.setOnClickListener {
            AuthManager.logout()
            startActivity(Intent(activity, StartActivity::class.java))
            activity?.finish()
        }
    }
}
