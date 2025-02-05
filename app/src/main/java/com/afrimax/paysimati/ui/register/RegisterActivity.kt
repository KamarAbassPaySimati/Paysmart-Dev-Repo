package com.afrimax.paysimati.ui.register

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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.ui.text_field.verify_phone.VerifyPhoneField
import com.afrimax.paysimati.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.CreateUserRequestBody
import com.afrimax.paysimati.data.model.CreateUserResponse
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.SecurityQuestionAnswerModel
import com.afrimax.paysimati.data.model.SecurityQuestionsResponse
import com.afrimax.paysimati.data.model.SendOtpRequestBody
import com.afrimax.paysimati.data.model.SendOtpResponse
import com.afrimax.paysimati.databinding.ActivityRegisterBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.GuideBottomSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.VerificationBottomSheet
import com.afrimax.paysimati.ui.utils.interfaces.VerificationBottomSheetInterface
import com.afrimax.paysimati.ui.webview.WebViewActivity
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.RecaptchaManager
import com.afrimax.paysimati.util.countries
import com.afrimax.paysimati.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.recaptcha.RecaptchaAction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : BaseActivity(), VerificationBottomSheetInterface {
    private lateinit var b: ActivityRegisterBinding
    private lateinit var fileResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var guideSheet: BottomSheetDialogFragment
    private lateinit var verificationBottomSheet: BottomSheetDialogFragment
    private var isEmailVerified = false
    private var emailRecordId = ""
    private var phoneRecordId = ""
    private var profilePicUri: Uri? = null
    private var isPicUploaded: Boolean = false
    private var profilePicBaseString: String = ""
    private val items = countries.map { it.dialCode }


    @Inject
    lateinit var recaptchaManager: RecaptchaManager

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
        setUpPhoneField()
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
                termsAndConditionsIntent.putExtra(Constants.ANIMATE, true)
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
                val privacyPolicyIntent = Intent(this@RegisterActivity, WebViewActivity::class.java)
                privacyPolicyIntent.putExtra(Constants.TYPE, Constants.PRIVACY_POLICY_TYPE)
                privacyPolicyIntent.putExtra(Constants.ANIMATE, true)
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

    private fun setSpinnerDropdownHeight(
        spinner: AppCompatSpinner, height: Int, verticalOffset: Int
    ) {
        try {
            val popup = AppCompatSpinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup[spinner] as ListPopupWindow
            popupWindow.height = height
            popupWindow.verticalOffset = verticalOffset
        } catch (e: Exception) {
            e.printStackTrace()
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
            if (isPicUploaded) {
                profilePicUri = null
                isPicUploaded = false
                b.onboardRegistrationActivityCameraIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_camera
                    )
                )
                b.onboardRegistrationActivityProfileIV.apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RegisterActivity, R.drawable.ic_no_image
                        )
                    )
                    background = ContextCompat.getDrawable(
                        this@RegisterActivity, R.drawable.dashed_outline_background
                    )
                }
            } else {
                launchFilePicker()
            }
        }

        b.onboardRegistrationActivityEmailVerifyButton.setOnClickListener {
            validateFieldsForEmailOtp()
        }

        b.onboardRegistrationActivityPhoneTF.apply {
            setVerifyButtonClickListener { validateFieldsForPhoneOtp() }
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

    private fun setUpPhoneField() {
        b.onboardRegistrationActivityPhoneTF.apply {
            setCountryCodes(ArrayList<String>().apply {
                add("+1")
                add("+27")
                add("+39")
                add("+44")
                add("+46")
                add("+91")
                add("+234")
                add("+265")
            })
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

        if (!validatePhone(b.onboardRegistrationActivityPhoneTF)) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityPhoneTF
        } else {
            if (!b.onboardRegistrationActivityPhoneTF.isPhoneVerified) {
                isValid = false
                if (focusView == null) focusView = b.onboardRegistrationActivityPhoneTF
                b.onboardRegistrationActivityPhoneTF.showWarning(getString(R.string.please_verify_phone))
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
            showButtonLoader(
                b.onboardRegistrationActivitySubmitButton,
                b.onboardRegistrationActivitySubmitButtonLoaderLottie
            )
            lifecycleScope.launch {
                val recaptchaClient = recaptchaManager.getClient()
                if (recaptchaClient != null) {
                    recaptchaClient.execute(RecaptchaAction.SIGNUP).onSuccess { _ ->
                        registerCustomer()
                    }.onFailure { exception ->
                        "Response".showLogE(exception.message ?: "")
                        showToast(getString(R.string.default_error_toast))
                    }
                } else {
                    registerCustomer()
                }
            }
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun registerCustomer() {

        //If registering  a customer check for profile picture
//        val profilePic = if (profilePicUri != null) "$profilePicUri" else ""
        val profilePic = if (profilePicUri != null) profilePicBaseString else ""
        val makeVisible =
            profilePic.isNotEmpty() && b.onboardRegistrationActivityMakeVisibleCB.isChecked
        val firstName = b.onboardRegistrationActivityFirstNameET.text.toString()
        val middleName = b.onboardRegistrationActivityMiddleNameET.text.toString()
        val lastName = b.onboardRegistrationActivityLastNameET.text.toString()
        val countryCode = b.onboardRegistrationActivityPhoneTF.countryCode
        val phoneNumber = b.onboardRegistrationActivityPhoneTF.text.replace(" ", "")
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
                            this@RegisterActivity, RegistrationSuccessfulActivity::class.java
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
                            response.errorBody()!!.string(), DefaultResponse::class.java
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
        actionButton.text = getString(R.string.empty_string)
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
            lifecycleScope.launch {
                val recaptchaClient = recaptchaManager.getClient()
                if (recaptchaClient != null) {
                    recaptchaClient.execute(RecaptchaAction.SIGNUP).onSuccess { _ ->
                        sendOtp(Constants.OTP_EMAIL_TYPE)
                    }.onFailure { exception ->
                        "Response".showLogE(exception.message ?: "")
                        showToast(getString(R.string.default_error_toast))
                    }
                } else {
                    sendOtp(Constants.OTP_EMAIL_TYPE)
                }
            }
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun validateFieldsForPhoneOtp(): Job? {
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

        if (!validatePhone(b.onboardRegistrationActivityPhoneTF)) {
            isValid = false
            if (focusView == null) focusView = b.onboardRegistrationActivityPhoneTF
        }

        if (isValid) {
            return lifecycleScope.launch {
                val recaptchaClient = recaptchaManager.getClient()
                if (recaptchaClient != null) {
                    recaptchaClient.execute(RecaptchaAction.SIGNUP).onSuccess { _ ->
                        sendOtp(Constants.OTP_SMS_TYPE)
                    }.onFailure { exception ->
                        "Response".showLogE(exception.message ?: "")
                        showToast(getString(R.string.default_error_toast))
                    }
                } else {
                    sendOtp(Constants.OTP_SMS_TYPE)
                }
            }
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
        return null
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
                value = b.onboardRegistrationActivityPhoneTF.text
                countryCode = b.onboardRegistrationActivityPhoneTF.countryCode
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
                                }
                            }
                            bundle.putString(Constants.OTP_FIRST_NAME, firstName)
                            bundle.putString(Constants.OTP_MIDDLE_NAME, middleName)
                            bundle.putString(Constants.OTP_LAST_NAME, lastName)
                            bundle.putString(Constants.OTP_TOKEN, body.token)
                            verificationBottomSheet.arguments = bundle
                            verificationBottomSheet.show(
                                supportFragmentManager, VerificationBottomSheet.TAG
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
                                    b.onboardRegistrationActivityPhoneTF.showWarning(errorResponse.message)
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
                                //
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
            nameEditText.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text_error_background)
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

    private fun validatePhone(phoneEditText: VerifyPhoneField): Boolean {
        var isValid = true
        when {
            phoneEditText.text.isEmpty() -> {
                b.onboardRegistrationActivityPhoneTF.showWarning(getString(R.string.required_field))
                isValid = false
            }

            !PhoneNumberFormatter.isValidPhoneNumber(
                phoneEditText.countryCode, phoneEditText.text
            ) -> {
                b.onboardRegistrationActivityPhoneTF.showWarning(getString(R.string.invalid_phone))
                isValid = false
            }
        }
        return isValid
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
                    this@RegisterActivity, R.string.default_error_toast, Toast.LENGTH_LONG
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
        if (isPicUploaded) {
            b.onboardRegistrationActivityCameraIV.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_delete
                )
            )
        } else {
            b.onboardRegistrationActivityCameraIV.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_camera
                )
            )
        }
        profilePicUri = uri
        val stream = contentResolver.openInputStream(uri)
        if (stream != null) {
            withContext(Dispatchers.IO) {
                val bitmap = BitmapFactory.decodeStream(stream)
                val resizedAndCompressedImage = resizeAndCompressImage(bitmap)
                val rotatedBitmap =
                    rotateBitmapIfNeeded(this@RegisterActivity, resizedAndCompressedImage, uri)
                val base64String = bitmapToBase64(rotatedBitmap)
                profilePicBaseString = base64String
                val imageByteArray = base64ToByteArray(base64String)
                val imageBitmap = byteArrayToBitmap(imageByteArray)
                if (stream.available() < (10 * 1024 * 1024)) {
                    runOnUiThread {
                        Glide.with(this@RegisterActivity).load(imageBitmap)
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
        try {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream
            ) // 80 is the quality percentage
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP).trimEnd('=')
        } finally {
            try {
                byteArrayOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun resizeAndCompressImage(bitmap: Bitmap): Bitmap {
        val aspectRatioX = 3
        val aspectRatioY = 4
        val format = Bitmap.CompressFormat.JPEG
        val quality = 80
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val maxDimension = 800

        // Calculate the aspect ratio
        val desiredAspectRatio = aspectRatioX.toFloat() / aspectRatioY

        // Calculate new dimensions maintaining the aspect ratio
        val newWidth: Int
        val newHeight: Int

        if (originalWidth > originalHeight * desiredAspectRatio) {
            newWidth = (originalHeight * desiredAspectRatio).toInt()
            newHeight = originalHeight
        } else {
            newWidth = originalWidth
            newHeight = (originalWidth / desiredAspectRatio).toInt()
        }

        // Resize the dimensions to fit within maxDimension
        val scale = maxDimension.toFloat() / maxOf(newWidth, newHeight)
        val resizedWidth = (newWidth * scale).toInt()
        val resizedHeight = (newHeight * scale).toInt()

        // Crop the bitmap to the new dimensions
        val cropX = (originalWidth - newWidth) / 2
        val cropY = (originalHeight - newHeight) / 2
        val croppedBitmap = Bitmap.createBitmap(bitmap, cropX, cropY, newWidth, newHeight)

        // Resize the cropped bitmap to the new dimensions
        val resizedBitmap =
            Bitmap.createScaledBitmap(croppedBitmap, resizedWidth, resizedHeight, true)

        // Compress the resized bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            resizedBitmap.compress(format, quality, byteArrayOutputStream)
        } finally {
            try {
                byteArrayOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Convert the byte array back to a Bitmap
        val compressedByteArray = byteArrayOutputStream.toByteArray()
        return BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
    }


    private fun rotateBitmapIfNeeded(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val ei: ExifInterface
        var orientation = ExifInterface.ORIENTATION_NORMAL
        try {
            inputStream?.let {
                ei = ExifInterface(it)
                orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
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
        phoneRecordId = recordId
        b.onboardRegistrationActivityPhoneTF.isPhoneVerified = true
    }
}