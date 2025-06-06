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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.shopko.R
import com.example.shopko.utils.enums.SortOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetDialog(
    private var selectedOption: SortOption?,
    private val onSortSelected: (SortOption?) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var btnPriceAsc: Button
    private lateinit var btnPriceDesc: Button
    private lateinit var btnDistance: Button
    private lateinit var btnSave: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnReset: TextView

    private var chosenOption: SortOption? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        dialog?.window?.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPriceAsc = view.findViewById(R.id.btnPriceAsc)
        btnPriceDesc = view.findViewById(R.id.btnPriceDesc)
        btnDistance = view.findViewById(R.id.btnDistance)
        btnSave = view.findViewById(R.id.btnSave)
        btnBack = view.findViewById(R.id.btnBack)
        btnReset = view.findViewById(R.id.btnReset)

        if (selectedOption == null) {
            selectedOption = SortOption.PRICE_ASC
        }
        chosenOption = selectedOption
        updateSelectedUI(chosenOption)

        btnPriceAsc.setOnClickListener {
            chosenOption = SortOption.PRICE_ASC
            updateSelectedUI(chosenOption)
        }

        btnPriceDesc.setOnClickListener {
            chosenOption = SortOption.PRICE_DESC
            updateSelectedUI(chosenOption)
        }

        btnDistance.setOnClickListener {
            chosenOption = SortOption.DISTANCE
            updateSelectedUI(chosenOption)
        }

        btnSave.setOnClickListener {
            onSortSelected(chosenOption)
            dismiss()
        }

        btnBack.setOnClickListener {
            dismiss()
        }

        btnReset.setOnClickListener {
            chosenOption = SortOption.PRICE_ASC
            updateSelectedUI(chosenOption)
        }
    }

    private fun updateSelectedUI(currentSelection: SortOption?) {
        val selectedBg = R.drawable.sort_button_selected
        val unselectedBg = R.drawable.sort_button_unselected
        val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        val unselectedTextColor = ContextCompat.getColor(requireContext(), R.color.glavna)

        val buttons = listOf(
            Pair(btnPriceAsc, SortOption.PRICE_ASC),
            Pair(btnPriceDesc, SortOption.PRICE_DESC),
            Pair(btnDistance, SortOption.DISTANCE)
        )

        buttons.forEach { (button, option) ->
            if (currentSelection == option) {
                button.setBackgroundResource(selectedBg)
                button.setTextColor(selectedTextColor)
            } else {
                button.setBackgroundResource(unselectedBg)
                button.setTextColor(unselectedTextColor)
            }
        }
    }
}
