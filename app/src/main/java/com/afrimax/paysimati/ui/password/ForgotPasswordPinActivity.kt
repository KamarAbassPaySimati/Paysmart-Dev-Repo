package com.afrimax.paysimati.ui.password

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.transition.Slide
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.SendForgotOtpResponse
import com.afrimax.paysimati.data.model.UpdatePinPasswordRequest
import com.afrimax.paysimati.data.model.VerifyForgotOtpResponse
import com.afrimax.paysimati.databinding.ActivityForgotPasswordPinBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.login.LoginActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.PasswordGuideBottomSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.PinGuideBottomSheet
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.MaskPinTransformation
import com.afrimax.paysimati.util.RecaptchaManager
import com.afrimax.paysimati.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
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
import java.util.regex.Pattern

class ForgotPasswordPinActivity : BaseActivity() {
    private lateinit var b: ActivityForgotPasswordPinBinding
    private lateinit var forgotCredentialType: String
    private lateinit var countDownTimer: CountDownTimer
    private var paymaartId = ""
    private var encryptedOtp = ""
    private var securityQuestionId = ""
    private var email = ""
    private var resendCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityForgotPasswordPinBinding.inflate(layoutInflater)
        setAnimation()
        setContentView(b.root)

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        forgotCredentialType = intent.getStringExtra(Constants.FORGOT_CREDENTIAL_TYPE)
            ?: Constants.FORGOT_CREDENTIAL_PIN

        initViews()
        setUpListeners()
    }
