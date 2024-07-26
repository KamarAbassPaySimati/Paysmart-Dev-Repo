package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.SortParamterBottomSheetBinding
import com.afrimax.paymaart.ui.refundrequest.SortParameter
import com.afrimax.paymaart.ui.utils.interfaces.RefundRequestSortFilterInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortParameterBottomSheet(private val sortParameters: List<SortParameter>) : BottomSheetDialogFragment() {
    private lateinit var binding: SortParamterBottomSheetBinding
    private lateinit var sheetCallback: RefundRequestSortFilterInterface
    private lateinit var radioGroup: RadioGroup
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SortParamterBottomSheetBinding.inflate(inflater, container, false)
        radioGroup = binding.sortParameterRadioGroup
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.sortParameterRadioGroup.orientation = LinearLayout.VERTICAL
        for (i in sortParameters.indices) {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_radio_button, radioGroup, false)
            val textView = view.findViewById<TextView>(R.id.customRadioText)
            val radioButton = view.findViewById<AppCompatRadioButton>(R.id.customRadioButton)
            textView.text = sortParameters[i].name
            radioButton.id = i
            radioButton.setOnClickListener {
                dismiss()
                sheetCallback.onSortParameterSelected(sortParameters[i].id)
            }
            radioGroup.addView(view)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as RefundRequestSortFilterInterface
    }

    companion object{
        const val TAG = "SortParameterBottomSheet"
    }
}