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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.objects.CurrentStoreComboResult.storeCombo
import com.example.shopko.data.model.entitys.StoreComboResult
import com.example.shopko.data.model.objects.UserArticleList.articleList
import com.example.shopko.ui.adapters.StoresAdapter
import com.example.shopko.ui.components.FilterBottomSheetDialog
import com.example.shopko.ui.components.SortBottomSheetDialog
import com.example.shopko.utils.enums.Filters
import com.example.shopko.utils.enums.HourFilter
import com.example.shopko.utils.enums.SortOption
import com.example.shopko.utils.enums.StoreFilter
import com.example.shopko.utils.sorting.sortStoreCombo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoresFragment : Fragment() {

    private lateinit var adapter: StoresAdapter
    private var storeList: List<StoreComboResult> = emptyList()

    private lateinit var loadingSpinner: ProgressBar
    private lateinit var resultStoreCount: TextView

    private var selectedFilter: Filters = Filters.BYPRICE
    private var selectedStore: StoreFilter = StoreFilter.Sve
    private var workTime: HourFilter = HourFilter.Sve
    private var selectedStoreCount: Int = 1
    private var currentSortOption: SortOption? = null
    private var selectedDistance: Float = 5f



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
        resultStoreCount = view.findViewById(R.id.resultCount)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewStores)
        adapter = StoresAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadData()

        val searchBar: EditText = view.findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStores(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        view.findViewById<LinearLayout>(R.id.btnOpenFilter).setOnClickListener {
            FilterBottomSheetDialog(
                selectedStoreCount,
                selectedDistance,
                selectedStore,
                workTime
            ) { newCount, newDistance, newStore, newWorkTime ->
                selectedStoreCount = newCount
                selectedDistance = newDistance
                selectedStore = newStore
                workTime = newWorkTime
                reloadData(requireView())
            }.show(childFragmentManager, "BottomSheetDialog")
        }




        view.findViewById<LinearLayout>(R.id.btnOpenSort).setOnClickListener {
            SortBottomSheetDialog(currentSortOption) { newSortOption ->
                newSortOption?.let {
                    currentSortOption = it
                    selectedFilter = when (it) {
                        SortOption.PRICE_ASC -> Filters.BYPRICE
                        SortOption.PRICE_DESC -> Filters.BYPRICE_DESC
                        SortOption.DISTANCE -> Filters.BYDISTANCE
                    }
                    loadData()
                }
            }.show(childFragmentManager, "SortDialog")
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE
            val filteredArticles = articleList.filter { it.isChecked }
            storeCombo = withContext(Dispatchers.IO) {
                sortStoreCombo(requireContext(), filteredArticles, selectedStoreCount, selectedFilter, selectedStore, workTime)
            }
            applyFiltersAndUpdateUI("")
            loadingSpinner.visibility = View.GONE
        }
    }

    private fun filterStores(query: String) {
        applyFiltersAndUpdateUI(query)
    }

    private fun applyFiltersAndUpdateUI(query: String) {
        val filteredList = storeCombo.filter {
            it.store.size <= selectedStoreCount && it.distance <= selectedDistance*1000 &&
                    it.store.any { s ->
                        s.name.contains(query, ignoreCase = true) ||
                                s.address.contains(query, ignoreCase = true) ||
                                s.city.contains(query, ignoreCase = true)
                    }
        }
        storeList = filteredList
        adapter.updateList(storeList)

        resultStoreCount.text = "${storeList.count()} rezultata"
    }
    private fun reloadData(view: View) {
        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE

            val filteredArticles = articleList.filter { it.isChecked }

            storeCombo = withContext(Dispatchers.IO) {
                sortStoreCombo(requireContext(), filteredArticles, selectedStoreCount, selectedFilter, selectedStore, workTime)
            }

            storeCombo = withContext(Dispatchers.IO) {
                val allCombos = sortStoreCombo(requireContext(), filteredArticles, selectedStoreCount, selectedFilter, selectedStore, workTime)
                allCombos.filter { it.distance <= selectedDistance*1000 }
            }


            storeList = storeCombo
            setupRecyclerView(view)
            loadingSpinner.visibility = View.GONE
            resultStoreCount.text = "${storeList.count()} rezultata"
        }
    }
    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewStores)
        adapter = StoresAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }


}
