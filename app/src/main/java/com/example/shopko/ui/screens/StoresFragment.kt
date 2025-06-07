package com.example.shopko.ui.screens

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.ui.adapters.StoresAdapter
import com.example.shopko.ui.components.FilterBottomSheetDialog
import com.example.shopko.ui.components.SortBottomSheetDialog
import com.example.shopko.utils.enums.Filters
import com.example.shopko.utils.enums.SortOption
import com.example.shopko.utils.sorting.sortStoreCombo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoresFragment : Fragment() {

    private lateinit var adapter: StoresAdapter
    private lateinit var storeList: List<StoreComboResult>
    private lateinit var originalList: List<StoreComboResult>
    private lateinit var loadingSpinner: ProgressBar
    private var selectedFilter: Filters = Filters.BYPRICE
    private var selectedStoreCount: Int = 1
    private lateinit var resultStoreCount: TextView
    private var currentSortOption: SortOption? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_stores, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val btnNavigate = view.findViewById<ImageButton>(R.id.btnBack)
        btnNavigate.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        loadingSpinner.visibility = View.VISIBLE
        resultStoreCount = view.findViewById(R.id.resultCount)

        lifecycleScope.launch {
            originalList = withContext(Dispatchers.IO) {
                val filteredArticles = articleList.filter { it.isChecked }
                sortStoreCombo(requireContext(), filteredArticles, selectedStoreCount, selectedFilter)
            }
            storeList = originalList
            setupRecyclerView(view, storeList)
            loadingSpinner.visibility = View.GONE
            resultStoreCount.text = "${storeList.count()} rezultata"
        }

        val searchBar: EditText = view.findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStores(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        view.findViewById<LinearLayout>(R.id.btnOpenFilter).setOnClickListener {
            FilterBottomSheetDialog().show(childFragmentManager, "BottomSheetDialog")
        }

        view.findViewById<LinearLayout>(R.id.btnOpenSort).setOnClickListener {
            SortBottomSheetDialog(currentSortOption) { newSortOption ->
                if (newSortOption != null) {
                    currentSortOption = newSortOption
                    selectedFilter = when (newSortOption) {
                        SortOption.PRICE_ASC -> Filters.BYPRICE
                        SortOption.PRICE_DESC -> Filters.BYPRICE_DESC
                        SortOption.DISTANCE -> Filters.BYDISTANCE
                    }
                    reloadData(requireView())
                }
            }.show(childFragmentManager, "SortDialog")
        }
    }

    private fun setupRecyclerView(view: View, data: List<StoreComboResult>) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewStores)
        adapter = StoresAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun filterStores(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = if (query.isEmpty()) {
                originalList
            } else {
                originalList.filter { combo ->
                    combo.store.any { store ->
                        store.name.contains(query, ignoreCase = true) ||
                                store.address.contains(query, ignoreCase = true) ||
                                store.city.contains(query, ignoreCase = true)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                updateAdapterData(filteredList)
            }
        }
    }

    private fun updateAdapterData(newStoreList: List<StoreComboResult>) {
        val diffCallback = StoreDiffCallback(storeList, newStoreList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        storeList = newStoreList
        diffResult.dispatchUpdatesTo(adapter)
        resultStoreCount.text = "${storeList.count()} rezultata"
    }

    private fun reloadData(view: View) {
        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE
            originalList = withContext(Dispatchers.IO) {
                val filteredArticles = articleList.filter { it.isChecked }
                sortStoreCombo(requireContext(), filteredArticles, selectedStoreCount, selectedFilter)
            }
            storeList = originalList
            setupRecyclerView(view, storeList)
            loadingSpinner.visibility = View.GONE
            resultStoreCount.text = "${storeList.count()} rezultata"
        }
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
}
