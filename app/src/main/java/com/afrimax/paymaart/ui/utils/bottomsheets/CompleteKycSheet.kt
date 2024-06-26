package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.databinding.CompleteKycBottomSheetBinding
import com.afrimax.paymaart.ui.kyc.KycProgressActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CompleteKycSheet: BottomSheetDialogFragment() {
    private lateinit var b: CompleteKycBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = CompleteKycBottomSheetBinding.inflate(inflater, container, false)
        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {
        b.sheetCompleteKycCompleteKycButton.setOnClickListener {
            dismiss()
            startActivity(Intent(requireActivity(), KycProgressActivity::class.java))
        }

        b.sheetCompleteKycSkipTV.setOnClickListener {
            dismiss()
        }

    }

    companion object {
        const val TAG = "CompleteKycSheet"
    }
}