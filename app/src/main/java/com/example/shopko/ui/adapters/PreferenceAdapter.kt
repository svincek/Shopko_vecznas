package com.example.shopko.ui.preference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.Article
import com.example.shopko.data.preference.ArticlePreference
import com.example.shopko.data.preference.PreferenceManager

class PreferenceAdapter(
    private val articles: List<Article>,
    private val context: Context,
    private val articleType: String,
    private val onSave: (List<ArticlePreference>) -> Unit
) : RecyclerView.Adapter<PreferenceAdapter.PreferenceViewHolder>() {

    private val selectedPrefs = mutableSetOf<ArticlePreference>()

    init {
        selectedPrefs.addAll(PreferenceManager.getMultiplePreferences(context, articleType))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preference, parent, false)
        return PreferenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        val article = articles[position]
        val pref = ArticlePreference(article.brand, article.unitSize)
        holder.bind(article, selectedPrefs.contains(pref))

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = selectedPrefs.contains(pref)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedPrefs.add(pref) else selectedPrefs.remove(pref)
        }

        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int = articles.size

    fun getSelectedPreferences(): List<ArticlePreference> {
        return selectedPrefs.toList()
    }

    override fun onViewDetachedFromWindow(holder: PreferenceViewHolder) {
        super.onViewDetachedFromWindow(holder)
        onSave(selectedPrefs.toList())
    }

    inner class PreferenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandText: TextView = itemView.findViewById(R.id.brandText)
        val sizeText: TextView = itemView.findViewById(R.id.sizeText)
        val checkBox: CheckBox = itemView.findViewById(R.id.prefCheckBox)

        fun bind(article: Article, isSelected: Boolean) {
            brandText.text = article.brand
            sizeText.text = article.unitSize
            checkBox.isChecked = isSelected
        }
    }


}
