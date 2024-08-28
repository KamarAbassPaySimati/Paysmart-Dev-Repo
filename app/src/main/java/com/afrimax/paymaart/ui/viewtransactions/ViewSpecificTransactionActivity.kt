package com.afrimax.paymaart.ui.viewtransactions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.Extra
import com.afrimax.paymaart.data.model.GetTransactionDetailsResponse
import com.afrimax.paymaart.data.model.Membership
import com.afrimax.paymaart.data.model.TransactionDetail
import com.afrimax.paymaart.databinding.ActivityViewSpecificTransactionBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.payment.FlagTransactionActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.formatEpochTime
import com.afrimax.paymaart.util.formatEpochTimeTwo
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.showLogE
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class ViewSpecificTransactionActivity : BaseActivity() {
    private lateinit var b: ActivityViewSpecificTransactionBinding
    private lateinit var nextScreenResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var shareLauncher: ActivityResultLauncher<Intent>
    private var isFlagged = false
    private var transactionId: String = ""
    private var paymentStatusData: TransactionDetail? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityViewSpecificTransactionBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.paymentReceiptActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.offWhite)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.offWhite)
        initViews()
        setupView()
        getTransactionDetailsApi()
    }

    private fun initViews() {
        transactionId = intent.getStringExtra(Constants.TRANSACTION_ID) ?: ""

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                finishAffinity().apply {
//                    finishAfterTransition()
//                }
//            }
//        })


        nextScreenResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if ((result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) && data != null) {
                    isFlagged = data.getBooleanExtra(Constants.FLAGGED_STATUS, false)
                    if (isFlagged) makeTransactionFlagged()
                }
            }

        shareLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                b.paymentReceiptActivitySharePaymentIV.setOnClickListener { shareReceipt() }
            }

    }

    private fun setupView() {
        b.viewSpecificTransactionToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.paymentReceiptActivityFlagPaymentIV.setOnClickListener {
            nextScreenResultLauncher.launch(Intent(
                this@ViewSpecificTransactionActivity, FlagTransactionActivity::class.java
            ).apply {
                putExtra(Constants.TRANSACTION_ID, paymentStatusData?.transactionId)
            })
        }

        b.paymentReceiptActivitySharePaymentIV.setOnClickListener {
            shareReceipt()
        }
    }

    private fun getTransactionDetailsApi() {
        b.paymentReceiptActivityContentBox.visibility = View.GONE
        b.registrationActivityLoaderLottie.visibility = View.VISIBLE

        lifecycleScope.launch {
            val idToken = fetchIdToken()

            val getTransactionCall =
                ApiClient.apiService.getTransactionDetailsApi(idToken, transactionId)

            getTransactionCall.enqueue(object : Callback<GetTransactionDetailsResponse> {
                override fun onResponse(
                    call: Call<GetTransactionDetailsResponse>,
                    response: Response<GetTransactionDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        paymentStatusData = body.transactionDetail
                        setupReceiptScreen(paymentStatusData)
                        b.paymentReceiptActivityContentBox.visibility = View.VISIBLE
                        b.registrationActivityLoaderLottie.visibility = View.GONE
                    } else {
                        showToast(getString(R.string.default_error_toast))
                    }
                }

                override fun onFailure(call: Call<GetTransactionDetailsResponse>, t: Throwable) {
                    "Response".showLogE(t.message ?: "")
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun setupReceiptScreen(transactionDetail: TransactionDetail?) {
        val commonView = transactionDetail?.toCommonView()
        if (transactionDetail?.flagged == true) makeTransactionFlagged()
        when (transactionDetail?.transactionType) {
            CASH_IN, CASHIN -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.cash_in_value)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.cash_in_successful)
                b.paymentReceiptActivityBalanceContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityBalanceTV.text = getString(
                    R.string.amount_formatted,
                    getFormattedAmount(transactionDetail.receiverClosingBalance)
                )
            }

            CASH_OUT, CASHOUT -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.cash_out_value)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.cash_out_successful)
                b.paymentReceiptActivityBalanceContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityBalanceTV.text = getString(
                    R.string.amount_formatted,
                    getFormattedAmount(transactionDetail.receiverClosingBalance)
                )
            }

            AFRIMAX -> {
                setupCommonView(commonView)
                if (transactionDetail.extras != null) {
                    val extraModel = Gson().fromJson(transactionDetail.extras, Extra::class.java)
                    b.paymentReceiptActivityToAfrimaxNameContainer.visibility = View.VISIBLE
                    b.paymentReceiptActivityToAfrimaxIdContainer.visibility = View.VISIBLE
                    b.paymentReceiptActivityToAfrimaxNameTV.text = extraModel.afrimaxCustomerName
                    b.paymentReceiptActivityToAfrimaxIdTV.text =
                        extraModel.afrimaxCustomerId.toString()
                    if (!extraModel.afrimaxPlanName.isNullOrEmpty()) {
                        b.paymentReceiptActivityPlanContainer.visibility = View.VISIBLE
                        b.paymentReceiptActivityPlanTV.text = extraModel.afrimaxPlanName
                    }
                }
            }

            PAY_IN -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.pay_in_value)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.pay_in_successful)
                b.paymentReceiptActivityBalanceContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityBalanceTV.text = getString(
                    R.string.amount_formatted,
                    getFormattedAmount(transactionDetail.receiverClosingBalance)
                )
                b.paymentReceiptActivityFromPaymaartIdTV.text = transactionDetail.enteredBy
                b.paymentReceiptActivityFromPaymaartNameTV.text = transactionDetail.enteredByName
            }

            PAY_PERSON, PAY_UNREGISTERED -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityNoteContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityNoteTV.text =
                    if (transactionDetail.note.isNullOrEmpty()) "-" else transactionDetail.note
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.txn_value)
            }

            REFUND, PAY_UNREGISTERED_REFUND -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.refund_value)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.refund_successful)
                b.paymentReceiptActivityNoteContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityBalanceContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityNoteTV.text = transactionDetail.note
                b.paymentReceiptActivityBalanceTV.text =
                    getFormattedAmount(transactionDetail.receiverClosingBalance)

            }

            PAYMAART -> {
                setupCommonView(commonView)
                val membershipData =
                    Gson().fromJson(transactionDetail.membershipData, Membership::class.java)
                val membershipName = when (membershipData.membershipName) {
                    "prime" -> getString(R.string.prime)
                    "primex" -> getString(R.string.primeX)
                    else -> getString(R.string.go)
                }
                val membershipValidity = membershipData.membershipShipValidity.toString()
                val membershipStart = formatEpochTimeTwo(membershipData.membershipStart)
                val membershipExpiry = formatEpochTimeTwo(membershipData.membershipExpiry)
                b.paymentReceiptActivityMembershipContainer.visibility =
                    if (membershipData.membershipName == null) View.GONE else View.VISIBLE
                b.paymentReceiptActivityMembershipTV.text = getString(
                    R.string.formatted_membership_value_two,
                    membershipName,
                    membershipValidity,
                    membershipStart,
                    membershipExpiry
                )
            }

            INTEREST -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityFromPaymaartNameTV.text = getString(R.string.trust_acc_int)
                b.paymentReceiptActivityFromPaymaartIdContainer.visibility = View.GONE
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.interest_value)
                b.paymentReceiptActivityBalanceContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityBalanceTVKey.text = getString(R.string.interest_period)
                b.paymentReceiptActivityBalanceTV.text = getString(
                    R.string.interest_period_formatted,
                    getQuarterEndDate(transactionDetail.createdAt)
                )
                b.paymentReceiptActivityVatInclContainer.visibility = View.VISIBLE
                b.paymentReceiptActivityVatInclTV.text =
                    getString(R.string.amount_formatted, getFormattedAmount(transactionDetail.vat))

            }

            G2P_PAY_IN -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.g2p_pay_in_successful)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.g2p_pay_in_value)
                b.paymentReceiptActivityBalanceTV.text = getString(
                    R.string.amount_formatted,
                    getFormattedAmount(transactionDetail.receiverClosingBalance)
                )
            }

            CASH_OUT_REQUEST -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.cash_out_value)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.cash_out_requested)
                b.paymentReceiptActivityStatusContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.paymentScreenOrange
                    )
                )
                b.paymentReceiptActivityFlagPaymentIV.visibility = View.GONE
            }

            CASH_OUT_FAILED -> {
                setupCommonView(commonView)
                b.paymentReceiptActivityPaymentTypeTV.text = getString(R.string.cash_out_value)
                b.paymentReceiptActivityStatusTV.text = getString(R.string.cash_out_failed)
                b.paymentReceiptActivityStatusContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.errorRed
                    )
                )
                b.paymentReceiptActivityFlagPaymentIV.visibility = View.GONE
            }
        }
    }

    private fun setupCommonView(data: CommonViewData?) {
        b.paymentReceiptActivityFromPaymaartNameTV.text = data?.fromPaymaartName
        b.paymentReceiptActivityFromPaymaartIdTV.text = data?.fromPaymaartID
        b.paymentReceiptActivityToPaymaartNameTV.text = data?.toPaymaartName
        b.paymentReceiptActivityToPaymaartIdTV.text = data?.toPaymaartId
        b.paymentReceiptActivityPaymentValueTV.text =
            getString(R.string.amount_formatted, getFormattedAmount(data?.txnValue))
        b.paymentReceiptActivityTxnFeeTV.text =
            getString(R.string.amount_formatted, getFormattedAmount(data?.txnFee))
        b.paymentReceiptActivityVatInclTV.text =
            getString(R.string.amount_formatted, getFormattedAmount(data?.vatIncluded))
        b.paymentReceiptActivityTxnIdTV.text = data?.txnId
        b.paymentReceiptActivityDateTimeTV.text = formatEpochTime(data?.dateTime)

    }


    private fun makeTransactionFlagged() {
        b.paymentReceiptActivityFlagPaymentIV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_payment_status_button_filled)
        b.paymentReceiptActivityFlagPaymentIV.setOnClickListener {
            showToast("This transaction is already flagged")
        }
    }

    private fun shareReceipt() {
        b.paymentReceiptActivitySharePaymentIV.setOnClickListener(null)
        val bitmap = getBitmapFromView(
            b.paymentReceiptActivitySV,
            b.paymentReceiptActivitySV.getChildAt(0).height,
            b.paymentReceiptActivitySV.getChildAt(0).width
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, getImageToShare(bitmap))
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra(Intent.EXTRA_SUBJECT, "")
            type = "image/png"
        }

        shareLauncher.launch(Intent.createChooser(intent, "Share Via"))
    }


    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable: Drawable? = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun getImageToShare(bitmap: Bitmap): Uri? {
        val imageFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "payment_receipt.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri =
                FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
        } catch (e: Exception) {
            //
        }
        return uri
    }

    companion object {
        const val CASHIN = "cashin"
        const val CASH_IN = "cash_in"
        const val CASHOUT = "cashout"
        const val CASH_OUT = "cash_out"
        const val AFRIMAX = "afrimax"
        const val PAY_IN = "pay_in"
        const val PAY_PERSON = "pay_person"
        const val PAY_UNREGISTERED = "pay_unregister"
        const val REFUND = "refund"
        const val PAY_UNREGISTERED_REFUND = "pay_unregister_refund"
        const val PAYMAART = "paymaart"
        const val INTEREST = "interest"
        const val G2P_PAY_IN = "g2p_pay_in"
        const val CASH_OUT_REQUEST = "cashout_request"
        const val CASH_OUT_FAILED = "cashout_failed"
    }
}

