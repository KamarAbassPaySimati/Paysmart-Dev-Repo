package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.data.utils.safeApiCall2
import com.afrimax.paysimati.common.domain.utils.Result
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.CashOutRequestBody
import com.afrimax.paysimati.data.model.MerchantRequestPay
import com.afrimax.paysimati.data.model.PayMerchantRequest
import com.afrimax.paysimati.data.model.PayToAfrimaxRequestBody
import com.afrimax.paysimati.data.model.PayToRegisteredPersonRequest
import com.afrimax.paysimati.data.model.PayToUnRegisteredPersonRequest
import com.afrimax.paysimati.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paysimati.data.model.SubscriptionPaymentRequestBody
import com.afrimax.paysimati.databinding.SendPaymentBottomSheetBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


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
        setUpAutoAcceptPinField()

        return binding.root
    }

    private fun setupView() {
        parentActivity = activity as BaseActivity
        loginMode = parentActivity.retrieveLoginMode() ?: Constants.SELECTION_PIN

        when (loginMode) {
            Constants.SELECTION_PIN -> {
                binding.sendPaymentSheetAPF.visibility = VISIBLE
                binding.sendPaymentPasswordContainer.visibility = View.GONE

                binding.sendPaymentConfirmButton.visibility = View.GONE
            }

            Constants.SELECTION_PASSWORD -> {
                binding.sendPaymentSheetAPF.visibility = View.GONE
                binding.sendPaymentPasswordContainer.visibility = VISIBLE

                binding.sendPaymentConfirmButton.visibility = VISIBLE
            }
        }

        binding.sendPaymentSubText.text = when (data) {
            is SubscriptionDetailsRequestBody, is PayToUnRegisteredPersonRequest, is PayToRegisteredPersonRequest, is PayMerchantRequest -> getString(
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

        binding.sendPaymentConfirmButton.setOnClickListener {
            lifecycleScope.launch {
                validatePasswordField()
            }
        }

        binding.sendPaymentPasswordToggle.setOnClickListener {
            onTogglePasswordClicked()
        }

        binding.sendPaymentPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.sendPaymentPassword.text.isEmpty()) {
                    binding.sendPaymentPasswordETWarning.visibility = VISIBLE
                    binding.sendPaymentPasswordETWarning.text = getString(R.string.required_field)
                    binding.sendPaymentPasswordBox.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    binding.sendPaymentPasswordToggle.visibility = View.GONE
                } else {
                    binding.sendPaymentPasswordETWarning.visibility = View.GONE
                    binding.sendPaymentPasswordBox.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    binding.sendPaymentPasswordToggle.visibility = VISIBLE
                }
            }
        })
    }

    private fun setUpAutoAcceptPinField() {
        binding.sendPaymentSheetAPF.apply {
            onPinEntered {
                lifecycleScope.launch {
                    when (data) {
                        is SubscriptionDetailsRequestBody -> onConfirmClickedPayPaymaart(text, data)
                        is PayToAfrimaxRequestBody -> onConfirmClickedPayAfrimax(text, data)
                        is CashOutRequestBody -> onConfirmClickedCashOut(text, data)
                        is PayToUnRegisteredPersonRequest -> onConfirmClickedPayUnRegisteredPerson(
                            text, data
                        )

                        is PayToRegisteredPersonRequest -> onConfirmClickedPayRegisteredPerson(
                            text, data
                        )

                        is PayMerchantRequest -> onConfirmClickedPayMerchant(text, data)

                        is MerchantRequestPay -> onConfirmClickedPayMerchantRequest(text, data)
                    }
                }
            }
        }
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

    private suspend fun validatePasswordField() {

        var isValid = true
        binding.sendPaymentPasswordETWarning.visibility = View.GONE
        binding.sendPaymentPasswordBox.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        if (binding.sendPaymentPassword.text.isEmpty()) {
            isValid = false
            binding.sendPaymentPasswordETWarning.visibility = VISIBLE
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
                    binding.sendPaymentPassword.text.toString(), data
                )

                is PayMerchantRequest ->
                    onConfirmClickedPayMerchant(
                        binding.sendPaymentPassword.text.toString(), data
                    )

                is MerchantRequestPay ->
                    onConfirmClickedPayMerchantRequest(
                        binding.sendPaymentPassword.text.toString(), data
                    )
            }
        }
    }

    private suspend fun onConfirmClickedPayPaymaart(
        password: String, data: SubscriptionDetailsRequestBody
    ) {
        val activity = context as BaseActivity
        val credential = AESCrypt.encrypt(password)
        activity.hideKeyboard(view, requireContext())

        val subscriptionPaymentRequestBody = SubscriptionPaymentRequestBody(
            referenceNumber = data.referenceNumber,
            subType = data.subType,
            credentials = credential,
            autoRenew = data.autoRenew
        )


        val idToken = activity.fetchIdToken()
        val subscriptionHandler = safeApiCall2 {
            ApiClient.apiService.subscriptionPayment(
                idToken, subscriptionPaymentRequestBody
            )
        }

        when (subscriptionHandler) {
            is Result.Success -> {
                dismiss()
                sheetCallback.onPaymentSuccess(subscriptionHandler.data.subscriptionPaymentDetails)
            }

            is Result.Error -> handleError(subscriptionHandler.error.errorMessage)
        }
    }

    private suspend fun onConfirmClickedPayAfrimax(
        password: String, data: PayToAfrimaxRequestBody
    ) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        activity.hideKeyboard(view, requireContext())
        val newRequestBody = data.copy(password = encryptedPassword)

        val idToken = activity.fetchIdToken()
        val payToAfrimaxHandler = safeApiCall2 {
            ApiClient.apiService.payToAfrimax(
                idToken, newRequestBody
            )
        }

        when (payToAfrimaxHandler) {
            is Result.Success -> {
                dismiss()
                sheetCallback.onPaymentSuccess(payToAfrimaxHandler.data.payAfrimaxResponse)
            }

            is Result.Error -> handleError(payToAfrimaxHandler.error.errorMessage)
        }
    }

    private suspend fun onConfirmClickedCashOut(password: String, data: CashOutRequestBody) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(password = encryptedPassword)
        activity.hideKeyboard(view, requireContext())

        val idToken = activity.fetchIdToken()
        val selfCashOutCall = safeApiCall2 {
            ApiClient.apiService.cashOut(
                idToken, newRequestBody
            )
        }

        when (selfCashOutCall) {
            is Result.Success -> {
                sheetCallback.onPaymentSuccess(selfCashOutCall.data.cashOutResponse)
                dismiss()
            }

            is Result.Error -> handleError(selfCashOutCall.error.errorMessage)
        }
    }

    private suspend fun onConfirmClickedPayUnRegisteredPerson(
        password: String, data: PayToUnRegisteredPersonRequest
    ) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(password = encryptedPassword)
        activity.hideKeyboard(view, requireContext())

        val idToken = activity.fetchIdToken()
        val payToUnRegisteredCall = safeApiCall2 {
            ApiClient.apiService.payToUnRegisteredPerson(
                idToken, newRequestBody
            )
        }

        when (payToUnRegisteredCall) {
            is Result.Success -> {
                sheetCallback.onPaymentSuccess(payToUnRegisteredCall.data.data)
                dismiss()
            }

            is Result.Error -> handleError(payToUnRegisteredCall.error.errorMessage)
        }
    }


    private suspend fun onConfirmClickedPayRegisteredPerson(
        password: String, data: PayToRegisteredPersonRequest
    ) {
        val activity = requireContext() as BaseActivity
        val encryptedPassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(credential = encryptedPassword)
        activity.hideKeyboard(view, requireContext())

        val idToken = activity.fetchIdToken()
        val payToRegisteredCall = safeApiCall2 {
            ApiClient.apiService.payToRegisteredPerson(
                idToken, newRequestBody
            )
        }

        when (payToRegisteredCall) {
            is Result.Success -> {
                sheetCallback.onPaymentSuccess(payToRegisteredCall.data.data)
                dismiss()
            }

            is Result.Error -> handleError(payToRegisteredCall.error.errorMessage)
        }
    }

    private suspend fun onConfirmClickedPayMerchant(
        password: String, data: PayMerchantRequest
    ) {
        val activity = requireContext() as BaseActivity
        val encryptedpassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(password = encryptedpassword)
        activity.hideKeyboard(view, requireContext())
        val idtoken = activity.fetchIdToken()

        val payToMerchant = safeApiCall2 {
            ApiClient.apiService.getTaxForMechant(
                idtoken, newRequestBody
            )
        }

        when (payToMerchant) {
            is Result.Success -> {
                sheetCallback.onPaymentSuccess(payToMerchant.data.paymerchant)
                dismiss()
            }

            is Result.Error -> handleError(payToMerchant.error.errorMessage)
        }

    }

    private suspend fun onConfirmClickedPayMerchantRequest(
        password: String,
        data: MerchantRequestPay
    ) {

        val activity = requireContext() as BaseActivity
        val encryptedpassword = AESCrypt.encrypt(password)
        val newRequestBody = data.copy(password = encryptedpassword)
        activity.hideKeyboard(view, requireContext())
        val idtoken = activity.fetchIdToken()

        val payToMerchant = safeApiCall2 {
            ApiClient.apiService.payMerchantRequest(
                idtoken, newRequestBody
            )
        }

        when (payToMerchant) {
            is Result.Success -> {
                sheetCallback.onPaymentSuccess(payToMerchant.data.paymerchant)
                dismiss()
            }

            is Result.Error -> handleError(payToMerchant.error.errorMessage)
        }

    }

    private fun showInvalidCredentialError() {
        when (loginMode) {
            Constants.SELECTION_PIN -> {
                binding.sendPaymentSheetAPF.showWarning(warningText = getString(R.string.invalid_pin))
            }

            Constants.SELECTION_PASSWORD -> {
                binding.sendPaymentPasswordETWarning.visibility = VISIBLE
                binding.sendPaymentPasswordETWarning.text = getString(R.string.invalid_password)
                context?.let {
                    binding.sendPaymentPasswordBox.background = ContextCompat.getDrawable(
                        it, R.drawable.bg_edit_text_error
                    )
                }
            }
        }
    }

    private fun handleError(errorMessage: String) {
        when {
            errorMessage == "Invalid Password" || errorMessage == "Invalid Credential" || errorMessage == "Incorrect password" || errorMessage == "Unauthorized" -> {
                showInvalidCredentialError()
            }

            else -> {
                sheetCallback.onPaymentFailure(errorMessage)
                dismiss()
            }
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