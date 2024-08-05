package com.afrimax.paymaart.ui.cashout

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
import com.afrimax.paymaart.data.model.TransactionDetailsResponse
import com.afrimax.paymaart.databinding.ActivityCashOutBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.membership.MembershipPlanModel
import com.afrimax.paymaart.ui.payment.PaymentSuccessfulActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.TotalReceiptSheet
import com.afrimax.paymaart.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.getInitials
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CashOutActivity : BaseActivity(), SendPaymentInterface {
    private lateinit var b: ActivityCashOutBinding
    private lateinit var userData: IndividualSearchUserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCashOutBinding.inflate(layoutInflater)
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


        b.selfCashOutActivityShortNameTV.text = getInitials(userData.name)

        b.selfCashOutActivityNameTV.text = userData.name
        b.selfCashOutActivityPaymaartIdTV.text = userData.paymaartId
    }

    private fun setUpListeners() {

        b.selfCashOutActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.selfCashOutActivityAmountET.setOnClickListener { _ ->
            b.selfCashOutActivityAmountET.setSelection(b.selfCashOutActivityAmountET.text!!.length)
        }

        b.payAfrimaxActivityAmountBox.setOnClickListener {
            b.selfCashOutActivityAmountET.requestFocus()
            showKeyboard(this)
        }

        b.selfCashOutActivityCompleteCashOutButton.setOnClickListener {
            validateAmount()
        }

        configureAmountTextChangeListener()
    }

    private fun configureAmountTextChangeListener() {
        b.selfCashOutActivityAmountET.addTextChangedListener(object : TextWatcher {

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
                    b.selfCashOutActivityAmountET.removeTextChangedListener(this)

                    //User already entered a decimal point
                    if (numList.size > 1) finalString =
                        "${numList[0].take(7)}.${numList[1].take(2)}"
                    else if (currentAmount.length > 7) finalString = currentAmount.take(7)

                    finalString = finalString.replaceFirst("^0+".toRegex(), "")
                    b.selfCashOutActivityAmountET.setText(finalString)
                    b.selfCashOutActivityAmountET.setSelection(finalString.length)

                    if (finalString.isEmpty()) b.selfCashOutActivityAmountET.setGravity(Gravity.START)
                    else b.selfCashOutActivityAmountET.setGravity(Gravity.CENTER)

                    //re register listener
                    b.selfCashOutActivityAmountET.addTextChangedListener(this)
                } else if (s.toString().isEmpty()) {
                    b.selfCashOutActivityAmountET.setGravity(Gravity.START)
                }

                //Hide any error warning
                b.selfCashOutActivityPaymentErrorBox.visibility = View.INVISIBLE
            }
        })
    }

    private fun validateAmount() {
        var isValid = true
        val amount = b.selfCashOutActivityAmountET.text.toString()

        val numList = amount.split(".")
        val mainDigits = numList[0]
        val decimalDigits = if (numList.size > 1) numList[1] else ""

        when {
            amount.isEmpty() -> {
                isValid = false
                b.selfCashOutActivityPaymentErrorBox.visibility = View.VISIBLE
                b.selfCashOutActivityPaymentErrorTV.text = getString(R.string.please_enter_amount)
            }

            amount.toDouble() < 1.0 -> {
                isValid = false
                b.selfCashOutActivityPaymentErrorBox.visibility = View.VISIBLE
                b.selfCashOutActivityPaymentErrorTV.text =
                    getString(R.string.minimum_amount_is_1_mwk)
            }

            mainDigits.length > 7 || decimalDigits.length > 2 -> {
                isValid = false
                b.selfCashOutActivityPaymentErrorBox.visibility = View.VISIBLE
                b.selfCashOutActivityPaymentErrorTV.text = getString(R.string.invalid_amount)
            }

        }

        if (isValid) {
            //Valid amount
            hideKeyboard(this@CashOutActivity)
            fetchTransactionDetails(amount)
        }
    }

    private fun fetchTransactionDetails(amount: String){
        showLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            lifecycleScope.launch {
                val transactionDetailsHandler = ApiClient.apiService.getTransactionDetails(
                    idToken,
                    amount
                )
                transactionDetailsHandler.enqueue(object: Callback<TransactionDetailsResponse>{
                    override fun onResponse(
                        call: Call<TransactionDetailsResponse>,
                        response: Response<TransactionDetailsResponse>,
                    ) {
                        if (response.isSuccessful && response.body() != null){
                            val data = response.body()!!.transactionDetails
                            val cashOutModel = CashOutModel(
                                transactionFee = data.grossTransactionFee.toString(),
                                vat = data.vatAmount.toString(),
                                amount = amount,
                                receiverPaymaartId = userData.paymaartId,
                                displayAmount = data.totalAmount
                            )
                            val totalReceiptSheet = TotalReceiptSheet(cashOutModel)
                            totalReceiptSheet.show(supportFragmentManager, totalReceiptSheet.tag)
                        }
                        hideLoader()
                    }

                    override fun onFailure(call: Call<TransactionDetailsResponse>, throwable: Throwable) {
                        hideLoader()
                        showToast(getString(R.string.default_error_toast))
                    }

                })
            }
        }
    }

    private fun showLoader(){
        b.selfCashOutActivityCompleteCashOutLoaderLottie.visibility = View.VISIBLE
        b.selfCashOutActivityCompleteCashOutButton.apply {
            isEnabled = false
            text = getString(R.string.empty_string)
        }
    }

    private fun hideLoader() {
        b.selfCashOutActivityCompleteCashOutLoaderLottie.visibility = View.GONE
        b.selfCashOutActivityCompleteCashOutButton.apply {
            isEnabled = true
            text = getString(R.string.complete_cash_out)
        }
    }


    override fun onPaymentSuccess(successData: Any?) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@CashOutActivity).toBundle()
        startActivity(Intent(this@CashOutActivity, PaymentSuccessfulActivity::class.java).apply {
            putExtra(Constants.SUCCESS_PAYMENT_DATA, successData as Parcelable)
        }, options)
    }

    override fun onPaymentFailure(message: String) {
        b.selfCashOutActivityPaymentErrorBox.visibility = View.VISIBLE
        b.selfCashOutActivityPaymentErrorTV.text = message
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {}
}