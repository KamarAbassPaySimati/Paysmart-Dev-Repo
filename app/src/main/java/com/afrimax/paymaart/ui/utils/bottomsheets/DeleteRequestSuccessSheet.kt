package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.databinding.DeleteRequestSuccessSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteRequestSuccessSheet: BottomSheetDialogFragment() {
    private lateinit var b: DeleteRequestSuccessSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = DeleteRequestSuccessSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.deleteRequestSuccessSheetBackToHomeButton.setOnClickListener {
            dismiss()
            requireActivity().finish()
        }
    }

    companion object {
        const val TAG = "DeleteRequestSuccessSheet"
    }
}