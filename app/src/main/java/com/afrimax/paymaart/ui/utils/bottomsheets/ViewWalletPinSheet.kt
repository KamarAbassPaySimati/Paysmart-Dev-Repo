package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.ViewWalletResponse
import com.afrimax.paymaart.databinding.ViewWalletBalancePinBottomSheetBinding
import com.afrimax.paymaart.ui.home.HomeActivity
import com.afrimax.paymaart.ui.utils.interfaces.HomeInterface
import com.afrimax.paymaart.util.AESCrypt
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.LoginPinTransformation
import com.afrimax.paymaart.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewWalletPinSheet : BottomSheetDialogFragment() {

    private lateinit var b: ViewWalletBalancePinBottomSheetBinding

    private lateinit var sheetCallback: HomeInterface

    private lateinit var viewWalletScope: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = ViewWalletBalancePinBottomSheetBinding.inflate(inflater, container, false)

        viewWalletScope = requireArguments().getString(Constants.VIEW_WALLET_SCOPE)
            ?: Constants.VIEW_WALLET_SIMPLE_SCOPE

        initViews()
        setUpListeners()

        return b.root
    }

    private fun initViews() {
        b.viewWalletPinSheetET.transformationMethod = LoginPinTransformation()
    }

    private fun setUpListeners() {

        b.viewWalletPinSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.viewWalletPinSheetViewButton.setOnClickListener {
            validateFieldForView()
        }

        configureEditTextPinChangeListener()

    }

    private fun validateFieldForView() {
        var isValid = true
        b.viewWalletPinSheetETWarningTV.visibility = View.GONE

        if (b.viewWalletPinSheetET.text.toString().isEmpty()) {
            isValid = false
            b.viewWalletPinSheetETWarningTV.visibility = View.VISIBLE
            b.viewWalletPinSheetETWarningTV.text = getString(R.string.required_field)
        } else if (b.viewWalletPinSheetET.text.toString().length != 6) {
            isValid = false
            b.viewWalletPinSheetETWarningTV.visibility = View.VISIBLE
            b.viewWalletPinSheetETWarningTV.text = getString(R.string.invalid_pin)
        }

        if (isValid) getWalletDetailsApi()

    }

    private fun configureEditTextPinChangeListener() {
        b.viewWalletPinSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(
                pinText: CharSequence?, index: Int, isBackSpace: Int, isNewDigit: Int
            ) {
                //Remove any error warning text while typing
                if (b.viewWalletPinSheetET.text.toString().isEmpty()) {
                    b.viewWalletPinSheetETWarningTV.visibility = View.VISIBLE
                    b.viewWalletPinSheetETWarningTV.text = getString(R.string.required_field)
                } else {
                    b.viewWalletPinSheetETWarningTV.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                //
            }

        })
    }

    private fun getWalletDetailsApi() {
        showButtonLoader(
            b.viewWalletPinSheetViewButton, b.viewWalletPinSheetViewButtonLoaderLottie
        )
        lifecycleScope.launch {
            val activity = requireActivity() as HomeActivity
            val idToken = activity.fetchIdToken()

            val encryptedPin = AESCrypt.encrypt(b.viewWalletPinSheetET.text.toString())
            val selfKycDetailsCall = ApiClient.apiService.viewWallet(idToken, encryptedPin)

            selfKycDetailsCall.enqueue(object : Callback<ViewWalletResponse> {
                override fun onResponse(
                    call: Call<ViewWalletResponse>, response: Response<ViewWalletResponse>
                ) {
                    hideButtonLoader(
                        b.viewWalletPinSheetViewButton,
                        b.viewWalletPinSheetViewButtonLoaderLottie,
                        getString(R.string.view)
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        sheetCallback.onClickViewBalance(viewWalletScope, body.data)
                        dismiss()
                    } else {
                        b.viewWalletPinSheetETWarningTV.visibility = View.VISIBLE
                        b.viewWalletPinSheetETWarningTV.text = getString(R.string.invalid_pin)
                    }
                }

                override fun onFailure(call: Call<ViewWalletResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.viewWalletPinSheetViewButton,
                        b.viewWalletPinSheetViewButtonLoaderLottie,
                        getString(R.string.view)
                    )
                    "Response".showLogE(t.message ?: "")
                    activity.showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        b.viewWalletPinSheetET.isEnabled = false
        b.viewWalletPinSheetCloseButton.isEnabled = false
        b.viewWalletPinSheetViewButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.viewWalletPinSheetET.isEnabled = true
        b.viewWalletPinSheetCloseButton.isEnabled = true
        b.viewWalletPinSheetViewButton.isEnabled = true
        loaderLottie.visibility = View.GONE

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as HomeInterface
    }

    companion object {
        const val TAG = "ViewWalletPinSheet"
    }
}