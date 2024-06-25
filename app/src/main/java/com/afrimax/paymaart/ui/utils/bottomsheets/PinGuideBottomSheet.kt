package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.PinGuideBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PinGuideBottomSheet: BottomSheetDialogFragment() {
    private lateinit var b: PinGuideBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = PinGuideBottomSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.pinGuideSheetCloseButton.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.Base_Theme_PaymaartAgent_Bottom_Sheet_Registration_Guide

    companion object {
        const val TAG = "PinGuideSheet"
    }
}