package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.PasswordGuideBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PasswordGuideBottomSheet: BottomSheetDialogFragment() {
    private lateinit var b: PasswordGuideBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = PasswordGuideBottomSheetBinding.inflate(inflater, container, false)

        b.passwordGuideSheetCloseButton.setOnClickListener {
            dismiss()
        }

        return b.root
    }

    override fun getTheme(): Int = R.style.Base_Theme_PaymaartAgent_Bottom_Sheet_Registration_Guide

    companion object {
        const val TAG = "PasswordGuideSheet"
    }
}