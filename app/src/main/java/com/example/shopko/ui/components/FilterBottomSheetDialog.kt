// FilterBottomSheetDialog.kt
package com.example.shopko.ui.components


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import com.example.shopko.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FilterBottomSheetDialog(

) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
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