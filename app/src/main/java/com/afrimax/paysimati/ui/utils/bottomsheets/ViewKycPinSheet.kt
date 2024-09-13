package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.SelfKycDetailsResponse
import com.afrimax.paysimati.databinding.ViewKycPinBottomSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.ViewSelfKycInterface
import com.afrimax.paysimati.ui.viewkyc.ViewKycDetailsActivity
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.LoginPinTransformation
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewKycPinSheet : BottomSheetDialogFragment() {
    private lateinit var b: ViewKycPinBottomSheetBinding

    private lateinit var sheetCallback: ViewSelfKycInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = ViewKycPinBottomSheetBinding.inflate(inflater, container, false)

        initViews()
        setUpListeners()

        return b.root
    }

    private fun initViews() {
        b.viewKycPinSheetET.transformationMethod = LoginPinTransformation()
    }

    private fun setUpListeners() {

        b.viewKycPinSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.viewKycPinSheetViewButton.setOnClickListener {
            validateFieldForView()
        }

        configureEditTextPinChangeListener()

    }

    private fun validateFieldForView() {
        var isValid = true
        b.viewKycPinSheetETWarningTV.visibility = View.GONE

        if (b.viewKycPinSheetET.text.toString().isEmpty()) {
            isValid = false
            b.viewKycPinSheetETWarningTV.visibility = View.VISIBLE
            b.viewKycPinSheetETWarningTV.text = getString(R.string.required_field)
        } else if (b.viewKycPinSheetET.text.toString().length != 6) {
            isValid = false
            b.viewKycPinSheetETWarningTV.visibility = View.VISIBLE
            b.viewKycPinSheetETWarningTV.text = getString(R.string.invalid_pin)
        }

        if (isValid) getKycDetailsApi()

    }

    private fun configureEditTextPinChangeListener() {
        b.viewKycPinSheetET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(
                pinText: CharSequence?, index: Int, isBackSpace: Int, isNewDigit: Int
            ) {
                //Remove any error warning text while typing
                if (b.viewKycPinSheetET.text.toString().isEmpty()) {
                    b.viewKycPinSheetETWarningTV.visibility = View.VISIBLE
                    b.viewKycPinSheetETWarningTV.text = getString(R.string.required_field)
                } else {
                    b.viewKycPinSheetETWarningTV.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                //
            }

        })
    }

    private fun getKycDetailsApi() {
        showButtonLoader(
            b.viewKycPinSheetViewButton, b.viewKycPinSheetViewButtonLoaderLottie
        )
        lifecycleScope.launch {
            val activity = requireActivity() as ViewKycDetailsActivity
            val idToken = activity.fetchIdToken()

            val encryptedPin = AESCrypt.encrypt(b.viewKycPinSheetET.text.toString())
            val selfKycDetailsCall = ApiClient.apiService.getSelfKycDetails(encryptedPin, idToken)

            selfKycDetailsCall.enqueue(object : Callback<SelfKycDetailsResponse> {
                override fun onResponse(
                    call: Call<SelfKycDetailsResponse>, response: Response<SelfKycDetailsResponse>
                ) {
                    hideButtonLoader(
                        b.viewKycPinSheetViewButton,
                        b.viewKycPinSheetViewButtonLoaderLottie,
                        getString(R.string.view)
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        sheetCallback.onClickViewButton(body.data)
                        dismiss()
                    } else {
                        b.viewKycPinSheetETWarningTV.visibility = View.VISIBLE
                        b.viewKycPinSheetETWarningTV.text = getString(R.string.invalid_pin)
                    }
                }

                override fun onFailure(call: Call<SelfKycDetailsResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.viewKycPinSheetViewButton,
                        b.viewKycPinSheetViewButtonLoaderLottie,
                        getString(R.string.view)
                    )
                    activity.showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        b.viewKycPinSheetET.isEnabled = false
        b.viewKycPinSheetCloseButton.isEnabled = false
        b.viewKycPinSheetViewButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.viewKycPinSheetET.isEnabled = true
        b.viewKycPinSheetCloseButton.isEnabled = true
        b.viewKycPinSheetViewButton.isEnabled = true
        loaderLottie.visibility = View.GONE

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as ViewSelfKycInterface
    }

    companion object {
        const val TAG = "ViewKycPinSheet"
    }
}