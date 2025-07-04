package com.example.shopko.ui.preference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.entitys.ArticleEntity

class SavedPreferencesAdapter(
    private val favourites: List<ArticleEntity>
) : RecyclerView.Adapter<SavedPreferencesAdapter.PreferenceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_preference, parent, false)
        return PreferenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        val article = favourites[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = favourites.size

    inner class PreferenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeText: TextView = itemView.findViewById(R.id.typeTextView)
        private val brandText: TextView = itemView.findViewById(R.id.brandTextView)
        private val sizeText: TextView = itemView.findViewById(R.id.sizeTextView)

        fun bind(article: ArticleEntity) {
            typeText.text = article.subcategory
            brandText.text = article.brand
            sizeText.text = article.quantity
        }
    }
}