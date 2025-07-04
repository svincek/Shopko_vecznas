package com.example.shopko.ui.preference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.entitys.ArticleEntity

class PreferenceAdapter(
    private val articles: List<ArticleEntity>,
    private val onFavouriteChanged: (ArticleEntity) -> Unit
) : RecyclerView.Adapter<PreferenceAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preference, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    inner class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val brandText: TextView = itemView.findViewById(R.id.brandText)
        private val sizeText: TextView = itemView.findViewById(R.id.sizeText)
        private val checkBox: CheckBox = itemView.findViewById(R.id.prefCheckBox)

        fun bind(article: ArticleEntity) {
            brandText.text = article.brand
            sizeText.text = article.quantity
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = article.isFavourite

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                article.isFavourite = isChecked
                onFavouriteChanged(article)
            }

            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }
}


