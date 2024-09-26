package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.SortParamterBottomSheetBinding
import com.afrimax.paysimati.ui.refundrequest.SortParameter
import com.afrimax.paysimati.ui.utils.interfaces.RefundRequestSortFilterInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortParameterBottomSheet(private val sortParameters: List<SortParameter>, private val selectedId: Int) : BottomSheetDialogFragment() {
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
            view.setOnClickListener {
                dismiss()
                sheetCallback.onSortParameterSelected(i)
            }
            radioGroup.addView(view)
        }
        if (selectedId >= 0) { radioGroup.check(selectedId) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as RefundRequestSortFilterInterface
    }

    companion object{
        const val TAG = "SortParameterBottomSheet"
    }
}