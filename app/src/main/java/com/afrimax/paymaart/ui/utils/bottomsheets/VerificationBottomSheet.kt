package com.afrimax.paymaart.ui.utils.bottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.SendOtpRequestBody
import com.afrimax.paymaart.data.model.SendOtpResponse
import com.afrimax.paymaart.data.model.VerifyOtpRequestBody
import com.afrimax.paymaart.data.model.VerifyOtpResponse
import com.afrimax.paymaart.databinding.VerificationBottomSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.VerificationBottomSheetInterface
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.getStringExt
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class VerificationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: VerificationBottomSheetBinding
    private lateinit var sheetCallback: VerificationBottomSheetInterface
    private var resendCount = 0
    private var token = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = VerificationBottomSheetBinding.inflate(inflater, container, false)

        val firstName = requireArguments().getString(Constants.OTP_FIRST_NAME) ?: ""
        val middleName = requireArguments().getString(Constants.OTP_MIDDLE_NAME) ?: ""
        val lastName = requireArguments().getString(Constants.OTP_LAST_NAME) ?: ""
        val type = requireArguments().getString(Constants.TYPE) ?: ""
        val value = requireArguments().getString(Constants.OTP_VALUE) ?: ""
        val countryCode = requireArguments().getString(Constants.OTP_COUNTRY_CODE) ?: ""
        token = requireArguments().getString(Constants.OTP_TOKEN) ?: ""
        token = requireArguments().getString(Constants.OTP_TOKEN) ?: ""

        when (type) {
            Constants.OTP_EMAIL_TYPE -> {
                binding.registrationVerificationSheetHeadTV.text = requireContext().getStringExt(R.string.email_verification)
                binding.registrationVerificationSheetSubTV.text = requireContext().getStringExt(R.string.email_verify_sub_text)

                binding.registrationVerificationSheetContentTV.text = value
            }

            Constants.OTP_SMS_TYPE -> {
                binding.registrationVerificationSheetHeadTV.text = requireContext().getStringExt(R.string.phone_verification)

                binding.registrationVerificationSheetSubTV.text = requireContext().getStringExt(R.string.phone_verify_sub_text)

                binding.registrationVerificationSheetContentTV.text = "$countryCode $value"
            }
        }

        binding.registrationVerificationSheetCloseButton.setOnClickListener {
            dismiss()
        }

        binding.registrationVerificationSheetContentTV.setOnClickListener {
            dismiss()
        }

        binding.registrationVerificationSheetVerifyButton.setOnClickListener {
            verifyOtpApi(type, token)
        }

        binding.registrationVerificationSheetResendTV.setOnClickListener {
            resendOtpApi(firstName, middleName, lastName, type, value, countryCode)
            startTimer()
            binding.registrationVerificationSheetResendTV.isEnabled = false
        }

        configureCodeEditTextFocusListeners(binding.registrationVerificationSheetCodeET)
        startTimer()

        return binding.root
    }

    private fun verifyOtpApi(type: String, token: String) {
        if (validateOtp()) {
            showButtonLoader(
                binding.registrationVerificationSheetVerifyButton,
                binding.registrationVerificationSheetVerifyButtonLoaderLottie
            )
            val otp = binding.registrationVerificationSheetCodeET.text.toString()
            val verifyOtpCall = ApiClient.apiService.verifyOtp(VerifyOtpRequestBody(otp, token))
            verifyOtpCall.enqueue(object : Callback<VerifyOtpResponse> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        hideButtonLoader(
                            binding.registrationVerificationSheetVerifyButton,
                            binding.registrationVerificationSheetVerifyButtonLoaderLottie,
                            getString(R.string.verify)
                        )
                        when (type) {
                            Constants.OTP_EMAIL_TYPE -> {
                                sheetCallback.onEmailVerified(body.recordId)
                                dismiss()
                            }

                            Constants.OTP_SMS_TYPE -> {
                                sheetCallback.onPhoneVerified(body.recordId)
                                dismiss()
                            }
                        }
                    } else {
                        hideButtonLoader(
                            binding.registrationVerificationSheetVerifyButton,
                            binding.registrationVerificationSheetVerifyButtonLoaderLottie,
                            getString(R.string.verify)
                        )
                        showOtpWarning(R.string.invalid_otp)
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                    hideButtonLoader(
                        binding.registrationVerificationSheetVerifyButton,
                        binding.registrationVerificationSheetVerifyButtonLoaderLottie,
                        getString(R.string.verify)
                    )
                    showOtpWarning(R.string.invalid_otp)
                }
            })
        }
    }

    private fun resendOtpApi(
        firstName: String,
        middleName: String,
        lastName: String,
        type: String,
        value: String,
        countryCode: String
    ) {
        val otpCall = ApiClient.apiService.sentOtp(
            SendOtpRequestBody(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                type = type,
                value = value.replace(" ", ""),
                countryCode = countryCode
            )
        )
        otpCall.enqueue(object : Callback<SendOtpResponse> {
            override fun onResponse(
                call: Call<SendOtpResponse>, response: Response<SendOtpResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    token = body.token //Update OTP token
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.successfully_resent_otp),
                        Toast.LENGTH_LONG
                    ).show()
                    resendCount++
                    if (resendCount >= 3) {
                        binding.registrationVerificationSheetBottomTextTV.text =
                            ContextCompat.getString(requireContext(), R.string.resend_limit_is_3)
                        binding.registrationVerificationSheetBottomTextTV.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.accentInformation
                            )
                        )
                        binding.registrationVerificationSheetResendTV.visibility = View.GONE
                        binding.registrationVerificationSheetTimerTV.visibility = View.GONE
                    }
                } else {
                    val errorResponse: SendOtpResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        SendOtpResponse::class.java
                    )
                    Toast.makeText(
                        requireContext(), errorResponse.message, Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(), getString(R.string.default_error_toast), Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as VerificationBottomSheetInterface
    }

    private fun validateOtp(): Boolean {
        var isValid = true
        if (binding.registrationVerificationSheetCodeET.text.isEmpty()) {
            isValid = false
            showOtpWarning(R.string.required_field)
        } else if (binding.registrationVerificationSheetCodeET.text.length != 6) {
            isValid = false
            showOtpWarning(R.string.invalid_otp)
        } else {
            binding.registrationVerificationSheetWarningTV.visibility = View.GONE
            binding.registrationVerificationSheetCodeBox.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_background)
        }
        return isValid
    }

    private fun showOtpWarning(warning: Int) {
        binding.registrationVerificationSheetWarningTV.visibility = View.VISIBLE
        binding.registrationVerificationSheetWarningTV.text =
            ContextCompat.getString(requireContext(), warning)
        binding.registrationVerificationSheetCodeBox.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_error_background)
    }

    private fun startTimer() {

        val df = SimpleDateFormat("mm:ss", Locale.CANADA) // HH for 0-23
        df.timeZone = TimeZone.getTimeZone("GMT")

        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val d = Date(millisUntilFinished)
                binding.registrationVerificationSheetTimerTV.text = df.format(d)
            }

            override fun onFinish() {
                binding.registrationVerificationSheetResendTV.isEnabled = true
            }
        }.start()
    }

    private fun configureCodeEditTextFocusListeners(et: EditText) {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_focused_background)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_background)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.registrationVerificationSheetCodeBox.background = focusDrawable
            else binding.registrationVerificationSheetCodeBox.background = notInFocusDrawable
        }
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        binding.registrationVerificationSheetContentTV.isEnabled = false
        binding.registrationVerificationSheetCodeET.isEnabled = false
        binding.registrationVerificationSheetCloseButton.isEnabled = false
        binding.registrationVerificationSheetVerifyButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        binding.registrationVerificationSheetContentTV.isEnabled = true
        binding.registrationVerificationSheetCodeET.isEnabled = true
        binding.registrationVerificationSheetCloseButton.isEnabled = true
        binding.registrationVerificationSheetVerifyButton.isEnabled = true
        loaderLottie.visibility = View.GONE

    }

    companion object {
        const val TAG = "RegistrationVerificationSheet"
    }
}