private val recaptchaClient: RecaptchaClient
    get() = RecaptchaManager.getClient()!!

    private fun initViews() {
        showEmailView()

        b.forgotPasswordPinActivityNewPinET.transformationMethod = MaskPinTransformation()
        b.forgotPasswordPinActivityConfirmPinET.transformationMethod = MaskPinTransformation()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    b.forgotPasswordPinActivityOtpView.isVisible || b.forgotPasswordPinActivitySetPasswordView.isVisible || b.forgotPasswordPinActivitySetPinView.isVisible -> {
                        showEmailView()
                    }

                    else -> finishAfterTransition()
                }
            }
        })

        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val d = Date(millisUntilFinished)
                b.forgotPasswordPinActivityOtpTimerTV.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("GMT")
                    }.format(d)
            }

            override fun onFinish() {
                b.forgotPasswordPinActivityOtpResendTV.isEnabled = true
            }
        }

    }

    private fun setUpListeners() {
        b.forgotPasswordPinActivityCloseButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.forgotPasswordPinActivityProceedButton.setOnClickListener {
            onClickProceed()
        }

        b.forgotPasswordPinActivityOtpResendTV.setOnClickListener {
            onClickResendOtp()
        }

        b.forgotPasswordPinActivityOtpVerifyButton.setOnClickListener {
            onClickVerify()
        }

        b.forgotPasswordPinActivityPasswordGuideTV.setOnClickListener {
            PasswordGuideBottomSheet().show(supportFragmentManager, PasswordGuideBottomSheet.TAG)
        }

        b.forgotPasswordPinActivityPinGuideTV.setOnClickListener {
            PinGuideBottomSheet().show(supportFragmentManager, PinGuideBottomSheet.TAG)
        }

        b.forgotPasswordPinActivityNewPasswordToggleTV.setOnClickListener {
            onClickNewPasswordToggle()
        }

        b.forgotPasswordPinActivityConfirmPasswordToggleTV.setOnClickListener {
            onClickConfirmPasswordToggle()
        }

        b.forgotPasswordPinActivityPasswordResetButton.setOnClickListener {
            onClickReset()
        }

        b.forgotPasswordPinActivityPinResetButton.setOnClickListener {
            onClickReset()
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
        setUpEmailEditTextFilters(b.forgotPasswordPinActivityEmailET)
    }

    private fun onClickConfirmPasswordToggle() {
        val passwordTransformation =
            b.forgotPasswordPinActivityConfirmPasswordET.transformationMethod
        if (passwordTransformation != null) {
            //Password is hidden make it visible
            b.forgotPasswordPinActivityConfirmPasswordET.transformationMethod = null
            b.forgotPasswordPinActivityConfirmPasswordToggleTV.text = getString(R.string.hide)
        } else {
            //Make it hidden
            b.forgotPasswordPinActivityConfirmPasswordET.transformationMethod =
                PasswordTransformationMethod()
            b.forgotPasswordPinActivityConfirmPasswordToggleTV.text = getString(R.string.show)
        }
        b.forgotPasswordPinActivityConfirmPasswordET.setSelection(b.forgotPasswordPinActivityConfirmPasswordET.length())
    }

    private fun onClickNewPasswordToggle() {
        val passwordTransformation = b.forgotPasswordPinActivityNewPasswordET.transformationMethod
        if (passwordTransformation != null) {
            //Password is hidden make it visible
            b.forgotPasswordPinActivityNewPasswordET.transformationMethod = null
            b.forgotPasswordPinActivityNewPasswordToggleTV.text = getString(R.string.hide)
        } else {
            //Make it hidden
            b.forgotPasswordPinActivityNewPasswordET.transformationMethod =
                PasswordTransformationMethod()
            b.forgotPasswordPinActivityNewPasswordToggleTV.text = getString(R.string.show)
        }
        b.forgotPasswordPinActivityNewPasswordET.setSelection(b.forgotPasswordPinActivityNewPasswordET.length())
    }

    private fun onClickResendOtp() {
        b.forgotPasswordPinActivityOtpResendTV.isEnabled = false
        countDownTimer.start()
        lifecycleScope.launch {
            recaptchaClient
                .execute(RecaptchaAction.custom(Constants.RESEND_OTP))
                .onSuccess {
                    resendOtpApi()
                }.onFailure {exception ->
                    "Response".showLogE(exception.message ?: "")
                    showToast(getString(R.string.default_error_toast))
                }
        }
    }

    private fun onClickReset() {
        hideKeyboard(this)
        lifecycleScope.launch {
            recaptchaClient
                .execute(RecaptchaAction.custom(Constants.RESET))
                .onSuccess {
                    when (forgotCredentialType) {
                        Constants.FORGOT_CREDENTIAL_PIN -> {
                            if (validatePin()) {

                                updatePinApi()
                            }
                        }

                        Constants.FORGOT_CREDENTIAL_PASSWORD -> {
                            if (validatePassword()) {
                                updatePasswordApi()
                            }
                        }
                    }
                }.onFailure { exception ->
                    "Response".showLogE(exception.message ?: "")
                    showToast(getString(R.string.default_error_toast))
                }
        }
    }

    private fun onClickVerify() {
        if (validateOtp()) {
            hideKeyboard(this)
            lifecycleScope.launch {
                recaptchaClient
                    .execute(RecaptchaAction.custom(Constants.VERIFY_OTP))
                    .onSuccess {
                        verifyForgotOtpApi()
                    }.onFailure { exception ->
                        "Response".showLogE(exception.message ?: "")
                        showToast(getString(R.string.default_error_toast))
                    }
            }
        }
    }

    private fun onClickProceed() {
        if (validateEmail(
                b.forgotPasswordPinActivityEmailET, b.forgotPasswordPinActivityEmailWarningTV
            )
        ) {
            hideKeyboard(this)
            showButtonLoader(b.forgotPasswordPinActivityProceedButton, b.forgotPasswordPinActivityProceedButtonLoaderLottie)
            lifecycleScope.launch {
                recaptchaClient
                    .execute(RecaptchaAction.custom(Constants.SEND_OTP))
                    .onSuccess { _ ->
                        sendOtpApi()
                    }.onFailure { exception ->
                        "Response".showLogE(exception.message ?: "")
                        showToast(getString(R.string.default_error_toast))
                    }
            }
        }
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextChangeListener(
            b.forgotPasswordPinActivityEmailET, b.forgotPasswordPinActivityEmailWarningTV
        )
        configureCodeEditTextChangeListener()
        configureNewPasswordEditTextChangeListener()
        configureConfirmPasswordEditTextChangeListener()
        configureEditTextChangeListener(
            b.forgotPasswordPinActivityPasswordSecurityQuestionET,
            b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV
        )
        configureEditTextPinChangeListener(
            b.forgotPasswordPinActivityNewPinET, b.forgotPasswordPinActivityNewPinWarningTV
        )
        configureEditTextPinChangeListener(
            b.forgotPasswordPinActivityConfirmPinET, b.forgotPasswordPinActivityConfirmPinWarningTV
        )
        configureEditTextChangeListener(
            b.forgotPasswordPinActivityPinSecurityQuestionET,
            b.forgotPasswordPinActivityPinSecurityQuestionWarningTV
        )
    }

    private fun configureNewPasswordEditTextChangeListener() {
        b.forgotPasswordPinActivityNewPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.forgotPasswordPinActivityNewPasswordET.text.isEmpty()) {
                    b.forgotPasswordPinActivityNewPasswordWarningTV.visibility = View.VISIBLE
                    b.forgotPasswordPinActivityNewPasswordWarningTV.text =
                        getString(R.string.required_field)
                    b.forgotPasswordPinActivityNewPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_error
                        )
                    b.forgotPasswordPinActivityNewPasswordToggleTV.visibility = View.GONE
                    //Hide password conditions field
                    b.forgotPasswordPinActivityNewPasswordConditionsContainer.visibility = View.GONE
                } else {
                    b.forgotPasswordPinActivityNewPasswordWarningTV.visibility = View.GONE
                    b.forgotPasswordPinActivityNewPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_focused
                        )
                    b.forgotPasswordPinActivityNewPasswordToggleTV.visibility = View.VISIBLE
                    //Show password conditions field
                    b.forgotPasswordPinActivityNewPasswordConditionsContainer.visibility =
                        View.VISIBLE
                    validatePasswordConditions()
                }
            }

        })
    }

    private fun validatePasswordConditions(): Boolean {
        var isValid = true
        val typedPassword = b.forgotPasswordPinActivityNewPasswordET.text.toString()

        if (typedPassword.length < 8 || typedPassword.length > 12) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordCondition1TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_circle_default_selected, 0, 0, 0
            )
        } else {
            b.forgotPasswordPinActivityNewPasswordCondition1TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_done_green, 0, 0, 0
            )
        }

        val tpUpperCase: Pattern = Pattern.compile(".*[A-Z].*")
        if (!tpUpperCase.matcher(typedPassword).matches()) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordCondition2TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_circle_default_selected, 0, 0, 0
            )
        } else {
            b.forgotPasswordPinActivityNewPasswordCondition2TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_done_green, 0, 0, 0
            )
        }

        val tpLowerCase: Pattern = Pattern.compile(".*[a-z].*")
        if (!tpLowerCase.matcher(typedPassword).matches()) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordCondition3TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_circle_default_selected, 0, 0, 0
            )
        } else {
            b.forgotPasswordPinActivityNewPasswordCondition3TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_done_green, 0, 0, 0
            )
        }

        val tpNumber: Pattern = Pattern.compile(".*[0-9].*")
        if (!tpNumber.matcher(typedPassword).matches()) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordCondition4TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_circle_default_selected, 0, 0, 0
            )
        } else {
            b.forgotPasswordPinActivityNewPasswordCondition4TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_done_green, 0, 0, 0
            )
        }

        val tpSpecialCharacter: Pattern =
            Pattern.compile(".*['#$%&*+<=>@\\\\^_`|~(){}/,:;\\-\\[\\]].*")
        if (!tpSpecialCharacter.matcher(typedPassword).matches()) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordCondition5TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_circle_default_selected, 0, 0, 0
            )
        } else {
            b.forgotPasswordPinActivityNewPasswordCondition5TV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_done_green, 0, 0, 0
            )
        }

        val tpConsecutiveNumber: Pattern =
            Pattern.compile("^(?!.*(\\d)\\1{3})(?!.*1234)(?!.*2345)(?!.*3456)(?!.*4567)(?!.*5678)(?!.*6789)(?!.*7890)(?!.*8901)(?!.*9012)(?!.*0123).+\$")
        if (!tpConsecutiveNumber.matcher(typedPassword).matches()) {
            isValid = false
        }

        return isValid
    }

    private fun configureConfirmPasswordEditTextChangeListener() {
        b.forgotPasswordPinActivityConfirmPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.forgotPasswordPinActivityConfirmPasswordET.text.isEmpty()) {
                    b.forgotPasswordPinActivityConfirmPasswordWarningTV.visibility = View.VISIBLE
                    b.forgotPasswordPinActivityConfirmPasswordWarningTV.text =
                        getString(R.string.required_field)
                    b.forgotPasswordPinActivityConfirmPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_error
                        )
                    b.forgotPasswordPinActivityConfirmPasswordToggleTV.visibility = View.GONE
                } else {
                    b.forgotPasswordPinActivityConfirmPasswordWarningTV.visibility = View.GONE
                    b.forgotPasswordPinActivityConfirmPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_focused
                        )
                    b.forgotPasswordPinActivityConfirmPasswordToggleTV.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun configureCodeEditTextChangeListener() {
        b.forgotPasswordPinActivityOtpET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.forgotPasswordPinActivityOtpET.text.isEmpty()) {
                    b.forgotPasswordPinActivityOtpWarningTV.visibility = View.VISIBLE
                    b.forgotPasswordPinActivityOtpWarningTV.text =
                        getString(R.string.required_field)
                    b.forgotPasswordPinActivityOtpBox.background = ContextCompat.getDrawable(
                        this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    b.forgotPasswordPinActivityOtpWarningTV.visibility = View.GONE
                    b.forgotPasswordPinActivityOtpBox.background = ContextCompat.getDrawable(
                        this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun configureEditTextPinChangeListener(et: EditText, tv: TextView) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(
                pinText: CharSequence?, index: Int, isBackSpace: Int, isNewDigit: Int
            ) {
                //Remove any error warning text while typing
                if (et.text.toString().isEmpty()) {
                    tv.visibility = View.VISIBLE
                    tv.text = getString(R.string.required_field)
                } else {
                    tv.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                //
            }

        })
    }


    private fun setupEditTextFocusListeners() {
        configureEditTextFocusListeners(
            b.forgotPasswordPinActivityEmailET, b.forgotPasswordPinActivityEmailWarningTV
        )
        configureCodeEditTextFocusListener(b.forgotPasswordPinActivityOtpET)
        configureNewPasswordEditTextFocusListener()
        configureConfirmPasswordEditTextFocusListener()
        configurePasswordSecurityQuestionEditTextFocusListener()
        configurePinSecurityQuestionEditTextFocusListener()
    }

    private fun configureNewPasswordEditTextFocusListener(
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.forgotPasswordPinActivityNewPasswordET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.forgotPasswordPinActivityNewPasswordBox.background = focusDrawable
                else if (b.forgotPasswordPinActivityNewPasswordWarningTV.isVisible) b.forgotPasswordPinActivityNewPasswordBox.background =
                    errorDrawable
                else b.forgotPasswordPinActivityNewPasswordBox.background = notInFocusDrawable
            }
    }

    private fun configureConfirmPasswordEditTextFocusListener(
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.forgotPasswordPinActivityConfirmPasswordET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.forgotPasswordPinActivityConfirmPasswordBox.background =
                    focusDrawable
                else if (b.forgotPasswordPinActivityConfirmPasswordWarningTV.isVisible) b.forgotPasswordPinActivityConfirmPasswordBox.background =
                    errorDrawable
                else b.forgotPasswordPinActivityConfirmPasswordBox.background = notInFocusDrawable
                //Hide password condition view
                b.forgotPasswordPinActivityNewPasswordConditionsContainer.visibility = View.GONE
            }
    }


    private fun configureEditTextFocusListeners(nameET: EditText, warningText: TextView) {
        nameET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
            val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            val notInFocusDrawable =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

            if (hasFocus) nameET.background = focusDrawable
            else if (warningText.isVisible) nameET.background = errorDrawable
            else nameET.background = notInFocusDrawable

        }
    }

    private fun configurePasswordSecurityQuestionEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.forgotPasswordPinActivityPasswordSecurityQuestionET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.forgotPasswordPinActivityPasswordSecurityQuestionET.background =
                    focusDrawable
                else if (b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV.isVisible) b.forgotPasswordPinActivityPasswordSecurityQuestionET.background =
                    errorDrawable
                else b.forgotPasswordPinActivityPasswordSecurityQuestionET.background =
                    notInFocusDrawable
                b.forgotPasswordPinActivityNewPasswordConditionsContainer.visibility = View.GONE
            }
    }

    private fun configurePinSecurityQuestionEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.forgotPasswordPinActivityPinSecurityQuestionET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.forgotPasswordPinActivityPinSecurityQuestionET.background =
                    focusDrawable
                else if (b.forgotPasswordPinActivityPinSecurityQuestionWarningTV.isVisible) b.forgotPasswordPinActivityPinSecurityQuestionET.background =
                    errorDrawable
                else b.forgotPasswordPinActivityPinSecurityQuestionET.background =
                    notInFocusDrawable
            }
    }

    private fun configureCodeEditTextFocusListener(et: EditText) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.forgotPasswordPinActivityOtpBox.background = focusDrawable
            else if (b.forgotPasswordPinActivityOtpWarningTV.isVisible) b.forgotPasswordPinActivityOtpBox.background =
                errorDrawable
            else b.forgotPasswordPinActivityOtpBox.background = notInFocusDrawable
        }
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
                        this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    warningText.visibility = View.GONE
                    et.background = ContextCompat.getDrawable(
                        this@ForgotPasswordPinActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun validateEmail(emailEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        when {
            emailEditText.text.isEmpty() -> {
                showEmailWarning(getString(R.string.required_field))
                isValid = false
            }

            !emailEditText.text.toString().isValidEmail() -> {
                showEmailWarning(getString(R.string.invalid_email))
                isValid = false
            }

            else -> {
                warningText.visibility = View.GONE
                b.forgotPasswordPinActivityEmailET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            }
        }
        return isValid
    }

    private fun showEmailView() {
        //Show email view by default
        b.forgotPasswordPinActivityEmailView.visibility = View.VISIBLE
        b.forgotPasswordPinActivityOtpView.visibility = View.GONE
        b.forgotPasswordPinActivitySetPasswordView.visibility = View.GONE
        b.forgotPasswordPinActivitySetPinView.visibility = View.GONE

        when (forgotCredentialType) {
            Constants.FORGOT_CREDENTIAL_PIN -> {
                b.forgotPasswordPinActivityEmailTitleTV.text = getString(R.string.reset_pin)
                b.forgotPasswordPinActivityEmailSubtextTV.text =
                    getString(R.string.enter_registered_email_to_reset_your_pin)
            }

            Constants.FORGOT_CREDENTIAL_PASSWORD -> {
                b.forgotPasswordPinActivityEmailTitleTV.text = getString(R.string.reset_password)
                b.forgotPasswordPinActivityEmailSubtextTV.text =
                    getString(R.string.enter_registered_email_to_reset_your_password)
            }
        }
    }

    private fun showOtpView() {
        b.forgotPasswordPinActivityOtpView.visibility = View.VISIBLE
        b.forgotPasswordPinActivityEmailView.visibility = View.GONE
        b.forgotPasswordPinActivitySetPasswordView.visibility = View.GONE
        b.forgotPasswordPinActivitySetPinView.visibility = View.GONE

        when (forgotCredentialType) {
            Constants.FORGOT_CREDENTIAL_PIN -> {
                b.forgotPasswordPinActivityOtpTitleTV.text = getString(R.string.reset_pin)
                b.forgotPasswordPinActivityOtpSubtextTV.text =
                    getString(R.string.reset_pin_otp_subtext)
            }

            Constants.FORGOT_CREDENTIAL_PASSWORD -> {
                b.forgotPasswordPinActivityOtpTitleTV.text = getString(R.string.reset_password)
                b.forgotPasswordPinActivityOtpSubtextTV.text =
                    getString(R.string.reset_password_otp_subtext)
            }
        }

        //Reset fields
        b.forgotPasswordPinActivityOtpET.text.clear()
        b.forgotPasswordPinActivityOtpBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.forgotPasswordPinActivityOtpWarningTV.visibility = View.GONE

        //start timer
        countDownTimer.start()
    }

    private fun showPasswordView(question: String) {
        b.forgotPasswordPinActivitySetPasswordView.visibility = View.VISIBLE
        b.forgotPasswordPinActivityOtpView.visibility = View.GONE
        b.forgotPasswordPinActivityEmailView.visibility = View.GONE
        b.forgotPasswordPinActivitySetPinView.visibility = View.GONE

        //Reset fields
        b.forgotPasswordPinActivityNewPasswordET.text.clear()
        b.forgotPasswordPinActivityConfirmPasswordET.text.clear()
        b.forgotPasswordPinActivityPasswordSecurityQuestionET.text.clear()

        b.forgotPasswordPinActivityNewPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.forgotPasswordPinActivityConfirmPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.forgotPasswordPinActivityPasswordSecurityQuestionET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.forgotPasswordPinActivityNewPasswordWarningTV.visibility = View.GONE
        b.forgotPasswordPinActivityConfirmPasswordWarningTV.visibility = View.GONE
        b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV.visibility = View.GONE

        //Set question
        b.forgotPasswordPinActivityPasswordSecurityQuestionTV.text = question

        countDownTimer.cancel()
    }

    private fun showPinView(question: String) {
        b.forgotPasswordPinActivitySetPinView.visibility = View.VISIBLE
        b.forgotPasswordPinActivitySetPasswordView.visibility = View.GONE
        b.forgotPasswordPinActivityOtpView.visibility = View.GONE
        b.forgotPasswordPinActivityEmailView.visibility = View.GONE

        //Reset fields
        b.forgotPasswordPinActivityNewPinET.text?.clear()
        b.forgotPasswordPinActivityConfirmPinET.text?.clear()
        b.forgotPasswordPinActivityPinSecurityQuestionET.text.clear()

        b.forgotPasswordPinActivityPinSecurityQuestionET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.forgotPasswordPinActivityNewPinWarningTV.visibility = View.GONE
        b.forgotPasswordPinActivityConfirmPinWarningTV.visibility = View.GONE
        b.forgotPasswordPinActivityPinSecurityQuestionWarningTV.visibility = View.GONE

        //Set question
        b.forgotPasswordPinActivityPinSecurityQuestionTV.text = question

        countDownTimer.cancel()
    }

    private fun showPasswordPinUpdatedView() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.pin_password_change_bottom_sheet, null)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setCancelable(false)
        val titleTextView = dialogView.findViewById<TextView>(R.id.sheetPinPasswordChangeTitleTV)
        val subTitleTextView = dialogView.findViewById<TextView>(R.id.sheetPinPasswordChangeSubTextTV)
        val loginButton = dialogView.findViewById<AppCompatButton>(R.id.sheetPinPasswordChangeButton)
        loginButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this@ForgotPasswordPinActivity, LoginActivity::class.java))
            finishAffinity()
        }
        when (forgotCredentialType) {
            Constants.FORGOT_CREDENTIAL_PIN -> {
                titleTextView.text = getString(R.string.pin_changed)
                subTitleTextView.text = getString(R.string.your_pin_has_been_successfully_changed)
            }

            Constants.FORGOT_CREDENTIAL_PASSWORD -> {
                titleTextView.text = getString(R.string.password_changed)
                subTitleTextView.text = getString(R.string.your_password_has_been_successfully_changed)
            }
        }
        bottomSheetDialog.show()
    }

    private fun validateOtp(): Boolean {
        var isValid = true
        if (b.forgotPasswordPinActivityOtpET.text.isEmpty()) {
            isValid = false
            showOtpWarning(R.string.required_field)
        } else if (b.forgotPasswordPinActivityOtpET.text.length != 6) {
            isValid = false
            showOtpWarning(R.string.invalid_otp)
        } else {
            b.forgotPasswordPinActivityOtpWarningTV.visibility = View.GONE
            b.forgotPasswordPinActivityOtpBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun showOtpWarning(warning: Int) {
        b.forgotPasswordPinActivityOtpWarningTV.visibility = View.VISIBLE
        b.forgotPasswordPinActivityOtpWarningTV.text = ContextCompat.getString(this, warning)
        b.forgotPasswordPinActivityOtpBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun validatePassword(): Boolean {
        var isValid = true

        b.forgotPasswordPinActivityConfirmPasswordWarningTV.visibility = View.GONE
        b.forgotPasswordPinActivityConfirmPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        if (b.forgotPasswordPinActivityNewPasswordET.text.toString() != b.forgotPasswordPinActivityConfirmPasswordET.text.toString()) {
            isValid = false
            b.forgotPasswordPinActivityConfirmPasswordWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityConfirmPasswordWarningTV.text =
                getString(R.string.password_does_not_match)
            b.forgotPasswordPinActivityConfirmPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }

        if (!validatePasswordConditions()) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityNewPasswordWarningTV.text =
                getString(R.string.weak_password_text)
            b.forgotPasswordPinActivityNewPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }

        if (b.forgotPasswordPinActivityNewPasswordET.text.isEmpty()) {
            isValid = false
            b.forgotPasswordPinActivityNewPasswordWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityNewPasswordWarningTV.text =
                getString(R.string.required_field)
            b.forgotPasswordPinActivityNewPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }
        if (b.forgotPasswordPinActivityConfirmPasswordET.text.isEmpty()) {
            isValid = false
            b.forgotPasswordPinActivityConfirmPasswordWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityConfirmPasswordWarningTV.text =
                getString(R.string.required_field)
            b.forgotPasswordPinActivityConfirmPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }

        if (b.forgotPasswordPinActivityPasswordSecurityQuestionET.text.isEmpty()) {
            isValid = false
            b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV.text =
                getString(R.string.required_field)
            b.forgotPasswordPinActivityPasswordSecurityQuestionET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }

        return isValid
    }

    private fun validatePin(): Boolean {
        var isValid = true

        b.forgotPasswordPinActivityConfirmPinWarningTV.visibility = View.GONE

        if (b.forgotPasswordPinActivityNewPinET.text.toString() != b.forgotPasswordPinActivityConfirmPinET.text.toString()) {
            isValid = false
            b.forgotPasswordPinActivityConfirmPinWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityConfirmPinWarningTV.text =
                getString(R.string.pin_does_not_match)
        }
        if (!validatePinCondition()) {
            isValid = false
            b.forgotPasswordPinActivityNewPinWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityNewPinWarningTV.text = getString(R.string.weak_pin_text)
        }
        if (b.forgotPasswordPinActivityNewPinET.text.toString().isEmpty()) {
            isValid = false
            b.forgotPasswordPinActivityNewPinWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityNewPinWarningTV.text = getString(R.string.required_field)
        } else if (b.forgotPasswordPinActivityNewPinET.text.toString().length != 6) {
            isValid = false
            b.forgotPasswordPinActivityNewPinWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityNewPinWarningTV.text = getString(R.string.invalid_pin)
        }

        if (b.forgotPasswordPinActivityConfirmPinET.text.toString().isEmpty()) {
            isValid = false
            b.forgotPasswordPinActivityConfirmPinWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityConfirmPinWarningTV.text = getString(R.string.required_field)
        } else if (b.forgotPasswordPinActivityConfirmPinET.text.toString().length != 6) {
            isValid = false
            b.forgotPasswordPinActivityConfirmPinWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityConfirmPinWarningTV.text = getString(R.string.invalid_pin)
        }

        if (b.forgotPasswordPinActivityPinSecurityQuestionET.text.isEmpty()) {
            isValid = false
            b.forgotPasswordPinActivityPinSecurityQuestionWarningTV.visibility = View.VISIBLE
            b.forgotPasswordPinActivityPinSecurityQuestionWarningTV.text =
                getString(R.string.required_field)
            b.forgotPasswordPinActivityPinSecurityQuestionET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }
        return isValid
    }

    private fun validatePinCondition(): Boolean {
        val typedPin = b.forgotPasswordPinActivityNewPinET.text.toString()
        val tpUpperCase: Pattern =
            Pattern.compile("^(?!([0-9])\\1+\$)(?!.*(.).*\\2)(?!01|12|23|34|45|56|67|78|89|98)(?!.*1234\$)\\d{6}\$")
        return tpUpperCase.matcher(typedPin).matches()
    }

    private fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun showEmailWarning(warning: String) {
        b.forgotPasswordPinActivityEmailWarningTV.visibility = View.VISIBLE
        b.forgotPasswordPinActivityEmailWarningTV.text = warning
        b.forgotPasswordPinActivityEmailET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun setUpEmailEditTextFilters(et: EditText) {
        et.filters += object : InputFilter.AllCaps() {
            override fun filter(
                source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
            ): CharSequence {
                return source!!.filterNot { char -> char.isWhitespace() }.toString().lowercase()
            }
        }
    }

    private fun sendOtpApi() {
        email = b.forgotPasswordPinActivityEmailET.text.toString()
        val sendForgotOtpCall =
            if (forgotCredentialType == Constants.FORGOT_CREDENTIAL_PIN) ApiClient.apiService.sendForgotOtp(
                b.forgotPasswordPinActivityEmailET.text.toString(), PIN
            )
            else ApiClient.apiService.sendForgotOtp(
                b.forgotPasswordPinActivityEmailET.text.toString(), PASSWORD
            )

        sendForgotOtpCall.enqueue(object : Callback<SendForgotOtpResponse> {
            override fun onResponse(
                call: Call<SendForgotOtpResponse>, response: Response<SendForgotOtpResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    paymaartId = body.user_id
                    encryptedOtp = body.Encrypted_otp
                    hideButtonLoader(
                        b.forgotPasswordPinActivityProceedButton,
                        b.forgotPasswordPinActivityProceedButtonLoaderLottie,
                        getString(R.string.proceed)
                    )
                    showOtpView()
                } else {
                    val errorResponse: SendForgotOtpResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<SendForgotOtpResponse>() {}.type
                    )
                    showEmailWarning(errorResponse.message)
                    hideButtonLoader(
                        b.forgotPasswordPinActivityProceedButton,
                        b.forgotPasswordPinActivityProceedButtonLoaderLottie,
                        getString(R.string.proceed)
                    )
                }
            }

            override fun onFailure(call: Call<SendForgotOtpResponse>, t: Throwable) {
                showToast(getString(R.string.default_error_toast))
                hideButtonLoader(
                    b.forgotPasswordPinActivityProceedButton,
                    b.forgotPasswordPinActivityProceedButtonLoaderLottie,
                    getString(R.string.proceed)
                )
            }
        })
    }

    private fun verifyForgotOtpApi() {
        showButtonLoader(
            b.forgotPasswordPinActivityOtpVerifyButton,
            b.forgotPasswordPinActivityOtpVerifyButtonLoaderLottie
        )

        val verifyForgotOtpCall = ApiClient.apiService.verifyForgotOtp(
            b.forgotPasswordPinActivityOtpET.text.toString(), encryptedOtp, paymaartId
        )
        verifyForgotOtpCall.enqueue(object : Callback<VerifyForgotOtpResponse> {
            override fun onResponse(
                call: Call<VerifyForgotOtpResponse>, response: Response<VerifyForgotOtpResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    securityQuestionId = body.id
                    hideButtonLoader(
                        b.forgotPasswordPinActivityOtpVerifyButton,
                        b.forgotPasswordPinActivityOtpVerifyButtonLoaderLottie,
                        getString(R.string.verify)
                    )
                    when (forgotCredentialType) {
                        Constants.FORGOT_CREDENTIAL_PIN -> showPinView(body.question)
                        Constants.FORGOT_CREDENTIAL_PASSWORD -> showPasswordView(body.question)
                    }
                } else {
                    val errorResponse: VerifyForgotOtpResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<VerifyForgotOtpResponse>() {}.type
                    )
                    showOtpWarning(errorResponse.message)
                    hideButtonLoader(
                        b.forgotPasswordPinActivityOtpVerifyButton,
                        b.forgotPasswordPinActivityOtpVerifyButtonLoaderLottie,
                        getString(R.string.verify)
                    )
                }
            }

            override fun onFailure(call: Call<VerifyForgotOtpResponse>, t: Throwable) {
                showToast(getString(R.string.default_error_toast))
                hideButtonLoader(
                    b.forgotPasswordPinActivityOtpVerifyButton,
                    b.forgotPasswordPinActivityOtpVerifyButtonLoaderLottie,
                    getString(R.string.verify)
                )
            }

        })
    }

    private fun resendOtpApi() {
        val resendForgotOtpCall =
            if (forgotCredentialType == Constants.FORGOT_CREDENTIAL_PIN) ApiClient.apiService.sendForgotOtp(
                b.forgotPasswordPinActivityEmailET.text.toString(), PIN
            )
            else ApiClient.apiService.sendForgotOtp(
                email, PASSWORD
            )

        resendForgotOtpCall.enqueue(object : Callback<SendForgotOtpResponse> {
            override fun onResponse(
                call: Call<SendForgotOtpResponse>, response: Response<SendForgotOtpResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    paymaartId = body.user_id
                    encryptedOtp = body.Encrypted_otp
                    showToast(getString(R.string.successfully_resent_otp))
                    resendCount++
                    if (resendCount >= 3) {
                        b.forgotPasswordPinActivityOtpBottomTextTV.text = ContextCompat.getString(
                            this@ForgotPasswordPinActivity, R.string.resend_limit_is_3
                        )
                        b.forgotPasswordPinActivityOtpBottomTextTV.setTextColor(
                            ContextCompat.getColor(
                                this@ForgotPasswordPinActivity, R.color.accentInformation
                            )
                        )
                        b.forgotPasswordPinActivityOtpResendTV.visibility = View.GONE
                        b.forgotPasswordPinActivityOtpTimerTV.visibility = View.GONE
                    }
                } else {
                    val errorResponse: SendForgotOtpResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<SendForgotOtpResponse>() {}.type
                    )
                    showEmailWarning(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<SendForgotOtpResponse>, t: Throwable) {
                showToast(getString(R.string.default_error_toast))
            }
        })
    }

    private fun updatePinApi() {
        showButtonLoader(
            b.forgotPasswordPinActivityPinResetButton,
            b.forgotPasswordPinActivityPinResetButtonLoaderLottie
        )
        val updatePinApiCall = ApiClient.apiService.updatePinPassword(
            UpdatePinPasswordRequest(
                password = AESCrypt.encrypt(b.forgotPasswordPinActivityNewPinET.text.toString()),
                confirm_password = AESCrypt.encrypt(b.forgotPasswordPinActivityConfirmPinET.text.toString()),
                email_address = email,
                type = PIN,
                question_id = securityQuestionId,
                answer = b.forgotPasswordPinActivityPinSecurityQuestionET.text.toString()
                    .lowercase()
            )
        )

        updatePinApiCall.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>, response: Response<DefaultResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    hideButtonLoader(
                        b.forgotPasswordPinActivityPinResetButton,
                        b.forgotPasswordPinActivityPinResetButtonLoaderLottie,
                        getString(R.string.reset)
                    )
                    showPasswordPinUpdatedView()
                } else {
                    val errorResponse: DefaultResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<DefaultResponse>() {}.type
                    )
                    showPinQuestionWarning(errorResponse.message)
                    hideButtonLoader(
                        b.forgotPasswordPinActivityPinResetButton,
                        b.forgotPasswordPinActivityPinResetButtonLoaderLottie,
                        getString(R.string.reset)
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                showToast(getString(R.string.default_error_toast))
            }

        })
    }

    private fun updatePasswordApi() {
        showButtonLoader(
            b.forgotPasswordPinActivityPasswordResetButton,
            b.forgotPasswordPinActivityPasswordResetButtonLoaderLottie
        )
        val updatePinPasswordApiCall = ApiClient.apiService.updatePinPassword(
            UpdatePinPasswordRequest(
                password = AESCrypt.encrypt(b.forgotPasswordPinActivityNewPasswordET.text.toString()),
                confirm_password = AESCrypt.encrypt(b.forgotPasswordPinActivityConfirmPasswordET.text.toString()),
                email_address = email,
                type = PASSWORD,
                question_id = securityQuestionId,
                answer = b.forgotPasswordPinActivityPasswordSecurityQuestionET.text.toString()
                    .lowercase()
            )
        )

        updatePinPasswordApiCall.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>, response: Response<DefaultResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    hideButtonLoader(
                        b.forgotPasswordPinActivityPasswordResetButton,
                        b.forgotPasswordPinActivityPasswordResetButtonLoaderLottie,
                        getString(R.string.reset)
                    )
                    showPasswordPinUpdatedView()
                } else {
                    val errorResponse: DefaultResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<DefaultResponse>() {}.type
                    )
                    showPasswordQuestionWarning(errorResponse.message)
                    hideButtonLoader(
                        b.forgotPasswordPinActivityPasswordResetButton,
                        b.forgotPasswordPinActivityPasswordResetButtonLoaderLottie,
                        getString(R.string.reset)
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                showToast(getString(R.string.default_error_toast))
            }

        })
    }

    private fun showOtpWarning(warning: String) {
        b.forgotPasswordPinActivityOtpWarningTV.visibility = View.VISIBLE
        b.forgotPasswordPinActivityOtpWarningTV.text = warning
        b.forgotPasswordPinActivityOtpBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun showPinQuestionWarning(warning: String) {
        b.forgotPasswordPinActivityPinSecurityQuestionWarningTV.visibility = View.VISIBLE
        b.forgotPasswordPinActivityPinSecurityQuestionWarningTV.text = warning
        b.forgotPasswordPinActivityPinSecurityQuestionET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun showPasswordQuestionWarning(warning: String) {
        b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV.visibility = View.VISIBLE
        b.forgotPasswordPinActivityPasswordSecurityQuestionWarningTV.text = warning
        b.forgotPasswordPinActivityPasswordSecurityQuestionET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = View.VISIBLE
    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = View.GONE

    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())

        window.enterTransition = slide
        window.returnTransition = slide
    }

    companion object {
        const val PASSWORD = "password"
        const val PIN = "pin"
    }
}