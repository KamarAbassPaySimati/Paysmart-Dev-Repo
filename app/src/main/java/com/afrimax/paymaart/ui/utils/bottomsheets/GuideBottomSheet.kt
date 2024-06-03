package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.GuideBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GuideBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: GuideBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GuideBottomSheetBinding.inflate(inflater, container, false)
        binding.registrationGuideSheetCloseButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
    override fun getTheme(): Int = R.style.Guide_Bottom_Sheet_Style

    companion object {
        const val TAG = "RegistrationGuideSheet"
    }
}