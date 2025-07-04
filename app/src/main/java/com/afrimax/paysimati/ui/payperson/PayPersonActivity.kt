package com.afrimax.paysimati.ui.payperson

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
import com.afrimax.paysimati.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.IndividualSearchUserData
import com.afrimax.paysimati.data.model.PayToRegisteredPersonRequest
import com.afrimax.paysimati.data.model.PayToUnRegisteredPersonRequest
import com.afrimax.paysimati.databinding.ActivityPayPersonBinding
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


class PayPersonActivity : BaseActivity(), SendPaymentInterface {

    private lateinit var b: ActivityPayPersonBinding

    private lateinit var userData: IndividualSearchUserData

    private var isRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPayPersonBinding.inflate(layoutInflater)
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
        userData = intent.parcelable<IndividualSearchUserData>(Constants.USER_DATA)!!
        if (userData.paymaartId.isNotEmpty() && userData.phoneNumber.isNotEmpty()) isRegistered =
            true
    }

    private fun setUpLayout() {
        if (userData.profilePicture.isNullOrEmpty()) {
            b.payPersonActivityShortNameTV.apply {
                visibility = VISIBLE
                text = getInitials(userData.name)
            }
        } else {
            b.payPersonActivityProfileIV.also {
                it.visibility = VISIBLE
                Glide.with(this).load(BuildConfig.CDN_BASE_URL + userData.profilePicture)
                    .centerCrop().into(it)
            }
        }
        b.payPersonActivityNameTV.text = userData.name

        b.payPersonActivityPaymaartIdTV.apply {
            if (userData.paymaartId.isNotBlank()) {
                visibility = VISIBLE
                val content = if (userData.paymaartId.startsWith("CMR")) {
                    PaymaartIdFormatter.formatCustomerId(userData.paymaartId)
                } else {
                    PhoneNumberFormatter.formatWholeNumber(userData.paymaartId)
                }
                text = content
            } else {
                visibility = GONE
            }
        }

        b.payPersonActivityPhoneNumberTV.apply {
            if (userData.phoneNumber.isNotBlank()) {
                visibility = VISIBLE
                val content = if (userData.countryCode.isNotBlank()) {
                    "${userData.countryCode} ${
                        PhoneNumberFormatter.format(
                            userData.countryCode, userData.phoneNumber
                        )
                    }"
                } else {
                    PhoneNumberFormatter.formatWholeNumber(userData.phoneNumber)
                }
                text = content
            } else {
                visibility = GONE
            }
        }

    }


    private fun setUpListeners() {

        b.payPersonActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.payPersonActivityAmountET.setOnClickListener { _ ->
            b.payPersonActivityAmountET.setSelection(b.payPersonActivityAmountET.text!!.length)
        }

        b.payAfrimaxActivityAmountBox.setOnClickListener {
            b.payPersonActivityAmountET.requestFocus()
            showKeyboard(this)
        }

        b.payPersonActivitySendPaymentButton.setOnClickListener {
            validateAmount()
        }

        configureAmountTextChangeListener()
        configureNoteTextChangeListener()
    }

    private fun configureAmountTextChangeListener() {
        b.payPersonActivityAmountET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                //
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //before value will be 1 if the user is clicking backspace
                if (before != 1) {
                    val currentAmount = s.toString()
                    val numList = s.toString().split(".")

                    var finalString = s.toString()

                    //Remove listener
                    b.payPersonActivityAmountET.removeTextChangedListener(this)

                    //User already entered a decimal point
                    if (numList.size > 1) finalString =
                        "${numList[0].take(7)}.${numList[1].take(2)}"
                    else if (currentAmount.length > 7) finalString = currentAmount.take(7)

                    finalString = finalString.replaceFirst("^0+".toRegex(), "")
                    b.payPersonActivityAmountET.setText(finalString)
                    b.payPersonActivityAmountET.setSelection(finalString.length)

                    if (finalString.isEmpty()) b.payPersonActivityAmountET.setGravity(Gravity.START)
                    else b.payPersonActivityAmountET.setGravity(Gravity.CENTER)

                    //re register listener
                    b.payPersonActivityAmountET.addTextChangedListener(this)
                } else if (s.toString().isEmpty()) {
                    b.payPersonActivityAmountET.setGravity(Gravity.START)
                }

                //Hide any error warning
                b.payPersonActivityPaymentErrorBox.visibility = GONE
            }
        })
    }

    private fun configureNoteTextChangeListener() {
        b.payPersonActivityAddNoteET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                //
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) b.payPersonActivityAddNoteET.setGravity(Gravity.START)
                else b.payPersonActivityAddNoteET.setGravity(Gravity.CENTER)
            }
        })
    }

    private fun validateAmount() {
        var isValid = true
        val amount = b.payPersonActivityAmountET.text.toString()

        val numList = amount.split(".")
        val mainDigits = numList[0]
        val decimalDigits = if (numList.size > 1) numList[1] else ""

        when {
            amount.isEmpty() -> {
                isValid = false
                b.payPersonActivityPaymentErrorBox.visibility = VISIBLE
                b.payPersonActivityPaymentErrorTV.text = getString(R.string.please_enter_amount)
            }

            isRegistered && amount.toDouble() < 100.0 -> {
                isValid = false
                b.payPersonActivityPaymentErrorBox.visibility = VISIBLE
                b.payPersonActivityPaymentErrorTV.text =
                    getString(R.string.minimum_amount_is_100_mwk)
            }

            !isRegistered && amount.toDouble() < 500.0 -> {
                isValid = false
                b.payPersonActivityPaymentErrorBox.visibility = VISIBLE
                b.payPersonActivityPaymentErrorTV.text =
                    getString(R.string.minimum_amount_is_500_mwk)
            }

            mainDigits.length > 7 || decimalDigits.length > 2 -> {
                isValid = false
                b.payPersonActivityPaymentErrorBox.visibility = VISIBLE
                b.payPersonActivityPaymentErrorTV.text = getString(R.string.invalid_amount)
            }

        }

        if (isValid) {
            //Valid amount
            hideKeyboard(this@PayPersonActivity)
            if (userData.paymaartId.isEmpty() && userData.phoneNumber.isNotEmpty()) {
                //unregistered
                val phone = if (userData.phoneNumber.startsWith("+")) userData.phoneNumber.replace(
                    " ", ""
                ) else "+265${userData.phoneNumber}".replace(" ", "")

                getTaxAndVatForUnRegisteredApi(amount = amount.toDouble(), phone)
            } else if (userData.paymaartId.isNotEmpty() && userData.phoneNumber.isEmpty()) {
                //unregistered
                val phone = if (userData.paymaartId.startsWith("+")) userData.paymaartId.replace(
                    " ", ""
                ) else "+265${userData.paymaartId}".replace(" ", "")

                getTaxAndVatForUnRegisteredApi(amount = amount.toDouble(), phone)
            } else {
                //registered
                getTaxAndVatForRegisteredApi(amount = amount.toDouble())
            }
        }
    }

    private fun getTaxAndVatForUnRegisteredApi(amount: Double, phone: String) {
        showButtonLoader(
            b.payPersonActivitySendPaymentButton, b.payPersonActivitySendPaymentButtonLoaderLottie
        )

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val payPersonUnregisteredCall = ApiClient.apiService.getTaxForPayToUnRegisteredPerson(
                idToken, PayToUnRegisteredPersonRequest(
                    amount = amount,
                    phoneNumber = phone,
                    callType = true,
                    receiverName = userData.name,
                    senderId = this@PayPersonActivity.retrievePaymaartId(),
                    note = b.payPersonActivityAddNoteET.text.toString(),
                    password = null
                )
            )
            val body = payPersonUnregisteredCall.body()

            if (payPersonUnregisteredCall.isSuccessful && body != null){
                hideButtonLoader(
                    b.payPersonActivitySendPaymentButton,
                    b.payPersonActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.send_payment)
                )
                TotalReceiptSheet(
                    PayPersonUnRegisteredModel(
                        amount = body.data.totalAmount,
                        enteredAmount = amount.toString(),
                        vat = body.data.vatAmount,
                        txnFee = body.data.grossTransactionFee,
                        phoneNumber = phone,
                        receiverName = userData.name,
                        note = b.payPersonActivityAddNoteET.text.toString(),
                        senderId = this@PayPersonActivity.retrievePaymaartId()
                    )
                ).show(supportFragmentManager, TotalReceiptSheet.TAG)
            } else {
                showToast(getString(R.string.default_error_toast))
            }
        }
    }

    private fun getTaxAndVatForRegisteredApi(amount: Double) {
        showButtonLoader(
            b.payPersonActivitySendPaymentButton, b.payPersonActivitySendPaymentButtonLoaderLottie
        )

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val payPersonUnregisteredCall = ApiClient.apiService.getTaxForPayToRegisteredPerson(
                idToken, PayToRegisteredPersonRequest(
                    paymaartId = userData.paymaartId,
                    transactionAmount = amount,
                )
            )
            val body = payPersonUnregisteredCall.body()

            if (payPersonUnregisteredCall.isSuccessful && body != null) {

                hideButtonLoader(
                    b.payPersonActivitySendPaymentButton,
                    b.payPersonActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.send_payment)
                )
                TotalReceiptSheet(
                    PayPersonRegisteredModel(
                        amount = body.totalAmount.toString(),
                        enteredAmount = amount.toString(),
                        vat = body.vat.toString(),
                        txnFee = body.transactionFee.toString(),
                        note = b.payPersonActivityAddNoteET.text.toString(),
                        paymaartId = userData.paymaartId
                    )
                ).show(supportFragmentManager, TotalReceiptSheet.TAG)
            }
            else if (
                payPersonUnregisteredCall.code() ==400 && body!=null
            ){
                hideButtonLoader(
                    b.payPersonActivitySendPaymentButton,
                    b.payPersonActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.send_payment)
                )
                TotalReceiptSheet(
                    PayPersonRegisteredModel(
                        amount = body.totalAmount.toString(),
                        enteredAmount = amount.toString(),
                        vat = body.vat.toString(),
                        txnFee = body.transactionFee.toString(),
                        note = b.payPersonActivityAddNoteET.text.toString(),
                        paymaartId = userData.paymaartId
                    )
                ).show(supportFragmentManager, TotalReceiptSheet.TAG)
            }
            else {
                showToast(getString(R.string.default_error_toast))
            }
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
        b.payPersonActivityPaymentErrorBox.visibility = VISIBLE
        b.payPersonActivityPaymentErrorTV.text = message
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {
        //
    }
}