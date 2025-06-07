package com.example.shopko.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.ArticleStores
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.data.model.StoreComboResultParcelable
import com.example.shopko.ui.screens.StoreDetailActivity

class StoresAdapter(private var storeList: List<StoreComboResult>) :
    RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.StoreName)
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
        val storeCombo = storeList[position]
        holder.name.text = storeCombo.store.joinToString(" + ") { it.name }
        holder.price.text = "€%.2f".format(storeCombo.totalPrice)
        holder.distance.text = "${"%.1f".format(storeCombo.distance / 1000f)} km"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val firstStore = storeCombo.store.first()
            val articlesMapped = storeCombo.matchedArticles.map {
                ArticleStores(
                    type = (it.name).toString(),
                    brand = it.brand.toString(),
                    category = it.subcategory.toString(),
                    unitSize = it.quantity.toString(),
                    price = it.price.toString(),
                    quantity = it.buyQuantity,
                    id = it.productId
                )
            }

            val intent = Intent(context, StoreDetailActivity::class.java).apply {
                putExtra(
                    "storeCombo", StoreComboResultParcelable(
                        firstStore.name,
                        firstStore.address + " " + firstStore.city,
                        firstStore.workTime,
                        articlesMapped,
                        storeCombo.totalPrice
                    )
                )
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = storeList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<StoreComboResult>) {
        storeList = newList
        notifyDataSetChanged()
    }
}
