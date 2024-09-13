package com.afrimax.paymaart.ui.kyc

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.KycSaveCustomerPreferenceRequest
import com.afrimax.paymaart.databinding.ActivityKycSelectBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KycSelectActivity : BaseActivity() {
    private lateinit var b: ActivityKycSelectBinding
    private lateinit var startKycResultLauncher: ActivityResultLauncher<Intent>

    private var kycScope = ""
    private lateinit var paymaartId: String
    private lateinit var onboardScope: String
    private lateinit var citizenship: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityKycSelectBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboardKycSelectActivity)) { v, insets ->
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
        paymaartId = intent.getStringExtra(Constants.ONBOARD_USER_PAYMAART_ID) ?: ""
        onboardScope = intent.getStringExtra(Constants.ONBOARD_SCOPE) ?: ""
        citizenship = intent.getStringExtra(Constants.KYC_CITIZENSHIP) ?: ""

        startKycResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if ((result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) && data != null) {
                    kycScope = data.getStringExtra(Constants.KYC_SCOPE) ?: ""
                }
            }
    }

    private fun setUpListeners() {
        b.onboardKycSelectActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.onboardKycSelectActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(i, options)
        }

        b.onboardKycSelectActivityMalawiBox.setOnClickListener {
            toggleMalawiCitizenDropList()
        }

        b.onboardKycSelectActivityFullKycButton.setOnClickListener {
            /**User already filled full kyc and came back, so we are not calling the API again*/
            if (kycScope == Constants.KYC_MALAWI_FULL) {
                val i = Intent(this, KycAddressActivity::class.java)
                i.putExtra(Constants.KYC_SCOPE, Constants.KYC_MALAWI_FULL)
                i.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                i.putExtra(Constants.ONBOARD_SCOPE, onboardScope)
                startKycResultLauncher.launch(i)
            } else {
                saveCustomerMalawiFullKycApi()
            }
        }

        b.onboardKycSelectActivitySimplifiedKycButton.setOnClickListener {
            /**User already filled simplified kyc and came back, so we are not calling the API again*/
            if (kycScope == Constants.KYC_MALAWI_SIMPLIFIED) {
                val i = Intent(this, KycAddressActivity::class.java)
                i.putExtra(Constants.KYC_SCOPE, Constants.KYC_MALAWI_SIMPLIFIED)
                i.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                i.putExtra(Constants.ONBOARD_SCOPE, onboardScope)
                startKycResultLauncher.launch(i)
            } else {
                saveCustomerMalawiSimplifiedKycApi()
            }
        }

        b.onboardKycSelectActivityNonMalawiContainer.setOnClickListener {
            /**User already filled non malawi kyc and came back, so we are not calling the API again*/
            if (kycScope == Constants.KYC_NON_MALAWI) {
                val i = Intent(this, KycAddressActivity::class.java)
                i.putExtra(Constants.KYC_SCOPE, Constants.KYC_NON_MALAWI)
                i.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                i.putExtra(Constants.ONBOARD_SCOPE, onboardScope)
                startKycResultLauncher.launch(i)
            } else {
                saveCustomerNonMalawiKycApi()
            }
        }
    }

    private fun toggleMalawiCitizenDropList() {
        val transition = AutoTransition()
        transition.duration = 100

        if (b.onboardKycSelectActivityMalawiCitizenHiddenContainer.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.onboardKycSelectActivityBaseCV, transition
            )
            b.onboardKycSelectActivityMalawiCitizenHiddenContainer.visibility = View.GONE
            b.onboardKycSelectActivityMalawiContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_select_kyc_option_unselected)
            b.onboardKycSelectActivityMalawiCitizenExpandButton.animate().rotation(0f)
                .setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.onboardKycSelectActivityBaseCV, transition
            )
            b.onboardKycSelectActivityMalawiCitizenHiddenContainer.visibility = View.VISIBLE
            b.onboardKycSelectActivityMalawiContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_select_kyc_option_selected)
            b.onboardKycSelectActivityMalawiCitizenExpandButton.animate().rotation(180f)
                .setDuration(100)
        }
    }

    private fun saveCustomerMalawiFullKycApi() {
        showButtonLoader(
            b.onboardKycSelectActivityFullKycButton,
            b.onboardKycSelectActivityFullKycButtonLoaderLottie
        )
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveMalawiFullPreferenceCall =
                ApiClient.apiService.saveCustomerKYCPreference(
                    "Bearer $idToken",
                    KycSaveCustomerPreferenceRequest(
                        kyc_type = Constants.KYC_TYPE_MALAWI_FULL,
                        citizen = Constants.KYC_CITIZEN_MALAWIAN,
                    )
                )

            saveMalawiFullPreferenceCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycSelectActivityFullKycButton,
                                b.onboardKycSelectActivityFullKycButtonLoaderLottie,
                                getString(R.string.full_kyc)
                            )
                            val i = Intent(
                                this@KycSelectActivity, KycAddressActivity::class.java
                            )
                            i.putExtra(Constants.KYC_SCOPE, Constants.KYC_MALAWI_FULL)
                            i.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                            i.putExtra(Constants.ONBOARD_SCOPE, onboardScope)
                            startKycResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideButtonLoader(
                                b.onboardKycSelectActivityFullKycButton,
                                b.onboardKycSelectActivityFullKycButtonLoaderLottie,
                                getString(R.string.full_kyc)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycSelectActivityFullKycButton,
                            b.onboardKycSelectActivityFullKycButtonLoaderLottie,
                            getString(R.string.full_kyc)
                        )
                    }
                }
            })
        }
    }

    private fun saveCustomerMalawiSimplifiedKycApi() {
        showButtonLoader(
            b.onboardKycSelectActivitySimplifiedKycButton,
            b.onboardKycSelectActivitySimplifiedKycButtonLoaderLottie
        )
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveMalawiSimplifiedPreferenceCall =
                ApiClient.apiService.saveCustomerKYCPreference(
                    idToken, KycSaveCustomerPreferenceRequest(
                        kyc_type = Constants.KYC_TYPE_MALAWI_SIMPLIFIED,
                        citizen = Constants.KYC_CITIZEN_MALAWIAN,
                    )
                )

            saveMalawiSimplifiedPreferenceCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycSelectActivitySimplifiedKycButton,
                                b.onboardKycSelectActivitySimplifiedKycButtonLoaderLottie,
                                getString(R.string.simplified_kyc_star)
                            )
                            val i = Intent(
                                this@KycSelectActivity, KycAddressActivity::class.java
                            )
                            i.putExtra(Constants.KYC_SCOPE, Constants.KYC_MALAWI_SIMPLIFIED)
                            i.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                            i.putExtra(Constants.ONBOARD_SCOPE, onboardScope)
                            startKycResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideButtonLoader(
                                b.onboardKycSelectActivitySimplifiedKycButton,
                                b.onboardKycSelectActivitySimplifiedKycButtonLoaderLottie,
                                getString(R.string.simplified_kyc_star)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycSelectActivitySimplifiedKycButton,
                            b.onboardKycSelectActivitySimplifiedKycButtonLoaderLottie,
                            getString(R.string.simplified_kyc_star)
                        )
                    }
                }
            })
        }
    }


    private fun saveCustomerNonMalawiKycApi() {
        showNonMalawiButtonLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveMalawiSimplifiedPreferenceCall =
                ApiClient.apiService.saveCustomerKYCPreference(
                    idToken, KycSaveCustomerPreferenceRequest(
                        kyc_type = Constants.KYC_TYPE_NON_MALAWI_FULL,
                        citizen = citizenship.ifEmpty { Constants.KYC_CITIZEN_NON_MALAWIAN },
                    )
                )

            saveMalawiSimplifiedPreferenceCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            hideNonMalawiButtonLoader()
                            val i = Intent(
                                this@KycSelectActivity, KycAddressActivity::class.java
                            )
                            i.putExtra(Constants.KYC_SCOPE, Constants.KYC_NON_MALAWI)
                            i.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                            i.putExtra(Constants.ONBOARD_SCOPE, onboardScope)
                            startKycResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideNonMalawiButtonLoader()
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideNonMalawiButtonLoader()
                    }
                }
            })
        }
    }

    private fun showNonMalawiButtonLoader() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        b.onboardKycSelectActivityEarthContainer.visibility = View.INVISIBLE
        b.onboardKycSelectActivityNonMalawiTV.visibility = View.INVISIBLE
        b.onboardKycSelectActivityNonMalawiKycButtonLoaderLottie.visibility = View.VISIBLE
    }

    private fun hideNonMalawiButtonLoader() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        b.onboardKycSelectActivityEarthContainer.visibility = View.VISIBLE
        b.onboardKycSelectActivityNonMalawiTV.visibility = View.VISIBLE
        b.onboardKycSelectActivityNonMalawiKycButtonLoaderLottie.visibility = View.GONE
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