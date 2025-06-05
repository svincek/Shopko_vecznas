package com.example.shopko.ui.preference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.preference.ArticlePreference

class SavedPreferencesAdapter(
    private val preferences: Map<String, List<ArticlePreference>>
) : RecyclerView.Adapter<SavedPreferencesAdapter.PreferenceViewHolder>() {

    private val flatList = preferences.flatMap { (type, prefs) ->
        prefs.map { pref -> Pair(type, pref) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_preference, parent, false)
        return PreferenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        val (type, pref) = flatList[position]
        holder.bind(type, pref)
    }

    override fun getItemCount(): Int = flatList.size

    inner class PreferenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeText: TextView = itemView.findViewById(R.id.typeTextView)
        private val brandText: TextView = itemView.findViewById(R.id.brandTextView)
        private val sizeText: TextView = itemView.findViewById(R.id.sizeTextView)

        fun bind(type: String, pref: ArticlePreference) {
            typeText.text = type
            brandText.text = pref.brand
            sizeText.text = pref.unitSize
        }
    }
}
