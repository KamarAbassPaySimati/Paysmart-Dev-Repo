package com.afrimax.paymaart.ui.register

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.RecaptchaApiClient
import com.afrimax.paymaart.data.model.CreateUserRequestBody
import com.afrimax.paymaart.data.model.CreateUserResponse
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.RecaptchaRequestBody
import com.afrimax.paymaart.data.model.RecaptchaResponse
import com.afrimax.paymaart.data.model.SecurityQuestionAnswerModel
import com.afrimax.paymaart.data.model.SecurityQuestionsResponse
import com.afrimax.paymaart.data.model.SendOtpRequestBody
import com.afrimax.paymaart.data.model.SendOtpResponse
import com.afrimax.paymaart.databinding.ActivityRegisterBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.GuideBottomSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.VerificationBottomSheet
import com.afrimax.paymaart.ui.utils.interfaces.VerificationBottomSheetInterface
import com.afrimax.paymaart.ui.webview.WebViewActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetApi
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Executor

class RegisterActivity : BaseActivity(), VerificationBottomSheetInterface {
    private lateinit var b: ActivityRegisterBinding
    private lateinit var fileResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var guideSheet: BottomSheetDialogFragment
    private lateinit var verificationBottomSheet: BottomSheetDialogFragment
    private var isEmailVerified = false
    private var isPhoneVerified = false
    private var emailRecordId = ""
    private var phoneRecordId = ""
    private var profilePicUri: Uri? = null
    private var isPicUploaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        initViews()
        setUpLayout()
        setupListeners()
        retrieveSecurityQuestionsApi()
    }

    private fun setUpLayout() {

        b.onboardRegistrationActivityTnCPrivacyPolicyTV.movementMethod =
            LinkMovementMethod.getInstance()

        val clickableSpanTnC: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val termsAndConditionsIntent =
                    Intent(this@RegisterActivity, WebViewActivity::class.java)
                termsAndConditionsIntent.putExtra(
                    Constants.TYPE, Constants.TERMS_AND_CONDITIONS_TYPE
                )
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@RegisterActivity)
                        .toBundle()
                startActivity(termsAndConditionsIntent, options)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val clickableSpanPrivacyPolicy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val privacyPolicyIntent =
                    Intent(this@RegisterActivity, WebViewActivity::class.java)
                privacyPolicyIntent.putExtra(Constants.TYPE, Constants.PRIVACY_POLICY_TYPE)
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@RegisterActivity)
                        .toBundle()
                startActivity(privacyPolicyIntent, options)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        b.onboardRegistrationActivityProfileContainer.visibility = View.VISIBLE
        val ss = SpannableString(getString(R.string.terms_and_conditions))
        ss.setSpan(clickableSpanTnC, 36, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpanPrivacyPolicy, 59, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.onboardRegistrationActivityTnCPrivacyPolicyTV.text = ss
    }

    private fun initViews() {
        guideSheet = GuideBottomSheet()

        if (BuildConfig.STAGE == Constants.STAGE_DEV || BuildConfig.STAGE == Constants.STAGE_QA) {
            //This is required for Indian phone number testing
            b.onboardRegistrationActivityCountryCodeSpinner.visibility = View.VISIBLE
            b.onboardRegistrationActivityCountryCodeDropDownIV.visibility = View.VISIBLE
            b.onboardRegistrationActivityCountryCodeTV.visibility = View.GONE

            val items = arrayOf("+265", "+91")
            val adapter = ArrayAdapter(this, R.layout.spinner_country_code, items)
            b.onboardRegistrationActivityCountryCodeSpinner.adapter = adapter

            b.onboardRegistrationActivityCountryCodeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?, p1: View?, position: Int, p3: Long,
                    ) {
                        when (position) {
                            0 -> b.onboardRegistrationActivityPhoneET.filters =
                                arrayOf(InputFilter.LengthFilter(11))

                            //Indian Phone numbers are 10 in size + 2 for space
                            1 -> b.onboardRegistrationActivityPhoneET.filters =
                                arrayOf(InputFilter.LengthFilter(12))
                        }
                        if (b.onboardRegistrationActivityPhoneET.text.toString()
                                .isNotEmpty()
                        ) b.onboardRegistrationActivityPhoneET.text!!.clear()
                        isPhoneVerified = false
                        b.onboardRegistrationActivityPhoneVerifyButton.visibility = View.VISIBLE
                        b.onboardRegistrationActivityPhoneVerifiedTV.visibility = View.GONE
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //
                    }
                }
        } else {
            b.onboardRegistrationActivityCountryCodeSpinner.visibility = View.GONE
            b.onboardRegistrationActivityCountryCodeDropDownIV.visibility = View.GONE
            b.onboardRegistrationActivityCountryCodeTV.visibility = View.VISIBLE
        }

        fileResultLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                lifecycleScope.launch {
                    if (uri != null) {
                        isPicUploaded = true
                        populateSelectedFile(uri)
                    }
                }
            }
    }

    private fun setupListeners() {

        b.onboardRegistrationActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.onboardRegistrationActivityProfileIV.setOnClickListener {
            launchFilePicker()
        }

        b.onboardRegistrationActivityCameraIV.setOnClickListener {
            if (isPicUploaded){
                profilePicUri = null
                b.onboardRegistrationActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera))
                b.onboardRegistrationActivityProfileIV.apply {
                    setImageDrawable(ContextCompat.getDrawable(this@RegisterActivity, R.drawable.ic_no_image))
                    background = ContextCompat.getDrawable(this@RegisterActivity, R.drawable.dashed_outline_background)
                }
            }else{
                launchFilePicker()
            }
        }

        b.onboardRegistrationActivityEmailVerifyButton.setOnClickListener {
            validateFieldsForEmailOtp()
        }

        b.onboardRegistrationActivityPhoneVerifyButton.setOnClickListener {
            validateFieldsForPhoneOtp()
        }

        b.onboardRegistrationActivityPhoneET.setOnClickListener { _ ->
            b.onboardRegistrationActivityPhoneET.setSelection(b.onboardRegistrationActivityPhoneET.text!!.length)
        }

        b.onboardRegistrationActivityGuideButton.setOnClickListener {
            guideSheet.show(supportFragmentManager, GuideBottomSheet.TAG)

        }

        b.onboardRegistrationActivitySubmitButton.setOnClickListener {
            validateFieldsForSubmit()
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
        setUpLastNameEditTextFilters(b.onboardRegistrationActivityLastNameET)
        setUpEmailEditTextFilters(b.onboardRegistrationActivityEmailET)

        b.onboardRegistrationActivityCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) b.onboardRegistrationActivityTnCPrivacyPolicyWarningTV.visibility =
                View.GONE
            else b.onboardRegistrationActivityTnCPrivacyPolicyWarningTV.visibility = View.VISIBLE
        }
    }

    private fun setUpLastNameEditTextFilters(et: EditText) {
        et.filters += InputFilter.AllCaps()
    }

    private fun setUpEmailEditTextFilters(et: EditText) {
        et.filters += object : InputFilter.AllCaps() {
            override fun filter(
                source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int,
            ): CharSequence {
                return source!!.filterNot { char -> char.isWhitespace() }.toString().lowercase()
            }
        }
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextChangeListeners(
            b.onboardRegistrationActivityFirstNameET,
            b.onboardRegistrationActivityFirstNameWarningTV
        )
        configureEditTextChangeListeners(
            b.onboardRegistrationActivityMiddleNameET,
            b.onboardRegistrationActivityMiddleNameWarningTV
        )
        configureEditTextChangeListeners(
            b.onboardRegistrationActivityLastNameET, b.onboardRegistrationActivityLastNameWarningTV
        )
        configureEditTextEmailChangeListener()
        configureEditTextPhoneChangeListener()
        configureEditTextSecurityQuestionChangeListeners(b.onboardRegistrationActivitySecurityQuestion1ET)
        configureEditTextSecurityQuestionChangeListeners(b.onboardRegistrationActivitySecurityQuestion2ET)
        configureEditTextSecurityQuestionChangeListeners(b.onboardRegistrationActivitySecurityQuestion3ET)
        configureEditTextSecurityQuestionChangeListeners(b.onboardRegistrationActivitySecurityQuestion4ET)
    }


    private fun configureEditTextSecurityQuestionChangeListeners(securityQuestionET: EditText) {
        securityQuestionET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Restrict users to enter space at first
                if (securityQuestionET.text.startsWith(" ")) {
                    securityQuestionET.setText(securityQuestionET.text.toString().trim())
                    securityQuestionET.setSelection(securityQuestionET.text.length)
                }
                if (securityQuestionET.text.isEmpty() || b.onboardRegistrationActivitySecurityQuestionTV.isVisible) validateSecurityQuestions()
                securityQuestionET.background = ContextCompat.getDrawable(
                    this@RegisterActivity, R.drawable.edit_text_focused_background
                )
            }
        })
    }

    private fun configureEditTextChangeListeners(nameET: EditText, warningTV: TextView) {
        nameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (nameET.text.isEmpty()) {
                    warningTV.visibility = View.VISIBLE
                    nameET.background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.edit_text_error_background
                    )
                } else {
                    warningTV.visibility = View.GONE
                    nameET.background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.edit_text_focused_background
                    )
                }
            }
        })
    }

    private fun configureEditTextPhoneChangeListener() {
        b.onboardRegistrationActivityPhoneET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //If they change phone they have to verify it again
                isPhoneVerified = false
                if (b.onboardRegistrationActivityPhoneVerifiedTV.visibility == View.VISIBLE) {
                    b.onboardRegistrationActivityPhoneVerifyButton.visibility = View.VISIBLE
                    b.onboardRegistrationActivityPhoneVerifiedTV.visibility = View.GONE
                }

                //Remove any warning text
                if (b.onboardRegistrationActivityPhoneET.text.toString().isEmpty()) {
                    b.onboardRegistrationActivityPhoneWarningTV.visibility = View.VISIBLE
                    b.onboardRegistrationActivityPhoneBox.background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.edit_text_error_background
                    )
                } else {
                    b.onboardRegistrationActivityPhoneWarningTV.visibility = View.GONE
                    b.onboardRegistrationActivityPhoneBox.background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.edit_text_focused_background
                    )
                }
            }
        })
    }

    private fun configureEditTextEmailChangeListener() {
        b.onboardRegistrationActivityEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //If they change email they have to verify it again
                isEmailVerified = false
                if (b.onboardRegistrationActivityEmailVerifiedTV.visibility == View.VISIBLE) {
                    b.onboardRegistrationActivityEmailVerifyButton.visibility = View.VISIBLE
                    b.onboardRegistrationActivityEmailVerifiedTV.visibility = View.GONE
                }

                //Remove any warning messages
                if (b.onboardRegistrationActivityEmailET.text.isEmpty()) {
                    b.onboardRegistrationActivityEmailWarningTV.visibility = View.VISIBLE
                    b.onboardRegistrationActivityEmailBox.background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.edit_text_error_background
                    )
                } else {
                    b.onboardRegistrationActivityEmailWarningTV.visibility = View.GONE
                    b.onboardRegistrationActivityEmailBox.background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.edit_text_focused_background
                    )
                }
            }
        })
    }

    private fun setupEditTextFocusListeners() {
        configureEditTextFocusListeners(
            b.onboardRegistrationActivityFirstNameET,
            b.onboardRegistrationActivityFirstNameWarningTV
        )
        configureEditTextFocusListeners(
            b.onboardRegistrationActivityMiddleNameET,
            b.onboardRegistrationActivityMiddleNameWarningTV
        )
        configureEditTextFocusListeners(
            b.onboardRegistrationActivityLastNameET, b.onboardRegistrationActivityLastNameWarningTV
        )
        configureEmailEditTextFocusListener(
            b.onboardRegistrationActivityEmailET, b.onboardRegistrationActivityEmailWarningTV
        )
        configurePhoneEditTextFocusListener(
            b.onboardRegistrationActivityPhoneET, b.onboardRegistrationActivityPhoneWarningTV
        )
        configureEditTextSecurityQuestionFocusListeners(
            b.onboardRegistrationActivitySecurityQuestion1ET,
            b.onboardRegistrationActivitySecurityQuestionTV
        )
        configureEditTextSecurityQuestionFocusListeners(
            b.onboardRegistrationActivitySecurityQuestion2ET,
            b.onboardRegistrationActivitySecurityQuestionTV
        )
        configureEditTextSecurityQuestionFocusListeners(
            b.onboardRegistrationActivitySecurityQuestion3ET,
            b.onboardRegistrationActivitySecurityQuestionTV
        )
        configureEditTextSecurityQuestionFocusListeners(
            b.onboardRegistrationActivitySecurityQuestion4ET,
            b.onboardRegistrationActivitySecurityQuestionTV
        )
    }


    private fun configureEditTextFocusListeners(nameET: EditText, warningText: TextView) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_focused_background)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_background)

        nameET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) nameET.background = focusDrawable
            else if (warningText.isVisible) nameET.background = errorDrawable
            else nameET.background = notInFocusDrawable

        }
    }

    private fun configureEmailEditTextFocusListener(
        et: EditText, warningTV: TextView,
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_focused_background)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_background)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.onboardRegistrationActivityEmailBox.background = focusDrawable
            else if (warningTV.isVisible) b.onboardRegistrationActivityEmailBox.background =
                errorDrawable
            else b.onboardRegistrationActivityEmailBox.background = notInFocusDrawable
        }
    }

    private fun configurePhoneEditTextFocusListener(
        et: EditText, warningTV: TextView,
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_focused_background)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_background)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.onboardRegistrationActivityPhoneBox.background = focusDrawable
            else if (warningTV.isVisible) b.onboardRegistrationActivityPhoneBox.background =
                errorDrawable
            else b.onboardRegistrationActivityPhoneBox.background = notInFocusDrawable
        }
    }

    private fun configureEditTextSecurityQuestionFocusListeners(
        nameET: EditText, warningText: TextView,
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_focused_background)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_background)

        nameET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) nameET.background = focusDrawable
            else if (warningText.isVisible && nameET.text.isEmpty()) nameET.background =
                errorDrawable
            else nameET.background = notInFocusDrawable

        }
    }

    private fun validateFieldsForSubmit() {
        var isValid = true
        var focusView: View? = null

        if (!validateNames(
                b.onboardRegistrationActivityFirstNameET,
                b.onboardRegistrationActivityFirstNameWarningTV
            )
        ) {
            isValid = false
            focusView = b.onboardRegistrationActivityFirstNameET
        }

        if (!validateNames(
                b.onboardRegistrationActivityMiddleNameET,
                b.onboardRegistrationActivityMiddleNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityMiddleNameET
        }

        if (!validateNames(
                b.onboardRegistrationActivityLastNameET,
                b.onboardRegistrationActivityLastNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityLastNameET
        }

        if (!validateEmail(
                b.onboardRegistrationActivityEmailET, b.onboardRegistrationActivityEmailWarningTV
            )
        ) {
            //Not valid email
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityEmailET
        } else {
            //Entered valid email
            if (!isEmailVerified) {
                //Email not verified
                isValid = false
                if (focusView == null) focusView = b.onboardRegistrationActivityEmailET
                b.onboardRegistrationActivityEmailWarningTV.visibility = View.VISIBLE
                b.onboardRegistrationActivityEmailWarningTV.text =
                    ContextCompat.getString(this, R.string.please_verify_email)
            } else {
                //Email verified
                b.onboardRegistrationActivityEmailWarningTV.visibility = View.GONE
            }
        }

        if (!validatePhone(
                b.onboardRegistrationActivityPhoneET, b.onboardRegistrationActivityPhoneWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityPhoneET
        } else {
            if (!isPhoneVerified) {
                isValid = false
                if (focusView == null) focusView = b.onboardRegistrationActivityPhoneET
                b.onboardRegistrationActivityPhoneWarningTV.visibility = View.VISIBLE
                b.onboardRegistrationActivityPhoneWarningTV.text =
                    ContextCompat.getString(this, R.string.please_verify_phone)
            } else {
                b.onboardRegistrationActivityPhoneWarningTV.visibility = View.GONE
            }
        }

        if (!validateSecurityQuestions()) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivitySecurityQuestion4ET
        }

        if (!b.onboardRegistrationActivityCB.isChecked) {
            b.onboardRegistrationActivityTnCPrivacyPolicyWarningTV.visibility = View.VISIBLE
            isValid = false
            if (focusView == null) focusView =
                b.onboardRegistrationActivityTnCPrivacyPolicyWarningTV
        } else {
            b.onboardRegistrationActivityTnCPrivacyPolicyWarningTV.visibility = View.GONE
        }

        if (isValid) {
//            val siteKey = BuildConfig.SITE_KEY
//            val secretKey = BuildConfig.SECRET_KEY
//            SafetyNet.getClient(this@RegisterActivity).verifyWithRecaptcha("6LeE2wgqAAAAAG0Gd8kXjYRgI-YlJfuYrLF6r8uV")
//                .addOnSuccessListener{ response ->
//                    // Indicates communication with reCAPTCHA service was
//                    // successful.
//                    val userResponseToken = response.tokenResult ?: ""
//                    "UserToken".showLogE(userResponseToken)
//                    registerCustomer()
//                }
//                .addOnFailureListener{ e ->
//                    if (e is ApiException) {
//                        "RecaptchaError".showLogE("Error 1: ${e.message}")
//                    } else {
//                        "RecaptchaError".showLogE("Error 2: ${e.message}")
//                    }
//                }

            registerCustomer()

        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun registerCustomer() {
        showButtonLoader(
            b.onboardRegistrationActivitySubmitButton,
            b.onboardRegistrationActivitySubmitButtonLoaderLottie
        )

        //If registering  a customer check for profile picture
//        val profilePic = if (profilePicUri != null) "$profilePicUri" else ""
        val profilePic = ""
        val makeVisible = profilePic.isNotEmpty() && b.onboardRegistrationActivityMakeVisibleCB.isChecked
        val firstName = b.onboardRegistrationActivityFirstNameET.text.toString()
        val middleName = b.onboardRegistrationActivityMiddleNameET.text.toString()
        val lastName = b.onboardRegistrationActivityLastNameET.text.toString()
        val countryCode = getCountryCode()
        val phoneNumber = b.onboardRegistrationActivityPhoneET.text.toString().replace(" ", "")
        val email = b.onboardRegistrationActivityEmailET.text.toString()

        val securityQuestions = obtainSecurityQuestionAnswers()


        val registerCustomerCall = ApiClient.apiService.registerCustomer(
            CreateUserRequestBody(
                first_name = firstName,
                middle_name = middleName,
                last_name = lastName,
                country_code = countryCode,
                phone_number = phoneNumber,
                email = email,
                email_otp_id = emailRecordId,
                phone_otp_id = phoneRecordId,
                security_questions = securityQuestions,
                profile_pic = profilePic,
                public_profile = makeVisible
            )
        )

        registerCustomerCall.enqueue(object : Callback<CreateUserResponse> {
            override fun onResponse(
                call: Call<CreateUserResponse>, response: Response<CreateUserResponse>,
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    runOnUiThread {
                        hideButtonLoader(
                            b.onboardRegistrationActivitySubmitButton,
                            b.onboardRegistrationActivitySubmitButtonLoaderLottie,
                            getString(R.string.submit)
                        )
                        showToast(body.message)
                        val i = Intent(
                            this@RegisterActivity,
                            RegistrationSuccessfulActivity::class.java
                        )
                        i.putExtra(Constants.INTENT_DATA_EMAIL, email)
                        startActivity(i)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        hideButtonLoader(
                            b.onboardRegistrationActivitySubmitButton,
                            b.onboardRegistrationActivitySubmitButtonLoaderLottie,
                            getString(R.string.submit)
                        )
                        val errorResponse: DefaultResponse = Gson().fromJson(
                            response.errorBody()!!.string(),
                            DefaultResponse::class.java
                        )
                        showToast(errorResponse.message)
                    }
                }

            }

            override fun onFailure(call: Call<CreateUserResponse>, t: Throwable) {
                runOnUiThread {
                    hideButtonLoader(
                        b.onboardRegistrationActivitySubmitButton,
                        b.onboardRegistrationActivitySubmitButtonLoaderLottie,
                        getString(R.string.submit)
                    )
                    showToast(getString(R.string.default_error_toast))
                }
            }
        })
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView,
    ) {
        actionButton.text = ""
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = View.VISIBLE
    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String,
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = View.GONE

    }

    private fun getCountryCode(): String {
        return if (BuildConfig.STAGE == Constants.STAGE_DEV || BuildConfig.STAGE == Constants.STAGE_QA) {
            b.onboardRegistrationActivityCountryCodeSpinner.selectedItem.toString()
        } else {
            "+265"
        }
    }

    private fun obtainSecurityQuestionAnswers(): List<SecurityQuestionAnswerModel> {
        val securityQuestions = ArrayList<SecurityQuestionAnswerModel>()

        if (b.onboardRegistrationActivitySecurityQuestion1ET.text.isNotEmpty()) {
            securityQuestions.add(
                SecurityQuestionAnswerModel(
                    b.onboardRegistrationActivitySecurityQuestion1TV.getTag(
                        R.string.security_question_id
                    ).toString(),
                    b.onboardRegistrationActivitySecurityQuestion1ET.text.toString().lowercase()
                )
            )
        }

        if (b.onboardRegistrationActivitySecurityQuestion2ET.text.isNotEmpty()) {
            securityQuestions.add(
                SecurityQuestionAnswerModel(
                    b.onboardRegistrationActivitySecurityQuestion2TV.getTag(
                        R.string.security_question_id
                    ).toString(),
                    b.onboardRegistrationActivitySecurityQuestion2ET.text.toString().lowercase()
                )
            )
        }

        if (b.onboardRegistrationActivitySecurityQuestion3ET.text.isNotEmpty()) {
            securityQuestions.add(
                SecurityQuestionAnswerModel(
                    b.onboardRegistrationActivitySecurityQuestion3TV.getTag(
                        R.string.security_question_id
                    ).toString(),
                    b.onboardRegistrationActivitySecurityQuestion3ET.text.toString().lowercase()
                )
            )
        }

        if (b.onboardRegistrationActivitySecurityQuestion4ET.text.isNotEmpty()) {
            securityQuestions.add(
                SecurityQuestionAnswerModel(
                    b.onboardRegistrationActivitySecurityQuestion4TV.getTag(
                        R.string.security_question_id
                    ).toString(),
                    b.onboardRegistrationActivitySecurityQuestion4ET.text.toString().lowercase()
                )
            )
        }
        return securityQuestions
    }

    private fun validateFieldsForEmailOtp() {
        var isValid = true
        var focusView: View? = null

        if (!validateNames(
                b.onboardRegistrationActivityFirstNameET,
                b.onboardRegistrationActivityFirstNameWarningTV
            )
        ) {
            isValid = false
            focusView = b.onboardRegistrationActivityFirstNameET
        }

        if (!validateNames(
                b.onboardRegistrationActivityMiddleNameET,
                b.onboardRegistrationActivityMiddleNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityMiddleNameET
        }

        if (!validateNames(
                b.onboardRegistrationActivityLastNameET,
                b.onboardRegistrationActivityLastNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityLastNameET
        }

        if (!validateEmail(
                b.onboardRegistrationActivityEmailET, b.onboardRegistrationActivityEmailWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityEmailET
        }

        if (isValid) {
            sendOtp(Constants.OTP_EMAIL_TYPE)
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun validateFieldsForPhoneOtp() {
        var isValid = true
        var focusView: View? = null

        if (!validateNames(
                b.onboardRegistrationActivityFirstNameET,
                b.onboardRegistrationActivityFirstNameWarningTV
            )
        ) {
            isValid = false
            focusView = b.onboardRegistrationActivityFirstNameET
        }

        if (!validateNames(
                b.onboardRegistrationActivityMiddleNameET,
                b.onboardRegistrationActivityMiddleNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityMiddleNameET
        }

        if (!validateNames(
                b.onboardRegistrationActivityLastNameET,
                b.onboardRegistrationActivityLastNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityLastNameET
        }

        if (!isEmailVerified) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityEmailET
            b.onboardRegistrationActivityEmailWarningTV.visibility = View.VISIBLE
            b.onboardRegistrationActivityEmailWarningTV.text =
                ContextCompat.getString(this, R.string.please_verify_email)
        } else {
            b.onboardRegistrationActivityEmailWarningTV.visibility = View.GONE
        }

        if (!validatePhone(
                b.onboardRegistrationActivityPhoneET, b.onboardRegistrationActivityPhoneWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityPhoneET
        }

        if (isValid) {
            sendOtp(Constants.OTP_SMS_TYPE)
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }


    private fun sendOtp(type: String) {
        val firstName = b.onboardRegistrationActivityFirstNameET.text.toString()
        val middleName = b.onboardRegistrationActivityMiddleNameET.text.toString()
        val lastName = b.onboardRegistrationActivityLastNameET.text.toString()
        var value = ""
        var countryCode = ""
        when (type) {
            Constants.OTP_EMAIL_TYPE -> {
                value = b.onboardRegistrationActivityEmailET.text.toString()
                b.onboardRegistrationActivityEmailVerifyPB.visibility = View.VISIBLE
                b.onboardRegistrationActivityEmailVerifyButton.visibility = View.GONE
            }

            Constants.OTP_SMS_TYPE -> {
                value = b.onboardRegistrationActivityPhoneET.text.toString()
                b.onboardRegistrationActivityPhoneVerifyPB.visibility = View.VISIBLE
                b.onboardRegistrationActivityPhoneVerifyButton.visibility = View.GONE
                countryCode = getCountryCode()
            }
        }


        lifecycleScope.launch {
            val sendOtpRequestBody = SendOtpRequestBody(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                type = type,
                value = value,
                countryCode = countryCode
            )
            val otpCall = ApiClient.apiService.sentOtp(sendOtpRequestBody)
            otpCall.enqueue(object : Callback<SendOtpResponse> {
                override fun onResponse(
                    call: Call<SendOtpResponse>, response: Response<SendOtpResponse>,
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            verificationBottomSheet = VerificationBottomSheet()
                            verificationBottomSheet.isCancelable = false
                            val bundle = Bundle()
                            when (type) {
                                Constants.OTP_EMAIL_TYPE -> {
                                    bundle.putString(Constants.TYPE, Constants.OTP_EMAIL_TYPE)
                                    bundle.putString(
                                        Constants.OTP_VALUE, value
                                    )
                                    b.onboardRegistrationActivityEmailVerifyPB.visibility =
                                        View.GONE
                                    b.onboardRegistrationActivityEmailVerifyButton.visibility =
                                        View.VISIBLE
                                }

                                Constants.OTP_SMS_TYPE -> {
                                    bundle.putString(Constants.TYPE, Constants.OTP_SMS_TYPE)
                                    bundle.putString(Constants.OTP_VALUE, value)
                                    bundle.putString(Constants.OTP_COUNTRY_CODE, countryCode)
                                    b.onboardRegistrationActivityPhoneVerifyPB.visibility =
                                        View.GONE
                                    b.onboardRegistrationActivityPhoneVerifyButton.visibility =
                                        View.VISIBLE
                                }
                            }
                            bundle.putString(Constants.OTP_FIRST_NAME, firstName)
                            bundle.putString(Constants.OTP_MIDDLE_NAME, middleName)
                            bundle.putString(Constants.OTP_LAST_NAME, lastName)
                            bundle.putString(Constants.OTP_TOKEN, body.token)
                            verificationBottomSheet.arguments = bundle
                            verificationBottomSheet.show(
                                supportFragmentManager,
                                VerificationBottomSheet.TAG
                            )
                        }

                    } else {
                        runOnUiThread {
                            val errorResponse: SendOtpResponse = Gson().fromJson(
                                response.errorBody()!!.string(),
                                object : TypeToken<SendOtpResponse>() {}.type
                            )
                            when (type) {
                                Constants.OTP_EMAIL_TYPE -> {
                                    showEmailWarning(errorResponse.message)
                                    b.onboardRegistrationActivityEmailVerifyPB.visibility =
                                        View.GONE
                                    b.onboardRegistrationActivityEmailVerifyButton.visibility =
                                        View.VISIBLE
                                }

                                Constants.OTP_SMS_TYPE -> {
                                    showPhoneWarning(errorResponse.message)
                                    b.onboardRegistrationActivityPhoneVerifyPB.visibility =
                                        View.GONE
                                    b.onboardRegistrationActivityPhoneVerifyButton.visibility =
                                        View.VISIBLE
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        when (type) {
                            Constants.OTP_EMAIL_TYPE -> {
                                b.onboardRegistrationActivityEmailVerifyPB.visibility = View.GONE
                                b.onboardRegistrationActivityEmailVerifyButton.visibility =
                                    View.VISIBLE
                            }

                            Constants.OTP_SMS_TYPE -> {
                                b.onboardRegistrationActivityPhoneVerifyPB.visibility = View.GONE
                                b.onboardRegistrationActivityPhoneVerifyButton.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }
            })
        }

    }


    private fun validateNames(nameEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        if (nameEditText.text.isEmpty()) {
            warningText.visibility = View.VISIBLE
            nameEditText.background = ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
            isValid = false
        } else {
            warningText.visibility = View.GONE
            nameEditText.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_background)
        }
        return isValid
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
                b.onboardRegistrationActivityEmailBox.background =
                    ContextCompat.getDrawable(this, R.drawable.edit_text_background)
            }
        }
        return isValid
    }

    private fun showEmailWarning(warning: String) {
        b.onboardRegistrationActivityEmailWarningTV.visibility = View.VISIBLE
        b.onboardRegistrationActivityEmailWarningTV.text = warning
        b.onboardRegistrationActivityEmailBox.background =
            ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
    }

    private fun validatePhone(phoneEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        when {
            phoneEditText.text!!.isEmpty() -> {
                showPhoneWarning(getString(R.string.required_field))
                isValid = false
            }

            phoneEditText.text.toString().replace(" ", "").length < 9 -> {
                showPhoneWarning(getString(R.string.invalid_phone))
                isValid = false
            }

            else -> {
                warningText.visibility = View.GONE
                b.onboardRegistrationActivityPhoneBox.background =
                    ContextCompat.getDrawable(this, R.drawable.edit_text_background)
            }
        }
        return isValid
    }

    private fun showPhoneWarning(warning: String) {
        b.onboardRegistrationActivityPhoneWarningTV.visibility = View.VISIBLE
        b.onboardRegistrationActivityPhoneWarningTV.text = warning
        b.onboardRegistrationActivityPhoneBox.background =
            ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
    }

    private fun validateSecurityQuestions(): Boolean {
        var isValid = true
        var numSecurityQuestions = 0

        if (b.onboardRegistrationActivitySecurityQuestion1ET.text.isNotEmpty()) numSecurityQuestions++
        if (b.onboardRegistrationActivitySecurityQuestion2ET.text.isNotEmpty()) numSecurityQuestions++
        if (b.onboardRegistrationActivitySecurityQuestion3ET.text.isNotEmpty()) numSecurityQuestions++
        if (b.onboardRegistrationActivitySecurityQuestion4ET.text.isNotEmpty()) numSecurityQuestions++

        if (numSecurityQuestions < 3) {
            isValid = false
            b.onboardRegistrationActivitySecurityQuestionTV.visibility = View.VISIBLE
            styleSecurityQuestionET(b.onboardRegistrationActivitySecurityQuestion1ET)
            styleSecurityQuestionET(b.onboardRegistrationActivitySecurityQuestion2ET)
            styleSecurityQuestionET(b.onboardRegistrationActivitySecurityQuestion3ET)
            styleSecurityQuestionET(b.onboardRegistrationActivitySecurityQuestion4ET)
        } else {
            b.onboardRegistrationActivitySecurityQuestionTV.visibility = View.GONE
            b.onboardRegistrationActivitySecurityQuestion1ET.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_background)
            b.onboardRegistrationActivitySecurityQuestion2ET.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_background)
            b.onboardRegistrationActivitySecurityQuestion3ET.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_background)
            b.onboardRegistrationActivitySecurityQuestion4ET.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_background)
        }
        return isValid
    }

    private fun styleSecurityQuestionET(securityQuestionEditText: EditText) {
        if (securityQuestionEditText.text.isEmpty()) {
            securityQuestionEditText.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
        } else {
            securityQuestionEditText.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_background)
        }
    }

    private fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun retrieveSecurityQuestionsApi() {
        val securityQuestionsCall = ApiClient.apiService.getSecurityQuestions()
        securityQuestionsCall.enqueue(object : Callback<SecurityQuestionsResponse> {
            override fun onResponse(
                call: Call<SecurityQuestionsResponse>,
                response: Response<SecurityQuestionsResponse>,
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    populateSecurityQuestions(body)
                } else {
                    val errorResponse: SecurityQuestionsResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<SecurityQuestionsResponse>() {}.type
                    )
                    Toast.makeText(
                        this@RegisterActivity, errorResponse.message, Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<SecurityQuestionsResponse>, t: Throwable) {
                Toast.makeText(
                    this@RegisterActivity,
                    R.string.default_error_toast,
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun populateSecurityQuestions(body: SecurityQuestionsResponse) {
        val questions = body.data

        if (questions.size == 4) {
            b.onboardRegistrationActivitySecurityQuestion1TV.text = questions[0].question
            b.onboardRegistrationActivitySecurityQuestion1TV.setTag(
                R.string.security_question_id, questions[0].id
            )

            b.onboardRegistrationActivitySecurityQuestion2TV.text = questions[1].question
            b.onboardRegistrationActivitySecurityQuestion2TV.setTag(
                R.string.security_question_id, questions[1].id
            )

            b.onboardRegistrationActivitySecurityQuestion3TV.text = questions[2].question
            b.onboardRegistrationActivitySecurityQuestion3TV.setTag(
                R.string.security_question_id, questions[2].id
            )

            b.onboardRegistrationActivitySecurityQuestion4TV.text = questions[3].question
            b.onboardRegistrationActivitySecurityQuestion4TV.setTag(
                R.string.security_question_id, questions[3].id
            )

            b.onboardRegistrationActivityContentBox.visibility = View.VISIBLE
            b.onboardRegistrationActivityLoaderLottie.visibility = View.GONE

        } else {
            Toast.makeText(
                this@RegisterActivity,
                "Something wrong happened! Try again later.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun launchFilePicker() {
        fileResultLauncher.launch(
            arrayOf(
                "image/jpeg", "image/jpg", "image/png"
            )
        )
    }

    private suspend fun populateSelectedFile(uri: Uri) {
        if (isPicUploaded){
            b.onboardRegistrationActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete))
        }else{
            b.onboardRegistrationActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera))
        }
        profilePicUri = uri
        val stream = contentResolver.openInputStream(uri)
        if (stream != null) {
            withContext(Dispatchers.IO) {
                val bitmap = BitmapFactory.decodeStream(stream)
                val rotatedBitmap = rotateBitmapIfNeeded(this@RegisterActivity, bitmap, uri)
                val base64String = bitmapToBase64(rotatedBitmap)
                val imageByteArray = base64ToByteArray(base64String)
                val imageBitmap = byteArrayToBitmap(imageByteArray)
                if (stream.available() < (10 * 1024 * 1024)) {
                    runOnUiThread {
                        Glide.with(this@RegisterActivity).load(profilePicUri)
                            .placeholder(R.drawable.ic_no_image)
                            .into(b.onboardRegistrationActivityProfileIV)
                    }
                } else {
                    runOnUiThread { showToast("Can't upload file with size greater than 10MB") }
                }
                stream.close()
            }
        }
    }

    private fun base64ToByteArray(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // 80 is the quality percentage
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun rotateBitmapIfNeeded(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val ei: ExifInterface
        var orientation = ExifInterface.ORIENTATION_NORMAL
        try {
            inputStream?.let {
                ei = ExifInterface(it)
                orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }



    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private suspend fun amplifyUpload(uri: Uri): String {
        val stream = contentResolver.openInputStream(uri)

        if (stream != null) {
            val objectKey = "customer_profile/${UUID.randomUUID()}/${
                getFileNameFromUri(
                    this, uri
                )
            }"
            val upload = Amplify.Storage.uploadInputStream(objectKey, stream)
            try {
                val result = upload.result()
                return result.key
            } catch (error: StorageException) {
                //
            }
        }
        return ""
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(context: Context, uri: Any): String {
        if (uri is Uri) {
            val fileName: String
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.moveToFirst()
            fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                ?: "PM_${System.currentTimeMillis()}.jpg"
            cursor?.close()
            return fileName
        }
        return ""
    }

    override fun onEmailVerified(recordId: String) {
        isEmailVerified = true
        emailRecordId = recordId
        b.onboardRegistrationActivityEmailVerifyButton.visibility = View.GONE
        b.onboardRegistrationActivityEmailVerifiedTV.visibility = View.VISIBLE
    }

    override fun onPhoneVerified(recordId: String) {
        isPhoneVerified = true
        phoneRecordId = recordId
        b.onboardRegistrationActivityPhoneVerifyButton.visibility = View.GONE
        b.onboardRegistrationActivityPhoneVerifiedTV.visibility = View.VISIBLE
    }
}