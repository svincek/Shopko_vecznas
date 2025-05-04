package com.example.shopko

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.entitys.StoreComboMatchResult

class StoresAdapter(private var storeList: List<StoreComboMatchResult>) :
    RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {

    // ViewHolder class for holding store item views
    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.StoreName)
        val location: TextView = itemView.findViewById(R.id.StoreLocation)
        val price: TextView = itemView.findViewById(R.id.ArticlePrice)
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
        holder.price.text = String.format("%.2f", store.totalPrice)
    }

    override fun getItemCount(): Int = storeList.size

    // Updates the dataset using DiffUtil for efficient UI updates
    fun updateData(newStoreList: List<StoreComboMatchResult>) {
        this.storeList = newStoreList
        notifyDataSetChanged()
    }

    // A utility class for DiffUtil to calculate dataset changes
    private class StoreDiffCallback(
        private val oldList: List<StoreComboMatchResult>,
        private val newList: List<StoreComboMatchResult>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].store == newList[newItemPosition].store
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}