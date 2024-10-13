package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.data.utils.safeApiCall
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.databinding.ViewKycPasswordBottomSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.ViewSelfKycInterface
import com.afrimax.paysimati.ui.viewkyc.ViewKycDetailsActivity
import com.afrimax.paysimati.util.AESCrypt
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ViewKycPasswordSheet : BottomSheetDialogFragment() {
    private lateinit var b: ViewKycPasswordBottomSheetBinding

    private lateinit var sheetCallback: ViewSelfKycInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = ViewKycPasswordBottomSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.viewKycPasswordSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.viewKycPasswordSheetViewButton.setOnClickListener {
            validateFieldForView()
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()

    }

    private fun validateFieldForView() {
        var isValid = true
        b.viewKycPasswordSheetETWarningTV.visibility = View.GONE
        b.viewKycPasswordSheetET.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        if (b.viewKycPasswordSheetET.text.isEmpty()) {
            isValid = false
            b.viewKycPasswordSheetETWarningTV.visibility = View.VISIBLE
            b.viewKycPasswordSheetETWarningTV.text = getString(R.string.required_field)
            b.viewKycPasswordSheetET.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }

        if (isValid) getKycDetailsApi()

    }

    private fun configureEditTextFocusListener() {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val errorDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        b.viewKycPasswordSheetET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.viewKycPasswordSheetET.background = focusDrawable
            else if (b.viewKycPasswordSheetETWarningTV.isVisible) b.viewKycPasswordSheetET.background =
                errorDrawable
            else b.viewKycPasswordSheetET.background = notInFocusDrawable
        }
    }

    private fun configureEditTextChangeListener() {
        b.viewKycPasswordSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.viewKycPasswordSheetET.text.isEmpty()) {
                    b.viewKycPasswordSheetETWarningTV.visibility = View.VISIBLE
                    b.viewKycPasswordSheetETWarningTV.text = getString(R.string.required_field)
                    b.viewKycPasswordSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    b.viewKycPasswordSheetETWarningTV.visibility = View.GONE
                    b.viewKycPasswordSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun getKycDetailsApi() {
        showButtonLoader(
            b.viewKycPasswordSheetViewButton, b.viewKycPasswordSheetViewButtonLoaderLottie
        )
        lifecycleScope.launch {
            val activity = requireActivity() as ViewKycDetailsActivity
            val idToken = activity.fetchIdToken()

            val encryptedPassword = AESCrypt.encrypt(b.viewKycPasswordSheetET.text.toString())
            val viewKycCall = safeApiCall {
                ApiClient.apiService.getSelfKycDetails(encryptedPassword, idToken)
            }

            when (viewKycCall) {
                is GenericResult.Success -> {
                    sheetCallback.onClickViewButton(viewKycCall.data.data)
                    dismiss()
                }

                is GenericResult.Error -> {
                    b.viewKycPasswordSheetETWarningTV.visibility = View.VISIBLE
                    b.viewKycPasswordSheetETWarningTV.text = getString(R.string.invalid_password)
                    b.viewKycPasswordSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                }
            }

            hideButtonLoader(
                b.viewKycPasswordSheetViewButton,
                b.viewKycPasswordSheetViewButtonLoaderLottie,
                getString(R.string.view)
            )
        }
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = getString(R.string.empty_string)
        b.viewKycPasswordSheetET.isEnabled = false
        b.viewKycPasswordSheetCloseButton.isEnabled = false
        b.viewKycPasswordSheetViewButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.viewKycPasswordSheetET.isEnabled = true
        b.viewKycPasswordSheetCloseButton.isEnabled = true
        b.viewKycPasswordSheetViewButton.isEnabled = true
        loaderLottie.visibility = View.GONE

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as ViewSelfKycInterface
    }

    companion object {
        const val TAG = "ViewKycPasswordSheet"
    }
}