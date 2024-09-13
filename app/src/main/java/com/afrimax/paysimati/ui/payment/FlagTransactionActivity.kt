package com.afrimax.paysimati.ui.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.FlagTransactionRequest
import com.afrimax.paysimati.databinding.ActivityFlagTransactionBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.FlaggingSuccessSheet
import com.afrimax.paysimati.util.Constants
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlagTransactionActivity : BaseActivity() {
    private lateinit var b: ActivityFlagTransactionBinding
    private lateinit var transactionId: String
    private var isFlagged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFlagTransactionBinding.inflate(layoutInflater)
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

        transactionId = intent.getStringExtra(Constants.TRANSACTION_ID) ?: ""

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val callbackIntent = Intent()
                callbackIntent.putExtra(Constants.FLAGGED_STATUS, isFlagged)
                setResult(RESULT_CANCELED, callbackIntent)
                finish()
            }
        })
    }

    private fun setUpListeners() {

        b.flagTransactionActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.flagTransactionActivityReason1Title.setOnClickListener {
            b.flagTransactionActivityReason1CB.isChecked =
                !(b.flagTransactionActivityReason1CB.isChecked)
        }

        b.flagTransactionActivityReason2Title.setOnClickListener {
            b.flagTransactionActivityReason2CB.isChecked =
                !(b.flagTransactionActivityReason2CB.isChecked)
        }

        b.flagTransactionActivityReason3Title.setOnClickListener {
            b.flagTransactionActivityReason3CB.isChecked =
                !(b.flagTransactionActivityReason3CB.isChecked)
        }

        b.flagTransactionActivityReason4Title.setOnClickListener {
            b.flagTransactionActivityReason4CB.isChecked =
                !(b.flagTransactionActivityReason4CB.isChecked)
        }

        listOf(
            b.flagTransactionActivityReason1CB,
            b.flagTransactionActivityReason2CB,
            b.flagTransactionActivityReason3CB,
            b.flagTransactionActivityReason4CB
        ).forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                toggleFlagTransactionButton()
            }
        }

        b.flagTransactionActivityFlagButton.setOnClickListener {
            flagTransactionApi()
        }
    }

    private fun toggleFlagTransactionButton() {
        b.flagTransactionActivityFlagButton.isEnabled = checkSelectedFields()
    }

    private fun checkSelectedFields(): Boolean {
        if (b.flagTransactionActivityReason1CB.isChecked) return true
        if (b.flagTransactionActivityReason2CB.isChecked) return true
        if (b.flagTransactionActivityReason3CB.isChecked) return true
        if (b.flagTransactionActivityReason4CB.isChecked) return true
        return false
    }

    private fun getReasons(): ArrayList<String> {
        val reasonsList = ArrayList<String>()
        if (b.flagTransactionActivityReason1CB.isChecked) reasonsList.add("ft01")
        if (b.flagTransactionActivityReason2CB.isChecked) reasonsList.add("ft02")
        if (b.flagTransactionActivityReason3CB.isChecked) reasonsList.add("ft03")
        if (b.flagTransactionActivityReason4CB.isChecked) reasonsList.add("ft04")
        return reasonsList
    }

    private fun flagTransactionApi() {
        showButtonLoader(
            b.flagTransactionActivityFlagButton, b.flagTransactionActivityFlagButtonLoaderLottie
        )
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val flagTransactionRequestBody = FlagTransactionRequest(transactionId = transactionId, reasons = getReasons())
            val flagTransactionCall = ApiClient.apiService.flagTransaction(
                idToken,
                flagTransactionRequestBody
            )

            flagTransactionCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    hideButtonLoader(
                        b.flagTransactionActivityFlagButton,
                        b.flagTransactionActivityFlagButtonLoaderLottie,
                        getString(R.string.flag_transaction)
                    )

                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        isFlagged = true
                        FlaggingSuccessSheet().apply {
                            isCancelable = false
                        }.show(
                            supportFragmentManager, FlaggingSuccessSheet.TAG
                        )
                    } else {
                        showToast(getString(R.string.default_error_toast))
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.flagTransactionActivityFlagButton,
                        b.flagTransactionActivityFlagButtonLoaderLottie,
                        getString(R.string.flag_transaction)
                    )
                    showToast(getString(R.string.default_error_toast))
                }
            })
        }
    }


    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = getString(R.string.empty_string)
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