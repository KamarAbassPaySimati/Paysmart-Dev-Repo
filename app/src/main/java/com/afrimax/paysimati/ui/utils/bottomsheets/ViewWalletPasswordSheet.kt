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
import com.afrimax.paysimati.databinding.ViewWalletBalancePasswordBottomSheetBinding
import com.afrimax.paysimati.ui.home.HomeActivity
import com.afrimax.paysimati.ui.utils.interfaces.HomeInterface
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.Constants
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ViewWalletPasswordSheet : BottomSheetDialogFragment() {

    private lateinit var b: ViewWalletBalancePasswordBottomSheetBinding

    private lateinit var sheetCallback: HomeInterface
    private lateinit var viewWalletScope: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = ViewWalletBalancePasswordBottomSheetBinding.inflate(inflater, container, false)


        viewWalletScope = requireArguments().getString(Constants.VIEW_WALLET_SCOPE)
            ?: Constants.VIEW_WALLET_SIMPLE_SCOPE

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.viewWalletPasswordSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.viewWalletPasswordSheetViewButton.setOnClickListener {
            lifecycleScope.launch {
                validateFieldForView()
            }
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()

    }

    private suspend fun validateFieldForView() {
        var isValid = true
        b.viewWalletPasswordSheetETWarningTV.visibility = View.GONE
        b.viewWalletPasswordSheetET.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        if (b.viewWalletPasswordSheetET.text.isEmpty()) {
            isValid = false
            b.viewWalletPasswordSheetETWarningTV.visibility = View.VISIBLE
            b.viewWalletPasswordSheetETWarningTV.text = getString(R.string.required_field)
            b.viewWalletPasswordSheetET.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }

        if (isValid) getWalletDetailsApi()

    }

    private fun configureEditTextFocusListener() {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val errorDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        b.viewWalletPasswordSheetET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.viewWalletPasswordSheetET.background = focusDrawable
                else if (b.viewWalletPasswordSheetETWarningTV.isVisible) b.viewWalletPasswordSheetET.background =
                    errorDrawable
                else b.viewWalletPasswordSheetET.background = notInFocusDrawable
            }
    }

    private fun configureEditTextChangeListener() {
        b.viewWalletPasswordSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.viewWalletPasswordSheetET.text.isEmpty()) {
                    b.viewWalletPasswordSheetETWarningTV.visibility = View.VISIBLE
                    b.viewWalletPasswordSheetETWarningTV.text = getString(R.string.required_field)
                    b.viewWalletPasswordSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    b.viewWalletPasswordSheetETWarningTV.visibility = View.GONE
                    b.viewWalletPasswordSheetET.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private suspend fun getWalletDetailsApi() {
        showButtonLoader(
            b.viewWalletPasswordSheetViewButton, b.viewWalletPasswordSheetViewButtonLoaderLottie
        )

        val activity = requireActivity() as HomeActivity
        val idToken = activity.fetchIdToken()
        activity.hideKeyboard(view, requireContext())
        val encryptedPassword = AESCrypt.encrypt(b.viewWalletPasswordSheetET.text.toString())
        val viewWalletCall = safeApiCall {
            ApiClient.apiService.viewWallet(idToken, encryptedPassword)
        }

        when (viewWalletCall) {
            is GenericResult.Success -> {
                sheetCallback.onClickViewBalance(viewWalletScope, viewWalletCall.data.data)
                dismiss()
            }

            is GenericResult.Error -> {
                b.viewWalletPasswordSheetETWarningTV.visibility = View.VISIBLE
                b.viewWalletPasswordSheetETWarningTV.text = getString(R.string.invalid_password)
                b.viewWalletPasswordSheetET.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.bg_edit_text_error
                )
            }
        }

        hideButtonLoader(
            b.viewWalletPasswordSheetViewButton,
            b.viewWalletPasswordSheetViewButtonLoaderLottie,
            getString(R.string.view)
        )
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        b.viewWalletPasswordSheetET.isEnabled = false
        b.viewWalletPasswordSheetCloseButton.isEnabled = false
        b.viewWalletPasswordSheetViewButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.viewWalletPasswordSheetET.isEnabled = true
        b.viewWalletPasswordSheetCloseButton.isEnabled = true
        b.viewWalletPasswordSheetViewButton.isEnabled = true
        loaderLottie.visibility = View.GONE

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as HomeInterface
    }

    companion object {
        const val TAG = "viewWalletPasswordSheet"
    }
}