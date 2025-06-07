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
import com.example.shopko.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider

class FilterBottomSheetDialog(
    private val currentStoreCount: Int,
    private val currentDistance: Float,
    private val onApplyFilters: (Int, Float) -> Unit
) : BottomSheetDialogFragment() {


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
    private lateinit var btnInterspar: Button
    private lateinit var btnTommy: Button
    private lateinit var btnStoreAll: Button
    private lateinit var btnWorkingAll: Button
    private lateinit var btnWorkingNow: Button
    private lateinit var btnWorkingSunday: Button

    interface OnFilterChangeListener {
        fun onFilterChanged(storeCount: Int, maxDistance: Float)
    }

    private var filterChangeListener: OnFilterChangeListener? = null

    fun setOnFilterChangeListener(listener: OnFilterChangeListener) {
        filterChangeListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter, container, false)

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
        btnInterspar = view.findViewById(R.id.btnInterspar)
        btnTommy = view.findViewById(R.id.btnTommy)
        btnStoreAll = view.findViewById(R.id.btnStoreSve)


        // Postavi početne vrijednosti
        storeCount.value = currentStoreCount.toFloat()
        storeDistance.value = currentDistance

        btnSave.setOnClickListener {
            val selectedCount = storeCount.value.toInt()
            val selectedDistance = storeDistance.value
            onApplyFilters(selectedCount, selectedDistance)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        return view
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
