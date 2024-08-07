package com.afrimax.paymaart.ui.kyc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.SaveBasicDetailsSelfKycRequest
import com.afrimax.paymaart.data.model.SelfKycDetailsResponse
import com.afrimax.paymaart.data.model.SendOtpForEditSelfKycRequest
import com.afrimax.paymaart.data.model.SendOtpForEditSelfKycResponse
import com.afrimax.paymaart.data.model.ViewUserData
import com.afrimax.paymaart.databinding.ActivityKycCustomerPersonalDetailsBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.EditKycVerificationSheet
import com.afrimax.paymaart.ui.utils.interfaces.KycYourPersonalDetailsInterface
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.countries
import com.afrimax.paymaart.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class KycCustomerPersonalDetailsActivity : BaseActivity(), KycYourPersonalDetailsInterface {
    private lateinit var b: ActivityKycCustomerPersonalDetailsBinding
    private lateinit var kycScope: String
    private lateinit var viewScope: String
    private var isEmailVerified = false
    private var isPhoneVerified = false
    private var emailRecordId = ""
    private var phoneRecordId = ""
    private var shouldKeepPhoneNumber = false
    private var isEmailUpdated = false
    private var isPhoneUpdated = false
    private lateinit var nextScreenResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileResultLauncher: ActivityResultLauncher<Array<String>>
    private var sendEmail = true
    private var profilePicUri: Uri? = null
    private var isPicUploaded: Boolean = false
    private var isProfilePicUpdated = false
    private lateinit var profilePicUrl: String
    private var publicProfile: Boolean = false
    private var isPublicProfileUpdate: Boolean = false
    private val items = countries.map { it.dialCode }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycCustomerPersonalDetailsBinding.inflate(layoutInflater)
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
        setUpListeners()
    }

    private fun initViews() {
        kycScope = intent.getStringExtra(Constants.KYC_SCOPE) ?: ""
        viewScope = intent.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_EDIT
        profilePicUrl = intent.getStringExtra(Constants.PROFILE_PICTURE) ?: ""
        publicProfile = intent.getBooleanExtra(Constants.PUBLIC_PROFILE, false)
        val adapter = ArrayAdapter(this, R.layout.spinner_country_code, items)
        setSpinnerDropdownHeight(b.kycYourPersonalDetailsActivityCountryCodeSpinner, 800, 150)
        b.kycYourPersonalDetailsActivityCountryCodeSpinner.adapter = adapter

        nextScreenResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if ((result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) && data != null) {
                    kycScope = data.getStringExtra(Constants.KYC_SCOPE) ?: ""
                    viewScope = data.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_EDIT
                    sendEmail = data.getBooleanExtra(Constants.KYC_SEND_EMAIL, true)
                }
            }

        fileResultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                lifecycleScope.launch {
                    if (uri != null) {
                        isPicUploaded = true
                        isProfilePicUpdated = true
                        populateSelectedFile(uri)
                    }
                }
            }
    }

    private fun setSpinnerDropdownHeight(spinner: AppCompatSpinner, height: Int, verticalOffset: Int) {
        try {
            val popup = AppCompatSpinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup.get(spinner) as ListPopupWindow
            popupWindow.height = height
            popupWindow.verticalOffset = verticalOffset
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpLayout() {
        b.kycYourPersonalDetailsActivityFirstNameET.background =
            ContextCompat.getDrawable(this, R.color.defaultSelected)
        b.kycYourPersonalDetailsActivityFirstNameET.isEnabled = false

        b.kycYourPersonalDetailsActivityMiddleNameET.background =
            ContextCompat.getDrawable(this, R.color.defaultSelected)
        b.kycYourPersonalDetailsActivityMiddleNameET.isEnabled = false

        b.kycYourPersonalDetailsActivityLastNameET.background =
            ContextCompat.getDrawable(this, R.color.defaultSelected)
        b.kycYourPersonalDetailsActivityLastNameET.isEnabled = false

        when (kycScope) {
            Constants.KYC_MALAWI_FULL -> {
                b.kycYourPersonalDetailsActivityTitleTV.text = getString(R.string.full_kyc_reg)
            }

            Constants.KYC_MALAWI_SIMPLIFIED -> {
                b.kycYourPersonalDetailsActivityTitleTV.text = getString(R.string.simplified_kyc)
            }

            Constants.KYC_NON_MALAWI -> {
                b.kycYourPersonalDetailsActivityTitleTV.text = getString(R.string.non_malawi_full_kyc)
            }
        }

        when (viewScope) {
            Constants.VIEW_SCOPE_EDIT -> {
                //
            }

            Constants.VIEW_SCOPE_UPDATE -> {
                //Hide skip button
                b.kycYourPersonalDetailsActivitySkipButton.visibility = View.GONE
            }
        }

        if (profilePicUrl.isNotEmpty()) {
            isPicUploaded = true
            val picUrl = BuildConfig.CDN_BASE_URL + profilePicUrl
            Glide
                .with(this)
                .load(picUrl)
                .placeholder(R.drawable.ic_no_image)
                .into(b.kycYourPersonalDetailsActivityProfileIV)
            b.kycYourPersonalDetailsActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete))
        }
        if (publicProfile)
            b.kycYourPersonalDetailsActivityMakeVisibleCB.isChecked = true
    }

    private fun setUpListeners() {

        b.kycYourPersonalDetailsActivityCountryCodeTV.setOnClickListener {
            b.kycYourPersonalDetailsActivityCountryCodeSpinner.performClick()
        }

        b.kycYourPersonalDetailsActivityCountryCodeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?, position: Int, p3: Long
                ) {
                    if (b.kycYourPersonalDetailsActivityPhoneET.text.toString()
                            .isNotEmpty() && !shouldKeepPhoneNumber
                    ) {
                        b.kycYourPersonalDetailsActivityPhoneET.text!!.clear()

                        isPhoneVerified = false
                        b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility =
                            View.VISIBLE
                        b.kycYourPersonalDetailsActivityPhoneVerifiedTV.visibility = View.GONE
                    }
                    shouldKeepPhoneNumber = false
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //
                }
            }

        b.kycYourPersonalDetailsActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycYourPersonalDetailsActivityEmailVerifyButton.setOnClickListener {
            if (validateEmail(
                    b.kycYourPersonalDetailsActivityEmailET,
                    b.kycYourPersonalDetailsActivityEmailWarningTV
                )
            ) {
                sentOtpForEditSelfKycApi(Constants.OTP_EMAIL_TYPE)
            }
        }

        b.kycYourPersonalDetailsActivityPhoneVerifyButton.setOnClickListener {
            if (validatePhone(
                    b.kycYourPersonalDetailsActivityPhoneET,
                    b.kycYourPersonalDetailsActivityPhoneWarningTV
                )
            ) {
                sentOtpForEditSelfKycApi(Constants.OTP_SMS_TYPE)
            }
        }

        b.kycYourPersonalDetailsActivityPhoneET.setOnClickListener { _ ->
            b.kycYourPersonalDetailsActivityPhoneET.setSelection(b.kycYourPersonalDetailsActivityPhoneET.text!!.length)
        }

        b.kycYourPersonalDetailsActivitySkipButton.setOnClickListener {
            val i = Intent(this, KycAddressActivity::class.java)
            i.putExtra(Constants.KYC_SCOPE, kycScope)
            //The view scope is to edit the exist data
            i.putExtra(Constants.VIEW_SCOPE, viewScope)
            i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
            nextScreenResultLauncher.launch(i)
        }

        b.kycYourPersonalDetailsActivitySaveAndContinueButton.setOnClickListener {
            if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
                //User can't edit personal details if they are updating to full kyc, so save and continue work as skip
                val i = Intent(
                    this@KycCustomerPersonalDetailsActivity, KycAddressActivity::class.java
                )
                i.putExtra(Constants.KYC_SCOPE, kycScope)
                i.putExtra(Constants.VIEW_SCOPE, viewScope)
                i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                nextScreenResultLauncher.launch(i)
            } else {
                validateFieldsForSubmit()
            }
        }
        b.kycYourPersonalDetailsActivityProfileIV.setOnClickListener {
            if (!isPicUploaded){
                launchFilePicker()
            }
        }
        b.kycYourPersonalDetailsActivityCameraIV.setOnClickListener {
            if (isPicUploaded){
                populateProfilePicture()
            }else{
                launchFilePicker()
            }
        }
        b.kycYourPersonalDetailsActivityMakeVisibleCB.setOnClickListener{
            isPublicProfileUpdate = true
        }
        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
        setUpLastNameEditTextFilters(b.kycYourPersonalDetailsActivityLastNameET)
        setUpEmailEditTextFilters(b.kycYourPersonalDetailsActivityEmailET)
    }

    private fun populateProfilePicture() {
        isPicUploaded = false
        profilePicUri = null
        isProfilePicUpdated = true //ProfilePic is update even when they are removed.
        b.kycYourPersonalDetailsActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera))
        b.kycYourPersonalDetailsActivityProfileIV.apply {
            setImageDrawable(ContextCompat.getDrawable(this@KycCustomerPersonalDetailsActivity, R.drawable.ic_no_image))
            background = ContextCompat.getDrawable(this@KycCustomerPersonalDetailsActivity, R.drawable.dashed_outline_background)
        }
    }


    private fun setUpLastNameEditTextFilters(et: EditText) {
        et.filters += InputFilter.AllCaps()
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

    private fun setupEditTextFocusListeners() {
        configureEmailEditTextFocusListener(
            b.kycYourPersonalDetailsActivityEmailET, b.kycYourPersonalDetailsActivityEmailWarningTV
        )
        configurePhoneEditTextFocusListener(
            b.kycYourPersonalDetailsActivityPhoneET, b.kycYourPersonalDetailsActivityPhoneWarningTV
        )
    }

    private fun configureEmailEditTextFocusListener(
        et: EditText, warningTV: TextView
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.kycYourPersonalDetailsActivityEmailBox.background = focusDrawable
            else if (warningTV.isVisible) b.kycYourPersonalDetailsActivityEmailBox.background =
                errorDrawable
            else b.kycYourPersonalDetailsActivityEmailBox.background = notInFocusDrawable
        }
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextEmailChangeListener()
        configureEditTextPhoneChangeListener()
    }

    private fun configureEditTextEmailChangeListener() {
        b.kycYourPersonalDetailsActivityEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //If they change email they have to verify it again
                isEmailVerified = false

                if (b.kycYourPersonalDetailsActivityEmailVerifiedTV.visibility == View.VISIBLE) {
                    b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility = View.VISIBLE
                    b.kycYourPersonalDetailsActivityEmailVerifiedTV.visibility = View.GONE
                }

                //Remove any warning messages
                if (b.kycYourPersonalDetailsActivityEmailET.text.isEmpty()) {
                    b.kycYourPersonalDetailsActivityEmailWarningTV.visibility = View.VISIBLE
                    b.kycYourPersonalDetailsActivityEmailBox.background = ContextCompat.getDrawable(
                        this@KycCustomerPersonalDetailsActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    b.kycYourPersonalDetailsActivityEmailWarningTV.visibility = View.GONE
                    b.kycYourPersonalDetailsActivityEmailBox.background = ContextCompat.getDrawable(
                        this@KycCustomerPersonalDetailsActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun configureEditTextPhoneChangeListener() {
        b.kycYourPersonalDetailsActivityPhoneET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //If they change phone they have to verify it again
                isPhoneVerified = false

                if (b.kycYourPersonalDetailsActivityPhoneVerifiedTV.visibility == View.VISIBLE) {
                    b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility = View.VISIBLE
                    b.kycYourPersonalDetailsActivityPhoneVerifiedTV.visibility = View.GONE
                }

                //Remove any warning text
                if (b.kycYourPersonalDetailsActivityPhoneET.text.toString().isEmpty()) {
                    b.kycYourPersonalDetailsActivityPhoneWarningTV.visibility = View.VISIBLE
                    b.kycYourPersonalDetailsActivityPhoneWarningTV.text =
                        getString(R.string.required_field)
                    b.kycYourPersonalDetailsActivityPhoneBox.background = ContextCompat.getDrawable(
                        this@KycCustomerPersonalDetailsActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    b.kycYourPersonalDetailsActivityPhoneWarningTV.visibility = View.GONE
                    b.kycYourPersonalDetailsActivityPhoneBox.background = ContextCompat.getDrawable(
                        this@KycCustomerPersonalDetailsActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun configurePhoneEditTextFocusListener(
        et: EditText, warningTV: TextView
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.kycYourPersonalDetailsActivityPhoneBox.background = focusDrawable
            else if (warningTV.isVisible) b.kycYourPersonalDetailsActivityPhoneBox.background =
                errorDrawable
            else b.kycYourPersonalDetailsActivityPhoneBox.background = notInFocusDrawable
        }
    }

    private fun validateFieldsForSubmit() {
        var isValid = true
        var focusView: View? = null

        if (!validateEmail(
                b.kycYourPersonalDetailsActivityEmailET,
                b.kycYourPersonalDetailsActivityEmailWarningTV
            )
        ) {
            //Not valid email
            isValid = false
            focusView = b.kycYourPersonalDetailsActivityEmailET
        } else {
            //Entered valid email
            if (!isEmailVerified) {
                //Email not verified
                isValid = false
                focusView = b.kycYourPersonalDetailsActivityEmailET
                b.kycYourPersonalDetailsActivityEmailWarningTV.visibility = View.VISIBLE
                b.kycYourPersonalDetailsActivityEmailWarningTV.text =
                    ContextCompat.getString(this, R.string.please_verify_email)
            } else {
                //Email verified
                b.kycYourPersonalDetailsActivityEmailWarningTV.visibility = View.GONE
            }
        }

        if (!validatePhone(
                b.kycYourPersonalDetailsActivityPhoneET,
                b.kycYourPersonalDetailsActivityPhoneWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.kycYourPersonalDetailsActivityPhoneET
        } else {
            if (!isPhoneVerified) {
                isValid = false
                if (focusView == null) focusView = b.kycYourPersonalDetailsActivityPhoneET
                b.kycYourPersonalDetailsActivityPhoneWarningTV.visibility = View.VISIBLE
                b.kycYourPersonalDetailsActivityPhoneWarningTV.text =
                    ContextCompat.getString(this, R.string.please_verify_phone)
            } else {
                b.kycYourPersonalDetailsActivityPhoneWarningTV.visibility = View.GONE
            }
        }

        if (isValid) {
            if (isEmailUpdated || isPhoneUpdated || isProfilePicUpdated || isPublicProfileUpdate) {
                //Only call API if anything is changed
                saveBasicDetailsApi()
            } else {
                val i = Intent(
                    this@KycCustomerPersonalDetailsActivity, KycAddressActivity::class.java
                )
                i.putExtra(Constants.KYC_SCOPE, kycScope)
                //the view scope is to edit the exist data
                i.putExtra(Constants.VIEW_SCOPE, viewScope)
                i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                nextScreenResultLauncher.launch(i)
            }
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }

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
                b.kycYourPersonalDetailsActivityEmailBox.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            }
        }
        return isValid
    }

    private fun showEmailWarning(warning: String) {
        b.kycYourPersonalDetailsActivityEmailWarningTV.visibility = View.VISIBLE
        b.kycYourPersonalDetailsActivityEmailWarningTV.text = warning
        b.kycYourPersonalDetailsActivityEmailBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

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
                b.kycYourPersonalDetailsActivityPhoneBox.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            }
        }
        return isValid
    }

    private fun showPhoneWarning(warning: String) {
        b.kycYourPersonalDetailsActivityPhoneWarningTV.visibility = View.VISIBLE
        b.kycYourPersonalDetailsActivityPhoneWarningTV.text = warning
        b.kycYourPersonalDetailsActivityPhoneBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun getCountryCode(): String {
        return if (BuildConfig.STAGE == Constants.STAGE_DEV || BuildConfig.STAGE == Constants.STAGE_QA) {
            b.kycYourPersonalDetailsActivityCountryCodeSpinner.selectedItem.toString()
        } else {
            "+265"
        }
    }

    private fun sentOtpForEditSelfKycApi(type: String) {
        val firstName =
            b.kycYourPersonalDetailsActivityFirstNameET.text.toString().ifEmpty { "" }
        val middleName =
            b.kycYourPersonalDetailsActivityMiddleNameET.text.toString().ifEmpty { "" }
        val lastName = b.kycYourPersonalDetailsActivityLastNameET.text.toString().ifEmpty { "" }
        var value = ""
        var countryCode = ""
        when (type) {
            Constants.OTP_EMAIL_TYPE -> {
                value = b.kycYourPersonalDetailsActivityEmailET.text.toString()
                b.kycYourPersonalDetailsActivityEmailVerifyPB.visibility = View.VISIBLE
                b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility = View.GONE
            }

            Constants.OTP_SMS_TYPE -> {
                value = b.kycYourPersonalDetailsActivityPhoneET.text.toString()
                b.kycYourPersonalDetailsActivityPhoneVerifyPB.visibility = View.VISIBLE
                b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility = View.GONE
                countryCode = getCountryCode()
            }
        }

        lifecycleScope.launch {
            val idToken = fetchIdToken()
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
                        val verificationSheet = EditKycVerificationSheet()
                        verificationSheet.isCancelable = false
                        val bundle = Bundle()
                        when (type) {
                            Constants.OTP_EMAIL_TYPE -> {
                                bundle.putString(Constants.TYPE, Constants.OTP_EMAIL_TYPE)
                                bundle.putString(
                                    Constants.OTP_VALUE, value
                                )
                                b.kycYourPersonalDetailsActivityEmailVerifyPB.visibility = View.GONE
                                b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility =
                                    View.VISIBLE
                            }

                            Constants.OTP_SMS_TYPE -> {
                                bundle.putString(Constants.TYPE, Constants.OTP_SMS_TYPE)
                                bundle.putString(Constants.OTP_VALUE, value)
                                bundle.putString(Constants.OTP_COUNTRY_CODE, countryCode)
                                b.kycYourPersonalDetailsActivityPhoneVerifyPB.visibility = View.GONE
                                b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility =
                                    View.VISIBLE
                            }
                        }
                        bundle.putString(Constants.OTP_FIRST_NAME, firstName)
                        bundle.putString(Constants.OTP_MIDDLE_NAME, middleName)
                        bundle.putString(Constants.OTP_LAST_NAME, lastName)
                        bundle.putString(Constants.OTP_TOKEN, body.token)
                        bundle.putString(Constants.OTP_SECURITY_QUESTION, body.question)
                        bundle.putString(Constants.OTP_QUESTION_ID, body.question_id)
                        verificationSheet.arguments = bundle
                        verificationSheet.show(
                            supportFragmentManager, EditKycVerificationSheet.TAG
                        )

                    } else {
                        val errorResponse: DefaultResponse = Gson().fromJson(
                            response.errorBody()!!.string(),
                            object : TypeToken<DefaultResponse>() {}.type
                        )
                        when (type) {
                            Constants.OTP_EMAIL_TYPE -> {
                                showEmailWarning(errorResponse.message)
                                b.kycYourPersonalDetailsActivityEmailVerifyPB.visibility = View.GONE
                                b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility =
                                    View.VISIBLE
                            }

                            Constants.OTP_SMS_TYPE -> {
                                showPhoneWarning(errorResponse.message)
                                b.kycYourPersonalDetailsActivityPhoneVerifyPB.visibility = View.GONE
                                b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SendOtpForEditSelfKycResponse>, t: Throwable) {
                    showToast(getString(R.string.default_error_toast))
                    when (type) {
                        Constants.OTP_EMAIL_TYPE -> {
                            b.kycYourPersonalDetailsActivityEmailVerifyPB.visibility = View.GONE
                            b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility =
                                View.VISIBLE
                        }

                        Constants.OTP_SMS_TYPE -> {
                            b.kycYourPersonalDetailsActivityPhoneVerifyPB.visibility = View.GONE
                            b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility =
                                View.VISIBLE
                        }
                    }
                }
            })
        }

    }

    private fun saveBasicDetailsApi() {
        showButtonLoader(
            b.kycYourPersonalDetailsActivitySaveAndContinueButton,
            b.kycYourPersonalDetailsActivitySaveAndContinueButtonLoaderLottie
        )

        val email = b.kycYourPersonalDetailsActivityEmailET.text.toString()
        val phone = b.kycYourPersonalDetailsActivityPhoneET.text.toString().replace(" ", "")
        val countryCode = getCountryCode()

        lifecycleScope.launch {
            val picUrl = if (profilePicUri != null) amplifyUpload(profilePicUri!!) else ""
            "Response".showLogE(picUrl)
            val idToken = fetchIdToken()
            val saveBasicDetailsCall = ApiClient.apiService.saveBasicDetailsSelfKyc(
                idToken, SaveBasicDetailsSelfKycRequest(
                    email = if (isEmailUpdated) email else null,
                    phone_number = if (isPhoneUpdated) phone else null,
                    country_code = if (isPhoneUpdated) countryCode else null,
                    email_id = if (isEmailUpdated) emailRecordId else null,
                    phone_number_id = if (isPhoneUpdated) phoneRecordId else null,
                    profile_pic = if (isProfilePicUpdated) picUrl else null,
                    public_profile = if (isPublicProfileUpdate) picUrl.isNotEmpty() || profilePicUrl.isNotEmpty() && b.kycYourPersonalDetailsActivityMakeVisibleCB.isChecked else null
                )
            )

            saveBasicDetailsCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    hideButtonLoader(
                        b.kycYourPersonalDetailsActivitySaveAndContinueButton,
                        b.kycYourPersonalDetailsActivitySaveAndContinueButtonLoaderLottie,
                        getString(R.string.save_and_continue)
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        val i = Intent(
                            this@KycCustomerPersonalDetailsActivity, KycAddressActivity::class.java
                        )
                        i.putExtra(Constants.KYC_SCOPE, kycScope)
                        //the view scope is to edit the exist data
                        i.putExtra(Constants.VIEW_SCOPE, viewScope)
                        i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                        nextScreenResultLauncher.launch(i)
                    } else {
                        showToast(getString(R.string.default_error_toast))
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.kycYourPersonalDetailsActivitySaveAndContinueButton,
                        b.kycYourPersonalDetailsActivitySaveAndContinueButtonLoaderLottie,
                        getString(R.string.save_and_continue)
                    )
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        getLatestUserDataApi()
    }

    private fun getLatestUserDataApi() {

        //Hide main UI
        b.kycYourPersonalDetailsActivityLoaderLottie.visibility = View.VISIBLE
        b.kycYourPersonalDetailsActivityContentBox.visibility =
            View.INVISIBLE //use INVISIBLE instead of GONE, otherwise spinner listener will be called when making VISIBLE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val getUserKycDataCall = ApiClient.apiService.getSelfKycUserData("Bearer $idToken")

            getUserKycDataCall.enqueue(object : Callback<SelfKycDetailsResponse> {
                override fun onResponse(
                    call: Call<SelfKycDetailsResponse>, response: Response<SelfKycDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populatePersonalFields(body.data)
                    } else {
                        runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                    }
                }

                override fun onFailure(call: Call<SelfKycDetailsResponse>, t: Throwable) {
                    runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                }
            })
        }

    }

    private fun populatePersonalFields(data: ViewUserData) {

        b.kycYourPersonalDetailsActivityFirstNameET.setText(data.first_name)
        b.kycYourPersonalDetailsActivityMiddleNameET.setText(data.middle_name)
        b.kycYourPersonalDetailsActivityLastNameET.setText(data.last_name)

        b.kycYourPersonalDetailsActivityEmailET.setText(data.email)
        b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility = View.GONE
        b.kycYourPersonalDetailsActivityEmailVerifiedTV.visibility = View.VISIBLE
        isEmailVerified = true

        //Set country code
        if ((BuildConfig.STAGE == Constants.STAGE_DEV || BuildConfig.STAGE == Constants.STAGE_QA) && data.country_code == "+91") {
            //We don't want to clear the phone number while changing country code from spinner, so make it true
            shouldKeepPhoneNumber = true

            b.kycYourPersonalDetailsActivityCountryCodeSpinner.setSelection(1)
            b.kycYourPersonalDetailsActivityPhoneET.filters =
                arrayOf(InputFilter.LengthFilter(12))
        }

        b.kycYourPersonalDetailsActivityPhoneET.setText(data.phone_number)
        b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility = View.GONE
        b.kycYourPersonalDetailsActivityPhoneVerifiedTV.visibility = View.VISIBLE
        isPhoneVerified = true

        //Hide all warnings
        b.kycYourPersonalDetailsActivityEmailWarningTV.visibility = View.GONE
        b.kycYourPersonalDetailsActivityPhoneWarningTV.visibility = View.GONE

        //Change backgrounds
        if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
            b.kycYourPersonalDetailsActivityEmailBox.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.kycYourPersonalDetailsActivityEmailET.isEnabled = false

            b.kycYourPersonalDetailsActivityPhoneBox.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.kycYourPersonalDetailsActivityPhoneET.isEnabled = false

            b.kycYourPersonalDetailsActivityCountryCodeSpinner.isEnabled = false
            b.kycYourPersonalDetailsActivityCountryCodeSpinner.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
        } else {
            b.kycYourPersonalDetailsActivityEmailBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.kycYourPersonalDetailsActivityPhoneBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }


        //Show main UI
        b.kycYourPersonalDetailsActivityLoaderLottie.visibility = View.GONE
        b.kycYourPersonalDetailsActivityContentBox.visibility = View.VISIBLE

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

    private fun launchFilePicker() {
        fileResultLauncher.launch(
            arrayOf(
                "image/jpeg", "image/jpg", "image/png"
            )
        )
    }

    private suspend fun populateSelectedFile(uri: Uri?) {
        if (isPicUploaded){
            b.kycYourPersonalDetailsActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete))
        }else{
            b.kycYourPersonalDetailsActivityCameraIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera))
        }
        if (uri != null) {
            profilePicUri = uri
            val stream = contentResolver.openInputStream(uri)
            if (stream != null) {
                withContext(Dispatchers.IO) {
                    if (stream.available() < (10 * 1024 * 1024)) {
                        runOnUiThread {
                            Glide.with(this@KycCustomerPersonalDetailsActivity).load(uri)
                                .placeholder(R.drawable.ic_no_image)
                                .into(b.kycYourPersonalDetailsActivityProfileIV)
                        }
                    } else {
                        runOnUiThread { showToast("Can't upload file with size greater than 10MB") }
                    }
                    stream.close()
                }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private suspend fun amplifyUpload(uri: Uri): String {
        val paymaartId = retrievePaymaartId()
        val stream = contentResolver.openInputStream(uri)

        if (stream != null && paymaartId != null) {
            val objectKey = "customer_profile/${UUID.randomUUID()}/${
                getFileNameFromUri(
                    this, uri
                )
            }"

            val upload = Amplify.Storage.uploadInputStream(objectKey, stream)
            try {
                val result = upload.result()
                "Result".showLogE(result.key)
                return result.key
            } catch (error: StorageException) {
                //
                "Result".showLogE(error)
            }
        }
        stream?.close()
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
        b.kycYourPersonalDetailsActivityEmailVerifyButton.visibility = View.GONE
        b.kycYourPersonalDetailsActivityEmailVerifiedTV.visibility = View.VISIBLE
        isEmailUpdated = true
    }

    override fun onPhoneVerified(recordId: String) {
        isPhoneVerified = true
        phoneRecordId = recordId
        b.kycYourPersonalDetailsActivityPhoneVerifyButton.visibility = View.GONE
        b.kycYourPersonalDetailsActivityPhoneVerifiedTV.visibility = View.VISIBLE
        isPhoneUpdated = true
    }
}