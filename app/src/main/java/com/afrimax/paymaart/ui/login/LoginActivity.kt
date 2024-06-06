package com.afrimax.paymaart.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityLoginBinding
import com.afrimax.paymaart.ui.register.RegisterActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.LoginLoginByDialog
import com.afrimax.paymaart.ui.utils.interfaces.LoginByDialogInterface
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.LoginPinTransformation
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.cognito.options.AuthFlowType
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.core.Amplify

class LoginActivity : AppCompatActivity(), LoginByDialogInterface {

    private lateinit var b: ActivityLoginBinding

    private var isPinSelected = true
    private var isPasswordSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpListeners()
    }

    private fun initViews() {
        b.loginActivityPinET.transformationMethod = LoginPinTransformation()

        if (BuildConfig.STAGE == Constants.STAGE_DEV || BuildConfig.STAGE == Constants.STAGE_QA) {
            //This is required for Indian phone number testing
            b.loginActivityCountryCodeSpinner.visibility = View.VISIBLE
            b.loginActivityCountryCodeDropDownIV.visibility = View.VISIBLE
            b.loginActivityCountryCodeTV.visibility = View.GONE

            val items = arrayOf("+265", "+91")
            val adapter = ArrayAdapter(this, R.layout.spinner_country_code, items)
            b.loginActivityCountryCodeSpinner.adapter = adapter

            b.loginActivityCountryCodeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?, p1: View?, position: Int, p3: Long
                    ) {
                        when (position) {
                            0 -> b.loginActivityPhoneET.filters =
                                arrayOf(InputFilter.LengthFilter(11))

                            //Indian Phone numbers are 10 in size + 2 for space
                            1 -> b.loginActivityPhoneET.filters =
                                arrayOf(InputFilter.LengthFilter(12))
                        }
                        if (b.loginActivityPhoneET.text.toString()
                                .isNotEmpty()
                        ) b.loginActivityPhoneET.text!!.clear()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //
                    }
                }
        } else {
            b.loginActivityCountryCodeSpinner.visibility = View.GONE
            b.loginActivityCountryCodeDropDownIV.visibility = View.GONE
            b.loginActivityCountryCodeTV.visibility = View.VISIBLE
        }
    }

    private fun setUpListeners() {
        b.loginActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.loginActivityPinButton.setOnClickListener {
            onClickPinButton()
        }

        b.loginActivityPasswordButton.setOnClickListener {
            onClickPassWordButton()
        }

        b.loginActivityLoginByBox.setOnClickListener {
            val loginByDialog = LoginLoginByDialog()
            loginByDialog.show(supportFragmentManager, LoginLoginByDialog.TAG)
        }

        b.loginActivityLoginButton.setOnClickListener {
            validateFieldsForSubmit()
        }

        b.loginActivityRegisterNowTV.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        b.loginActivityPasswordToggleTV.setOnClickListener {
            val passwordTransformation = b.loginActivityPasswordET.transformationMethod
            if (passwordTransformation != null) {
                //Password is hidden make it visible
                b.loginActivityPasswordET.transformationMethod = null
                b.loginActivityPasswordToggleTV.text = getString(R.string.hide)
            } else {
                //Make it hidden
                b.loginActivityPasswordET.transformationMethod = PasswordTransformationMethod()
                b.loginActivityPasswordToggleTV.text = getString(R.string.show)
            }
            b.loginActivityPasswordET.setSelection(b.loginActivityPasswordET.length())
        }

        b.loginActivityForgotPinTV.setOnClickListener {
//            val i = Intent(this, ForgotPasswordPinActivity::class.java)
//            i.putExtra(Constants.FORGOT_CREDENTIAL_TYPE, Constants.FORGOT_CREDENTIAL_PIN)
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
//            startActivity(i, options)
        }

        b.loginActivityForgotPasswordTV.setOnClickListener {
//            val i = Intent(this, ForgotPasswordPinActivity::class.java)
//            i.putExtra(Constants.FORGOT_CREDENTIAL_TYPE, Constants.FORGOT_CREDENTIAL_PASSWORD)
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
//            startActivity(i, options)
        }

        setUpEditTextChangeListeners()
        setUpEditTextFocusListeners()
        setUpEmailEditTextFilters(b.loginActivityEmailET)

    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextPhoneChangeListener()
        configureEditTextEmailChangeListener()
        configureEditTextPaymaartIdChangeListener()
        configureEditTextPinChangeListener()
        configureEditTextPasswordChangeListener()
    }

    private fun configureEditTextPhoneChangeListener() {
        b.loginActivityPhoneET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.loginActivityPhoneET.text.toString().isEmpty()) {
                    b.loginActivityPhoneWarningTV.visibility = View.VISIBLE
                    b.loginActivityPhoneWarningTV.text = getString(R.string.required_field)
                    b.loginActivityPhoneBox.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    b.loginActivityPhoneWarningTV.visibility = View.GONE
                    b.loginActivityPhoneBox.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }

        })
    }

    private fun configureEditTextEmailChangeListener() {
        b.loginActivityEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.loginActivityEmailET.text.isEmpty()) {
                    b.loginActivityEmailWarningTV.visibility = View.VISIBLE
                    b.loginActivityEmailWarningTV.text = getString(R.string.required_field)
                    b.loginActivityEmailET.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    b.loginActivityEmailWarningTV.visibility = View.GONE
                    b.loginActivityEmailET.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }

        })
    }

    private fun configureEditTextPaymaartIdChangeListener() {
        b.loginActivityPaymaartIdET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.loginActivityPaymaartIdET.text.isEmpty()) {
                    b.loginActivityPaymaartIdWarningTV.visibility = View.VISIBLE
                    b.loginActivityPaymaartIdWarningTV.text = getString(R.string.required_field)
                    b.loginActivityPaymaartIdBox.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    b.loginActivityPaymaartIdWarningTV.visibility = View.GONE
                    b.loginActivityPaymaartIdBox.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }

        })
    }

    private fun configureEditTextPasswordChangeListener() {
        b.loginActivityPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.loginActivityPasswordET.text.isEmpty()) {
                    b.loginActivityPasswordWarningTV.visibility = View.VISIBLE
                    b.loginActivityPasswordWarningTV.text = getString(R.string.required_field)
                    b.loginActivityPasswordBox.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_error
                    )
                    b.loginActivityPasswordToggleTV.visibility = View.GONE
                } else {
                    b.loginActivityPasswordWarningTV.visibility = View.GONE
                    b.loginActivityPasswordBox.background = ContextCompat.getDrawable(
                        this@LoginActivity, R.drawable.bg_edit_text_focused
                    )
                    b.loginActivityPasswordToggleTV.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun configureEditTextPinChangeListener() {
        b.loginActivityPinET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(
                pinText: CharSequence?, index: Int, isBackSpace: Int, isNewDigit: Int
            ) {
                //Remove any error warning text while typing
                if (b.loginActivityPinET.text.toString().isEmpty()) {
                    b.loginActivityPinWarningTV.visibility = View.VISIBLE
                    b.loginActivityPinWarningTV.text = getString(R.string.required_field)
                } else {
                    b.loginActivityPinWarningTV.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                //
            }

        })
    }

    private fun setUpEditTextFocusListeners() {
        configurePhoneEditTextFocusListener()
        configureEmailEditTextFocusListener()
        configurePaymaartIdEditTextFocusListener()
        configurePasswordEditTextFocusListener()
    }

    private fun configurePhoneEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.loginActivityPhoneET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.loginActivityPhoneBox.background = focusDrawable
            else if (b.loginActivityPhoneWarningTV.isVisible) b.loginActivityPhoneBox.background =
                errorDrawable
            else b.loginActivityPhoneBox.background = notInFocusDrawable
        }
    }

    private fun configureEmailEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.loginActivityEmailET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) b.loginActivityEmailET.background = focusDrawable
            else if (b.loginActivityEmailWarningTV.isVisible) b.loginActivityEmailET.background =
                errorDrawable
            else b.loginActivityEmailET.background = notInFocusDrawable
        }
    }

    private fun configurePaymaartIdEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.loginActivityPaymaartIdET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.loginActivityPaymaartIdBox.background = focusDrawable
                else if (b.loginActivityPaymaartIdWarningTV.isVisible) b.loginActivityPaymaartIdBox.background =
                    errorDrawable
                else b.loginActivityPaymaartIdBox.background = notInFocusDrawable
            }
    }

    private fun configurePasswordEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.loginActivityPasswordET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.loginActivityPasswordBox.background = focusDrawable
                else if (b.loginActivityPasswordWarningTV.isVisible) b.loginActivityPasswordBox.background =
                    errorDrawable
                else b.loginActivityPasswordBox.background = notInFocusDrawable
            }
    }


    private fun onClickPassWordButton() {
        if (isPinSelected) {
            b.loginActivityPasswordButton.background = ContextCompat.getDrawable(
                this@LoginActivity, R.drawable.bg_login_password_toggle_button_selected
            )
            b.loginActivityPinButton.background = ContextCompat.getDrawable(
                this@LoginActivity, R.drawable.bg_login_pin_toggle_button_unselected
            )
            b.loginActivityPinButton.setTextColor(
                ContextCompat.getColor(
                    this@LoginActivity, R.color.neutralGrey
                )
            )
            b.loginActivityPasswordButton.setTextColor(
                ContextCompat.getColor(
                    this@LoginActivity, R.color.primaryColor
                )
            )
            isPinSelected = false
            isPasswordSelected = true

            //Show Password field
            b.loginActivityPasswordContainer.visibility = View.VISIBLE
            b.loginActivityPinContainer.visibility = View.GONE

            //Clear PIN EditText field if not empty
            clearAllFields()
        }
    }

    private fun onClickPinButton() {
        if (isPasswordSelected) {
            b.loginActivityPinButton.background = ContextCompat.getDrawable(
                this@LoginActivity, R.drawable.bg_login_pin_toggle_button_selected
            )
            b.loginActivityPasswordButton.background = ContextCompat.getDrawable(
                this@LoginActivity, R.drawable.bg_login_password_toggle_button_unselected
            )
            b.loginActivityPinButton.setTextColor(
                ContextCompat.getColor(
                    this@LoginActivity, R.color.primaryColor
                )
            )
            b.loginActivityPasswordButton.setTextColor(
                ContextCompat.getColor(
                    this@LoginActivity, R.color.neutralGrey
                )
            )
            isPasswordSelected = false
            isPinSelected = true

            //Show Pin field
            b.loginActivityPinContainer.visibility = View.VISIBLE
            b.loginActivityPasswordContainer.visibility = View.GONE

            //Clear password EditText field if not empty
            clearAllFields()
        }
    }

    private fun clearAllFields() {
        if (!b.loginActivityPinET.text.isNullOrBlank()) b.loginActivityPinET.text!!.clear()
        if (!b.loginActivityPasswordET.text.isNullOrBlank()) b.loginActivityPasswordET.text!!.clear()
        if (!b.loginActivityPhoneET.text.isNullOrBlank()) b.loginActivityPhoneET.text!!.clear()
        if (!b.loginActivityEmailET.text.isNullOrBlank()) b.loginActivityEmailET.text!!.clear()
        if (!b.loginActivityPaymaartIdET.text.isNullOrBlank()) b.loginActivityPaymaartIdET.text!!.clear()


        //Also clear all the warnings
        b.loginActivityPhoneWarningTV.visibility = View.GONE
        b.loginActivityEmailWarningTV.visibility = View.GONE
        b.loginActivityPaymaartIdWarningTV.visibility = View.GONE
        b.loginActivityPinWarningTV.visibility = View.GONE
        b.loginActivityPasswordWarningTV.visibility = View.GONE

        b.loginActivityPhoneBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.loginActivityEmailET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.loginActivityPaymaartIdBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.loginActivityPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
    }

    override fun onSelection(selectionId: String) {
        when (selectionId) {
            Constants.SELECTION_PHONE_NUMBER -> {
                b.loginActivityLoginByTV.text = getString(R.string.phone)
                b.loginActivityPhoneContainer.visibility = View.VISIBLE
                b.loginActivityEmailContainer.visibility = View.GONE
                b.loginActivityPaymaartIdContainer.visibility = View.GONE

                //Clear all other edit text fields
                clearAllFields()
            }

            Constants.SELECTION_EMAIL -> {
                b.loginActivityLoginByTV.text = getString(R.string.email)
                b.loginActivityPhoneContainer.visibility = View.GONE
                b.loginActivityEmailContainer.visibility = View.VISIBLE
                b.loginActivityPaymaartIdContainer.visibility = View.GONE

                //Clear all other edit text fields
                clearAllFields()
            }

            Constants.SELECTION_PAYMAART_ID -> {
                b.loginActivityLoginByTV.text = getString(R.string.paymaart_id)
                b.loginActivityPhoneContainer.visibility = View.GONE
                b.loginActivityEmailContainer.visibility = View.GONE
                b.loginActivityPaymaartIdContainer.visibility = View.VISIBLE

                //Clear all other edit text fields
                clearAllFields()
            }
        }
    }

    private fun validateFieldsForSubmit() {
        var loginByField: EditText? = null
        var passCodeField: EditText? = null

        val loginBy = b.loginActivityLoginByTV.text.toString()
        when (loginBy) {
            getString(R.string.phone) -> loginByField = b.loginActivityPhoneET
            getString(R.string.email) -> loginByField = b.loginActivityEmailET
            getString(R.string.paymaart_id) -> loginByField = b.loginActivityPaymaartIdET
        }

        if (isPinSelected) passCodeField = b.loginActivityPinET
        else if (isPasswordSelected) passCodeField = b.loginActivityPasswordET

        if (loginByField != null && passCodeField != null) {
            //There are 6 possible combinations
            when {
                (loginByField == b.loginActivityPhoneET && passCodeField == b.loginActivityPinET) -> validatePhoneAndPin()
                (loginByField == b.loginActivityEmailET && passCodeField == b.loginActivityPinET) -> validateEmailAndPin()
                (loginByField == b.loginActivityPaymaartIdET && passCodeField == b.loginActivityPinET) -> validatePaymaartIdAndPin()
                (loginByField == b.loginActivityPhoneET && passCodeField == b.loginActivityPasswordET) -> validatePhoneAndPassword()
                (loginByField == b.loginActivityEmailET && passCodeField == b.loginActivityPasswordET) -> validateEmailAndPassword()
                (loginByField == b.loginActivityPaymaartIdET && passCodeField == b.loginActivityPasswordET) -> validatePaymaartIdAndPassword()
            }
        }
    }

    private fun validatePaymaartId(paymaartIdEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        if (paymaartIdEditText.text.isEmpty()) {
            isValid = false
            showPaymaartIdWarning(getString(R.string.required_field))
        } else if (paymaartIdEditText.text.length != 8) {
            isValid = false
            showPaymaartIdWarning(getString(R.string.invalid_paymaart_id))
        } else {
            warningText.visibility = View.GONE
            b.loginActivityPaymaartIdBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }

        return isValid
    }

    private fun showPaymaartIdWarning(warning: String) {
        b.loginActivityPaymaartIdWarningTV.visibility = View.VISIBLE
        b.loginActivityPaymaartIdWarningTV.text = warning
        b.loginActivityPaymaartIdBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
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
                b.loginActivityPhoneBox.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            }
        }
        return isValid
    }

    private fun showPhoneWarning(warning: String) {
        b.loginActivityPhoneWarningTV.visibility = View.VISIBLE
        b.loginActivityPhoneWarningTV.text = warning
        b.loginActivityPhoneBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
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
                b.loginActivityEmailET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            }
        }
        return isValid
    }

    private fun showEmailWarning(warning: String) {
        b.loginActivityEmailWarningTV.visibility = View.VISIBLE
        b.loginActivityEmailWarningTV.text = warning
        b.loginActivityEmailET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun validatePassword(passwordEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        if (passwordEditText.text.isEmpty()) {
            isValid = false
            showPasswordWarning(getString(R.string.required_field))
        } else if (passwordEditText.text.length < 8 || passwordEditText.text.length > 12) {
            isValid = false
            showPasswordWarning(getString(R.string.invalid_password))
        } else {
            warningText.visibility = View.GONE
            b.loginActivityPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid

    }

    private fun showPasswordWarning(warning: String) {
        b.loginActivityPasswordWarningTV.visibility = View.VISIBLE
        b.loginActivityPasswordWarningTV.text = warning
        b.loginActivityPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
    }

    private fun validatePin(pinEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        val pin = pinEditText.text.toString()
        if (pin.isEmpty()) {
            isValid = false
            showPinWarning(getString(R.string.required_field))
        } else if (pin.length != 6) {
            isValid = false
            showPinWarning(getString(R.string.invalid_pin))
        } else {
            warningText.visibility = View.GONE
        }
        return isValid

    }

    private fun showPinWarning(warning: String) {
        b.loginActivityPinWarningTV.visibility = View.VISIBLE
        b.loginActivityPinWarningTV.text = warning
    }


    private fun validatePhoneAndPin() {
        val isValidPhone = validatePhone(b.loginActivityPhoneET, b.loginActivityPhoneWarningTV)
        val isValidPin = validatePin(b.loginActivityPinET, b.loginActivityPinWarningTV)

        if (isValidPhone && isValidPin) {
            val phone = getCountryCode() + b.loginActivityPhoneET.text.toString().replace(" ", "")
            val pin = b.loginActivityPinET.text.toString()
            amplifyStartLogIn(phone, pin, Constants.SELECTION_PHONE_NUMBER, Constants.SELECTION_PIN)
        }
    }

    private fun validateEmailAndPin() {
        val isValidEmail = validateEmail(b.loginActivityEmailET, b.loginActivityEmailWarningTV)
        val isValidPin = validatePin(b.loginActivityPinET, b.loginActivityPinWarningTV)

        if (isValidEmail && isValidPin) {
            val email = b.loginActivityEmailET.text.toString()
            val pin = b.loginActivityPinET.text.toString()
            amplifyStartLogIn(email, pin, Constants.SELECTION_EMAIL, Constants.SELECTION_PIN)
        }
    }

    private fun validatePaymaartIdAndPin() {
        val isValidPaymaartId =
            validatePaymaartId(b.loginActivityPaymaartIdET, b.loginActivityPaymaartIdWarningTV)
        val isValidPin = validatePin(b.loginActivityPinET, b.loginActivityPinWarningTV)

        if (isValidPaymaartId && isValidPin) {
            val paymaartId =
                getString(R.string.paymaart_code) + b.loginActivityPaymaartIdET.text.toString()
            val pin = b.loginActivityPinET.text.toString()
            amplifyStartLogIn(
                paymaartId, pin, Constants.SELECTION_PAYMAART_ID, Constants.SELECTION_PIN
            )

        }
    }

    private fun validatePhoneAndPassword() {
        val isValidPhone = validatePhone(b.loginActivityPhoneET, b.loginActivityPhoneWarningTV)
        val isValidPassword =
            validatePassword(b.loginActivityPasswordET, b.loginActivityPasswordWarningTV)

        if (isValidPhone && isValidPassword) {
            val phone = getCountryCode() + b.loginActivityPhoneET.text.toString().replace(" ", "")
            val password = b.loginActivityPasswordET.text.toString()
            amplifyStartLogIn(
                phone, password, Constants.SELECTION_PHONE_NUMBER, Constants.SELECTION_PASSWORD
            )
        }
    }

    private fun validateEmailAndPassword() {
        val isValidEmail = validateEmail(b.loginActivityEmailET, b.loginActivityEmailWarningTV)
        val isValidPassword =
            validatePassword(b.loginActivityPasswordET, b.loginActivityPasswordWarningTV)

        if (isValidEmail && isValidPassword) {
            val email = b.loginActivityEmailET.text.toString()
            val password = b.loginActivityPasswordET.text.toString()
            amplifyStartLogIn(
                email, password, Constants.SELECTION_EMAIL, Constants.SELECTION_PASSWORD
            )
        }
    }

    private fun validatePaymaartIdAndPassword() {
        val isValidPaymaartId =
            validatePaymaartId(b.loginActivityPaymaartIdET, b.loginActivityPaymaartIdWarningTV)
        val isValidPassword =
            validatePassword(b.loginActivityPasswordET, b.loginActivityPasswordWarningTV)

        if (isValidPaymaartId && isValidPassword) {
            val paymaartId = b.loginActivityPaymaartIdET.text.toString()
            val password = b.loginActivityPasswordET.text.toString()
            amplifyStartLogIn(
                paymaartId, password, Constants.SELECTION_PAYMAART_ID, Constants.SELECTION_PASSWORD
            )
        }
    }

    private fun amplifyStartLogIn(
        userName: String, passCode: String, loginBy: String, loginMode: String
    ) {
        showButtonLoader(b.loginActivityLoginButton, b.loginActivityLoginLoaderLottie)
        val i = Intent(this, TwoFactorAuthActivity::class.java)
        i.putExtra(Constants.LOGIN_MODE, loginMode)

        val options =
            AWSCognitoAuthSignInOptions.builder().authFlowType(AuthFlowType.USER_SRP_AUTH).build()

        try {
            Amplify.Auth.signIn(userName, passCode, options, { result ->
                runOnUiThread {
                    hideButtonLoader()
                }
                val nextStep = result.nextStep
                when (nextStep.signInStep) {
                    AuthSignInStep.CONFIRM_SIGN_IN_WITH_TOTP_CODE -> {
                        i.putExtra(
                            Constants.TWO_FACTOR_AUTH_TYPE,
                            Constants.TWO_FACTOR_AUTH_DIRECT_TOTP_TYPE
                        )
                        startActivity(i)
                        finish()
                    }

                    AuthSignInStep.CONTINUE_SIGN_IN_WITH_TOTP_SETUP -> {
                        val totpDetails = nextStep.totpSetupDetails
                        if (totpDetails != null) {
                            val sharedSecret = totpDetails.sharedSecret
                            i.putExtra(
                                Constants.TWO_FACTOR_AUTH_TYPE, Constants.TWO_FACTOR_AUTH_SETUP_TYPE
                            )
                            i.putExtra(Constants.TWO_FACTOR_AUTH_SHARED_SECRET, sharedSecret)
                            startActivity(i)
                            finish()
                        } else {
                            runOnUiThread {
                                showToast(getString(R.string.default_error_toast))
                            }
                        }
                    }

                    else -> runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                    }
                }

            }) { error ->
                runOnUiThread {
                    hideButtonLoader()
                    when (loginBy) {
                        Constants.SELECTION_PHONE_NUMBER -> b.loginActivityPhoneBox.background =
                            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)

                        Constants.SELECTION_EMAIL -> b.loginActivityEmailET.background =
                            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)

                        Constants.SELECTION_PAYMAART_ID -> b.loginActivityPaymaartIdBox.background =
                            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
                    }
                    val warning =
                        if (error.recoverySuggestion.contains("User locked")) getString(R.string.account_blocked)
                        else getString(R.string.invalid_credentials)

                    if (isPinSelected) showPinWarning(warning)
                    else showPasswordWarning(warning)
                }
            }
        } catch (error: Exception) {
            showToast(getString(R.string.default_error_toast))
            hideButtonLoader()
        }
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


    private fun getCountryCode(): String {
        return if (BuildConfig.STAGE == Constants.STAGE_DEV || BuildConfig.STAGE == Constants.STAGE_QA) {
            b.loginActivityCountryCodeSpinner.selectedItem.toString()
        } else {
            "+265"
        }
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

    private fun hideButtonLoader() {
        b.loginActivityLoginButton.text = getString(R.string.login)
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        b.loginActivityLoginLoaderLottie.visibility = View.GONE

    }

    private fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
}