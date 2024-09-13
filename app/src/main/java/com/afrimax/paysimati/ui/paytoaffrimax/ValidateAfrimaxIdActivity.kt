package com.afrimax.paysimati.ui.paytoaffrimax

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.ValidateAfrimaxIdResponse
import com.afrimax.paysimati.databinding.ActivityValidateAfrimaxIdBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.util.Constants
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidateAfrimaxIdActivity : BaseActivity() {
    private lateinit var b: ActivityValidateAfrimaxIdBinding
    private var customerName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityValidateAfrimaxIdBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        setUpListeners()
        showEditableLayout()

    }

    private fun setUpListeners() {
        customerName = intent.getStringExtra(Constants.CUSTOMER_NAME) ?: ""
        b.validateAfrimaxIdActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.validateAfrimaxIdActivityContinueButton.setOnClickListener {
            validateFieldForContinue()
        }

        b.validateAfrimaxIdActivityReEnterButton.setOnClickListener {
            showEditableLayout()
        }

        b.validateAfrimaxIdActivityContinuePayButton.setOnClickListener {
            val intent = Intent(this, PayAfrimaxActivity::class.java)
            intent.putExtra(Constants.CUSTOMER_NAME, customerName)
            intent.putExtra(Constants.AFRIMAX_NAME, afrimaxName)
            intent.putExtra(Constants.AFRIMAX_ID, afrimaxId)
            startActivity(intent)
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
    }

    private val afrimaxId: String
        get() = b.validateAfrimaxIdActivityAfrimaxIdET.text.toString()

    private val afrimaxName: String
        get() = b.validateAfrimaxIdActivityAfrimaxNameTV.text.toString()

    private fun validateFieldForContinue() {
        var isValid = true

        if (b.validateAfrimaxIdActivityAfrimaxIdET.text.toString().isEmpty()) {
            isValid = false
            b.validateAfrimaxIdActivityAfrimaxIdWarningTV.visibility = View.VISIBLE
            b.validateAfrimaxIdActivityAfrimaxIdWarningTV.text = getString(R.string.required_field)
            b.validateAfrimaxIdActivityAfrimaxIdET.background = ContextCompat.getDrawable(
                this@ValidateAfrimaxIdActivity, R.drawable.bg_edit_text_error
            )
        }

        if (isValid) validateAfrimaxIdApi()
    }

    private fun setupEditTextFocusListeners() {
        configureEditTextFocusListeners(
            b.validateAfrimaxIdActivityAfrimaxIdET, b.validateAfrimaxIdActivityAfrimaxIdWarningTV
        )
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextChangeListeners(
            b.validateAfrimaxIdActivityAfrimaxIdET, b.validateAfrimaxIdActivityAfrimaxIdWarningTV
        )
    }

    private fun configureEditTextFocusListeners(nameET: EditText, warningText: TextView) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        nameET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) nameET.background = focusDrawable
            else if (warningText.isVisible) nameET.background = errorDrawable
            else nameET.background = notInFocusDrawable

        }
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
                    warningTV.text = getString(R.string.required_field)
                    nameET.background = ContextCompat.getDrawable(
                        this@ValidateAfrimaxIdActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    warningTV.visibility = View.GONE
                    nameET.background = ContextCompat.getDrawable(
                        this@ValidateAfrimaxIdActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun showEditableLayout() {

        b.validateAfrimaxIdActivityAfrimaxNameContainer.visibility = View.GONE
        b.validateAfrimaxIdActivityAfrimaxIdET.isEnabled = true

        //Show corresponding buttons
        b.validateAfrimaxIdActivityContinueButton.visibility = View.VISIBLE
        b.validateAfrimaxIdActivityReEnterContinueContainer.visibility = View.GONE

    }

    private fun showPreviewLayout() {

        b.validateAfrimaxIdActivityAfrimaxNameContainer.visibility = View.VISIBLE
        b.validateAfrimaxIdActivityAfrimaxIdET.isEnabled = false

        //Show corresponding buttons
        b.validateAfrimaxIdActivityContinueButton.visibility = View.GONE
        b.validateAfrimaxIdActivityReEnterContinueContainer.visibility = View.VISIBLE

    }

    private fun validateAfrimaxIdApi() {
        showButtonLoader(
            b.validateAfrimaxIdActivityContinueButton,
            b.validateAfrimaxIdActivityContinueButtonLoaderLottie
        )

        lifecycleScope.launch {
            val idToken = fetchIdToken()

            val validateAfrimaxIdCall = ApiClient.apiService.validateAfrimaxId(
                idToken, b.validateAfrimaxIdActivityAfrimaxIdET.text.toString().toInt()
            )

            validateAfrimaxIdCall.enqueue(object : Callback<ValidateAfrimaxIdResponse> {
                override fun onResponse(
                    call: Call<ValidateAfrimaxIdResponse>,
                    response: Response<ValidateAfrimaxIdResponse>
                ) {
                    hideButtonLoader(
                        b.validateAfrimaxIdActivityContinueButton,
                        b.validateAfrimaxIdActivityContinueButtonLoaderLottie,
                        getString(R.string._continue)
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        b.validateAfrimaxIdActivityAfrimaxNameTV.text = body.data.name
                        showPreviewLayout()
                    } else {
                        b.validateAfrimaxIdActivityAfrimaxIdWarningTV.visibility = View.VISIBLE
                        b.validateAfrimaxIdActivityAfrimaxIdWarningTV.text =
                            getString(R.string.invalid_afrimax_id)
                        b.validateAfrimaxIdActivityAfrimaxIdET.background =
                            ContextCompat.getDrawable(
                                this@ValidateAfrimaxIdActivity, R.drawable.bg_edit_text_error
                            )
                    }
                }

                override fun onFailure(call: Call<ValidateAfrimaxIdResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.validateAfrimaxIdActivityContinueButton,
                        b.validateAfrimaxIdActivityContinueButtonLoaderLottie,
                        getString(R.string._continue)
                    )
                    showToast(getString(R.string.default_error_toast))
                }

            })
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

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = View.GONE

    }
}