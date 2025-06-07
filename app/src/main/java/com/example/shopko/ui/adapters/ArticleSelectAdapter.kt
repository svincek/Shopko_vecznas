package com.example.shopko.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.ArticleDisplay

class ArticleSelectAdapter(
    private var articles: List<ArticleDisplay>,
    private val initialSelected: List<ArticleDisplay> = emptyList()
) : RecyclerView.Adapter<ArticleSelectAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<ArticleDisplay>()

    init {
        selectedItems.addAll(initialSelected)
    }

    fun getSelectedItems(): List<ArticleDisplay> = selectedItems.toList()

    fun updateList(newArticles: List<ArticleDisplay>) {
        val updatedArticles = newArticles.map { newArticle ->
            val isSelected = selectedItems.any { it.subcategory == newArticle.subcategory }
            ArticleDisplay(
                brand = newArticle.brand,
                subcategory = newArticle.subcategory,
                isChecked = isSelected,
                buyQuantity = newArticle.buyQuantity
            )
        }
        articles = updatedArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article_selectable, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.articleName)
        private val checkBox: CheckBox = itemView.findViewById(R.id.articleCheckBox)
        private val card: LinearLayout = itemView as LinearLayout

        fun bind(article: ArticleDisplay) {
            nameText.text = article.subcategory
            checkBox.isChecked = article.isChecked

            val toggle = {
                if (selectedItems.any { it.subcategory == article.subcategory }) {
                    selectedItems.removeIf { it.subcategory == article.subcategory }
                    article.isChecked = false
                } else {
                    selectedItems.add(article)
                    article.isChecked = true
                }
                notifyItemChanged(adapterPosition)
            }

            card.setOnClickListener { toggle() }
            checkBox.setOnClickListener { toggle() }
        }
    }
}

