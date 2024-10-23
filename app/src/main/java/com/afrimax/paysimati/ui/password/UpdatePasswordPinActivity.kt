package com.afrimax.paysimati.ui.password

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.UpdatePinOrPasswordRequest
import com.afrimax.paysimati.databinding.ActivityUpdatePasswordPinBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.login.LoginActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.PasswordGuideBottomSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.PinGuideBottomSheet
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.MaskPinTransformation
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.core.Amplify
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class UpdatePasswordPinActivity : BaseActivity() {
    private lateinit var b: ActivityUpdatePasswordPinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityUpdatePasswordPinBinding.inflate(layoutInflater)
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
        setUpListeners()
    }

    private fun initViews() {
        b.updatePasswordPinActivityNewPinET.transformationMethod = MaskPinTransformation()
        b.updatePasswordPinActivityConfirmPinET.transformationMethod = MaskPinTransformation()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAfterTransition()
            }
        })
    }

    private fun setUpListeners() {

        b.updatePasswordPinActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.updatePasswordPinActivityCurrentPasswordToggleTV.setOnClickListener {
            onClickCurrentPasswordToggle()
        }

        b.updatePasswordPinActivityNewPasswordToggleTV.setOnClickListener {
            onClickNewPasswordToggle()
        }

        b.updatePasswordPinActivityConfirmPasswordToggleTV.setOnClickListener {
            onClickConfirmPasswordToggle()
        }

        b.updatePasswordPinActivityPasswordGuideTV.setOnClickListener {
            PasswordGuideBottomSheet().show(supportFragmentManager, PasswordGuideBottomSheet.TAG)
        }

        b.updatePasswordPinActivityPinGuideTV.setOnClickListener {
            PinGuideBottomSheet().show(supportFragmentManager, PinGuideBottomSheet.TAG)
        }

        b.updatePasswordPinActivityUpdateTypeRG.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.updatePasswordPinActivityUpdateTypePinRB -> showUpdatePinView()
                R.id.updatePasswordPinActivityUpdateTypePasswordRB -> showUpdatePasswordView()
            }

        }

        b.updatePasswordPinActivityUpdateButton.setOnClickListener {
            onClickUpdate()
        }


        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
    }

    private fun onClickUpdate() {
        when {
            b.updatePasswordPinActivityPinContainer.isVisible -> {
                if (validatePin()) {
                    hideKeyboard(this)
                    updatePinOrPasswordApi(Constants.SELECTION_PIN)
                }
            }

            b.updatePasswordPinActivityPasswordContainer.isVisible -> {
                if (validatePassword()) {
                    hideKeyboard(this)
                    updatePinOrPasswordApi(Constants.SELECTION_PASSWORD)
                }
            }
        }
    }

    private fun updatePinOrPasswordApi(choosenType: String) {
        showButtonLoader(
            b.updatePasswordPinActivityUpdateButton,
            b.updatePasswordPinActivityUpdateButtonLoaderLottie
        )

        //encrypt and send
        val currentPinOrPassword =
            AESCrypt.encrypt(b.updatePasswordPinActivityCurrentPasswordET.text.toString())
        val newPassword =
            if (choosenType == Constants.SELECTION_PIN) AESCrypt.encrypt(b.updatePasswordPinActivityNewPinET.text.toString()) else AESCrypt.encrypt(
                b.updatePasswordPinActivityNewPasswordET.text.toString()
            )
        val confirmPassword =
            if (choosenType == Constants.SELECTION_PIN) AESCrypt.encrypt(b.updatePasswordPinActivityConfirmPinET.text.toString()) else AESCrypt.encrypt(
                b.updatePasswordPinActivityConfirmPasswordET.text.toString()
            )

        val type = if (choosenType == Constants.SELECTION_PIN) "pin" else "password"


        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val updatePinOrPasswordCall = ApiClient.apiService.updatePinOrPassword(
                idToken, UpdatePinOrPasswordRequest(
                    old_password = currentPinOrPassword,
                    new_password = newPassword,
                    confirm_password = confirmPassword,
                    type = type
                )
            )

            updatePinOrPasswordCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    hideButtonLoader(
                        b.updatePasswordPinActivityUpdateButton,
                        b.updatePasswordPinActivityUpdateButtonLoaderLottie,
                        getString(R.string.update)
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        Amplify.Auth.signOut {}
                        //Show pin update successful
                        showPinPasswordUpdateBottomSheet(choosenType)
                    } else {
                        val errorResponse: DefaultResponse = Gson().fromJson(
                            response.errorBody()!!.string(),
                            object : TypeToken<DefaultResponse>() {}.type
                        )
                        b.updatePasswordPinActivityCurrentPasswordWarningTV.text =
                            errorResponse.message
                        b.updatePasswordPinActivityCurrentPasswordWarningTV.visibility =
                            View.VISIBLE
                        b.updatePasswordPinActivityCurrentPasswordBox.background =
                            ContextCompat.getDrawable(
                                this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_error
                            )
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.updatePasswordPinActivityUpdateButton,
                        b.updatePasswordPinActivityUpdateButtonLoaderLottie,
                        getString(R.string.update)
                    )
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }

    }

    private fun setupEditTextFocusListeners() {
        configureCurrentPinPasswordEditTextFocusListener()
        configureNewPasswordEditTextFocusListener()
        configureConfirmPasswordEditTextFocusListener()
    }

    private fun setUpEditTextChangeListeners() {
        configureCurrentPinPasswordEditTextChangeListener()
        configureNewPasswordEditTextChangeListener()
        configureConfirmPasswordEditTextChangeListener()
        configureEditTextPinChangeListener(
            b.updatePasswordPinActivityNewPinET, b.updatePasswordPinActivityNewPinWarningTV
        )
        configureEditTextPinChangeListener(
            b.updatePasswordPinActivityConfirmPinET, b.updatePasswordPinActivityConfirmPinWarningTV
        )
    }

    private fun configureCurrentPinPasswordEditTextChangeListener() {
        b.updatePasswordPinActivityCurrentPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.updatePasswordPinActivityCurrentPasswordET.text.isEmpty()) {
                    b.updatePasswordPinActivityCurrentPasswordWarningTV.visibility = View.VISIBLE
                    b.updatePasswordPinActivityCurrentPasswordWarningTV.text =
                        getString(R.string.required_field)
                    b.updatePasswordPinActivityCurrentPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_error
                        )
                    b.updatePasswordPinActivityCurrentPasswordToggleTV.visibility = View.GONE
                } else {
                    b.updatePasswordPinActivityCurrentPasswordWarningTV.visibility = View.GONE

                    b.updatePasswordPinActivityCurrentPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_focused
                        )
                    b.updatePasswordPinActivityCurrentPasswordToggleTV.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun configureNewPasswordEditTextChangeListener() {
        b.updatePasswordPinActivityNewPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.updatePasswordPinActivityNewPasswordET.text.isEmpty()) {
                    b.updatePasswordPinActivityNewPasswordWarningTV.visibility = View.VISIBLE
                    b.updatePasswordPinActivityNewPasswordWarningTV.text =
                        getString(R.string.required_field)
                    b.updatePasswordPinActivityNewPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_error
                        )
                    b.updatePasswordPinActivityNewPasswordToggleTV.visibility = View.GONE
                } else {
                    b.updatePasswordPinActivityNewPasswordWarningTV.visibility = View.GONE
                    b.updatePasswordPinActivityNewPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_focused
                        )
                    b.updatePasswordPinActivityNewPasswordToggleTV.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun configureConfirmPasswordEditTextChangeListener() {
        b.updatePasswordPinActivityConfirmPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                //Remove any error warning text while typing
                if (b.updatePasswordPinActivityConfirmPasswordET.text.isEmpty()) {
                    b.updatePasswordPinActivityConfirmPasswordWarningTV.visibility = View.VISIBLE
                    b.updatePasswordPinActivityConfirmPasswordWarningTV.text =
                        getString(R.string.required_field)
                    b.updatePasswordPinActivityConfirmPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_error
                        )
                    b.updatePasswordPinActivityConfirmPasswordToggleTV.visibility = View.GONE
                } else {
                    b.updatePasswordPinActivityConfirmPasswordWarningTV.visibility = View.GONE
                    b.updatePasswordPinActivityConfirmPasswordBox.background =
                        ContextCompat.getDrawable(
                            this@UpdatePasswordPinActivity, R.drawable.bg_edit_text_focused
                        )
                    b.updatePasswordPinActivityConfirmPasswordToggleTV.visibility = View.VISIBLE
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

    private fun validateCurrentPasswordOrPin(): Boolean {
        var isValid = true
        b.updatePasswordPinActivityCurrentPasswordWarningTV.visibility = View.GONE

        if (b.updatePasswordPinActivityCurrentPasswordET.text.isEmpty()) {
            isValid = false
            b.updatePasswordPinActivityCurrentPasswordWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityCurrentPasswordWarningTV.text =
                getString(R.string.required_field)
            b.updatePasswordPinActivityCurrentPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }
        return isValid
    }

    private fun validatePassword(): Boolean {
        var isValid = true

        b.updatePasswordPinActivityConfirmPasswordWarningTV.visibility = View.GONE
        b.updatePasswordPinActivityConfirmPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        if (!validateCurrentPasswordOrPin()) {
            isValid = false
        }

        if (b.updatePasswordPinActivityNewPasswordET.text.toString() != b.updatePasswordPinActivityConfirmPasswordET.text.toString() && !b.updatePasswordPinActivityNewPasswordET.text.isNullOrEmpty()) {
            isValid = false
            b.updatePasswordPinActivityConfirmPasswordWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityConfirmPasswordWarningTV.text =
                getString(R.string.password_does_not_match)
            b.updatePasswordPinActivityConfirmPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }

        if (!validatePasswordConditions()) {
            isValid = false
            b.updatePasswordPinActivityNewPasswordWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityNewPasswordWarningTV.text =
                getString(R.string.weak_password_text)
            b.updatePasswordPinActivityNewPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }

        if (b.updatePasswordPinActivityNewPasswordET.text.isEmpty()) {
            isValid = false
            b.updatePasswordPinActivityNewPasswordWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityNewPasswordWarningTV.text =
                getString(R.string.required_field)
            b.updatePasswordPinActivityNewPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }
        if (b.updatePasswordPinActivityConfirmPasswordET.text.isEmpty()) {
            isValid = false
            b.updatePasswordPinActivityConfirmPasswordWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityConfirmPasswordWarningTV.text =
                getString(R.string.required_field)
            b.updatePasswordPinActivityConfirmPasswordBox.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        }


        return isValid
    }

    private fun validatePasswordConditions(): Boolean {
        var isValid = true
        val typedPassword = b.updatePasswordPinActivityNewPasswordET.text.toString()

        if (typedPassword.length < 8 || typedPassword.length > 12) isValid = false

        val tpUpperCase: Pattern = Pattern.compile(".*[A-Z].*")
        if (!tpUpperCase.matcher(typedPassword).matches()) isValid = false

        val tpLowerCase: Pattern = Pattern.compile(".*[a-z].*")
        if (!tpLowerCase.matcher(typedPassword).matches()) isValid = false

        val tpNumber: Pattern = Pattern.compile(".*[0-9].*")
        if (!tpNumber.matcher(typedPassword).matches()) isValid = false

        val tpSpecialCharacter: Pattern =
            Pattern.compile(".*['#$%&*+<=>@\\\\^_`|~(){}/,:;\\-\\[\\]].*")
        if (!tpSpecialCharacter.matcher(typedPassword).matches()) isValid = false

        val tpConsecutiveNumber: Pattern =
            Pattern.compile("^(?!.*(\\d)\\1{3})(?!.*1234)(?!.*2345)(?!.*3456)(?!.*4567)(?!.*5678)(?!.*6789)(?!.*7890)(?!.*8901)(?!.*9012)(?!.*0123).+\$")
        if (!tpConsecutiveNumber.matcher(typedPassword).matches()) isValid = false

        return isValid
    }

    private fun validatePin(): Boolean {
        var isValid = true

        b.updatePasswordPinActivityConfirmPinWarningTV.visibility = View.GONE

        if (!validateCurrentPasswordOrPin()) {
            isValid = false
        }

        if (b.updatePasswordPinActivityNewPinET.text.toString() != b.updatePasswordPinActivityConfirmPinET.text.toString()) {
            isValid = false
            b.updatePasswordPinActivityConfirmPinWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityConfirmPinWarningTV.text =
                getString(R.string.pin_does_not_match)
        }
        if (!validatePinCondition()) {
            isValid = false
            b.updatePasswordPinActivityNewPinWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityNewPinWarningTV.text = getString(R.string.weak_pin_text)
        }
        if (b.updatePasswordPinActivityNewPinET.text.toString().isEmpty()) {
            isValid = false
            b.updatePasswordPinActivityNewPinWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityNewPinWarningTV.text = getString(R.string.required_field)
        } else if (b.updatePasswordPinActivityNewPinET.text.toString().length != 6) {
            isValid = false
            b.updatePasswordPinActivityNewPinWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityNewPinWarningTV.text = getString(R.string.invalid_pin)
        }

        if (b.updatePasswordPinActivityConfirmPinET.text.toString().isEmpty()) {
            isValid = false
            b.updatePasswordPinActivityConfirmPinWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityConfirmPinWarningTV.text = getString(R.string.required_field)
        } else if (b.updatePasswordPinActivityConfirmPinET.text.toString().length != 6) {
            isValid = false
            b.updatePasswordPinActivityConfirmPinWarningTV.visibility = View.VISIBLE
            b.updatePasswordPinActivityConfirmPinWarningTV.text = getString(R.string.invalid_pin)
        }

        return isValid
    }

    private fun validatePinCondition(): Boolean {
        val typedPin = b.updatePasswordPinActivityNewPinET.text.toString()
        val tpUpperCase: Pattern =
            Pattern.compile("^(?!([0-9])\\1+\$)(?!.*(.).*\\2)(?!01|12|23|34|45|56|67|78|89|98)(?!.*1234\$)\\d{6}\$")
        return tpUpperCase.matcher(typedPin).matches()
    }


    private fun configureCurrentPinPasswordEditTextFocusListener(
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.updatePasswordPinActivityCurrentPasswordET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.updatePasswordPinActivityCurrentPasswordBox.background =
                    focusDrawable
                else if (b.updatePasswordPinActivityCurrentPasswordWarningTV.isVisible) b.updatePasswordPinActivityCurrentPasswordBox.background =
                    errorDrawable
                else b.updatePasswordPinActivityCurrentPasswordBox.background = notInFocusDrawable
            }
    }

    private fun configureNewPasswordEditTextFocusListener(
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.updatePasswordPinActivityNewPasswordET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.updatePasswordPinActivityNewPasswordBox.background = focusDrawable
                else if (b.updatePasswordPinActivityNewPasswordWarningTV.isVisible) b.updatePasswordPinActivityNewPasswordBox.background =
                    errorDrawable
                else b.updatePasswordPinActivityNewPasswordBox.background = notInFocusDrawable
            }
    }

    private fun configureConfirmPasswordEditTextFocusListener(
    ) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.updatePasswordPinActivityConfirmPasswordET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.updatePasswordPinActivityConfirmPasswordBox.background =
                    focusDrawable
                else if (b.updatePasswordPinActivityConfirmPasswordWarningTV.isVisible) b.updatePasswordPinActivityConfirmPasswordBox.background =
                    errorDrawable
                else b.updatePasswordPinActivityConfirmPasswordBox.background = notInFocusDrawable
            }
    }

    private fun onClickCurrentPasswordToggle() {
        val passwordTransformation =
            b.updatePasswordPinActivityCurrentPasswordET.transformationMethod
        if (passwordTransformation != null) {
            //Password is hidden make it visible
            b.updatePasswordPinActivityCurrentPasswordET.transformationMethod = null
            b.updatePasswordPinActivityCurrentPasswordToggleTV.text = getString(R.string.hide)
        } else {
            //Make it hidden
            b.updatePasswordPinActivityCurrentPasswordET.transformationMethod =
                PasswordTransformationMethod()
            b.updatePasswordPinActivityCurrentPasswordToggleTV.text = getString(R.string.show)
        }
        b.updatePasswordPinActivityCurrentPasswordET.setSelection(b.updatePasswordPinActivityCurrentPasswordET.length())
    }

    private fun onClickNewPasswordToggle() {
        val passwordTransformation = b.updatePasswordPinActivityNewPasswordET.transformationMethod
        if (passwordTransformation != null) {
            //Password is hidden make it visible
            b.updatePasswordPinActivityNewPasswordET.transformationMethod = null
            b.updatePasswordPinActivityNewPasswordToggleTV.text = getString(R.string.hide)
        } else {
            //Make it hidden
            b.updatePasswordPinActivityNewPasswordET.transformationMethod =
                PasswordTransformationMethod()
            b.updatePasswordPinActivityNewPasswordToggleTV.text = getString(R.string.show)
        }
        b.updatePasswordPinActivityNewPasswordET.setSelection(b.updatePasswordPinActivityNewPasswordET.length())
    }

    private fun onClickConfirmPasswordToggle() {
        val passwordTransformation =
            b.updatePasswordPinActivityConfirmPasswordET.transformationMethod
        if (passwordTransformation != null) {
            //Password is hidden make it visible
            b.updatePasswordPinActivityConfirmPasswordET.transformationMethod = null
            b.updatePasswordPinActivityConfirmPasswordToggleTV.text = getString(R.string.hide)
        } else {
            //Make it hidden
            b.updatePasswordPinActivityConfirmPasswordET.transformationMethod =
                PasswordTransformationMethod()
            b.updatePasswordPinActivityConfirmPasswordToggleTV.text = getString(R.string.show)
        }
        b.updatePasswordPinActivityConfirmPasswordET.setSelection(b.updatePasswordPinActivityConfirmPasswordET.length())
    }

    private fun showUpdatePinView() {
        hideKeyboard(this)
        b.updatePasswordPinActivityPinContainer.visibility = View.VISIBLE
        b.updatePasswordPinActivityPasswordContainer.visibility = View.GONE

        //Clear pin fields
        b.updatePasswordPinActivityNewPinET.text?.clear()
        b.updatePasswordPinActivityConfirmPinET.text?.clear()

        b.updatePasswordPinActivityNewPinWarningTV.visibility = View.GONE
        b.updatePasswordPinActivityConfirmPinWarningTV.visibility = View.GONE
    }

    private fun showUpdatePasswordView() {
        hideKeyboard(this)
        b.updatePasswordPinActivityPinContainer.visibility = View.GONE
        b.updatePasswordPinActivityPasswordContainer.visibility = View.VISIBLE

        //Clear password fields
        b.updatePasswordPinActivityNewPasswordET.text.clear()
        b.updatePasswordPinActivityConfirmPasswordET.text.clear()

        b.updatePasswordPinActivityNewPasswordWarningTV.visibility = View.GONE
        b.updatePasswordPinActivityConfirmPasswordWarningTV.visibility = View.GONE

        b.updatePasswordPinActivityNewPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.updatePasswordPinActivityConfirmPasswordBox.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
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

    private fun showPinPasswordUpdateBottomSheet(choosenType: String){
        val bottomSheetDialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.pin_password_change_bottom_sheet, null)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setCancelable(false)
        val titleTextView = dialogView.findViewById<TextView>(R.id.sheetPinPasswordChangeTitleTV)
        val subTitleTextView = dialogView.findViewById<TextView>(R.id.sheetPinPasswordChangeSubTextTV)
        val loginButton = dialogView.findViewById<AppCompatButton>(R.id.sheetPinPasswordChangeButton)
        loginButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this@UpdatePasswordPinActivity, LoginActivity::class.java))
            finishAffinity()
        }
        when (choosenType) {
            Constants.SELECTION_PIN -> {
                titleTextView.text = getString(R.string.pin_updated)
                subTitleTextView.text = getString(R.string.your_pin_has_been_successfully_updated)
            }

            Constants.SELECTION_PASSWORD -> {
                titleTextView.text = getString(R.string.password_updated)
                subTitleTextView.text = getString(R.string.your_password_has_been_successfully_updated)
            }
        }
        bottomSheetDialog.show()
    }
}