package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.NumberOfReferenceSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.KycYourIdentityInterface
import com.afrimax.paysimati.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycNumberOfReferenceSheet: BottomSheetDialogFragment() {
    private lateinit var b: NumberOfReferenceSheetBinding

    private lateinit var sheetCallback: KycYourIdentityInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = NumberOfReferenceSheetBinding.inflate(inflater, container, false)

        val numberOfReferences =
            requireArguments().getString(Constants.KYC_NUMBER_OF_REFERENCES) ?: ""
        b.kycNumberOfReferencesSheetET.setText(numberOfReferences)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.kycNumberOfReferencesSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycNumberOfReferencesSheetSubmitButton.setOnClickListener {
            validateFieldForSubmit()
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()

    }

    private fun validateFieldForSubmit() {
        var isValid = true

        if (b.kycNumberOfReferencesSheetET.text.isEmpty()) {
            isValid = false
            b.kycNumberOfReferencesSheetET.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }

        if (isValid) {
            sheetCallback.onNumberOfReferencesTyped(b.kycNumberOfReferencesSheetET.text.toString())
            dismiss()
        }
    }

    private fun configureEditTextFocusListener() {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        b.kycNumberOfReferencesSheetET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.kycNumberOfReferencesSheetET.background = focusDrawable
                else b.kycNumberOfReferencesSheetET.background = notInFocusDrawable
            }
    }

    private fun configureEditTextChangeListener() {
        b.kycNumberOfReferencesSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.kycNumberOfReferencesSheetET.text.isEmpty()) {
                    b.kycNumberOfReferencesSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    b.kycNumberOfReferencesSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycYourIdentityInterface
    }


    companion object {
        const val TAG = "kycNumberOfReferncesSheet"
    }
}