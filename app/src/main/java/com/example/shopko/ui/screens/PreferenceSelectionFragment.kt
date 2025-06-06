package com.example.shopko.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.preference.PreferenceManager
import com.example.shopko.data.preference.ArticlePreference
import com.example.shopko.data.repository.getStores
import kotlinx.coroutines.launch

class PreferenceSelectionFragment : Fragment() {

    private lateinit var articleType: String
    private lateinit var adapter: PreferenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleType = arguments?.getString("article_type") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_preference_selection, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.preferenceRecyclerView)

        val backButton = root.findViewById<ImageButton>(R.id.backButton)
        backButton.isEnabled = false



        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val storeList = getStores()
            val articleOptions = storeList
                .flatMap { it.articles }
                .filter { it.type == articleType }
                .distinctBy { "${it.brand}-${it.unitSize}" }

            adapter = PreferenceAdapter(articleOptions, requireContext(), articleType) {
                if (it.isEmpty()) {
                    PreferenceManager.clearPreference(requireContext(), articleType)
                } else {
                    PreferenceManager.setMultiplePreferences(requireContext(), articleType, it)
                }
                Toast.makeText(requireContext(), "Preferenca spremljena", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            recyclerView.adapter = adapter
            backButton.isEnabled = true
        }

        backButton.setOnClickListener {
            if (::adapter.isInitialized) {
                val selected = adapter.getSelectedPreferences()
                if (selected.isEmpty()) {
                    PreferenceManager.clearPreference(requireContext(), articleType)
                } else {
                    PreferenceManager.setMultiplePreferences(requireContext(), articleType, selected)
                }
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Pričekaj!", Toast.LENGTH_SHORT).show()
            }
        }


        return root
    }
}
