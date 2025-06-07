package com.example.shopko.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.ArticleStores

class ArticleStoreAdapter(private val articles: List<ArticleStores>) :
    RecyclerView.Adapter<ArticleStoreAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.articleName)
        val price: TextView = itemView.findViewById(R.id.articlePrice)
        val quantity: TextView = itemView.findViewById(R.id.articleQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article_detail, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.name.text = "${article.brand} ${article.type}"
        holder.price.text = "€%.2f".format(article.price.toDouble())
        holder.quantity.text = "x${article.quantity}"


    }

    override fun getItemCount(): Int = articles.size
}