data class CommonViewData(
    val fromPaymaartName: String,
    val fromPaymaartID: String,
    val toPaymaartName: String,
    val toPaymaartId: String,
    val txnValue: String,
    val txnFee: String,
    val vatIncluded: String,
    val txnId: String,
    val dateTime: Double,
    val balance: String,
    val transactionType: String,
)

fun TransactionDetail.toCommonView() = CommonViewData(
    fromPaymaartName = this.senderName ?: "-",
    fromPaymaartID = this.senderId ?: "-",
    toPaymaartName = this.receiverName ?: "-",
    toPaymaartId = this.receiverId ?: "-",
    txnValue = this.transactionAmount ?: "-",
    txnFee = this.transactionFee ?: "-",
    vatIncluded = this.vat ?: "-",
    txnId = this.transactionId,
    dateTime = this.createdAt ?: 0.0,
    balance = this.receiverClosingBalance ?: "-",
    transactionType = this.transactionType
)


fun <T> getQuarterEndDate(timestamp: T?): String {
    if (timestamp == null) return ""
    val newTimeStamp = when (timestamp) {
        is String -> timestamp.toLong()
        is Long -> timestamp
        is Double -> timestamp.toLong()
        else -> 0L
    }
    val calendar = Calendar.getInstance().apply {
        timeInMillis = newTimeStamp * 1000
    }

    // Determine the quarter and corresponding end date
    val month = calendar[Calendar.MONTH] + 1
    val year = calendar[Calendar.YEAR]
    val quarter = (month - 1) / 3 + 1

    val quarterEndDate = when (quarter) {
        1 -> "Q1 - 31 Mar $year"
        2 -> "Q2 - 30 Jun $year"
        3 -> "Q3 - 30 Sep $year"
        4 -> "Q4 - 31 Dec $year"
        else -> ""
    }

    return quarterEndDate
}