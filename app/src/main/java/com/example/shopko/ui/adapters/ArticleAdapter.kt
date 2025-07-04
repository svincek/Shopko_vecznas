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
import com.example.shopko.data.model.entitys.ArticleDisplay
import com.example.shopko.data.model.entitys.ArticleEntity
import com.example.shopko.data.model.objects.UserArticleList.articleList

class ArticleAdapter(
    private val fullList: MutableList<ArticleDisplay>,
    private var favouriteArticles: List<ArticleEntity> = emptyList()
) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var filteredList: MutableList<ArticleDisplay> = fullList.toMutableList()

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleName: TextView = itemView.findViewById(R.id.article_name)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val buttonMinus: ImageButton = itemView.findViewById(R.id.button_minus)
        val buttonPlus: ImageButton = itemView.findViewById(R.id.button_plus)
        val checkBox: CheckBox = itemView.findViewById(R.id.materialCheckBox)
        val starCount: TextView = itemView.findViewById(R.id.starCount)
        val starIcon: ImageView = itemView.findViewById(R.id.star_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    fun updateFavourites(newFavourites: List<ArticleEntity>) {
        favouriteArticles = newFavourites
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = filteredList[position]

        holder.articleName.text = currentArticle.subcategory
        holder.quantity.text = currentArticle.buyQuantity.toString()
        holder.checkBox.isChecked = currentArticle.isChecked

        val favoritesForSubcategory = favouriteArticles.filter { it.subcategory == currentArticle.subcategory }
            .distinctBy { it.brand to it.quantity }
        val countFavorites = favoritesForSubcategory.size

        when (countFavorites) {
            0 -> {
                holder.starIcon.setImageResource(R.drawable.icon_starbox_false)
                holder.starCount.text = "Odaberi favorita"
            }
            1 -> {
                holder.starIcon.setImageResource(R.drawable.icon_starbox_true)
                holder.starCount.text = favoritesForSubcategory[0].brand ?: "1 favorit"
            }
            else -> {
                holder.starIcon.setImageResource(R.drawable.icon_starbox_true)
                holder.starCount.text = "$countFavorites favorita"
            }
        }

        holder.starIcon.setOnClickListener {
            val bundle = Bundle().apply {
                putString("subcategory", currentArticle.subcategory)
                putString("quantity", currentArticle.quantity)
            }
            findNavController(holder.itemView).navigate(
                R.id.preferenceSelectionFragment,
                bundle
            )
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentArticle.isChecked = isChecked
        }

        holder.buttonMinus.setOnClickListener {
            if (currentArticle.buyQuantity > 0) {
                currentArticle.buyQuantity--
                notifyItemChanged(position)
                articleList.find { it.subcategory == currentArticle.subcategory }?.buyQuantity = currentArticle.buyQuantity
            }
        }

        holder.buttonPlus.setOnClickListener {
            currentArticle.buyQuantity++
            notifyItemChanged(position)
            articleList.find { it.subcategory == currentArticle.subcategory }?.buyQuantity = currentArticle.buyQuantity
        }
    }


    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            fullList.toMutableList()
        } else {
            fullList.filter {
                it.subcategory?.contains(query, ignoreCase = true) == true
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}
