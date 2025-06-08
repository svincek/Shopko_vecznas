package com.example.shopko.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.repository.AppDatabase
import com.example.shopko.ui.preference.SavedPreferencesAdapter
import kotlinx.coroutines.launch

class ProfilePreferencesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile_preferences, container, false)
        val db = AppDatabase.getDatabase(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.savedPreferencesRecyclerView)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val allPrefs = db.articleDao().getFavouriteArticles()
            val distinctPrefs = allPrefs.distinctBy { listOf(it.brand, it.subcategory, it.quantity) }
            val adapter = SavedPreferencesAdapter(distinctPrefs)
            recyclerView.adapter = adapter
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}
