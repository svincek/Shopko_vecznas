package com.example.shopko

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.entitys.StoreComboMatchResult
import com.example.shopko.entitys.UserArticleList.articleList
import com.example.shopko.enums.Filters
import com.example.shopko.utils.dataFunctions.sortStoreCombo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoresActivity : AppCompatActivity() {

    private lateinit var adapter: StoresAdapter
    private lateinit var storeList: List<StoreComboMatchResult>
    private lateinit var originalList: List<StoreComboMatchResult>

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

        lifecycleScope.launch {
            originalList = sortStoreCombo(articleList, 1, Filters.BYPRICE)
            storeList = originalList
            setupRecyclerView(storeList)
        }

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

    private fun setupRecyclerView(data: List<StoreComboMatchResult>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewStores)
        adapter = StoresAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Filters the store list based on the search query
    private fun filterStores(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = if (query.isEmpty()) {
                originalList
            } else {
                originalList.filter { combo ->
                    combo.store.any { store ->
                        store.name.contains(query, ignoreCase = true) ||
                                store.location.contains(query, ignoreCase = true)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                updateAdapterData(filteredList)
            }
        }
    }

    // Updates adapter data efficiently using DiffUtil
    private fun updateAdapterData(newStoreList: List<StoreComboMatchResult>) {
        val diffCallback = StoreDiffCallback(storeList, newStoreList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        storeList = newStoreList // Update the internal list reference
        diffResult.dispatchUpdatesTo(adapter)
    }

    // Custom DiffUtil Callback for Store comparison
    private class StoreDiffCallback(
        private val oldList: List<StoreComboMatchResult>,
        private val newList: List<StoreComboMatchResult>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Check if items are the same (e.g., by ID or unique identifier)
            return oldList[oldItemPosition].store == newList[newItemPosition].store
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Check if the content of items is the same
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}