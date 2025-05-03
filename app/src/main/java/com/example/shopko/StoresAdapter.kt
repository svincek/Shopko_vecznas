package com.example.shopko

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.entitys.Store

class StoresAdapter(private var storeList: List<Store>) :
    RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {

    // ViewHolder class for holding store item views
    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.StoreName)
        val location: TextView = itemView.findViewById(R.id.StoreLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stores, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.name.text = store.name
        holder.location.text = store.location
    }

    override fun getItemCount(): Int = storeList.size

    // Updates the dataset using DiffUtil for efficient UI updates
    fun updateData(newStoreList: List<Store>) {
        val diffCallback = StoreDiffCallback(storeList, newStoreList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        storeList = newStoreList
        diffResult.dispatchUpdatesTo(this) // Apply changes to RecyclerView
    }

    // A utility class for DiffUtil to calculate dataset changes
    private class StoreDiffCallback(
        private val oldList: List<Store>,
        private val newList: List<Store>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Assuming `id` uniquely identifies a Store
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Check if content has not changed
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}