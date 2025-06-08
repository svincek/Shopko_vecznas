package com.example.shopko.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopko.R
import com.example.shopko.data.model.CurrentStoreComboResult.storeCombo
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.data.model.StoreComboResultParcelable
import com.example.shopko.ui.screens.StoreDetailsActivity


class StoresAdapter() :
    RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.StoreName)
        val price: TextView = itemView.findViewById(R.id.ArticlePrice)
        val distance: TextView = itemView.findViewById(R.id.StoreDistance)
        val logo: ImageView = itemView.findViewById(R.id.logo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stores, parent, false)
        return StoreViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val storeCombo = storeCombo[position]
        val context = holder.itemView.context

        holder.name.text = storeCombo.store.joinToString(" + ") { it.name }
        holder.price.text = "€%.2f".format(storeCombo.totalPrice)
        holder.distance.text = "${"%.1f".format(storeCombo.distance / 1000f)} km"

        val imageUrl = storeCombo.store.first().logoUrl.orEmpty()

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_store1)
            .error(R.drawable.placeholder_store1)
            .into(holder.logo)

        holder.itemView.setOnClickListener {
            val storeNames = storeCombo.store.map { it.name }
            val storeLocations = storeCombo.store.map { "${it.address} ${it.city}" }
            val storeHours = storeCombo.store.map { it.workTime }
            val articlesMapped = storeCombo.matchedArticles

            val intent = Intent(context, StoreDetailsActivity::class.java).apply {
                putExtra("storeCombo", StoreComboResultParcelable(
                    storeNames,
                    storeLocations,
                    storeHours,
                    articlesMapped,
                    storeCombo.totalPrice
                ))
            }
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = storeCombo.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<StoreComboResult>) {
        storeCombo = newList
        notifyDataSetChanged()
    }
}
