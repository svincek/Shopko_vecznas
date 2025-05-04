package com.example.shopko

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.entitys.StoreComboMatchResult

class StoresAdapter(private var storeList: List<StoreComboMatchResult>) :
    RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.StoreName)
        val location: TextView = itemView.findViewById(R.id.StoreLocation)
        val price: TextView = itemView.findViewById(R.id.ArticlePrice)
        val distance: TextView = itemView.findViewById(R.id.StoreDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stores, parent, false)
        return StoreViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.name.text = store.store.joinToString(" + "){it.name}
        holder.location.text = store.store.joinToString(", "){it.location}
        holder.price.text = "€" + String.format("%.2f", store.totalPrice)
        holder.distance.text = "${"%.1f".format(store.distance / 1000)} km"
    }

    override fun getItemCount(): Int = storeList.size

}