package com.afrimax.paymaart.ui.utils.bottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.SendOtpForEditSelfKycRequest
import com.afrimax.paymaart.data.model.SendOtpForEditSelfKycResponse
import com.afrimax.paymaart.data.model.SendOtpResponse
import com.afrimax.paymaart.data.model.VerifyOtpForEditSelfErrorResponse
import com.afrimax.paymaart.data.model.VerifyOtpForEditSelfKycRequest
import com.afrimax.paymaart.data.model.VerifyOtpForEditSelfKycResponse
import com.afrimax.paymaart.databinding.EditKycVerificationBottomSheetBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.interfaces.KycYourPersonalDetailsInterface
import com.afrimax.paymaart.util.Constants
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EditKycVerificationSheet: BottomSheetDialogFragment() {
    private lateinit var b: EditKycVerificationBottomSheetBinding
    private lateinit var sheetCallback: KycYourPersonalDetailsInterface
    private var resendCount = 0
    private var token = ""
    private var questionId = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = EditKycVerificationBottomSheetBinding.inflate(inflater, container, false)

        questionId = requireArguments().getString(Constants.OTP_QUESTION_ID) ?: ""
        token = requireArguments().getString(Constants.OTP_TOKEN) ?: ""

        val firstName = requireArguments().getString(Constants.OTP_FIRST_NAME) ?: ""
        val middleName = requireArguments().getString(Constants.OTP_MIDDLE_NAME) ?: ""
        val lastName = requireArguments().getString(Constants.OTP_LAST_NAME) ?: ""
        val type = requireArguments().getString(Constants.TYPE) ?: ""
        val value = requireArguments().getString(Constants.OTP_VALUE) ?: ""
        val countryCode = requireArguments().getString(Constants.OTP_COUNTRY_CODE) ?: ""
        val question = requireArguments().getString(Constants.OTP_SECURITY_QUESTION) ?: ""

        when (type) {
            Constants.OTP_EMAIL_TYPE -> {
                b.editKycVerificationSheetHeadTV.text =
                    ContextCompat.getString(requireContext(), R.string.email_verification)
                b.editKycVerificationSheetSubTV.text =
                    ContextCompat.getString(requireContext(), R.string.email_verify_sub_text)

                b.editKycVerificationSheetContentTV.text = value
            }

            Constants.OTP_SMS_TYPE -> {
                b.editKycVerificationSheetHeadTV.text =
                    ContextCompat.getString(requireContext(), R.string.phone_verification)
                b.editKycVerificationSheetSubTV.text =
                    ContextCompat.getString(requireContext(), R.string.phone_verify_sub_text)

                b.editKycVerificationSheetContentTV.text = "$countryCode $value"
            }
        }

        b.editKycVerificationSheetSecurityQuestionTV.text = question

        b.editKycVerificationSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.editKycVerificationSheetContentTV.setOnClickListener {
            dismiss()
        }

        b.editKycVerificationSheetVerifyButton.setOnClickListener {
            if (validateFields()) {
                verifyOtp(type, token)
            }
        }

        b.editKycVerificationSheetResendTV.setOnClickListener {
            resendOtp(
                firstName, middleName, lastName, type, value, countryCode
            )
            startTimer()
            b.editKycVerificationSheetResendTV.isEnabled = false
        }

        configureCodeEditTextFocusListeners(b.editKycVerificationSheetCodeET)
        configureQuestionEditTextFocusListener()
        configureCodeEditTextChangeListener()
        configureEditTextChangeListener(
            b.editKycVerificationSheetSecurityQuestionET,
            b.editKycVerificationSheetSecurityQuestionWarningTV
        )

        startTimer()

        return b.root
    }

    override fun getTheme(): Int = R.style.EditKycVerificationSheet

    private fun configureQuestionEditTextFocusListener() {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val errorDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        b.editKycVerificationSheetSecurityQuestionET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.editKycVerificationSheetSecurityQuestionET.background =
                    focusDrawable
                else if (b.editKycVerificationSheetSecurityQuestionWarningTV.isVisible) b.editKycVerificationSheetSecurityQuestionET.background =
                    errorDrawable
                else b.editKycVerificationSheetSecurityQuestionET.background = notInFocusDrawable
            }
    }

    private fun configureCodeEditTextChangeListener() {
        b.editKycVerificationSheetCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.editKycVerificationSheetCodeET.text.isEmpty()) {
                    b.editKycVerificationSheetWarningTV.visibility = View.VISIBLE
                    b.editKycVerificationSheetWarningTV.text = getString(R.string.required_field)
                    b.editKycVerificationSheetCodeBox.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    b.editKycVerificationSheetWarningTV.visibility = View.GONE
                    b.editKycVerificationSheetCodeBox.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }


    private fun configureEditTextChangeListener(et: EditText, warningText: TextView) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et.text.isEmpty()) {
                    warningText.visibility = View.VISIBLE
                    et.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
                } else {
                    warningText.visibility = View.GONE
                    et.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (!validateOtp()) isValid = false
        if (!validateSecurityQuestion()) isValid = false
        return isValid
    }

    private fun verifyOtp(type: String, token: String) {
        showButtonLoader(
            b.editKycVerificationSheetVerifyButton,
            b.editKycVerificationSheetVerifyButtonLoaderLottie
        )
        val otp = b.editKycVerificationSheetCodeET.text.toString()
        val answer = b.editKycVerificationSheetSecurityQuestionET.text.toString().lowercase()

        val activity = requireActivity() as BaseActivity
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()

            val verifyOtpCall = ApiClient.apiService.verifyOtpForEditSelfKyc(
                idToken, VerifyOtpForEditSelfKycRequest(
                    otp = otp, token = token, question_id = questionId, answer = answer
                )
            )
            verifyOtpCall.enqueue(object : Callback<VerifyOtpForEditSelfKycResponse> {
                override fun onResponse(
                    call: Call<VerifyOtpForEditSelfKycResponse>,
                    response: Response<VerifyOtpForEditSelfKycResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        hideButtonLoader(
                            b.editKycVerificationSheetVerifyButton,
                            b.editKycVerificationSheetVerifyButtonLoaderLottie,
                            getString(R.string.verify)
                        )
                        when (type) {
                            Constants.OTP_EMAIL_TYPE -> {
                                sheetCallback.onEmailVerified(body.record_id)
                                dismiss()
                            }

                            Constants.OTP_SMS_TYPE -> {
                                sheetCallback.onPhoneVerified(body.record_id)
                                dismiss()
                            }
                        }
                    } else {
                        hideButtonLoader(
                            b.editKycVerificationSheetVerifyButton,
                            b.editKycVerificationSheetVerifyButtonLoaderLottie,
                            getString(R.string.verify)
                        )
                        val errorResponse: VerifyOtpForEditSelfErrorResponse = Gson().fromJson(
                            response.errorBody()!!.string(),
                            object : TypeToken<VerifyOtpForEditSelfErrorResponse>() {}.type
                        )
                        if (errorResponse.type == "otp") showOtpWarning(errorResponse.message)
                        else showAnswerWarning(errorResponse.message)

                    }
                }

                override fun onFailure(call: Call<VerifyOtpForEditSelfKycResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.editKycVerificationSheetVerifyButton,
                        b.editKycVerificationSheetVerifyButtonLoaderLottie,
                        getString(R.string.verify)
                    )
                    showOtpWarning(getString(R.string.invalid_otp))
                }
            })

        }

    }

    private fun resendOtp(
        firstName: String,
        middleName: String,
        lastName: String,
        type: String,
        value: String,
        countryCode: String
    ) {
        val activity = requireActivity() as BaseActivity
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val otpCall = ApiClient.apiService.sendOtpForEditSelfKyc(
                idToken, SendOtpForEditSelfKycRequest(
                    first_name = firstName,
                    middle_name = middleName,
                    last_name = lastName,
                    type = type,
                    value = value.replace(" ", ""),
                    country_code = countryCode
                )
            )

            otpCall.enqueue(object : Callback<SendOtpForEditSelfKycResponse> {
                override fun onResponse(
                    call: Call<SendOtpForEditSelfKycResponse>,
                    response: Response<SendOtpForEditSelfKycResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        token = body.token //Update with new OTP token
                        questionId = body.question_id//Update with new question id
                        //Show new question
                        b.editKycVerificationSheetSecurityQuestionTV.text = body.question

                        activity.showToast(getString(R.string.successfully_resent_otp))
                        resendCount++
                        if (resendCount >= 3) {
                            b.editKycVerificationSheetBottomTextTV.text = ContextCompat.getString(
                                requireContext(), R.string.resend_limit_is_3
                            )
                            b.editKycVerificationSheetBottomTextTV.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(), R.color.accentInformation
                                )
                            )
                            b.editKycVerificationSheetResendTV.visibility = View.GONE
                            b.editKycVerificationSheetTimerTV.visibility = View.GONE
                        }
                    } else {
                        val errorResponse: SendOtpResponse = Gson().fromJson(
                            response.errorBody()!!.string(),
                            object : TypeToken<SendOtpResponse>() {}.type
                        )
                        Toast.makeText(
                            requireContext(), errorResponse.message, Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SendOtpForEditSelfKycResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(), getString(R.string.default_error_toast), Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycYourPersonalDetailsInterface
    }

    private fun validateOtp(): Boolean {
        var isValid = true
        if (b.editKycVerificationSheetCodeET.text.isEmpty()) {
            isValid = false
            showOtpWarning(getString(R.string.required_field))
        } else if (b.editKycVerificationSheetCodeET.text.length != 6) {
            isValid = false
            showOtpWarning(getString(R.string.invalid_otp))
        } else {
            b.editKycVerificationSheetWarningTV.visibility = View.GONE
            b.editKycVerificationSheetCodeBox.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateSecurityQuestion(): Boolean {
        var isValid = true
        if (b.editKycVerificationSheetSecurityQuestionET.text.isEmpty()) {
            isValid = false
            b.editKycVerificationSheetSecurityQuestionWarningTV.visibility = View.VISIBLE
            b.editKycVerificationSheetSecurityQuestionWarningTV.text =
                getString(R.string.required_field)
        } else {
            b.editKycVerificationSheetSecurityQuestionWarningTV.visibility = View.GONE
            b.editKycVerificationSheetSecurityQuestionET.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun showOtpWarning(warning: String) {
        b.editKycVerificationSheetWarningTV.visibility = View.VISIBLE
        b.editKycVerificationSheetWarningTV.text = warning
        b.editKycVerificationSheetCodeBox.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
    }

    private fun showAnswerWarning(warning: String) {
        b.editKycVerificationSheetSecurityQuestionWarningTV.visibility = View.VISIBLE
        b.editKycVerificationSheetSecurityQuestionWarningTV.text = warning
        b.editKycVerificationSheetSecurityQuestionET.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
    }

    private fun startTimer() {

        val df = SimpleDateFormat("mm:ss", Locale.CANADA) // HH for 0-23
        df.timeZone = TimeZone.getTimeZone("GMT")

        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val d = Date(millisUntilFinished)
                b.editKycVerificationSheetTimerTV.text = df.format(d)
            }

            override fun onFinish() {
                b.editKycVerificationSheetResendTV.isEnabled = true
            }
        }.start()
    }

    private fun configureCodeEditTextFocusListeners(et: EditText) {
        val focusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.editKycVerificationSheetCodeBox.background = focusDrawable
            else b.editKycVerificationSheetCodeBox.background = notInFocusDrawable
        }
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        b.editKycVerificationSheetContentTV.isEnabled = false
        b.editKycVerificationSheetCodeET.isEnabled = false
        b.editKycVerificationSheetCloseButton.isEnabled = false
        b.editKycVerificationSheetVerifyButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.editKycVerificationSheetContentTV.isEnabled = true
        b.editKycVerificationSheetCodeET.isEnabled = true
        b.editKycVerificationSheetCloseButton.isEnabled = true
        b.editKycVerificationSheetVerifyButton.isEnabled = true
        loaderLottie.visibility = View.GONE

    }

    companion object {
        const val TAG = "EditKycVerificationSheet"
    }
}