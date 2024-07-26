package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.databinding.FilterParameterBottomSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.RefundRequestSortFilterInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterParameterBottom: BottomSheetDialogFragment() {
    private lateinit var binding: FilterParameterBottomSheetBinding
    private lateinit var sheetCallback: RefundRequestSortFilterInterface
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FilterParameterBottomSheetBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView() {
        val parameterList = mutableListOf<String>()
        binding.filterParameterApplyButton.setOnClickListener {
            if (binding.filterParameterRefundedCheckbox.isChecked) parameterList.add("refunded")
            if (binding.filterParameterPendingCheckbox.isChecked) parameterList.add("pending")
            if (binding.filterParameterRejectedCheckbox.isChecked) parameterList.add("rejected")
            dismiss()
            sheetCallback.onFilterParameterSelected(parameterList)
        }

        binding.filterParameterClearButton.setOnClickListener {
            binding.filterParameterRefundedCheckbox.isChecked = false
            binding.filterParameterPendingCheckbox.isChecked = false
            binding.filterParameterRejectedCheckbox.isChecked = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as RefundRequestSortFilterInterface
    }

    companion object{
        const val TAG = "FilterParameterBottom"
    }
}