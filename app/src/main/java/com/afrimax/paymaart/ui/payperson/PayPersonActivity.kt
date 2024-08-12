package com.afrimax.paymaart.ui.payperson

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.IndividualSearchUserData
import com.afrimax.paymaart.data.model.PayAfrimaxResponse
import com.afrimax.paymaart.data.model.PayToUnRegisteredPersonRequest
import com.afrimax.paymaart.data.model.PayUnRegisteredPersonResponse
import com.afrimax.paymaart.databinding.ActivityPayPersonBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.membership.MembershipPlanModel
import com.afrimax.paymaart.ui.payment.PaymentSuccessfulActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.TotalReceiptSheet
import com.afrimax.paymaart.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paymaart.util.Constants
import kotlinx.coroutines.launch


class PayPersonActivity : BaseActivity(), SendPaymentInterface {

    private lateinit var b: ActivityPayPersonBinding

    private lateinit var userData: IndividualSearchUserData

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
    }

    private fun setUpLayout() {

        val nameList = userData.name.uppercase().split(" ")
        val shortName = "${nameList[0][0]}${nameList[1][0]}${nameList[2][0]}"
        b.payPersonActivityShortNameTV.text = shortName

        b.payPersonActivityNameTV.text = userData.name
        b.payPersonActivityPaymaartIdTV.text = userData.paymaartId
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
                b.payPersonActivityPaymentErrorBox.visibility = View.GONE
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
                b.payPersonActivityPaymentErrorBox.visibility = View.VISIBLE
                b.payPersonActivityPaymentErrorTV.text = getString(R.string.please_enter_amount)
            }

            amount.toDouble() < 1.0 -> {
                isValid = false
                b.payPersonActivityPaymentErrorBox.visibility = View.VISIBLE
                b.payPersonActivityPaymentErrorTV.text = getString(R.string.minimum_amount_is_1_mwk)
            }

            mainDigits.length > 7 || decimalDigits.length > 2 -> {
                isValid = false
                b.payPersonActivityPaymentErrorBox.visibility = View.VISIBLE
                b.payPersonActivityPaymentErrorTV.text = getString(R.string.invalid_amount)
            }

        }

        if (isValid) {
            //Valid amount
            hideKeyboard(this@PayPersonActivity)
            getTaxAndVatApi(amount = amount.toDouble())
        }
    }

    private fun getTaxAndVatApi(amount: Double) {
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val payPersonUnregisteredCall = ApiClient.apiService.getTaxForPayToUnRegisteredPerson(
                idToken, PayToUnRegisteredPersonRequest(
                    amount = amount,
                    phoneNumber = "${userData.countryCode}${userData.phoneNumber}",
                    callType = true,
                    receiverName = userData.name,
                    senderId = this@PayPersonActivity.retrievePaymaartId(),
                    note = b.payPersonActivityAddNoteET.text.toString(),
                    password = null
                )
            )
            val body = payPersonUnregisteredCall.body()

            if (payPersonUnregisteredCall.isSuccessful && body != null) {
                TotalReceiptSheet(
                    PayPersonModel(
                        amount = amount.toString(),
                        vat = body.data.vatAmount,
                        txnFee = body.data.grossTransactionFee,
                        phoneNumber = "${userData.countryCode}${userData.phoneNumber}",
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

    override fun onPaymentSuccess(successData: Any?) {
        val intent = Intent(this, PaymentSuccessfulActivity::class.java)
        val sceneTransitions = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        if (successData != null && successData is PayUnRegisteredPersonResponse) {
            intent.putExtra(Constants.SUCCESS_PAYMENT_DATA, successData as Parcelable)
        }
        startActivity(intent, sceneTransitions)
        finishAfterTransition()
    }

    override fun onPaymentFailure(message: String) {
        b.payPersonActivityPaymentErrorBox.visibility = View.VISIBLE
        b.payPersonActivityPaymentErrorTV.text = message
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {
        //
    }
}