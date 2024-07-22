package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.databinding.FlagTransactionSuccessBottmSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FlaggingSuccessSheet : BottomSheetDialogFragment() {
    private lateinit var b: FlagTransactionSuccessBottmSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = FlagTransactionSuccessBottmSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.flaggingSuccessSheetDoneButton.setOnClickListener {
            dismiss()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        const val TAG = "FlaggingSuccessSheet"
    }
}