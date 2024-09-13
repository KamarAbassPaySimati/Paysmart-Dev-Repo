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
import com.afrimax.paysimati.databinding.DeleteAccountOthersSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.DeleteAccountInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteAccountOthersSheet: BottomSheetDialogFragment() {
    private lateinit var b: DeleteAccountOthersSheetBinding

    private lateinit var sheetCallback: DeleteAccountInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = DeleteAccountOthersSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.deleteAccountOthersSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.deleteAccountOthersSheetSubmitButton.setOnClickListener {
            validateFieldForSubmit()
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()
    }

    private fun validateFieldForSubmit() {
        var isValid = true

        if (b.deleteAccountOthersSheetET.text.isEmpty()) {
            isValid = false
            b.deleteAccountOthersSheetET.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }

        if (isValid) {
            sheetCallback.onDeleteReasonTyped(b.deleteAccountOthersSheetET.text.toString())
            dismiss()
        }
    }

    private fun configureEditTextFocusListener() {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        b.deleteAccountOthersSheetET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.deleteAccountOthersSheetET.background = focusDrawable
                else b.deleteAccountOthersSheetET.background = notInFocusDrawable
            }
    }

    private fun configureEditTextChangeListener() {
        b.deleteAccountOthersSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.deleteAccountOthersSheetET.text.isEmpty()) {
                    b.deleteAccountOthersSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    b.deleteAccountOthersSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as DeleteAccountInterface
    }


    companion object {
        const val TAG = "DeleteAccountOthersSheet"
    }
}