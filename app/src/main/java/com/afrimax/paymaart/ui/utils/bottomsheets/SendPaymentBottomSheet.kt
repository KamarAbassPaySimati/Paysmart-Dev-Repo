package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.SendPaymentBottomSheetBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SendPaymentBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: SendPaymentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SendPaymentBottomSheetBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView(){
        val parentActivity = activity as BaseActivity
        val loginMode = parentActivity.retrieveLoginMode()

        when (loginMode){
            Constants.SELECTION_PIN -> {
                binding.sendPaymentPinContainer.visibility = View.VISIBLE
                binding.sendPaymentPasswordContainer.visibility = View.GONE
            }

            Constants.SELECTION_PASSWORD -> {
                binding.sendPaymentPinContainer.visibility = View.GONE
                binding.sendPaymentPasswordContainer.visibility = View.VISIBLE
            }
        }

        binding.sendPaymentClose.setOnClickListener {
            dismiss()
        }

        binding.sendPaymentConfirm.setOnClickListener {
            when(loginMode){
                Constants.SELECTION_PIN -> validatePinField()
                Constants.SELECTION_PASSWORD -> validatePasswordField()
            }
        }

        binding.sendPaymentPasswordToggle.setOnClickListener {
            onTogglePasswordClicked()
        }

        binding.sendPaymentPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(
                pinText: CharSequence?, index: Int, isBackSpace: Int, isNewDigit: Int
            ) {
                //Remove any error warning text while typing
                if (binding.sendPaymentPin.text.toString().isEmpty()) {
                    binding.sendPaymentPinETWarning.visibility = View.VISIBLE
                    binding.sendPaymentPinETWarning.text = getString(R.string.required_field)
                } else {
                    binding.sendPaymentPinETWarning.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}

        })

        binding.sendPaymentPassword.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (binding.sendPaymentPassword.text.isEmpty()) {
                    binding.sendPaymentPasswordETWarning.visibility = View.VISIBLE
                    binding.sendPaymentPasswordETWarning.text = getString(R.string.required_field)
                    binding.sendPaymentPasswordBox
                        .background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    binding.sendPaymentPasswordToggle.visibility = View.GONE
                } else {
                    binding.sendPaymentPasswordETWarning.visibility = View.GONE

                    binding.sendPaymentPasswordBox.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    binding.sendPaymentPasswordToggle.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun onTogglePasswordClicked(){
        val passwordTransformation = binding.sendPaymentPassword.transformationMethod
        if (passwordTransformation != null) {
            //Password is hidden make it visible
            binding.sendPaymentPassword.transformationMethod = null
            binding.sendPaymentPasswordToggle.text = getString(R.string.hide)
        } else {
            //Make it hidden
            binding.sendPaymentPassword.transformationMethod = PasswordTransformationMethod()
            binding.sendPaymentPasswordToggle.text = getString(R.string.show)
        }
        binding.sendPaymentPassword.setSelection(binding.sendPaymentPassword.length())
    }

    private fun validatePinField() {
        var isValid = true
        binding.sendPaymentPinETWarning.visibility = View.GONE

        if (binding.sendPaymentPin.text.toString().isEmpty()) {
            isValid = false
            binding.sendPaymentPinETWarning.visibility = View.VISIBLE
            binding.sendPaymentPinETWarning.text = getString(R.string.required_field)
        } else if (binding.sendPaymentPin.text.toString().length != 6) {
            isValid = false
            binding.sendPaymentPinETWarning.visibility = View.VISIBLE
            binding.sendPaymentPinETWarning.text = getString(R.string.invalid_pin)
        }

        if (isValid) { }
    }

    private fun validatePasswordField(){
        var isValid = true
        binding.sendPaymentPasswordETWarning.visibility = View.GONE
        binding.sendPaymentPasswordBox.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        if (binding.sendPaymentPassword.text.isEmpty()) {
            isValid = false
            binding.sendPaymentPasswordETWarning.visibility = View.VISIBLE
            binding.sendPaymentPasswordETWarning.text = getString(R.string.required_field)
            binding.sendPaymentPasswordBox.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }

        if (isValid) { }
    }

    companion object {
        const val TAG = "SendPaymentBottomSheet"
    }
}