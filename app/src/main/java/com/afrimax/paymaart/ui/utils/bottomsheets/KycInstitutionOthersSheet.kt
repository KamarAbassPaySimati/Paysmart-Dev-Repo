package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.KycInstitutionOthersSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.KycOccupationEducationInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycInstitutionOthersSheet : BottomSheetDialogFragment() {

    private lateinit var b: KycInstitutionOthersSheetBinding

    private lateinit var sheetCallback: KycOccupationEducationInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = KycInstitutionOthersSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.kycInstitutionOthersSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycInstitutionOthersSheetSubmitButton.setOnClickListener {
            validateFieldForSubmit()
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()

    }

    private fun validateFieldForSubmit() {
        var isValid = true

        if (b.kycInstitutionOthersSheetET.text.isEmpty()) {
            isValid = false
            b.kycInstitutionOthersSheetET.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }

        if (isValid) {
            sheetCallback.onInstitutionTyped(b.kycInstitutionOthersSheetET.text.toString())
            dismiss()
        }
    }

    private fun configureEditTextFocusListener() {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        b.kycInstitutionOthersSheetET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.kycInstitutionOthersSheetET.background = focusDrawable
                else b.kycInstitutionOthersSheetET.background = notInFocusDrawable
            }
    }

    private fun configureEditTextChangeListener() {
        b.kycInstitutionOthersSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.kycInstitutionOthersSheetET.text.isEmpty()) {
                    b.kycInstitutionOthersSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    b.kycInstitutionOthersSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycOccupationEducationInterface
    }


    companion object {
        const val TAG = "KycInstitutionOthersSheet"
    }
}