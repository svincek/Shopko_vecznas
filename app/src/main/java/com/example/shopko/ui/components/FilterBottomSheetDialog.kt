package com.example.shopko.ui.components

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.shopko.R
import com.example.shopko.utils.enums.HourFilter
import com.example.shopko.utils.enums.StoreFilter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider

class FilterBottomSheetDialog(
    private val currentStoreCount: Int,
    private val currentDistance: Float,
    private val currentStoreFilter: StoreFilter,
    private val currentHourFilter: HourFilter,
    private val onApplyFilters: (Int, Float, StoreFilter, HourFilter) -> Unit
) : BottomSheetDialogFragment() {




    private var chosenStoreOption: StoreFilter? = StoreFilter.Sve
    private var chosenWorkingOption: HourFilter? = HourFilter.Sve

    private lateinit var storeCount: Slider
    private lateinit var storeDistance: Slider
    private lateinit var btnSave: ImageButton
    private lateinit var btnCancel: ImageButton
    private lateinit var btnKaufland: Button
    private lateinit var btnPlodine: Button
    private lateinit var btnLidl: Button
    private lateinit var btnKonzum: Button
    private lateinit var btnEurospin: Button
    private lateinit var btnStudenac: Button
    private lateinit var btnSpar: Button
    private lateinit var btnTommy: Button
    private lateinit var btnStoreAll: Button
    private lateinit var btnWorkingAll: Button
    private lateinit var btnWorkingNow: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter, container, false)

        // Inicijalizacija komponenti
        storeCount = view.findViewById(R.id.sliderStoreCount)
        storeDistance = view.findViewById(R.id.sliderDistance)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnKaufland = view.findViewById(R.id.btnKaufland)
        btnPlodine = view.findViewById(R.id.btnPlodine)
        btnLidl = view.findViewById(R.id.btnLidl)
        btnKonzum = view.findViewById(R.id.btnKonzum)
        btnEurospin = view.findViewById(R.id.btnEurospin)
        btnStudenac = view.findViewById(R.id.btnStudenac)
        btnSpar = view.findViewById(R.id.btnSpar)
        btnTommy = view.findViewById(R.id.btnTommy)
        btnStoreAll = view.findViewById(R.id.btnStoreSve)

        btnWorkingAll = view.findViewById(R.id.btnWorkingAll)
        btnWorkingNow = view.findViewById(R.id.btnWorkingNow)

        storeCount.value = currentStoreCount.toFloat()
        storeDistance.value = currentDistance

        btnSave.setOnClickListener {
            val selectedCount = storeCount.value.toInt()
            val selectedDistance = storeDistance.value
            val selectedStore = chosenStoreOption ?: StoreFilter.Sve
            val selectedHour = chosenWorkingOption ?: HourFilter.Sve
            onApplyFilters(selectedCount, selectedDistance, selectedStore, selectedHour)
            dismiss()
        }



        btnCancel.setOnClickListener {
            dismiss()
        }

        btnStoreAll.setOnClickListener {
            chosenStoreOption = StoreFilter.Sve
            updateSelectedUI()
        }
        btnKaufland.setOnClickListener {
            chosenStoreOption = StoreFilter.Kaufland
            updateSelectedUI()
        }
        btnPlodine.setOnClickListener {
            chosenStoreOption = StoreFilter.Plodine
            updateSelectedUI()
        }
        btnLidl.setOnClickListener {
            chosenStoreOption = StoreFilter.Lidl
            updateSelectedUI()
        }
        btnKonzum.setOnClickListener {
            chosenStoreOption = StoreFilter.Konzum
            updateSelectedUI()
        }
        btnEurospin.setOnClickListener {
            chosenStoreOption = StoreFilter.Eurospin
            updateSelectedUI()
        }
        btnStudenac.setOnClickListener {
            chosenStoreOption = StoreFilter.Studenac
            updateSelectedUI()
        }
        btnSpar.setOnClickListener {
            chosenStoreOption = StoreFilter.Spar
            updateSelectedUI()
        }
        btnTommy.setOnClickListener {
            chosenStoreOption = StoreFilter.Tommy
            updateSelectedUI()
        }

        btnWorkingAll.setOnClickListener {
            chosenWorkingOption = HourFilter.Sve
            updateSelectedUI()
        }
        btnWorkingNow.setOnClickListener {
            chosenWorkingOption = HourFilter.OtvorenoSada
            updateSelectedUI()
        }

        return view
    }

    private fun updateSelectedUI() {
        val selectedBg = R.drawable.sort_button_selected
        val unselectedBg = R.drawable.sort_button_unselected
        val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        val unselectedTextColor = ContextCompat.getColor(requireContext(), R.color.glavna)

        val storeButtons = listOf(
            Pair(btnKaufland, StoreFilter.Kaufland),
            Pair(btnLidl, StoreFilter.Lidl),
            Pair(btnPlodine, StoreFilter.Plodine),
            Pair(btnKonzum, StoreFilter.Konzum),
            Pair(btnEurospin, StoreFilter.Eurospin),
            Pair(btnSpar, StoreFilter.Spar),
            Pair(btnTommy, StoreFilter.Tommy),
            Pair(btnStudenac, StoreFilter.Studenac),
            Pair(btnStoreAll, StoreFilter.Sve)
        )

        val workingButtons = listOf(
            Pair(btnWorkingAll, HourFilter.Sve),
            Pair(btnWorkingNow, HourFilter.OtvorenoSada)
        )

        storeButtons.forEach { (button, option) ->
            if (chosenStoreOption == option) {
                button.setBackgroundResource(selectedBg)
                button.setTextColor(selectedTextColor)
            } else {
                button.setBackgroundResource(unselectedBg)
                button.setTextColor(unselectedTextColor)
            }
        }

        workingButtons.forEach { (button, option) ->
            if (chosenWorkingOption == option) {
                button.setBackgroundResource(selectedBg)
                button.setTextColor(selectedTextColor)
            } else {
                button.setBackgroundResource(unselectedBg)
                button.setTextColor(unselectedTextColor)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        dialog?.window?.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
