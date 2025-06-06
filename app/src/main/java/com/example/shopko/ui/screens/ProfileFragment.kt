package com.example.shopko.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.shopko.R


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
            //TODO: Logout
        }
    }
}
