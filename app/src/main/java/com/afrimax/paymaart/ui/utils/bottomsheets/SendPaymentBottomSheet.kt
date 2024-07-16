package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paymaart.data.model.SubscriptionPaymentRequestBody
import com.afrimax.paymaart.data.model.SubscriptionPaymentSuccessfulResponse
import com.afrimax.paymaart.databinding.SendPaymentBottomSheetBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paymaart.util.AESCrypt
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.LoginPinTransformation
import com.afrimax.paymaart.util.showLogE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SendPaymentBottomSheet(private val subscriptionDetailsRequestBody: SubscriptionDetailsRequestBody) : BottomSheetDialogFragment() {
    private lateinit var binding: SendPaymentBottomSheetBinding
    private lateinit var sheetCallback: SendPaymentInterface
    private lateinit var parentActivity: BaseActivity
    private lateinit var loginMode: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = SendPaymentBottomSheetBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView(){
        parentActivity = activity as BaseActivity
        loginMode = parentActivity.retrieveLoginMode() ?: Constants.SELECTION_PIN
        binding.sendPaymentPin.transformationMethod = LoginPinTransformation()
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
                pinText: CharSequence?, index: Int, isBackSpace: Int, isNewDigit: Int,
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

        if (isValid) {
            onConfirmClicked(binding.sendPaymentPin.text.toString())
        }
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

        if (isValid) {
            onConfirmClicked(binding.sendPaymentPassword.text.toString())
        }
    }

    private fun onConfirmClicked(password: String) {
        val activity = context as BaseActivity
        val credential = AESCrypt.encrypt(password)
        showButtonLoader()
        val subscriptionPaymentRequestBody = SubscriptionPaymentRequestBody(
            referenceNumber = subscriptionDetailsRequestBody.referenceNumber,
            subType = subscriptionDetailsRequestBody.subType,
            credentials = credential
        )

        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val subscriptionHandler = ApiClient.apiService.subscriptionPayment(
                idToken,
                subscriptionPaymentRequestBody
            )

            subscriptionHandler.enqueue(object : Callback<SubscriptionPaymentSuccessfulResponse> {
                override fun onResponse(call: Call<SubscriptionPaymentSuccessfulResponse>, response: Response<SubscriptionPaymentSuccessfulResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        dismiss()
                        sheetCallback.onPaymentSuccess(response.body()?.subscriptionPaymentDetails)
                    }else {
                        val errorBody = Gson().fromJson(response.errorBody()?.string(), DefaultResponse::class.java)
                        if (errorBody.message == "Invalid Credential") {
                            when (loginMode) {
                                Constants.SELECTION_PIN -> {
                                    binding.sendPaymentPinETWarning.apply {
                                        visibility = View.VISIBLE
                                        text = getString(R.string.invalid_pin)
                                    }
                                }
                                Constants.SELECTION_PASSWORD -> {
                                    binding.sendPaymentPasswordETWarning.apply {
                                        visibility = View.VISIBLE
                                        text = getString(R.string.invalid_password)
                                    }
                                }
                            }
                        }else{
                            displayError(errorBody.message)
                        }
                    }
                    hideButtonLoader()
                }

                override fun onFailure(call: Call<SubscriptionPaymentSuccessfulResponse>, throwable: Throwable) {
                    activity.showToast(getString(R.string.default_error_toast))
//                    displayError("")
                    hideButtonLoader()
                }

            })
        }

    }

    private fun displayError(message: String){
        //A quick fix.
        dismiss()
        sheetCallback.onPaymentFailure(message)
    }

    private fun showButtonLoader() {
        binding.sendPaymentConfirmLoaderLottie.visibility = View.VISIBLE
        binding.sendPaymentConfirm.apply {
            text = ""
            isEnabled = false
        }
    }

    private fun hideButtonLoader() {
        binding.sendPaymentConfirmLoaderLottie.visibility = View.GONE
        binding.sendPaymentConfirm.apply {
            text = getString(R.string.confirm)
            isEnabled = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as SendPaymentInterface
    }

    companion object {
        const val TAG = "SendPaymentBottomSheet"
    }
}