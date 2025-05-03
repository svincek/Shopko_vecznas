package com.example.shopko

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.example.shopko.entitys.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoresActivity : AppCompatActivity() {

    private lateinit var adapter: StoresAdapter
    private lateinit var storeList: List<Store>
    private lateinit var originalList: List<Store>

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge design
        setContentView(R.layout.activity_stores)

        window.setDecorFitsSystemWindows(false)
        val insetsController = window.insetsController
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        // Set up navigation button
        val btnNavigate = findViewById< ImageButton>(R.id.btnBack)
        btnNavigate.setOnClickListener {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }

        // Initialize the store data (replace with actual List<Store> data if already available)
        initializeStores()

        // Set up RecyclerView and Adapter
        setupRecyclerView()

        // Set up "Back" button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // Close the current screen
        }

        // Set up search functionality
        val searchBar: EditText = findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStores(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Initializes the store list with sample data
    private fun initializeStores() {
        // Sample data for stores (replace this with your actual `Store` model data)
        originalList = listOf(
            Store(1, "Brand A", "Supermarket A", "9AM-9PM", "Adresa 1", emptyList(), com.google.android.gms.maps.model.LatLng(0.0, 0.0)),
            Store(2, "Brand B", "Shop B", "8AM-8PM", "Adresa 2", emptyList(), com.google.android.gms.maps.model.LatLng(0.0, 0.0)),
            Store(3, "Brand C", "Market C", "7AM-7PM", "Adresa 3", emptyList(), com.google.android.gms.maps.model.LatLng(0.0, 0.0))
        )
        storeList = originalList
    }

    // Sets up RecyclerView
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewStores)
        adapter = StoresAdapter(storeList) // Pass the initial store list to the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Filters the store list based on the search query
    private fun filterStores(query: String) {
        lifecycleScope.launch(Dispatchers.IO) { // Use lifecycleScope for coroutine safety
            val filteredList = if (query.isEmpty()) {
                originalList // Show all stores if query is empty
            } else {
                originalList.filter { store ->
                    store.name.contains(query, ignoreCase = true) || store.location.contains(query, ignoreCase = true)
                }
            }

            // Update the adapter on the main thread
            withContext(Dispatchers.Main) {
                updateAdapterData(filteredList)
            }
        }
    }

    // Updates adapter data efficiently using DiffUtil
    private fun updateAdapterData(newStoreList: List<Store>) {
        val diffCallback = StoreDiffCallback(storeList, newStoreList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        storeList = newStoreList // Update the internal list reference
        diffResult.dispatchUpdatesTo(adapter)
    }

    // Custom DiffUtil Callback for Store comparison
    private class StoreDiffCallback(
        private val oldList: List<Store>,
        private val newList: List<Store>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Check if items are the same (e.g., by ID or unique identifier)
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Check if the content of items is the same
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}