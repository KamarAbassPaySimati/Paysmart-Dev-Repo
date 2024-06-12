package com.afrimax.paymaart.ui.login

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityTwoFactorAuthBinding
import com.afrimax.paymaart.ui.home.HomeActivity
import com.afrimax.paymaart.ui.kyc.KycProgressActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.LoginPinTransformation
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify

class TwoFactorAuthActivity : AppCompatActivity() {
    private lateinit var b: ActivityTwoFactorAuthBinding
    private lateinit var loginMode: String

    private var sharedSecret = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        b = ActivityTwoFactorAuthBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.twoFactorAuth)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        val authType = intent.getStringExtra(Constants.TWO_FACTOR_AUTH_TYPE)
            ?: Constants.TWO_FACTOR_AUTH_SETUP_TYPE

        when (authType) {
            Constants.TWO_FACTOR_AUTH_SETUP_TYPE -> {
                showAuthDisplayCodeContainer()

                sharedSecret = intent.getStringExtra(Constants.TWO_FACTOR_AUTH_SHARED_SECRET) ?: ""
                b.twoFactorAuthCodeTV.text = sharedSecret
            }

            Constants.TWO_FACTOR_AUTH_DIRECT_TOTP_TYPE -> {
                showAuthEnterCodeContainer()
                b.twoFactorAuthCopyCodeAgainTV.visibility = View.GONE
            }
        }

        initViews()
        setUpListeners()
    }

    private fun initViews() {
        b.twoFactorAuthCodeET.transformationMethod = LoginPinTransformation()
        loginMode = intent.getStringExtra(Constants.LOGIN_MODE) ?: ""
    }

    private fun setUpListeners() {
        b.twoFactorAuthCloseButton.setOnClickListener {
            if (b.twoFactorAuthSuccessfulContainer.isVisible) {
                startActivity(Intent(this@TwoFactorAuthActivity, HomeActivity::class.java))
                finishAffinity()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        b.twoFactorAuthCopyCodeTV.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(Constants.TWO_FACTOR_AUTH_SHARED_SECRET, sharedSecret)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Code copied successfully", Toast.LENGTH_SHORT).show()
        }

        b.twoFactorAuthContinueButton.setOnClickListener {
            showAuthEnterCodeContainer()
        }

        b.twoFactorAuthCopyCodeAgainTV.setOnClickListener {
            showAuthDisplayCodeContainer()
        }

        b.twoFactorAuthFinishButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            startActivity(Intent(this, KycProgressActivity::class.java))
            finishAffinity()
        }

        configureEditTextPinChangeListener()
    }

    private fun configureEditTextPinChangeListener() {
        b.twoFactorAuthCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.twoFactorAuthCodeET.text.toString()
                        .isNotEmpty()
                ) b.twoFactorAuthCodeWarningTV.visibility = View.GONE
                if (b.twoFactorAuthCodeET.text.toString().length == 6) {
                    //Check the code with Amplify
                    val totpCode = b.twoFactorAuthCodeET.text.toString()
                    hideKeyboard(this@TwoFactorAuthActivity)
                    amplifyConfirmSignIn(totpCode, sharedSecret.isNotEmpty())
                }
            }
        })
    }

    private fun amplifyConfirmSignIn(totpCode: String, isSettingUp: Boolean) {
        try {
            showCodeLoader(b.twoFactorAuthCodeWarningTV, b.twoFactorAuthCodeLoaderLottie)
            Amplify.Auth.confirmSignIn(totpCode, { result ->
                runOnUiThread {
                    hideCodeLoader(b.twoFactorAuthCodeLoaderLottie)
                }
                if (result.isSignedIn) {
                    Amplify.Auth.getCurrentUser({
                        //Save paymaartId to SharedPrefs for easy access
                        storePrefs(it.username, loginMode)
                        if (isSettingUp) {
                            //Setting up MFA add attribute to cognito and show successful screen
                            Amplify.Auth.updateUserAttribute(
                                AuthUserAttribute(
                                AuthUserAttributeKey.custom(Constants.CUSTOM_MFA_SECRET),
                                sharedSecret
                            ), {}, {})
                            runOnUiThread { showAuthSuccessfulContainer() }
                        } else {
                            //else directly take them to home screen
                            runOnUiThread {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finishAffinity()
                            }
                        }
                    }, {})


                } else {
                    showCodeWarning(getString(R.string.invalid_code))
                }

            }) { _ ->
                runOnUiThread {
                    hideCodeLoader(b.twoFactorAuthCodeLoaderLottie)
                    showCodeWarning(getString(R.string.invalid_code))
                }
            }
        } catch (error: Exception) {
            hideCodeLoader(b.twoFactorAuthCodeLoaderLottie)
            showCodeWarning(getString(R.string.invalid_code))
        }
    }

    private fun storePrefs(paymaartId: String, loginMode: String) {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.PREF_KEY_PAYMAART_ID, paymaartId)
        editor.putString(Constants.PREF_KEY_LOGIN_MODE, loginMode)
        editor.apply()
        editor.commit()
    }

    private fun showCodeWarning(warning: String) {
        b.twoFactorAuthCodeWarningTV.visibility = View.VISIBLE
        b.twoFactorAuthCodeWarningTV.text = warning
        if (!b.twoFactorAuthCodeET.text.isNullOrBlank()) b.twoFactorAuthCodeET.text!!.clear()
    }

    private fun showAuthDisplayCodeContainer() {
        b.twoFactorAuthDisplayCodeContainer.visibility = View.VISIBLE
        b.twoFactorAuthEnterCodeContainer.visibility = View.GONE
        b.twoFactorAuthSuccessfulContainer.visibility = View.GONE

        b.twoFactorAuthContinueButton.visibility = View.VISIBLE
        b.twoFactorAuthCopyCodeAgainTV.visibility = View.GONE
        b.twoFactorAuthFinishButton.visibility = View.GONE
    }

    private fun showAuthEnterCodeContainer() {
        b.twoFactorAuthDisplayCodeContainer.visibility = View.GONE
        b.twoFactorAuthEnterCodeContainer.visibility = View.VISIBLE
        b.twoFactorAuthSuccessfulContainer.visibility = View.GONE

        b.twoFactorAuthContinueButton.visibility = View.GONE
        b.twoFactorAuthCopyCodeAgainTV.visibility = View.VISIBLE
        b.twoFactorAuthFinishButton.visibility = View.GONE
    }

    private fun showAuthSuccessfulContainer() {
        b.twoFactorAuthDisplayCodeContainer.visibility = View.GONE
        b.twoFactorAuthEnterCodeContainer.visibility = View.GONE
        b.twoFactorAuthSuccessfulContainer.visibility = View.VISIBLE

        b.twoFactorAuthContinueButton.visibility = View.GONE
        b.twoFactorAuthCopyCodeAgainTV.visibility = View.GONE
        b.twoFactorAuthFinishButton.visibility = View.VISIBLE
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showCodeLoader(
        warningTV: TextView, loaderLottie: LottieAnimationView
    ) {
        warningTV.visibility = View.GONE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = View.VISIBLE
    }

    private fun hideCodeLoader(loaderLottie: LottieAnimationView) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = View.GONE
    }
}