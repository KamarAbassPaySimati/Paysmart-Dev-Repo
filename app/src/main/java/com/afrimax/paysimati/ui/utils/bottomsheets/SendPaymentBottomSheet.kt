package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.CashOutApiResponse
import com.afrimax.paysimati.data.model.CashOutRequestBody
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.PayToAfrimaxErrorResponse
import com.afrimax.paysimati.data.model.PayToAfrimaxRequestBody
import com.afrimax.paysimati.data.model.PayToAfrimaxResponse
import com.afrimax.paysimati.data.model.PayToRegisteredPersonRequest
import com.afrimax.paysimati.data.model.PayToUnRegisteredPersonRequest
import com.afrimax.paysimati.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paysimati.data.model.SubscriptionPaymentRequestBody
import com.afrimax.paysimati.data.model.SubscriptionPaymentSuccessfulResponse
import com.afrimax.paysimati.databinding.SendPaymentBottomSheetBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.LoginPinTransformation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SendPaymentBottomSheet(private val data: Any? = null) : BottomSheetDialogFragment() {
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

    private fun setupView() {
        parentActivity = activity as BaseActivity
        loginMode = parentActivity.retrieveLoginMode() ?: Constants.SELECTION_PIN
        binding.sendPaymentPin.transformationMethod = LoginPinTransformation()
        when (loginMode) {
            Constants.SELECTION_PIN -> {
                binding.sendPaymentPinContainer.visibility = View.VISIBLE
                binding.sendPaymentPasswordContainer.visibility = View.GONE
            }

            Constants.SELECTION_PASSWORD -> {
                binding.sendPaymentPinContainer.visibility = View.GONE
                binding.sendPaymentPasswordContainer.visibility = View.VISIBLE
            }
        }

        binding.sendPaymentSubText.text = when (data) {
            is SubscriptionDetailsRequestBody, is PayToUnRegisteredPersonRequest, is PayToRegisteredPersonRequest -> getString(
                R.string.send_payment_subtext
            )

            is PayToAfrimaxRequestBody -> getString(R.string.send_payment_subtext_pay_afrimax)
            is CashOutRequestBody -> {
                when (loginMode) {
                    Constants.SELECTION_PIN -> getString(R.string.enter_your_pin_to_make_secure_payment)
                    Constants.SELECTION_PASSWORD -> getString(R.string.enter_your_password_to_make_secure_payment)
                    else -> ""
                }
            }

            else -> ""
        }

        if (data is CashOutRequestBody) {
            binding.sendPaymentSheetTitle.text = getString(R.string.cash_out)
        }

        binding.sendPaymentClose.setOnClickListener {
            dismiss()
        }

        binding.sendPaymentConfirm.setOnClickListener {
            when (loginMode) {
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

        binding.sendPaymentPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (binding.sendPaymentPassword.text.isEmpty()) {
                    binding.sendPaymentPasswordETWarning.visibility = View.VISIBLE
                    binding.sendPaymentPasswordETWarning.text = getString(R.string.required_field)
                    binding.sendPaymentPasswordBox.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    binding.sendPaymentPasswordToggle.visibility = View.GONE
                } else {
                    binding.sendPaymentPasswordETWarning.visibility = View.GONE
                    binding.sendPaymentPasswordBox.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    binding.sendPaymentPasswordToggle.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun onTogglePasswordClicked() {
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
            when (data) {
                is SubscriptionDetailsRequestBody -> onConfirmClickedPayPaymaart(
                    binding.sendPaymentPin.text.toString(), data
                )

                is PayToAfrimaxRequestBody -> onConfirmClickedPayAfrimax(
                    binding.sendPaymentPin.text.toString(), data
                )

                is CashOutRequestBody -> onConfirmClickedCashOut(
                    binding.sendPaymentPin.text.toString(), data
                )

                is PayToUnRegisteredPersonRequest -> onConfirmClickedPayUnRegisteredPerson(
                    binding.sendPaymentPin.text.toString(), data
                )

                is PayToRegisteredPersonRequest -> onConfirmClickedPayRegisteredPerson(
                    binding.sendPaymentPin.text.toString(),
                    data
                )
            }
        }
    }

    private fun validatePasswordField() {
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
            when (data) {
                is SubscriptionDetailsRequestBody -> onConfirmClickedPayPaymaart(
                    binding.sendPaymentPassword.text.toString(), data
                )

                is PayToAfrimaxRequestBody -> onConfirmClickedPayAfrimax(
                    binding.sendPaymentPassword.text.toString(), data
                )

                is CashOutRequestBody -> onConfirmClickedCashOut(
                    binding.sendPaymentPassword.text.toString(), data
                )

                is PayToUnRegisteredPersonRequest -> onConfirmClickedPayUnRegisteredPerson(
                    binding.sendPaymentPassword.text.toString(), data
                )

                is PayToRegisteredPersonRequest -> onConfirmClickedPayRegisteredPerson(
                    binding.sendPaymentPassword.text.toString(),
                    data
                )
            }
        }
    }

    private fun onConfirmClickedPayPaymaart(
        password: String, data: SubscriptionDetailsRequestBody
    ) {
        val activity = context as BaseActivity
        val credential = AESCrypt.encrypt(password)
        activity.hideKeyboard(view, requireContext())
        showButtonLoader()
        val subscriptionPaymentRequestBody = SubscriptionPaymentRequestBody(
            referenceNumber = data.referenceNumber,
            subType = data.subType,
            credentials = credential,
            autoRenew = data.autoRenew
        )

        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val subscriptionHandler = ApiClient.apiService.subscriptionPayment(
                idToken, subscriptionPaymentRequestBody
            )

            subscriptionHandler.enqueue(object : Callback<SubscriptionPaymentSuccessfulResponse> {
                override fun onResponse(
                    call: Call<SubscriptionPaymentSuccessfulResponse>,
                    response: Response<SubscriptionPaymentSuccessfulResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        dismiss()
                        sheetCallback.onPaymentSuccess(response.body()?.subscriptionPaymentDetails)
                    } else {
                        val errorBody = Gson().fromJson(
                            response.errorBody()?.string(), DefaultResponse::class.java
                        )
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
                        } else {
                            displayError(errorBody.message)
                        }
                    }
                    hideButtonLoader()
                }

                override fun onFailure(
                    call: Call<SubscriptionPaymentSuccessfulResponse>, throwable: Throwable
                ) {
                    hideButtonLoader()
                    activity.showToast(getString(R.string.default_error_toast))
                }
            })
        }
    }

    private fun onConfirmClickedPayAfrimax(password: String, data: PayToAfrimaxRequestBody) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        activity.hideKeyboard(view, requireContext())
        val newRequestBody = data.copy(password = encryptedPassword)
        showButtonLoader()
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val payToAfrimaxHandler = ApiClient.apiService.payToAfrimax(
                idToken, newRequestBody
            )

            payToAfrimaxHandler.enqueue(object : Callback<PayToAfrimaxResponse> {
                override fun onResponse(
                    call: Call<PayToAfrimaxResponse>, response: Response<PayToAfrimaxResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        dismiss()
                        sheetCallback.onPaymentSuccess(response.body()?.payAfrimaxResponse)
                    } else {
                        val errorBody = Gson().fromJson(
                            response.errorBody()?.string(), PayToAfrimaxErrorResponse::class.java
                        )
                        if (errorBody.message == "Invalid password") {
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
                        } else {
                            displayError(errorBody.message)
                        }
                    }
                    hideButtonLoader()
                }

                override fun onFailure(call: Call<PayToAfrimaxResponse>, throwable: Throwable) {
                    hideButtonLoader()
                    activity.showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun onConfirmClickedCashOut(password: String, data: CashOutRequestBody) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(password = encryptedPassword)
        activity.hideKeyboard(view, requireContext())
        showButtonLoader()
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val selfCashOutCall = ApiClient.apiService.cashOut(
                idToken, newRequestBody
            )

            selfCashOutCall.enqueue(object : Callback<CashOutApiResponse> {
                override fun onResponse(
                    call: Call<CashOutApiResponse>,
                    response: Response<CashOutApiResponse>,
                ) {
                    hideButtonLoader()
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        sheetCallback.onPaymentSuccess(body.cashOutResponse)
                        dismiss()
                    } else {
                        val errorBody = Gson().fromJson(
                            response.errorBody()?.string(), DefaultResponse::class.java
                        )
                        if (errorBody.message == "Incorrect password") {
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
                        } else {
                            displayError(errorBody.message)
                        }

                    }
                }

                override fun onFailure(p0: Call<CashOutApiResponse>, p1: Throwable) {
                    hideButtonLoader()
                    activity.showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun onConfirmClickedPayUnRegisteredPerson(
        password: String, data: PayToUnRegisteredPersonRequest
    ) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(password = encryptedPassword)
        activity.hideKeyboard(view, requireContext())
        showButtonLoader()
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val payToUnRegisteredCall = ApiClient.apiService.payToUnRegisteredPerson(
                idToken, newRequestBody
            )

            val body = payToUnRegisteredCall.body()

            if (payToUnRegisteredCall.isSuccessful && body != null) {
                sheetCallback.onPaymentSuccess(body.data)
                dismiss()
            } else {
                val errorBody =
                    Gson().fromJson(
                        payToUnRegisteredCall.errorBody()?.string(),
                        DefaultResponse::class.java
                    )
                if (errorBody.message == "Incorrect password") {
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
                } else {
                    displayError(errorBody.message)
                }
            }

        }
    }


    private fun onConfirmClickedPayRegisteredPerson(
        password: String, data: PayToRegisteredPersonRequest
    ) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(credential = encryptedPassword)
        activity.hideKeyboard(view, requireContext())
        showButtonLoader()
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val payToUnRegisteredCall = ApiClient.apiService.payToRegisteredPerson(
                idToken, newRequestBody
            )

            val body = payToUnRegisteredCall.body()

            if (payToUnRegisteredCall.isSuccessful && body != null) {
                sheetCallback.onPaymentSuccess(body.data)
                dismiss()
            } else {
                val errorBody =
                    Gson().fromJson(
                        payToUnRegisteredCall.errorBody()?.string(),
                        DefaultResponse::class.java
                    )
                if (errorBody.message == "Incorrect password") {
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
                } else {
                    displayError(errorBody.message)
                }
            }

        }
    }

    private fun displayError(message: String) {
        //A quick fix.
        dismiss()
        sheetCallback.onPaymentFailure(message)
    }

    private fun showButtonLoader() {
        binding.sendPaymentConfirmLoaderLottie.visibility = View.VISIBLE
        binding.sendPaymentConfirm.apply {
            text = getString(R.string.empty_string)
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