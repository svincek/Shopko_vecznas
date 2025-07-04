package com.example.shopko.ui.screens

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.entitys.StoreComboResultParcelable
import com.example.shopko.ui.adapters.ArticleStoreAdapter

class StoreDetailsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_details)

        val insetsController = window.insetsController
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val storeCombo = intent.getParcelableExtra<StoreComboResultParcelable>("storeCombo")

        if (storeCombo != null) {
            val storeCount = storeCombo.storeNames.size.coerceAtMost(3)

            if (storeCount >= 1) {
                findViewById<TextView>(R.id.store1NameText).text = storeCombo.storeNames[0]
                findViewById<TextView>(R.id.store1LocationText).text = storeCombo.storeLocations[0]
                findViewById<TextView>(R.id.store1HoursText).text = storeCombo.storeHours[0]
                findViewById<TextView>(R.id.store1TotalPriceText).text =
                    "€%.2f".format(storeCombo.totalPrice)
                val recycler1 = findViewById<RecyclerView>(R.id.store1Recycler)
                recycler1.layoutManager = LinearLayoutManager(this)
                recycler1.adapter = ArticleStoreAdapter(storeCombo.articles.filter {
                    it.storeBrand == storeCombo.storeNames[0]
                })
            } else {
                findViewById<View>(R.id.store1).visibility = View.GONE
            }

            if (storeCount >= 2) {
                findViewById<View>(R.id.store2).visibility = View.VISIBLE
                findViewById<TextView>(R.id.store2NameText).text = storeCombo.storeNames[1]
                findViewById<TextView>(R.id.store2LocationText).text = storeCombo.storeLocations[1]
                findViewById<TextView>(R.id.store2HoursText).text = storeCombo.storeHours[1]
                findViewById<TextView>(R.id.store2TotalPriceText).text = "€%.2f".format(
                    storeCombo.articles
                        .filter { it.storeBrand == storeCombo.storeNames[1] }
                        .sumOf { it.price * (it.buyQuantity.takeIf { q -> q > 0 } ?: 1) }
                )
                val recycler2 = findViewById<RecyclerView>(R.id.store2Recycler)
                recycler2.layoutManager = LinearLayoutManager(this)
                recycler2.adapter = ArticleStoreAdapter(storeCombo.articles.filter {
                    it.storeBrand == storeCombo.storeNames[1]
                })
            } else {
                findViewById<View>(R.id.store2).visibility = View.GONE
            }

            if (storeCount == 3) {
                findViewById<View>(R.id.store3).visibility = View.VISIBLE
                findViewById<TextView>(R.id.store3NameText).text = storeCombo.storeNames[2]
                findViewById<TextView>(R.id.store3LocationText).text = storeCombo.storeLocations[2]
                findViewById<TextView>(R.id.store3HoursText).text = storeCombo.storeHours[2]
                findViewById<TextView>(R.id.store3TotalPriceText).text = "€%.2f".format(
                    storeCombo.articles
                        .filter { it.storeBrand == storeCombo.storeNames[2] }
                        .sumOf { it.price * (it.buyQuantity.takeIf { q -> q > 0 } ?: 1) }
                )
                val recycler3 = findViewById<RecyclerView>(R.id.store3Recycler)
                recycler3.layoutManager = LinearLayoutManager(this)
                recycler3.adapter = ArticleStoreAdapter(storeCombo.articles.filter {
                    it.storeBrand == storeCombo.storeNames[2]
                })
            } else {
                findViewById<View>(R.id.store3).visibility = View.GONE
            }
        }
    }
}