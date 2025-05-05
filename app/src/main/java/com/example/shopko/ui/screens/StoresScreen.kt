package com.example.shopko.ui.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.ui.MainApp
import com.example.shopko.ui.adapters.StoresAdapter
import com.example.shopko.ui.components.FilterDialogFragment
import com.example.shopko.utils.enums.Filters
import com.example.shopko.utils.sorting.sortStoreCombo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoresScreen : AppCompatActivity() {

    private lateinit var adapter: StoresAdapter
    private lateinit var storeList: List<StoreComboResult>
    private lateinit var originalList: List<StoreComboResult>
    private lateinit var loadingSpinner: ProgressBar
    private var selectedFilter: Filters = Filters.BYPRICE
    private var selectedStoreCount: Int = 1

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stores)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = window.insetsController
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        val btnNavigate = findViewById<ImageButton>(R.id.btnBack)
        btnNavigate.setOnClickListener {
            val intent = Intent(this, MainApp::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            originalList = sortStoreCombo(articleList, 1, Filters.BYPRICE)
            storeList = originalList
            setupRecyclerView(storeList)
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val searchBar: EditText = findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStores(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        loadingSpinner = findViewById(R.id.loadingSpinner)
        loadingSpinner.visibility = View.VISIBLE

        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE

            originalList = withContext(Dispatchers.IO) {
                sortStoreCombo(articleList, 1, Filters.BYPRICE)
            }

            storeList = originalList
            setupRecyclerView(storeList)

            loadingSpinner.visibility = View.GONE
        }

        findViewById<Button>(R.id.btnOpenFilter).setOnClickListener {
            FilterDialogFragment(selectedFilter, selectedStoreCount) { filter, count ->
                selectedFilter = filter
                selectedStoreCount = count
                reloadData()
            }.show(supportFragmentManager, "FilterDialog")
        }


    }

    private fun setupRecyclerView(data: List<StoreComboResult>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewStores)

        if (!::adapter.isInitialized) {
            adapter = StoresAdapter(data)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            storeList = data
        } else {
            updateAdapterData(data)
        }
    }


    private fun filterStores(query: String) {
        if (!::originalList.isInitialized) return

        lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = if (query.isBlank()) {
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
                if (filteredList != null) {
                    updateAdapterData(filteredList)
                }
            }
        }
    }


    private fun updateAdapterData(newStoreList: List<StoreComboResult>) {
        if (!::adapter.isInitialized || !::storeList.isInitialized) return

        val diffCallback = StoreDiffCallback(storeList, newStoreList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        storeList = newStoreList
        diffResult.dispatchUpdatesTo(adapter)
    }



    private class StoreDiffCallback(
        private val oldList: List<StoreComboResult>,
        private val newList: List<StoreComboResult>
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
    private fun reloadData() {
        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE

            originalList = withContext(Dispatchers.IO) {
                sortStoreCombo(articleList, selectedStoreCount, selectedFilter)
            }

            storeList = originalList
            setupRecyclerView(storeList)

            loadingSpinner.visibility = View.GONE
        }
    }
}