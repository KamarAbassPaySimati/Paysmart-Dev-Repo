package com.afrimax.paysimati.ui.paymerchant

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.common.presentation.utils.parseTillNumber
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MerchantRequestPay
import com.afrimax.paysimati.data.model.PayMerchantModel
import com.afrimax.paysimati.data.model.PayMerchantRequest
import com.afrimax.paysimati.data.model.PayMerchantRequestModel
import com.afrimax.paysimati.databinding.ActivityPayMerchantBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.membership.MembershipPlanModel
import com.afrimax.paysimati.ui.payment.PaymentSuccessfulActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.TotalReceiptSheet
import com.afrimax.paysimati.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.getInitials
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class PayMerchantActivity : BaseActivity(), SendPaymentInterface {
    private var paymaartID: String = ""
    private var merchantName: String = ""
    private var streetname: String = ""
    private var profilepic: String = ""
    private var tillno: String = ""
    private var amount:Double = 0.0
    private var statuscode :Int = 0
    private var transactionID : String = ""


    private lateinit var binding: ActivityPayMerchantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        paymaartID = intent.getStringExtra(Constants.PAYMAART_ID) ?: ""
        merchantName = intent.getStringExtra(Constants.MERCHANT_NAME) ?: ""
        streetname = intent.getStringExtra(Constants.STREET_NAME) ?: ""
        profilepic = intent.getStringExtra(Constants.PROFILE_PICTURE) ?: ""
        tillno = intent.getStringExtra(Constants.TILL_NUMBER) ?: ""
        amount = intent.getDoubleExtra(Constants.PAYMENT_AMOUNT,0.0)
        statuscode = intent.getIntExtra(Constants.STATUS_CODE,0)
        transactionID = intent.getStringExtra(Constants.TRANSACTION_ID) ?: ""

        super.onCreate(savedInstanceState)
        //  enableEdgeToEdge()
        binding = ActivityPayMerchantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.PayMerchantActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true


        setUpLayout()
        setUpListeners()
    }


    private fun setUpLayout() {
        if (profilepic.isNullOrEmpty()) {
            binding.payMerchantActivityShortNameTV.apply {
                visibility = VISIBLE
                text = getInitials(merchantName)
            }
        } else {
            binding.payMerchantActivityProfileIV.also {
                it.visibility = VISIBLE
                Glide.with(this).load(BuildConfig.CDN_BASE_URL + profilepic).centerCrop().into(it)
            }
        }

        binding.payMerchantActivityNameTV.text = merchantName

        binding.payMerchantActivityPaymaartIdTV.apply {
            if (paymaartID.isNotBlank()) {
                visibility = VISIBLE
                val formattedId = PaymaartIdFormatter.formatMerchantId(paymaartID)

                // Append address if it's not blank
                val addressPart = if (streetname.isNotBlank()) ", ${streetname}" else ""

                text = formattedId + addressPart
            } else {
                visibility = GONE
            }
        }
        binding.payMerchantActivitytillno.text = tillno.parseTillNumber()

        if (amount > 0.0) {
            binding.payMerchantActivityAmountET.apply {
                setText(amount.toString())
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
                setTextColor(resources.getColor(R.color.neutralGrey)) // Optional: Make it look disabled
            }
        }
    }

    private fun setUpListeners() {
        binding.payMerchantActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.payMerchantActivityAmountET.setOnClickListener {
            binding.payMerchantActivityAmountET.setSelection(binding.payMerchantActivityAmountET.text!!.length)
        }

        binding.payMerchantActivityAmountBox.setOnClickListener {
            binding.payMerchantActivityAmountET.requestFocus()
            showKeyboard(this@PayMerchantActivity)
        }
        binding.payMerchantActivitySendPaymentButton.setOnClickListener {
            validateAmount()
        }
        configureAmountTextChangeListener()
        configureNoteTextChangeListener()

    }

    private fun configureNoteTextChangeListener() {
        binding.payMerchantActivityAmountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (before != 1) {
                    val currentAmount = s.toString()
                    val numList = s.toString().split(".")
                    var finalString = s.toString()

                    //Remove listener
                    binding.payMerchantActivityAmountET.removeTextChangedListener(this)

                    //User already entered a decimal point
                    if (numList.size > 1) finalString =
                        "${numList[0].take(7)}.${numList[1].take(2)}"
                    else if (currentAmount.length > 7) finalString = currentAmount.take(7)

                    finalString = finalString.replaceFirst("^0+".toRegex(), "")
                    binding.payMerchantActivityAmountET.setText(finalString)
                    binding.payMerchantActivityAmountET.setSelection(finalString.length)

                    if (finalString.isEmpty()) binding.payMerchantActivityAmountET.setGravity(
                        Gravity.START
                    )
                    else binding.payMerchantActivityAmountET.setGravity(Gravity.CENTER)

                    //re register listener
                    binding.payMerchantActivityAmountET.addTextChangedListener(this)
                } else if (s.toString().isEmpty()) {
                    binding.payMerchantActivityAmountET.setGravity(Gravity.START)
                }
                binding.payMerchantActivityPaymentErrorBox.visibility = GONE
            }

        })

    }

    private fun configureAmountTextChangeListener() {
        binding.payMerchantActivityAddNoteET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString()
                        .isEmpty()
                ) binding.payMerchantActivityAddNoteET.setGravity(Gravity.START)
                else binding.payMerchantActivityAddNoteET.setGravity(Gravity.CENTER)
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }

        })
    }



    private fun validateAmount() {
        var isvalid = true
        val amount = binding.payMerchantActivityAmountET.text.toString()
        val numList = amount.split(".")
        val mainDigits = numList[0]
        val decimalDigits = if (numList.size > 1) numList[1] else ""

        when {
            amount.isEmpty() -> {
                isvalid = false
                binding.payMerchantActivityPaymentErrorBox.visibility = VISIBLE
                binding.payMerchantActivityPaymentErrorTV.text =
                    getString(R.string.please_enter_amount)
            }


            mainDigits.length > 7 || decimalDigits.length > 2 -> {
                isvalid = false
                binding.payMerchantActivityPaymentErrorBox.visibility = VISIBLE
                binding.payMerchantActivityPaymentErrorTV.text = getString(R.string.invalid_amount)
            }

        }
        if (isvalid) {
            if(statuscode==0){
                hideKeyboard(this@PayMerchantActivity)
                getTaxAndVatForMerchantTransaction(amount = amount.toDouble())

            }
            else{
                hideKeyboard(this@PayMerchantActivity)
                getTaxAndVatforMerchantRequestTransaction(amount = amount.toDouble())
            }
        }
    }



    private fun getTaxAndVatForMerchantTransaction(amount: Double) {
        showbuttonloader(
            binding.payMerchantActivitySendPaymentButton,
            binding.payMerchantActivitySendPaymentButtonLoaderLottie
        )

        lifecycleScope.launch {
            val idtoken = fetchIdToken()
            val response = ApiClient.apiService.getTaxForMechant(
                idtoken, PayMerchantRequest(
                    senderId = this@PayMerchantActivity.retrievePaymaartId(),
                    receiverId = paymaartID,
                    amount = amount,
                    flag = true,
                    entryBy = this@PayMerchantActivity.retrievePaymaartId(),
                    password = null,
                    tillnumber = tillno
                )

            )

            val body = response.body()
            if (response.isSuccessful && body != null) {
                hideButtonLoader(
                    binding.payMerchantActivitySendPaymentButton,
                    binding.payMerchantActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.send_payment)
                )
                TotalReceiptSheet(
                    PayMerchantModel(
                        amount = body.paymerchant.amount.toString(),
                        vat = body.paymerchant.vat.toString(),
                        txnFee = body.paymerchant.transactionfee.toString(),
                        recieiverid = paymaartID,
                        note = binding.payMerchantActivityAddNoteET.text.toString(),
                        senderId = this@PayMerchantActivity.retrievePaymaartId(),
                        entryBy = this@PayMerchantActivity.retrievePaymaartId(),
                        tillnumber = tillno
                    )

                ).show(supportFragmentManager, TotalReceiptSheet.TAG)

            } else {
                showToast(getString(R.string.default_error_toast))
            }


        }


    }


    private fun getTaxAndVatforMerchantRequestTransaction(amount: Double) {
        showbuttonloader(
            binding.payMerchantActivitySendPaymentButton,
            binding.payMerchantActivitySendPaymentButtonLoaderLottie
        )

        lifecycleScope.launch {
            val idtoken = fetchIdToken()
            val response = ApiClient.apiService.payMerchantRequest(
                idtoken, MerchantRequestPay(
                    senderId = this@PayMerchantActivity.retrievePaymaartId(),
                    requestId =transactionID,
                    entryBy = this@PayMerchantActivity.retrievePaymaartId(),
                    flag = true,
                    tillnumber = tillno,
                    receiverId = paymaartID,
                )

            )

            val body = response.body()
            if (response.isSuccessful && body != null) {
                hideButtonLoader(
                    binding.payMerchantActivitySendPaymentButton,
                    binding.payMerchantActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.send_payment)
                )
                TotalReceiptSheet(
                    PayMerchantRequestModel(
                        amount = body.paymerchant.amount.toString(),
                        vat = body.paymerchant.vat.toString(),
                        txnFee = body.paymerchant.transactionfee.toString(),
                        recieiverid = paymaartID,
                        note = binding.payMerchantActivityAddNoteET.text.toString(),
                        senderId = this@PayMerchantActivity.retrievePaymaartId(),
                        entryBy = this@PayMerchantActivity.retrievePaymaartId(),
                        tillnumber = tillno,
                        requestid = transactionID
                    )
           ).show(supportFragmentManager, TotalReceiptSheet.TAG)

            } else {
                showToast(getString(R.string.default_error_toast))
            }


        }


    }

    private fun showbuttonloader(actionButton: AppCompatButton, loaderLottie: LottieAnimationView) {
        actionButton.text = getString(R.string.empty_string)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = VISIBLE
    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = GONE
    }

    override fun onPaymentSuccess(successData: Any?) {
        val intent = Intent(this, PaymentSuccessfulActivity::class.java)
        val sceneTransitions = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        if (successData != null) {
            intent.putExtra(Constants.SUCCESS_PAYMENT_DATA, successData as Parcelable)
        }
        startActivity(intent, sceneTransitions)
        finishAfterTransition()
    }

    override fun onPaymentFailure(message: String) {
        binding.payMerchantActivityPaymentErrorBox.visibility = VISIBLE
        binding.payMerchantActivityPaymentErrorTV.text = message
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {
        //
    }

}