package com.example.shopko.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.Article
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.data.preference.PreferenceManager

class ArticleAdapter(private val article: MutableList<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleName: TextView = itemView.findViewById(R.id.article_name)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val buttonMinus: ImageButton = itemView.findViewById(R.id.button_minus)
        val buttonPlus: ImageButton = itemView.findViewById(R.id.button_plus)
        val checkBox: CheckBox = itemView.findViewById(R.id.materialCheckBox)
        val starCount: TextView = itemView.findViewById(R.id.starCount) // Zamijenjeno iz ImageView
        val starIcon: ImageView = itemView.findViewById(R.id.star_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = article[position]
        val context = holder.itemView.context

        holder.articleName.text = currentArticle.type
        holder.quantity.text = currentArticle.quantity.toString()
        holder.checkBox.isChecked = currentArticle.isChecked

        val preferences = PreferenceManager.getPreferences(context, currentArticle.type) ?: emptyList()

        if (preferences.isNotEmpty()) {
            holder.starIcon.setImageResource(R.drawable.icon_starbox_true)
            holder.starCount.text = "x${preferences.size}"
        } else {
            holder.starIcon.setImageResource(R.drawable.icon_starbox_false)
            holder.starCount.text = ""
        }

        holder.starIcon.setOnClickListener {
            val bundle = Bundle().apply {
                putString("article_type", currentArticle.type)
            }
            findNavController(holder.itemView).navigate(
                R.id.preferenceSelectionFragment,
                bundle
            )
        }


        // Checkbox logika
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentArticle.isChecked = isChecked
        }

        holder.buttonMinus.setOnClickListener {
            if (currentArticle.quantity > 0) {
                currentArticle.quantity--
                notifyItemChanged(position)
                articleList.filter { currentArticle.type == it.type }.forEach {
                    it.quantity = currentArticle.quantity
                }
            }
        }

        holder.buttonPlus.setOnClickListener {
            currentArticle.quantity++
            notifyItemChanged(position)
            articleList.filter { currentArticle.type == it.type }.forEach {
                it.quantity = currentArticle.quantity
            }
        }
    }


    override fun getItemCount(): Int = article.size
}
