package com.example.shopko

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.example.shopko.enums.Filters

class FilterDialogFragment(
    private val currentFilter: Filters,
    private val currentStoreCount: Int,
    private val onApply: (Filters, Int) -> Unit
) : DialogFragment() {

    private lateinit var priceRadioButton: RadioButton
    private lateinit var distanceRadioButton: RadioButton
    private lateinit var storeCountSpinner: Spinner
    private lateinit var applyButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter, container, false)

        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        priceRadioButton = view.findViewById(R.id.radioPrice)
        distanceRadioButton = view.findViewById(R.id.radioDistance)
        storeCountSpinner = view.findViewById(R.id.spinnerStoreCount)
        applyButton = view.findViewById(R.id.btnApply)

        priceRadioButton.isChecked = currentFilter == Filters.BYPRICE
        distanceRadioButton.isChecked = currentFilter == Filters.BYDISTANCE

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.store_count_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            storeCountSpinner.adapter = adapter
        }

        val spinnerIndex = resources.getStringArray(R.array.store_count_options)
            .indexOf(currentStoreCount.toString())
        storeCountSpinner.setSelection(spinnerIndex)

        applyButton.setOnClickListener {
            val selectedFilter = if (priceRadioButton.isChecked) Filters.BYPRICE else Filters.BYDISTANCE
            val selectedStoreCount = storeCountSpinner.selectedItem.toString().toInt()

            onApply(selectedFilter, selectedStoreCount)
            dismiss()
        }

        return view
    }
}
