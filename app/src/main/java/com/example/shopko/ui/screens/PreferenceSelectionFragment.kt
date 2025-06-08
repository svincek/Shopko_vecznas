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
import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.repository.AppDatabase
import kotlinx.coroutines.launch

class PreferenceSelectionFragment : Fragment() {

    private lateinit var subcategory: String
    private lateinit var quantity: String
    private lateinit var adapter: PreferenceAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subcategory = arguments?.getString("subcategory") ?: ""
        quantity = arguments?.getString("quantity") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_preference_selection, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.preferenceRecyclerView)
        val backButton = root.findViewById<ImageButton>(R.id.backButton)

        backButton.isEnabled = false
        db = AppDatabase.getDatabase(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val filtered: List<ArticleEntity> = db.articleDao().getArticlesBySubcategoryContains(subcategory)
                .distinctBy { it.brand to it.quantity }
            adapter = PreferenceAdapter(filtered) { updatedArticle ->
                lifecycleScope.launch {
                    db.articleDao().updateFavouriteStatus(
                        brand = updatedArticle.brand.toString(),
                        subcategory = updatedArticle.subcategory.toString(),
                        quantity = updatedArticle.quantity.toString(),
                        isFavourite = updatedArticle.isFavourite
                    )
                }
            }

            recyclerView.adapter = adapter
            backButton.isEnabled = true
        }

        backButton.setOnClickListener {
            Toast.makeText(requireContext(), "Favoriti spremljeni", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return root
    }
}