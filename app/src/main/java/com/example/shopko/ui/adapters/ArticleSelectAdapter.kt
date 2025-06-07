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
    private var articles: List<ArticleDisplay>
) : RecyclerView.Adapter<ArticleSelectAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<ArticleDisplay>()

    fun getSelectedItems(): List<ArticleDisplay> = selectedItems.toList()

    fun updateList(newArticles: List<ArticleDisplay>) {
        articles = newArticles
        selectedItems.clear()
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
        holder.bind(article, selectedItems.contains(article))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.articleName)
        private val checkBox: CheckBox = itemView.findViewById(R.id.articleCheckBox)
        private val card: LinearLayout = itemView as LinearLayout

        fun bind(article: ArticleDisplay, isChecked: Boolean) {
            nameText.text = article.subcategory
            checkBox.isChecked = isChecked

            val toggle = {
                if (selectedItems.contains(article)) {
                    selectedItems.remove(article)
                } else {
                    selectedItems.add(article)
                }
                notifyItemChanged(adapterPosition)
            }

            card.setOnClickListener { toggle() }
            checkBox.setOnClickListener { toggle() }
        }
    }
}
