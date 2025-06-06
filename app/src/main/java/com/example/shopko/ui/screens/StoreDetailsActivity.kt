package com.example.shopko.ui.screens

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.StoreComboResultParcelable
import com.example.shopko.ui.adapters.ArticleStoreAdapter

class StoreDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_details)

        val insetsController = window.insetsController
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewArticles)
        recyclerView.isNestedScrollingEnabled = false

        val storeCombo = intent.getParcelableExtra<StoreComboResultParcelable>("storeCombo")

        if (storeCombo != null) {
            findViewById<TextView>(R.id.storeNameText).text = storeCombo.storeName
            findViewById<TextView>(R.id.storeLocationText).text = storeCombo.storeLocation
            findViewById<TextView>(R.id.storeHoursText).text = storeCombo.storeHours
            findViewById<TextView>(R.id.totalPriceText).text = "€%.2f".format(storeCombo.totalPrice)

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewArticles)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ArticleStoreAdapter(storeCombo.articles)
        }
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


    }
}
